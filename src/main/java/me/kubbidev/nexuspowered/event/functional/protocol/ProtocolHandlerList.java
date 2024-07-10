package me.kubbidev.nexuspowered.event.functional.protocol;

import com.comphenix.protocol.events.PacketEvent;
import me.kubbidev.nexuspowered.event.ProtocolSubscription;
import me.kubbidev.nexuspowered.event.functional.FunctionalHandlerList;
import me.kubbidev.nexuspowered.util.Delegates;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ProtocolHandlerList extends FunctionalHandlerList<PacketEvent, ProtocolSubscription> {

    @NotNull
    @Override
    default ProtocolHandlerList consumer(@NotNull Consumer<? super PacketEvent> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @NotNull
    @Override
    ProtocolHandlerList biConsumer(@NotNull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler);
    
}