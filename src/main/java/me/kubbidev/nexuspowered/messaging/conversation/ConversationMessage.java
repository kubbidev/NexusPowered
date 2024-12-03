package me.kubbidev.nexuspowered.messaging.conversation;

import me.kubbidev.nexuspowered.messaging.codec.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a message sent via a {@link ConversationChannel}.
 *
 * <p>The conversation id should be serialised by the messages {@link Codec}.</p>
 */
public interface ConversationMessage {

    /**
     * Gets the ID of the conversation.
     *
     * @return the conversation id
     */
    @NotNull
    UUID getConversationId();

}