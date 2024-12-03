package me.kubbidev.nexuspowered.messaging.bungee;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.plugin.NexusPlugin;
import me.kubbidev.nexuspowered.promise.Promise;
import me.kubbidev.nexuspowered.terminable.composite.CompositeTerminable;
import me.kubbidev.nexuspowered.util.Players;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

@NotNullByDefault
public final class BungeeCordImpl implements BungeeCord, PluginMessageListener {

    /*
     * See:
     * - https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel
     * - https://github.com/SpigotMC/BungeeCord/blob/master/proxy/src/main/java/net/md_5/bungee/connection/DownstreamBridge.java#L223
     */

    /**
     * The name of the BungeeCord plugin channel.
     */
    private static final String CHANNEL = "BungeeCord";

    /**
     * The plugin instance
     */
    private final NexusPlugin plugin;

    /**
     * If the listener has been registered
     */
    private final AtomicBoolean setup = new AtomicBoolean(false);

    /**
     * The registered listeners
     */
    private final List<MessageCallback> listeners = new LinkedList<>();

    /**
     * Lock to guard the 'listeners' list
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Messages to be sent
     */
    private final Set<MessageAgent> queuedMessages = ConcurrentHashMap.newKeySet();

    public BungeeCordImpl(NexusPlugin plugin) {
        this.plugin = plugin;
    }

