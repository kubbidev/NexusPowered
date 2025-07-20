package me.kubbidev.nexuspowered.event.functional.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import me.kubbidev.nexuspowered.event.ProtocolSubscription;
import me.kubbidev.nexuspowered.event.functional.ExpiryTestStage;
import org.jetbrains.annotations.NotNull;

class ProtocolSubscriptionBuilderImpl implements ProtocolSubscriptionBuilder {

    final Set<PacketType>                                      types;
    final ListenerPriority                                     priority;
    final List<Predicate<PacketEvent>>                         filters         = new ArrayList<>(3);
    final List<BiPredicate<ProtocolSubscription, PacketEvent>> preExpiryTests  = new ArrayList<>(0);
    final List<BiPredicate<ProtocolSubscription, PacketEvent>> midExpiryTests  = new ArrayList<>(0);
    final List<BiPredicate<ProtocolSubscription, PacketEvent>> postExpiryTests = new ArrayList<>(0);
    BiConsumer<? super PacketEvent, Throwable> exceptionConsumer = DEFAULT_EXCEPTION_CONSUMER;

    ProtocolSubscriptionBuilderImpl(Set<PacketType> types, ListenerPriority priority) {
        this.types = ImmutableSet.copyOf(types);
        this.priority = priority;
    }

    @Override
    public @NotNull ProtocolSubscriptionBuilder expireIf(
        @NotNull BiPredicate<ProtocolSubscription, PacketEvent> predicate,
        @NotNull ExpiryTestStage... testPoints
    ) {
        Objects.requireNonNull(testPoints, "testPoints");
        Objects.requireNonNull(predicate, "predicate");
        for (ExpiryTestStage testPoint : testPoints) {
            switch (testPoint) {
                case PRE -> this.preExpiryTests.add(predicate);
                case POST_FILTER -> this.midExpiryTests.add(predicate);
                case POST_HANDLE -> this.postExpiryTests.add(predicate);
                default -> throw new IllegalArgumentException("Unknown ExpiryTestPoint: " + testPoint);
            }
        }
        return this;
    }

    @Override
    public @NotNull ProtocolSubscriptionBuilder filter(@NotNull Predicate<PacketEvent> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.filters.add(predicate);
        return this;
    }

    @Override
    public @NotNull ProtocolSubscriptionBuilder exceptionConsumer(
        @NotNull BiConsumer<? super PacketEvent, Throwable> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer, "exceptionConsumer");
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    @Override
    public @NotNull ProtocolHandlerList handlers() {
        return new ProtocolHandlerListImpl(this);
    }
}