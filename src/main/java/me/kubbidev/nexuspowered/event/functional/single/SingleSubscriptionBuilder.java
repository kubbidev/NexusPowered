package me.kubbidev.nexuspowered.event.functional.single;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.kubbidev.nexuspowered.event.SingleSubscription;
import me.kubbidev.nexuspowered.event.functional.ExpiryTestStage;
import me.kubbidev.nexuspowered.event.functional.SubscriptionBuilder;
import me.kubbidev.nexuspowered.util.Delegates;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/**
 * Functional builder for {@link SingleSubscription}s.
 *
 * @param <T> the event type
 */
public interface SingleSubscriptionBuilder<T extends Event> extends SubscriptionBuilder<T> {

    /**
     * Makes a HandlerBuilder for a given event
     *
     * @param eventClass the class of the event
     * @param <T>        the event type
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventClass is null
     */
    static @NotNull <T extends Event> SingleSubscriptionBuilder<T> newBuilder(@NotNull Class<T> eventClass) {
        return newBuilder(eventClass, EventPriority.NORMAL);
    }

    /**
     * Makes a HandlerBuilder for a given event
     *
     * @param eventClass the class of the event
     * @param priority   the priority to listen at
     * @param <T>        the event type
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventClass or priority is null
     */
    static @NotNull <T extends Event> SingleSubscriptionBuilder<T> newBuilder(@NotNull Class<T> eventClass,
                                                                              @NotNull EventPriority priority) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(priority, "priority");
        return new SingleSubscriptionBuilderImpl<>(eventClass, priority);
    }

    // override return type - we return SingleSubscriptionBuilder, not SubscriptionBuilder

    @Override
    default @NotNull SingleSubscriptionBuilder<T> expireIf(@NotNull Predicate<T> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE,
            ExpiryTestStage.POST_HANDLE);
    }

    @Override
    default @NotNull SingleSubscriptionBuilder<T> expireAfter(long duration, @NotNull TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @Override
    default @NotNull SingleSubscriptionBuilder<T> expireAfter(long maxCalls) {
        Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
        return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE,
            ExpiryTestStage.POST_HANDLE);
    }

    @Override
    @NotNull SingleSubscriptionBuilder<T> filter(@NotNull Predicate<T> predicate);

    /**
     * Add a expiry predicate.
     *
     * @param predicate  the expiry test
     * @param testPoints when to test the expiry predicate
     * @return ths builder instance
     */
    @NotNull SingleSubscriptionBuilder<T> expireIf(@NotNull BiPredicate<SingleSubscription<T>, T> predicate,
                                                   @NotNull ExpiryTestStage... testPoints);

    /**
     * Sets the exception consumer for the handler.
     *
     * <p> If an exception is thrown in the handler, it is passed to this consumer to be swallowed.
     *
     * @param consumer the consumer
     * @return the builder instance
     * @throws NullPointerException if the consumer is null
     */
    @NotNull SingleSubscriptionBuilder<T> exceptionConsumer(@NotNull BiConsumer<? super T, Throwable> consumer);

    /**
     * Sets that the handler should accept subclasses of the event type.
     *
     * @return the builder instance
     */
    @NotNull SingleSubscriptionBuilder<T> handleSubclasses();

    /**
     * Return the handler list builder to append handlers for the event.
     *
     * @return the handler list
     */
    @NotNull SingleHandlerList<T> handlers();

    /**
     * Builds and registers the Handler.
     *
     * @param handler the consumer responsible for handling the event.
     * @return a registered {@link SingleSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    default @NotNull SingleSubscription<T> handler(@NotNull Consumer<? super T> handler) {
        return this.handlers().consumer(handler).register();
    }

    /**
     * Builds and registers the Handler.
     *
     * @param handler the bi-consumer responsible for handling the event.
     * @return a registered {@link SingleSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    default @NotNull SingleSubscription<T> biHandler(@NotNull BiConsumer<SingleSubscription<T>, ? super T> handler) {
        return this.handlers().biConsumer(handler).register();
    }

}