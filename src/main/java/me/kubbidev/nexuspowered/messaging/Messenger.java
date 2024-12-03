package me.kubbidev.nexuspowered.messaging;

import com.google.common.reflect.TypeToken;
import me.kubbidev.nexuspowered.messaging.conversation.ConversationChannel;
import me.kubbidev.nexuspowered.messaging.conversation.ConversationMessage;
import me.kubbidev.nexuspowered.messaging.conversation.SimpleConversationChannel;
import me.kubbidev.nexuspowered.messaging.reqresp.ReqRespChannel;
import me.kubbidev.nexuspowered.messaging.reqresp.SimpleReqRespChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an object which manages messaging {@link Channel}s.
 */
public interface Messenger {

    /**
     * Gets a channel by name.
     *
     * @param name the name of the channel.
     * @param type the channel message type token
     * @param <T>  the channel message type
     * @return a channel
     */
    @NotNull
    <T> Channel<T> getChannel(@NotNull String name, @NotNull TypeToken<T> type);

    /**
     * Gets a conversation channel by name.
     *
     * @param name      the name of the channel
     * @param type      the channel outgoing message type token
     * @param replyType the channel incoming (reply) message type token
     * @param <T>       the channel message type
     * @param <R>       the channel reply type
     * @return a conversation channel
     */
    @NotNull
    default <T extends ConversationMessage, R extends ConversationMessage> ConversationChannel<T, R> getConversationChannel(@NotNull String name, @NotNull TypeToken<T> type, @NotNull TypeToken<R> replyType) {
        return new SimpleConversationChannel<>(this, name, type, replyType);
    }

    /**
     * Gets a req/resp channel by name.
     *
     * @param name     the name of the channel
     * @param reqType  the request typetoken
     * @param respType the response typetoken
     * @param <Req>    the request type
     * @param <Resp>   the response type
     * @return the req/resp channel
     */
    @NotNull
    default <Req, Resp> ReqRespChannel<Req, Resp> getReqRespChannel(@NotNull String name, @NotNull TypeToken<Req> reqType, @NotNull TypeToken<Resp> respType) {
        return new SimpleReqRespChannel<>(this, name, reqType, respType);
    }

    /**
     * Gets a channel by name.
     *
     * @param name  the name of the channel.
     * @param clazz the channel message class
     * @param <T>   the channel message type
     * @return a channel
     */
    @NotNull
    default <T> Channel<T> getChannel(@NotNull String name, @NotNull Class<T> clazz) {
        return getChannel(name, TypeToken.of(Objects.requireNonNull(clazz)));
    }

    /**
     * Gets a conversation channel by name.
     *
     * @param name       the name of the channel
     * @param clazz      the channel outgoing message class
     * @param replyClazz the channel incoming (reply) message class
     * @param <T>        the channel message type
     * @param <R>        the channel reply type
     * @return a conversation channel
     */
    @NotNull
    default <T extends ConversationMessage, R extends ConversationMessage> ConversationChannel<T, R> getConversationChannel(@NotNull String name, @NotNull Class<T> clazz, @NotNull Class<R> replyClazz) {
        return getConversationChannel(name,
                TypeToken.of(Objects.requireNonNull(clazz)),
                TypeToken.of(Objects.requireNonNull(replyClazz))
        );
    }

    /**
     * Gets a req/resp channel by name.
     *
     * @param name      the name of the channel
     * @param reqClass  the request class
     * @param respClass the response class
     * @param <Req>     the request type
     * @param <Resp>    the response type
     * @return the req/resp channel
     */
    @NotNull
    default <Req, Resp> ReqRespChannel<Req, Resp> getReqRespChannel(@NotNull String name, @NotNull Class<Req> reqClass, @NotNull Class<Resp> respClass) {
        return getReqRespChannel(name,
                TypeToken.of(Objects.requireNonNull(reqClass)),
                TypeToken.of(Objects.requireNonNull(respClass))
        );
    }
}