    private void ensureSetup() {
        if (!this.setup.compareAndSet(false, true)) {
            return;
        }

        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, CHANNEL);
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, CHANNEL, this);

        this.plugin.bind(CompositeTerminable.create()
                .with(() -> {
                    this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, CHANNEL);
                    this.plugin.getServer().getMessenger().unregisterIncomingPluginChannel(this.plugin, CHANNEL, this);
                })
                .with(Schedulers.builder()
                        .sync()
                        .afterAndEvery(3, TimeUnit.SECONDS)
                        .run(this::flushQueuedMessages)
                )
        );
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals(CHANNEL)) {
            return;
        }
        // create an input stream from the received data
        ByteArrayInputStream byteIn = new ByteArrayInputStream(message);

        // create a data input instance
        ByteArrayDataInput in = ByteStreams.newDataInput(byteIn);

        // read the sub channel and mark the beginning of the stream at this point, so we can reset to this position later
        String subChannel = in.readUTF();
        byteIn.mark(0);

        // pass the incoming message to all registered listeners
        this.lock.lock();
        try {
            Iterator<MessageCallback> it = this.listeners.iterator();
            while (it.hasNext()) {
                MessageCallback e = it.next();

                if (!e.getSubChannel().equals(subChannel)) {
                    continue;
                }

                byteIn.reset();
                boolean accepted = e.testResponse(player, in);
                if (!accepted) {
                    continue;
                }

                byteIn.reset();
                boolean shouldRemove = e.acceptResponse(player, in);
                if (shouldRemove) {
                    it.remove();
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Sends (or queues the sending of) the message encapsulated by the given message agent.
     *
     * @param agent the agent
     */
    private void sendMessage(MessageAgent agent) {
        // check if the agent has a specific player handle to use when sending the message
        Player player = agent.getHandle();

        // try to find a player
        if (player == null) {
            player = Iterables.getFirst(Players.all(), null);
            if (player != null) {
                sendToChannel(agent, player);
            } else {
                // no players online, queue the message
                this.queuedMessages.add(agent);
                ensureSetup();
            }
        } else {
            if (!player.isOnline()) {
                throw new IllegalStateException("Player not online");
            }
            sendToChannel(agent, player);
        }
    }

    private void flushQueuedMessages() {
        if (this.queuedMessages.isEmpty()) {
            return;
        }

        Player p = Iterables.getFirst(Players.all(), null);
        if (p != null) {
            this.queuedMessages.removeIf(agent -> {
                sendToChannel(agent, p);
                return true;
            });
        }
    }

    private void sendToChannel(MessageAgent agent, Player player) {
        ensureSetup();
        // create a new data output stream for the message
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(agent.getSubChannel());

        // append the agents data
        agent.appendPayload(out);

        byte[] buffer = out.toByteArray();
        player.sendPluginMessage(this.plugin, CHANNEL, buffer);

        // if the agent is also a MessageCallback, register it
        if (agent instanceof MessageCallback) {
            registerCallback((MessageCallback) agent);
        }
    }

    private void registerCallback(MessageCallback callback) {
        ensureSetup();
        this.lock.lock();
        try {
            this.listeners.add(callback);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void connect(@NotNull Player player, @NotNull String serverName) {
        sendMessage(new ConnectAgent(player, serverName));
    }

    @Override
    public void connectOther(@NotNull String playerName, @NotNull String serverName) {
        sendMessage(new ConnectOtherAgent(playerName, serverName));
    }

    @Override
    public @NotNull Promise<Map.Entry<String, Integer>> ip(@NotNull Player player) {
        Promise<Map.Entry<String, Integer>> fut = Promise.empty();
        sendMessage(new IPAgent(player, fut));
        return fut;
    }

    @Override
    public @NotNull Promise<Integer> playerCount(@NotNull String serverName) {
        Promise<Integer> fut = Promise.empty();
        sendMessage(new PlayerCountAgent(serverName, fut));
        return fut;
    }

    @Override
    public @NotNull Promise<List<String>> playerList(@NotNull String serverName) {
        Promise<List<String>> fut = Promise.empty();
        sendMessage(new PlayerListAgent(serverName, fut));
        return fut;
    }

    @Override
    public @NotNull Promise<List<String>> getServers() {
        Promise<List<String>> fut = Promise.empty();
        sendMessage(new GetServersAgent(fut));
        return fut;
    }

    @Override
    public void message(@NotNull String playerName, @NotNull String message) {
        sendMessage(new PlayerMessageAgent(playerName, message));
    }

    @Override
    public @NotNull Promise<String> getServer() {
        Promise<String> fut = Promise.empty();
        sendMessage(new GetServerAgent(fut));
        return fut;
    }

    @Override
    public @NotNull Promise<UUID> uuid(@NotNull Player player) {
        Promise<UUID> fut = Promise.empty();
        sendMessage(new UUIDAgent(player, fut));
        return fut;
    }

    @Override
    public @NotNull Promise<UUID> uuidOther(@NotNull String playerName) {
        Promise<UUID> fut = Promise.empty();
        sendMessage(new UUIDOtherAgent(playerName, fut));
        return fut;
    }

    @Override
    public @NotNull Promise<Map.Entry<String, Integer>> serverIp(@NotNull String serverName) {
        Promise<Map.Entry<String, Integer>> fut = Promise.empty();
        sendMessage(new ServerIPAgent(serverName, fut));
        return fut;
    }

    @Override
    public void kickPlayer(@NotNull String playerName, @NotNull String reason) {
        sendMessage(new KickPlayerAgent(playerName, reason));
    }

    @Override
    public void forward(@NotNull String serverName, @NotNull String channelName, byte @NotNull [] data) {
        sendMessage(new ForwardAgent(serverName, channelName, data));
    }

    @Override
    public void forward(@NotNull String serverName, @NotNull String channelName, @NotNull ByteArrayDataOutput data) {
        sendMessage(new ForwardAgent(serverName, channelName, data));
    }

    @Override
    public void forwardToPlayer(@NotNull String playerName, @NotNull String channelName, byte @NotNull [] data) {
        sendMessage(new ForwardToPlayerAgent(playerName, channelName, data));
    }

    @Override
    public void forwardToPlayer(@NotNull String playerName, @NotNull String channelName, @NotNull ByteArrayDataOutput data) {
        sendMessage(new ForwardToPlayerAgent(playerName, channelName, data));
    }

    @Override
    public void registerForwardCallbackRaw(@NotNull String channelName, @NotNull Predicate<byte[]> callback) {
        ForwardCustomCallback customCallback = new ForwardCustomCallback(channelName, callback);
        registerCallback(customCallback);
    }

    @Override
    public void registerForwardCallback(@NotNull String channelName, @NotNull Predicate<ByteArrayDataInput> callback) {
        Predicate<ByteArrayDataInput> cb = Objects.requireNonNull(callback, "callback");
        ForwardCustomCallback customCallback = new ForwardCustomCallback(channelName, bytes -> cb.test(ByteStreams.newDataInput(bytes)));
        registerCallback(customCallback);
    }

    /**
     * Responsible for monitoring incoming messages, and passing on the callback data if applicable.
     */
    private interface MessageCallback {

        /**
         * Gets the sub channel this callback is listening for.
         *
         * @return the message channel
         */
        String getSubChannel();

        /**
         * Returns true if the incoming data applies to this callback.
         *
         * @param receiver the player instance which received the data
         * @param in       the input data
         * @return true if the data is applicable
         */
        default boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return true;
        }

        /**
         * Accepts the incoming data, and returns true if this callback should now be de-registered.
         *
         * @param receiver the player instance which received the data
         * @param in       the input data
         * @return if the callback should be de-registered
         */
        boolean acceptResponse(Player receiver, ByteArrayDataInput in);

    }

    /**
     * Responsible for writing data to the output stream when the message is to be sent.
     */
    private interface MessageAgent {

        /**
         * Gets the sub channel this message should be sent using.
         *
         * @return the message channel
         */
        String getSubChannel();

        /**
         * Gets the player to send the message via.
         *
         * @return the player to send the message via, or null if any player should be used
         */
        @Nullable
        default Player getHandle() {
            return null;
        }

        /**
         * Appends the data for this message to the output stream.
         *
         * @param out the output stream
         */
        default void appendPayload(ByteArrayDataOutput out) {

        }
    }

    private static class ConnectAgent implements MessageAgent {
        private static final String CHANNEL = "Connect";

        private final Player player;
        private final String serverName;

        public ConnectAgent(Player player, String serverName) {
            this.player = player;
            this.serverName = serverName;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public @NotNull Player getHandle() {
            return this.player;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
        }
    }

    private static class ConnectOtherAgent implements MessageAgent {
        private static final String CHANNEL = "ConnectOther";

        private final String playerName;
        private final String serverName;

        public ConnectOtherAgent(String playerName, String serverName) {
            this.playerName = playerName;
            this.serverName = serverName;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.serverName);
        }
    }

    private static class IPAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "IP";

        private final Player player;
        private final Promise<Map.Entry<String, Integer>> callback;

        public IPAgent(Player player, Promise<Map.Entry<String, Integer>> callback) {
            this.player = player;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public @Nullable Player getHandle() {
            return this.player;
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return receiver.getUniqueId().equals(this.player.getUniqueId());
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            this.callback.supply(Maps.immutableEntry(in.readUTF(), in.readInt()));
            return true;
        }
    }

    private static class PlayerCountAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "PlayerCount";

        private final String serverName;
        private final Promise<Integer> callback;

        public PlayerCountAgent(String serverName, Promise<Integer> callback) {
            this.serverName = serverName;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return in.readUTF().equalsIgnoreCase(this.serverName);
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            in.readUTF();
            this.callback.supply(in.readInt());
            return true;
        }
    }

    private static class PlayerListAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "PlayerList";

        private final String serverName;
        private final Promise<List<String>> callback;

        public PlayerListAgent(String serverName, Promise<List<String>> callback) {
            this.serverName = serverName;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return in.readUTF().equalsIgnoreCase(this.serverName);
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            in.readUTF();
            String csv = in.readUTF();

            if (csv.isEmpty()) {
                this.callback.supply(ImmutableList.of());
                return true;
            }
            this.callback.supply(ImmutableList.copyOf(Splitter.on(", ").splitToList(csv)));
            return true;
        }
    }

    private static class GetServersAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "GetServers";

        private final Promise<List<String>> callback;

        public GetServersAgent(Promise<List<String>> callback) {
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            String csv = in.readUTF();

            if (csv.isEmpty()) {
                this.callback.supply(ImmutableList.of());
                return true;
            }
            this.callback.supply(ImmutableList.copyOf(Splitter.on(", ").splitToList(csv)));
            return true;
        }
    }

    private static class PlayerMessageAgent implements MessageAgent {
        private static final String CHANNEL = "Message";

        private final String playerName;
        private final String message;

        public PlayerMessageAgent(String playerName, String message) {
            this.playerName = playerName;
            this.message = message;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.message);
        }
    }

    private static class GetServerAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "GetServer";

        private final Promise<String> callback;

        public GetServerAgent(Promise<String> callback) {
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            this.callback.supply(in.readUTF());
            return true;
        }
    }

    private static class UUIDAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "UUID";

        private final Player player;
        private final Promise<UUID> callback;

        public UUIDAgent(Player player, Promise<UUID> callback) {
            this.player = player;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public @Nullable Player getHandle() {
            return this.player;
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return receiver.getUniqueId().equals(this.player.getUniqueId());
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            this.callback.supply(UUID.fromString(in.readUTF()));
            return true;
        }
    }

    private static class UUIDOtherAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "UUIDOther";

        private final String playerName;
        private final Promise<UUID> callback;

        public UUIDOtherAgent(String playerName, Promise<UUID> callback) {
            this.playerName = playerName;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return in.readUTF().equalsIgnoreCase(this.playerName);
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            in.readUTF();
            this.callback.supply(UUID.fromString(in.readUTF()));
            return true;
        }
    }

    private static class ServerIPAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "ServerIP";

        private final String serverName;
        private final Promise<Map.Entry<String, Integer>> callback;

        public ServerIPAgent(String serverName, Promise<Map.Entry<String, Integer>> callback) {
            this.serverName = serverName;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return in.readUTF().equalsIgnoreCase(this.serverName);
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            in.readUTF();
            this.callback.supply(Maps.immutableEntry(in.readUTF(), in.readInt()));
            return true;
        }

        @Override
        public String toString() {
            return "ServerIPAgent[" +
                    "serverName=" + serverName + ", " +
                    "callback=" + callback + ']';
        }

    }

    private static class KickPlayerAgent implements MessageAgent {
        private static final String CHANNEL = "KickPlayer";

        private final String playerName;
        private final String reason;

        public KickPlayerAgent(String playerName, String reason) {
            this.playerName = playerName;
            this.reason = reason;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.reason);
        }
    }

    private static final class ForwardAgent implements MessageAgent {
        private static final String CHANNEL = "Forward";

        private final String serverName;
        private final String channelName;
        private final byte[] data;

        public ForwardAgent(String serverName, String channelName, byte[] data) {
            this.serverName = serverName;
            this.channelName = channelName;
            this.data = data;
        }

        private ForwardAgent(String serverName, String channelName, ByteArrayDataOutput data) {
            this(serverName, channelName, data.toByteArray());
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
            out.writeUTF(this.channelName);
            out.writeShort(this.data.length);
            out.write(this.data);
        }
    }

    private static final class ForwardToPlayerAgent implements MessageAgent {
        private static final String CHANNEL = "ForwardToPlayer";

        private final String playerName;
        private final String channelName;
        private final byte[] data;

        public ForwardToPlayerAgent(String playerName, String channelName, byte[] data) {
            this.playerName = playerName;
            this.channelName = channelName;
            this.data = data;
        }

        public ForwardToPlayerAgent(String playerName, String channelName, ByteArrayDataOutput data) {
            this(playerName, channelName, data.toByteArray());
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.channelName);
            out.writeShort(this.data.length);
            out.write(this.data);
        }
    }

    private static class ForwardCustomCallback implements MessageCallback {
        private final String subChannel;
        private final Predicate<byte[]> callback;

        public ForwardCustomCallback(String subChannel, Predicate<byte[]> callback) {
            this.subChannel = subChannel;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return this.subChannel;
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            short len = in.readShort();
            byte[] data = new byte[len];
            in.readFully(data);
            return this.callback.test(data);
        }
    }
}