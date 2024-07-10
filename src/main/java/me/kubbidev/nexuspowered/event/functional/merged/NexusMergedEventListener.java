package me.kubbidev.nexuspowered.event.functional.merged;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import me.kubbidev.nexuspowered.Nexus;
import me.kubbidev.nexuspowered.event.MergedSubscription;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

class NexusMergedEventListener<T> implements MergedSubscription<T>, EventExecutor, Listener {
    private final TypeToken<T> handledClass;
    private final Map<Class<? extends Event>, MergedHandlerMapping<T, ? extends Event>> mappings;

    private final BiConsumer<? super Event, Throwable> exceptionConsumer;

    private final Predicate<T>[] filters;
    private final BiPredicate<MergedSubscription<T>, T>[] preExpiryTests;
    private final BiPredicate<MergedSubscription<T>, T>[] midExpiryTests;
    private final BiPredicate<MergedSubscription<T>, T>[] postExpiryTests;
    private final BiConsumer<MergedSubscription<T>, ? super T>[] handlers;

    private final AtomicLong callCount = new AtomicLong(0);
    private final AtomicBoolean active = new AtomicBoolean(true);

    @SuppressWarnings("unchecked")
    NexusMergedEventListener(MergedSubscriptionBuilderImpl<T> builder, List<BiConsumer<MergedSubscription<T>, ? super T>> handlers) {
        this.handledClass = builder.handledClass;
        this.mappings = ImmutableMap.copyOf(builder.mappings);
        this.exceptionConsumer = builder.exceptionConsumer;

        this.filters = builder.filters.toArray(new Predicate[0]);
        this.preExpiryTests = builder.preExpiryTests.toArray(new BiPredicate[0]);
        this.midExpiryTests = builder.midExpiryTests.toArray(new BiPredicate[0]);
        this.postExpiryTests = builder.postExpiryTests.toArray(new BiPredicate[0]);
        this.handlers = handlers.toArray(new BiConsumer[0]);
    }

    void register(Plugin plugin) {
        Map<Class<?>, EventPriority> registered = new IdentityHashMap<>();

        for (Map.Entry<Class<? extends Event>, MergedHandlerMapping<T, ? extends Event>> ent : this.mappings.entrySet()) {
            Class<? extends Event> type = ent.getKey();
            Class<? extends Event> registrationType = getRegistrationClass(type);

            // only register once
            EventPriority existing = registered.put(registrationType, ent.getValue().getPriority());
            if (existing != null) {
                if (existing != ent.getValue().getPriority()) {
                    throw new RuntimeException("Unable to register the same event with different priorities: " + type + " --> " + registrationType);
                }
                continue;
            }

            Nexus.plugins().registerEvent(registrationType, this, ent.getValue().getPriority(), this, plugin, false);
        }
    }

    @Override
    public void execute(@NotNull Listener listener, Event event) {
        MergedHandlerMapping<T, ? extends Event> mapping = this.mappings.get(event.getClass());
        if (mapping == null) {
            return;
        }

        Function<Object, T> function = mapping.getFunction();

        // this handler is disabled, so unregister from the event.
        if (!this.active.get()) {
            event.getHandlers().unregister(listener);
            return;
        }

        // obtain the handled instance
        T handledInstance = function.apply(event);

        // check pre-expiry tests
        for (BiPredicate<MergedSubscription<T>, T> test : this.preExpiryTests) {
            if (test.test(this, handledInstance)) {
                event.getHandlers().unregister(listener);
                this.active.set(false);
                return;
            }
        }

        // begin "handling" of the event
        try {
            // check the filters
            for (Predicate<T> filter : this.filters) {
                if (!filter.test(handledInstance)) {
                    return;
                }
            }

            // check mid-expiry tests
            for (BiPredicate<MergedSubscription<T>, T> test : this.midExpiryTests) {
                if (test.test(this, handledInstance)) {
                    event.getHandlers().unregister(listener);
                    this.active.set(false);
                    return;
                }
            }

            // call the handler
            for (BiConsumer<MergedSubscription<T>, ? super T> handler : this.handlers) {
                handler.accept(this, handledInstance);
            }

            // increment call counter
            this.callCount.incrementAndGet();
        } catch (Throwable t) {
            this.exceptionConsumer.accept(event, t);
        }

        // check post-expiry tests
        for (BiPredicate<MergedSubscription<T>, T> test : this.postExpiryTests) {
            if (test.test(this, handledInstance)) {
                event.getHandlers().unregister(listener);
                this.active.set(false);
                return;
            }
        }
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
        for (Class<? extends Event> clazz : this.mappings.keySet()) {
            unregisterListener(clazz, this);
        }

        return true;
    }

    @NotNull
    @Override
    public Class<? super T> getHandledClass() {
        return this.handledClass.getRawType();
    }

    @NotNull
    @Override
    public Set<Class<? extends Event>> getEventClasses() {
        return this.mappings.keySet();
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

    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException var2) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class) && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ".");
            }
        }
    }
}