package me.kubbidev.nexuspowered.messaging.reqresp;

import me.kubbidev.nexuspowered.messaging.conversation.ConversationMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A {@link ConversationMessage} used by the {@link ReqRespChannel}.
 *
 * @param <T> the body type
 */
class ReqResMessage<T> implements ConversationMessage {
    private final UUID id;
    private final T body;

    ReqResMessage(UUID id, T body) {
        this.id = id;
        this.body = body;
    }

    @Override
    public @NotNull UUID getConversationId() {
        return this.id;
    }

    public T getBody() {
        return this.body;
    }
}