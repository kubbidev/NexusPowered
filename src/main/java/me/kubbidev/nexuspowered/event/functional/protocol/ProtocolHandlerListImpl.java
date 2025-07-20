package me.kubbidev.nexuspowered.event.functional.protocol;

import com.comphenix.protocol.events.PacketEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import me.kubbidev.nexuspowered.event.ProtocolSubscription;
import org.jetbrains.annotations.NotNull;

class ProtocolHandlerListImpl implements ProtocolHandlerList {

    private final ProtocolSubscriptionBuilderImpl                             builder;
    private final List<BiConsumer<ProtocolSubscription, ? super PacketEvent>> handlers = new ArrayList<>(1);

    ProtocolHandlerListImpl(@NotNull ProtocolSubscriptionBuilderImpl builder) {
        this.builder = builder;
    }

    @Override
    public @NotNull ProtocolHandlerList biConsumer(
        @NotNull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler
    ) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @Override
    public @NotNull ProtocolSubscription register() {
        return new NexusProtocolListener(builder, handlers);
    }
}