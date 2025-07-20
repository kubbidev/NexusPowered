package me.kubbidev.nexuspowered.event.functional.protocol;

import com.comphenix.protocol.events.PacketEvent;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import me.kubbidev.nexuspowered.event.ProtocolSubscription;
import me.kubbidev.nexuspowered.event.functional.FunctionalHandlerList;
import me.kubbidev.nexuspowered.util.Delegates;
import org.jetbrains.annotations.NotNull;

public interface ProtocolHandlerList extends FunctionalHandlerList<PacketEvent, ProtocolSubscription> {

    @Override
    default @NotNull ProtocolHandlerList consumer(@NotNull Consumer<? super PacketEvent> handler) {
        Objects.requireNonNull(handler, "handler");
        return this.biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @Override
    @NotNull ProtocolHandlerList biConsumer(@NotNull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler);
}