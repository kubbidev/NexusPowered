package me.kubbidev.nexuspowered.event.functional.single;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import me.kubbidev.nexuspowered.Nexus;
import me.kubbidev.nexuspowered.event.SingleSubscription;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

class NexusEventListener<T extends Event> implements SingleSubscription<T>, EventExecutor, Listener {

    private final Class<T>      eventClass;
    private final EventPriority priority;

    private final BiConsumer<? super T, Throwable> exceptionConsumer;
    private final boolean                          handleSubclasses;

    private final Predicate<T>[]                                 filters;
    private final BiPredicate<SingleSubscription<T>, T>[]        preExpiryTests;
    private final BiPredicate<SingleSubscription<T>, T>[]        midExpiryTests;
    private final BiPredicate<SingleSubscription<T>, T>[]        postExpiryTests;
    private final BiConsumer<SingleSubscription<T>, ? super T>[] handlers;

    private final AtomicLong    callCount = new AtomicLong(0);
    private final AtomicBoolean active    = new AtomicBoolean(true);

    @SuppressWarnings("unchecked")
    NexusEventListener(SingleSubscriptionBuilderImpl<T> builder,
                       List<BiConsumer<SingleSubscription<T>, ? super T>> handlers) {
        this.eventClass = builder.eventClass;
        this.priority = builder.priority;
        this.exceptionConsumer = builder.exceptionConsumer;
        this.handleSubclasses = builder.handleSubclasses;

        this.filters = builder.filters.toArray(new Predicate[0]);
        this.preExpiryTests = builder.preExpiryTests.toArray(new BiPredicate[0]);
        this.midExpiryTests = builder.midExpiryTests.toArray(new BiPredicate[0]);
        this.postExpiryTests = builder.postExpiryTests.toArray(new BiPredicate[0]);
        this.handlers = handlers.toArray(new BiConsumer[0]);
    }

    private static void unregisterListener(Class<? extends Event> eventClass, Listener listener) {
        try {
            // unfortunately we can't cache this reflect call, as the method is static
            Method getHandlerListMethod = eventClass.getMethod("getHandlerList");
            HandlerList handlerList = (HandlerList) getHandlerListMethod.invoke(null);
            handlerList.unregister(listener);
        } catch (Throwable t) {
            // ignored
        }
    }

    void register(Plugin plugin) {
        Nexus.plugins().registerEvent(this.eventClass, this, this.priority, this, plugin, false);
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        // check we actually want this event
        if (this.handleSubclasses) {
            if (!this.eventClass.isInstance(event)) {
                return;
            }
        } else {
            if (event.getClass() != this.eventClass) {
                return;
            }
        }

        // this handler is disabled, so unregister from the event.
        if (!this.active.get()) {
            event.getHandlers().unregister(listener);
            return;
        }

        // obtain the event instance
        T eventInstance = this.eventClass.cast(event);

        // check pre-expiry tests
        for (BiPredicate<SingleSubscription<T>, T> test : this.preExpiryTests) {
            if (test.test(this, eventInstance)) {
                event.getHandlers().unregister(listener);
                this.active.set(false);
                return;
            }
        }

        // begin "handling" of the event
        try {
            // check the filters
            for (Predicate<T> filter : this.filters) {
                if (!filter.test(eventInstance)) {
                    return;
                }
            }

            // check mid-expiry tests
            for (BiPredicate<SingleSubscription<T>, T> test : this.midExpiryTests) {
                if (test.test(this, eventInstance)) {
                    event.getHandlers().unregister(listener);
                    this.active.set(false);
                    return;
                }
            }

            // call the handler
            for (BiConsumer<SingleSubscription<T>, ? super T> handler : this.handlers) {
                handler.accept(this, eventInstance);
            }

            // increment call counter
            this.callCount.incrementAndGet();
        } catch (Throwable t) {
            this.exceptionConsumer.accept(eventInstance, t);
        }

        // check post-expiry tests
        for (BiPredicate<SingleSubscription<T>, T> test : this.postExpiryTests) {
            if (test.test(this, eventInstance)) {
                event.getHandlers().unregister(listener);
                this.active.set(false);
                return;
            }
        }
    }

    @Override
    public @NotNull Class<T> getEventClass() {
        return this.eventClass;
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public boolean isClosed() {
        return !this.active.get();
    }

    @Override
    public long getCallCounter() {
        return this.callCount.get();
    }

    @Override
    public boolean unregister() {
        // already unregistered
        if (!this.active.getAndSet(false)) {
            return false;
        }

        // also remove the handler directly, just in case the event has a really low throughput.
        // (the event would also be unregistered next time it's called - but this obviously assumes
        // the event will be called again soon)
        unregisterListener(this.eventClass, this);
        return true;
    }
}