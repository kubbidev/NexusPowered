package me.kubbidev.nexuspowered.messaging;

import com.google.common.reflect.TypeToken;
import me.kubbidev.nexuspowered.interfaces.TypeAware;
import me.kubbidev.nexuspowered.messaging.codec.Codec;
import me.kubbidev.nexuspowered.promise.Promise;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an individual messaging channel.
 *
 * <p>Channels can be subscribed to through a {@link ChannelAgent}.</p>
 *
 * @param <T> the channel message type
 */
public interface Channel<T> extends TypeAware<T> {

    /**
     * Gets the name of the channel.
     *
     * @return the channel name
     */
    @NotNull
    String getName();

    /**
     * Gets the channels message type.
     *
     * @return the channels message type.
     */
    @Override
    @NotNull
    TypeToken<T> getType();

    /**
     * Gets the channels codec.
     *
     * @return the codec
     */
    @NotNull
    Codec<T> getCodec();

    /**
     * Creates a new {@link ChannelAgent} for this channel.
     *
     * @return a new channel agent.
     */
    @NotNull
    ChannelAgent<T> newAgent();

    /**
     * Creates a new {@link ChannelAgent} for this channel, and immediately
     * adds the given {@link ChannelListener} to it.
     *
     * @param listener the listener to register
     * @return the resultant agent
     */
    @NotNull
    default ChannelAgent<T> newAgent(ChannelListener<T> listener) {
        ChannelAgent<T> agent = newAgent();
        agent.addListener(listener);
        return agent;
    }

    /**
     * Sends a new message to the channel.
     *
     * <p>This method will return immediately, and the future will be completed
     * once the message has been sent.</p>
     *
     * @param message the message to dispatch
     * @return a promise which will complete when the message has sent.
     */
    @NotNull
    Promise<Void> sendMessage(@NotNull T message);

}