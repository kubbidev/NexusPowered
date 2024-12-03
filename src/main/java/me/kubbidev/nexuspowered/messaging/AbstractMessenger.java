package me.kubbidev.nexuspowered.messaging;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.cache.LoadingMap;
import me.kubbidev.nexuspowered.messaging.codec.Codec;
import me.kubbidev.nexuspowered.messaging.codec.GZipCodec;
import me.kubbidev.nexuspowered.messaging.codec.GsonCodec;
import me.kubbidev.nexuspowered.messaging.codec.Message;
import me.kubbidev.nexuspowered.promise.Promise;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An abstract implementation of {@link Messenger}.
 *
 * <p>Outgoing messages are passed to a {@link BiConsumer} to be passed on.</p>
 * <p>Incoming messages can be distributed using {@link #registerIncomingMessage(String, byte[])}.</p>
 */
@NotNullByDefault
public class AbstractMessenger implements Messenger {
    private final LoadingMap<Map.Entry<String, TypeToken<?>>, AbstractChannel<?>> channels = LoadingMap.of(spec ->
            new AbstractChannel<>(AbstractMessenger.this,
                    spec.getKey(),
                    spec.getValue()
            )
    );

    // consumer for outgoing messages. accepts in the format [channel name, message]
    private final BiConsumer<String, byte[]> outgoingMessages;

    // consumer for channel names which should be subscribed to.
    private final Consumer<String> notifySub;

    // consumer for channel names which should be unsubscribed from.
    private final Consumer<String> notifyUnsub;

    /**
     * Creates a new abstract messenger.
     *
     * @param outgoingMessages the consumer to pass outgoing messages to
     * @param notifySub        the consumer to pass the names of channels which should be subscribed to
     * @param notifyUnsub      the consumer to pass the names of channels which should be unsubscribed from
     */
    public AbstractMessenger(BiConsumer<String, byte[]> outgoingMessages, Consumer<String> notifySub, Consumer<String> notifyUnsub) {
        this.outgoingMessages = Objects.requireNonNull(outgoingMessages, "outgoingMessages");
        this.notifySub = Objects.requireNonNull(notifySub, "notifySub");
        this.notifyUnsub = Objects.requireNonNull(notifyUnsub, "notifyUnsub");
    }

    /**
     * Distributes an oncoming message to the channels held in this messenger.
     *
     * @param channel the channel the message was received on
     * @param message the message
     */
    public void registerIncomingMessage(String channel, byte[] message) {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(message, "message");

        for (Map.Entry<Map.Entry<String, TypeToken<?>>, AbstractChannel<?>> c : this.channels.entrySet()) {
            if (c.getKey().getKey().equals(channel)) {
                c.getValue().onIncomingMessage(message);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T> Channel<T> getChannel(@NotNull String name, @NotNull TypeToken<T> type) {
        Objects.requireNonNull(name, "name");
        Preconditions.checkArgument(!name.trim().isEmpty(), "name cannot be empty");
        Objects.requireNonNull(type, "type");

        return (Channel<T>) this.channels.get(Maps.immutableEntry(name, type));
    }

    @SuppressWarnings("unchecked")
    private static <T> Codec<T> getCodec(TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        do {
            Message message = rawType.getAnnotation(Message.class);
            if (message != null) {
                Class<? extends Codec<?>> codec = message.codec();
                try {
                    return (Codec<T>) codec.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        } while ((rawType = rawType.getSuperclass()) != null);

        return new GsonCodec<>(type);
    }

    private static class AbstractChannel<T> implements Channel<T> {
        private final AbstractMessenger messenger;
        private final String name;
        private final TypeToken<T> type;
        private final Codec<T> codec;

        private final Set<AbstractChannelAgent<T>> agents = ConcurrentHashMap.newKeySet();
        private boolean subscribed = false;

        private AbstractChannel(AbstractMessenger messenger, String name, TypeToken<T> type) {
            this.messenger = messenger;
            this.name = name;
            this.type = type;
            this.codec = new GZipCodec<>(AbstractMessenger.getCodec(type));
        }

        private void onIncomingMessage(byte[] message) {
            try {
                T decoded = this.codec.decode(message);
                Objects.requireNonNull(decoded, "decoded");

                for (AbstractChannelAgent<T> agent : this.agents) {
                    try {
                        agent.onIncomingMessage(decoded);
                    } catch (Exception e) {
                        new RuntimeException("Unable to pass decoded message to agent: " + decoded, e).printStackTrace();
                    }
                }

            } catch (Exception e) {
                new RuntimeException("Unable to decode message: " + Base64.getEncoder().encodeToString(message), e).printStackTrace();
            }
        }

        private void checkSubscription() {
            boolean shouldSubscribe = this.agents.stream().anyMatch(AbstractChannelAgent::hasListeners);
            if (shouldSubscribe == this.subscribed) {
                return;
            }
            this.subscribed = shouldSubscribe;

            Schedulers.async().run(() -> {
                try {
                    if (shouldSubscribe) {
                        this.messenger.notifySub.accept(this.name);
                    } else {
                        this.messenger.notifyUnsub.accept(this.name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public @NotNull String getName() {
            return this.name;
        }

        @Override
        public @NotNull TypeToken<T> getType() {
            return this.type;
        }

        @Override
        public @NotNull Codec<T> getCodec() {
            return this.codec;
        }

        @Override
        public @NotNull ChannelAgent<T> newAgent() {
            AbstractChannelAgent<T> agent = new AbstractChannelAgent<>(this);
            this.agents.add(agent);
            return agent;
        }

        @Override
        public @NotNull Promise<Void> sendMessage(@NotNull T message) {
            Objects.requireNonNull(message, "message");
            return Schedulers.async().call(() -> {
                byte[] buf = this.codec.encode(message);
                this.messenger.outgoingMessages.accept(this.name, buf);
                return null;
            });
        }
    }

    private static class AbstractChannelAgent<T> implements ChannelAgent<T> {
        private final Set<ChannelListener<T>> listeners = ConcurrentHashMap.newKeySet();

        @Nullable
        private AbstractChannel<T> channel;

        AbstractChannelAgent(@Nullable AbstractChannel<T> channel) {
            this.channel = channel;
        }

        private void onIncomingMessage(T message) {
            for (ChannelListener<T> listener : this.listeners) {
                Schedulers.async().run(() -> {
                    try {
                        listener.onMessage(this, message);
                    } catch (Exception e) {
                        new RuntimeException("Unable to pass decoded message to listener: " + listener, e).printStackTrace();
                    }
                });
            }
        }

        @Override
        public @NotNull Channel<T> getChannel() {
            Preconditions.checkState(this.channel != null, "agent not active");
            return this.channel;
        }

        @Override
        public @NotNull Set<ChannelListener<T>> getListeners() {
            Preconditions.checkState(this.channel != null, "agent not active");
            return ImmutableSet.copyOf(this.listeners);
        }

        @Override
        public boolean hasListeners() {
            return !this.listeners.isEmpty();
        }

        @Override
        public boolean addListener(@NotNull ChannelListener<T> listener) {
            Preconditions.checkState(this.channel != null, "agent not active");
            try {
                return this.listeners.add(listener);
            } finally {
                this.channel.checkSubscription();
            }
        }

        @Override
        public boolean removeListener(@NotNull ChannelListener<T> listener) {
            Preconditions.checkState(this.channel != null, "agent not active");
            try {
                return this.listeners.remove(listener);
            } finally {
                this.channel.checkSubscription();
            }
        }

        @Override
        public void close() {
            if (this.channel == null) {
                return;
            }

            this.listeners.clear();
            this.channel.agents.remove(this);
            this.channel.checkSubscription();
            this.channel = null;
        }
    }
}