package me.kubbidev.nexuspowered.messaging.reqresp;

import me.kubbidev.nexuspowered.messaging.Channel;
import me.kubbidev.nexuspowered.messaging.conversation.ConversationChannel;
import me.kubbidev.nexuspowered.promise.Promise;
import me.kubbidev.nexuspowered.terminable.Terminable;

/**
 * A generic request/response handler that can operate over the network.
 *
 * <p>This is a high-level interface, implemented in {@link SimpleReqRespChannel}
 * using lower-level {@link ConversationChannel}s and {@link Channel}s.</p>
 *
 * @param <Req>  the request type
 * @param <Resp> the response type
 */
public interface ReqRespChannel<Req, Resp> extends Terminable {

    /**
     * Sends a request and returns a promise encapsulating the response.
     *
     * <p>The promise will complete exceptionally if a response is not received before the timeout
     * expires, by default after 5 seconds.</p>
     *
     * @param request the request object
     * @return a promise encapsulating the response
     */
    Promise<Resp> request(Req request);

    /**
     * Registers a response handler.
     *
     * @param handler the response handler
     */
    void responseHandler(ResponseHandler<Req, Resp> handler);

    /**
     * Registers a response handler that returns a Promise.
     *
     * @param handler the response handler
     */
    void asyncResponseHandler(AsyncResponseHandler<Req, Resp> handler);

    @FunctionalInterface
    interface ResponseHandler<Req, Resp> {
        Resp response(Req request);
    }

    @FunctionalInterface
    interface AsyncResponseHandler<Req, Resp> {
        Promise<Resp> response(Req request);
    }

}