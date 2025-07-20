package me.kubbidev.nexuspowered.event;

import com.comphenix.protocol.PacketType;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a subscription to a set of packet events.
 */
public interface ProtocolSubscription extends Subscription {

    /**
     * Gets the packet types handled by this subscription.
     *
     * @return the types
     */
    @NotNull Set<PacketType> getPackets();
}