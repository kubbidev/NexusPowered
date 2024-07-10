package me.kubbidev.nexuspowered.messaging.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import me.kubbidev.nexuspowered.promise.Promise;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * API interface to encapsulate the BungeeCord Plugin Messaging API.
 *
 * <p>The returned futures should never be {@link Promise#join() joined} or waited for on
 * the Server thread.</p>
 */
public interface BungeeCord {

    /**
     * Server name to represent all servers on the proxy.
     */
    String ALL_SERVERS = "ALL";

    /**
     * Server name to represent only the online servers on the proxy.
     */
    String ONLINE_SERVERS = "ONLINE";

    /**
     * Connects a player to said sub server.
     *
     * @param player     the player to connect
     * @param serverName the name of the server to connect to
     */
    void connect(@NotNull Player player, @NotNull String serverName);

    /**
     * Connects a named player to said sub server.
     *
     * @param playerName the username of the player to connect
     * @param serverName the name of the server to connect to
     */
    void connectOther(@NotNull String playerName, @NotNull String serverName);

    /**
     * Get the real IP of a player.
     *
     * @param player the player to get the IP of
     * @return a future
     */
    @NotNull
    Promise<Map.Entry<String, Integer>> ip(@NotNull Player player);

    /**
     * Gets the amount of players on a certain server, or all servers.
     *
     * @param serverName the name of the server to get the player count for. Use {@link #ALL_SERVERS} to get the global count
     * @return a future
     */
    @NotNull
    Promise<Integer> playerCount(@NotNull String serverName);

    /**
     * Gets a list of players connected on a certain server, or all servers.
     *
     * @param serverName the name of the server to get the player list for. Use {@link #ALL_SERVERS} to get the global list
     * @return a future
     */
    @NotNull
    Promise<List<String>> playerList(@NotNull String serverName);

    /**
     * Get a list of server name strings, as defined in the BungeeCord config.
     *
     * @return a future
     */
    @NotNull
    Promise<List<String>> getServers();

    /**
     * Send a message (as in chat message) to the specified player.
     *
     * @param playerName the username of the player to send the message to
     * @param message    the message to send
     */
    void message(@NotNull String playerName, @NotNull String message);

    /**
     * Gets this servers name, as defined in the BungeeCord config.
     *
     * @return a future
     */
    @NotNull
    Promise<String> getServer();

    /**
     * Get the UUID of a player.
     *
     * @param player the player to get the uuid of
     * @return a future
     */
    @NotNull
    Promise<UUID> uuid(@NotNull Player player);

    /**
     * Get the UUID of any player connected to the proxy.
     *
     * @param playerName the username of the player to get the uuid of
     * @return a future
     */
    @NotNull
    Promise<UUID> uuidOther(@NotNull String playerName);

    /**
     * Get the IP of any server connected to the proxy.
     *
     * @param serverName the name of the server to get the ip of
     * @return a future
     */
    @NotNull
    Promise<Map.Entry<String, Integer>> serverIp(@NotNull String serverName);

    /**
     * Kick a player from the proxy.
     *
     * @param playerName the username of the player to kick
     * @param reason     the reason to display to the player when they are kicked
     */
    void kickPlayer(@NotNull String playerName, @NotNull String reason);

    /**
     * Sends a custom plugin message to a given server.
     *
     * <p>You can use {@link #registerForwardCallbackRaw(String, Predicate)} to register listeners on a given sub channel.</p>
     *
     * @param serverName  the name of the server to send to. use {@link #ALL_SERVERS} to send to all servers, or {@link #ONLINE_SERVERS} to only send to servers which are online.
     * @param channelName the name of the sub channel
     * @param data        the data to send
     */
    void forward(@NotNull String serverName, @NotNull String channelName, byte @NotNull [] data);

    /**
     * Sends a custom plugin message to a given server.
     *
     * <p>You can use {@link #registerForwardCallback(String, Predicate)} to register listeners on a given sub channel.</p>
     *
     * @param serverName  the name of the server to send to. use {@link #ALL_SERVERS} to send to all servers, or {@link #ONLINE_SERVERS} to only send to servers which are online.
     * @param channelName the name of the sub channel
     * @param data        the data to send
     */
    void forward(@NotNull String serverName, @NotNull String channelName, @NotNull ByteArrayDataOutput data);

    /**
     * Sends a custom plugin message to a given server.
     *
     * <p>You can use {@link #registerForwardCallbackRaw(String, Predicate)} to register listeners on a given sub channel.</p>
     *
     * @param playerName  the username of a player. BungeeCord will send the forward message to their server.
     * @param channelName the name of the sub channel
     * @param data        the data to send
     */
    void forwardToPlayer(@NotNull String playerName, @NotNull String channelName, byte @NotNull [] data);

    /**
     * Sends a custom plugin message to a given server.
     *
     * <p>You can use {@link #registerForwardCallback(String, Predicate)} to register listeners on a given sub channel.</p>
     *
     * @param playerName  the username of a player. BungeeCord will send the forward message to their server.
     * @param channelName the name of the sub channel
     * @param data        the data to send
     */
    void forwardToPlayer(@NotNull String playerName, @NotNull String channelName, @NotNull ByteArrayDataOutput data);

    /**
     * Registers a callback to listen for messages sent on forwarded sub channels.
     *
     * <p>Use {@link #forward(String, String, byte[])} to dispatch messages.</p>
     *
     * @param channelName the name of the channel to listen on
     * @param callback    the callback. the predicate should return true when the callback should be unregistered.
     */
    void registerForwardCallbackRaw(@NotNull String channelName, @NotNull Predicate<byte[]> callback);

    /**
     * Registers a callback to listen for messages sent on forwarded sub channels.
     *
     * <p>Use {@link #forward(String, String, ByteArrayDataOutput)} to dispatch messages.</p>
     *
     * @param channelName the name of the channel to listen on
     * @param callback    the callback. the predicate should return true when the callback should be unregistered.
     */
    void registerForwardCallback(@NotNull String channelName, @NotNull Predicate<ByteArrayDataInput> callback);

}