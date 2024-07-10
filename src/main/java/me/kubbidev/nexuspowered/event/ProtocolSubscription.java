package me.kubbidev.nexuspowered.event;

import com.comphenix.protocol.PacketType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Represents a subscription to a set of packet events.
 */
public interface ProtocolSubscription extends Subscription {

    /**
     * Gets the packet types handled by this subscription.
     *
     * @return the types
     */
    @NotNull
    Set<PacketType> getPackets();

}