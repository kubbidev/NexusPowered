package me.kubbidev.nexuspowered.messaging.reqresp;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import me.kubbidev.nexuspowered.messaging.Messenger;
import me.kubbidev.nexuspowered.messaging.conversation.ConversationChannel;
import me.kubbidev.nexuspowered.messaging.conversation.ConversationReply;
import me.kubbidev.nexuspowered.messaging.conversation.ConversationReplyListener;
import me.kubbidev.nexuspowered.promise.Promise;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implements a {@link ReqRespChannel} using {@link ConversationChannel}s.
 *
 * @param <Req>  the request type
 * @param <Resp> the response type
 */
public class SimpleReqRespChannel<Req, Resp> implements ReqRespChannel<Req, Resp> {
    private final ConversationChannel<ReqResMessage<Req>, ReqResMessage<Resp>> channel;

    public SimpleReqRespChannel(Messenger messenger, String name, TypeToken<Req> reqType, TypeToken<Resp> respType) {
        TypeToken<ReqResMessage<Req>> reqMsgType = new TypeToken<ReqResMessage<Req>>() {
        }.where(new TypeParameter<Req>() {
        }, reqType);
        TypeToken<ReqResMessage<Resp>> respMsgType = new TypeToken<ReqResMessage<Resp>>() {
        }.where(new TypeParameter<Resp>() {
        }, respType);
        this.channel = messenger.getConversationChannel(name, reqMsgType, respMsgType);
    }

    @Override
    public Promise<Resp> request(Req request) {
        ReqResMessage<Req> message = new ReqResMessage<>(UUID.randomUUID(), request);
        Promise<Resp> promise = Promise.empty();
        this.channel.sendMessage(message, new ConversationReplyListener<ReqResMessage<Resp>>() {
            @Override
            public @NotNull RegistrationAction onReply(@NotNull ReqResMessage<Resp> reply) {
                promise.supply(reply.getBody());
                return RegistrationAction.STOP_LISTENING;
            }

            @Override
            public void onTimeout(@NotNull List<ReqResMessage<Resp>> replies) {
                promise.supplyException(new TimeoutException("Request timed out"));
            }
        }, 5, TimeUnit.SECONDS);
        return promise;
    }

    @Override
    public void responseHandler(ResponseHandler<Req, Resp> handler) {
        this.channel.newAgent((agent, message) -> {
            UUID id = message.getConversationId();
            Req req = message.getBody();

            Resp resp = handler.response(req);
            if (resp != null) {
                return ConversationReply.of(new ReqResMessage<>(id, resp));
            } else {
                return ConversationReply.noReply();
            }
        });
    }

    @Override
    public void asyncResponseHandler(AsyncResponseHandler<Req, Resp> handler) {
        this.channel.newAgent((agent, message) -> {
            UUID id = message.getConversationId();
            Req req = message.getBody();

            Promise<Resp> promise = handler.response(req);
            if (promise != null) {
                Promise<ReqResMessage<Resp>> composedPromise = promise.thenApplyAsync(resp -> resp == null ? null : new ReqResMessage<>(id, resp));
                return ConversationReply.ofPromise(composedPromise);
            } else {
                return ConversationReply.noReply();
            }
        });
    }

    @Override
    public void close() {
        this.channel.close();
    }
}