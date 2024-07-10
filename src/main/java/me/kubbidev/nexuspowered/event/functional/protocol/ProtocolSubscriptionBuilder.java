package me.kubbidev.nexuspowered.event.functional.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import me.kubbidev.nexuspowered.event.ProtocolSubscription;
import me.kubbidev.nexuspowered.event.functional.ExpiryTestStage;
import me.kubbidev.nexuspowered.event.functional.SubscriptionBuilder;
import me.kubbidev.nexuspowered.util.Delegates;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Functional builder for {@link ProtocolSubscription}s.
 */
public interface ProtocolSubscriptionBuilder extends SubscriptionBuilder<PacketEvent> {

    /**
     * Makes a HandlerBuilder for the given packets
     *
     * @param packets the packets to handle
     * @return a {@link ProtocolSubscriptionBuilder} to construct the event handler
     */
    @NotNull
    static ProtocolSubscriptionBuilder newBuilder(@NotNull PacketType... packets) {
        return newBuilder(ListenerPriority.NORMAL, packets);
    }

    /**
     * Makes a HandlerBuilder for the given packets
     *
     * @param priority   the priority to listen at
     * @param packets the packets to handle
     * @return a {@link ProtocolSubscriptionBuilder} to construct the event handler
     */
    @NotNull
    static ProtocolSubscriptionBuilder newBuilder(@NotNull ListenerPriority priority, @NotNull PacketType... packets) {
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(packets, "packets");
        return new ProtocolSubscriptionBuilderImpl(ImmutableSet.copyOf(packets), priority);
    }

    // override return type - we return SingleSubscriptionBuilder, not SubscriptionBuilder

    @NotNull
    @Override
    default ProtocolSubscriptionBuilder expireIf(@NotNull Predicate<PacketEvent> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @NotNull
    @Override
    default ProtocolSubscriptionBuilder expireAfter(long duration, @NotNull TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @NotNull
    @Override
    default ProtocolSubscriptionBuilder expireAfter(long maxCalls) {
        Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
        return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @NotNull
    @Override
    ProtocolSubscriptionBuilder filter(@NotNull Predicate<PacketEvent> predicate);

    /**
     * Add a expiry predicate.
     *
     * @param predicate the expiry test
     * @param testPoints when to test the expiry predicate
     * @return ths builder instance
     */
    @NotNull
    ProtocolSubscriptionBuilder expireIf(@NotNull BiPredicate<ProtocolSubscription, PacketEvent> predicate, @NotNull ExpiryTestStage... testPoints);

    /**
     * Sets the exception consumer for the handler.
     *
     * <p> If an exception is thrown in the handler, it is passed to this consumer to be swallowed.
     *
     * @param consumer the consumer
     * @return the builder instance
     * @throws NullPointerException if the consumer is null
     */
    @NotNull
    ProtocolSubscriptionBuilder exceptionConsumer(@NotNull BiConsumer<? super PacketEvent, Throwable> consumer);

    /**
     * Return the handler list builder to append handlers for the event.
     *
     * @return the handler list
     */
    @NotNull
    ProtocolHandlerList handlers();

    /**
     * Builds and registers the Handler.
     *
     * @param handler the consumer responsible for handling the event.
     * @return a registered {@link ProtocolSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    @NotNull
    default ProtocolSubscription handler(@NotNull Consumer<? super PacketEvent> handler) {
        return handlers().consumer(handler).register();
    }

    /**
     * Builds and registers the Handler.
     *
     * @param handler the bi-consumer responsible for handling the event.
     * @return a registered {@link ProtocolSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    @NotNull
    default ProtocolSubscription biHandler(@NotNull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler) {
        return handlers().biConsumer(handler).register();
    }
    
}