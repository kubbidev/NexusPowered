package me.kubbidev.nexuspowered.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import me.kubbidev.nexuspowered.event.functional.protocol.ProtocolSubscriptionBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for working with ProtocolLib.
 */
public final class Protocol {

    /**
     * Makes a HandlerBuilder for the given packets.
     *
     * @param packets the packets to handle
     * @return a {@link ProtocolSubscriptionBuilder} to construct the event handler
     */
    @NotNull
    public static ProtocolSubscriptionBuilder subscribe(@NotNull PacketType... packets) {
        return ProtocolSubscriptionBuilder.newBuilder(packets);
    }

    /**
     * Makes a HandlerBuilder for the given packets.
     *
     * @param priority   the priority to listen at
     * @param packets the packets to handle
     * @return a {@link ProtocolSubscriptionBuilder} to construct the event handler
     */
    @NotNull
    public static ProtocolSubscriptionBuilder subscribe(@NotNull ListenerPriority priority, @NotNull PacketType... packets) {
        return ProtocolSubscriptionBuilder.newBuilder(priority, packets);
    }

    /**
     * Gets the protocol manager.
     *
     * @return the protocol manager.
     */
    @NotNull
    public static ProtocolManager manager() {
        return ProtocolLibrary.getProtocolManager();
    }

    /**
     * Sends a packet to the given player.
     *
     * @param player the player
     * @param packet the packet
     */
    public static void sendPacket(@NotNull Player player, @NotNull PacketContainer packet) {
        manager().sendServerPacket(player, packet);
    }

    /**
     * Sends a packet to all players connected to the server.
     *
     * @param packet the packet
     */
    public static void broadcastPacket(@NotNull PacketContainer packet) {
        manager().broadcastServerPacket(packet);
    }

    /**
     * Sends a packet to each of the given players
     *
     * @param players the players
     * @param packet the packet
     */
    public static void broadcastPacket(@NotNull Iterable<Player> players, @NotNull PacketContainer packet) {
        for (Player player : players) {
            sendPacket(player, packet);
        }
    }

}