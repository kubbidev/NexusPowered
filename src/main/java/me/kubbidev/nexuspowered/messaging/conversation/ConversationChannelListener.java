package me.kubbidev.nexuspowered.messaging.conversation;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an object listening to messages sent on the conversation channel.
 *
 * @param <T> the channel message type
 */
@FunctionalInterface
public interface ConversationChannelListener<T extends ConversationMessage, R extends ConversationMessage> {

    /**
     * Called when a message is posted to this listener.
     *
     * <p>This method is called asynchronously.</p>
     *
     * @param agent   the agent which forwarded the message.
     * @param message the message
     */
    ConversationReply<R> onMessage(@NotNull ConversationChannelAgent<T, R> agent, @NotNull T message);

}