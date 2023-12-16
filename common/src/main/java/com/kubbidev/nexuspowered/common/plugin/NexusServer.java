package com.kubbidev.nexuspowered.common.plugin;

import com.kubbidev.nexuspowered.common.sender.Sender;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface NexusServer {

    /**
     * Retrieves an optional player object based on the given username.
     *
     * @param username The username of the player.
     * @return An Optional containing the player object if found; otherwise, an empty Optional.
     */
    Optional<?> getPlayer(String username);

    /**
     * Retrieves an optional player object based on the given UUID.
     *
     * @param uniqueId The UUID of the player.
     * @return An Optional containing the player object if found; otherwise, an empty Optional.
     */
    Optional<?> getPlayer(UUID uniqueId);

    /**
     * Looks up the UUID of a player based on their username.
     *
     * @param username The username of the player.
     * @return An Optional containing the UUID of the player if found; otherwise, an empty Optional.
     */
    Optional<UUID> lookupUniqueId(String username);

    /**
     * Looks up the username of a player based on their UUID.
     *
     * @param uniqueId The UUID of the player.
     * @return An Optional containing the username of the player if found; otherwise, an empty Optional.
     */
    Optional<String> lookupUsername(UUID uniqueId);

    /**
     * Retrieves the count of online players.
     *
     * @return The count of online players.
     */
    int getPlayerCount();

    /**
     * Retrieves a collection of online player names.
     *
     * @return A collection of online player names.
     */
    Collection<String> getPlayerList();

    /**
     * Retrieves a collection of UUIDs of online players.
     *
     * @return A collection of UUIDs of online players.
     */
    Collection<UUID> getOnlinePlayers();

    /**
     * Checks if a player with the given UUID is online.
     *
     * @param uniqueId The UUID of the player to check.
     * @return true if the player is online; otherwise, false.
     */
    boolean isPlayerOnline(UUID uniqueId);

    /**
     * Checks if a player with the given UUID is connected.
     *
     * @param uniqueId The UUID of the player to check.
     * @return true if the player is connected; otherwise, false.
     */
    boolean isPlayerConnected(UUID uniqueId);

    /**
     * Retrieves a stream of online Sender objects.
     * Senders represent entities that can send messages or execute commands.
     *
     * @return A Stream of online Sender objects.
     */
    Stream<Sender> getOnlineSenders();

    /**
     * Retrieves the console Sender object.
     * The console Sender represents the server console.
     *
     * @return The Sender object representing the server console.
     */
    Sender getConsoleSender();
}