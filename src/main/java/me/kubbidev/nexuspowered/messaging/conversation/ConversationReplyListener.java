package me.kubbidev.nexuspowered.messaging.conversation;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Represents an object listening for replies sent on the conversation channel.
 *
 * @param <R> the reply type
 */
public interface ConversationReplyListener<R extends ConversationMessage> {

    static <R extends ConversationMessage> ConversationReplyListener<R> of(Function<? super R, RegistrationAction> onReply) {
        return new ConversationReplyListener<R>() {
            @Override
            public @NotNull RegistrationAction onReply(@NotNull R reply) {
                return onReply.apply(reply);
            }

            @Override
            public void onTimeout(@NotNull List<R> replies) {

            }
        };
    }

    /**
     * Called when a message is posted to this listener.
     *
     * <p>This method is called asynchronously.</p>
     *
     * @param reply the reply message
     * @return the action to take
     */
    @NotNull
    RegistrationAction onReply(@NotNull R reply);

    /**
     * Called when the listener times out.
     *
     * <p>A listener times out if the "timeout wait period" passes before the listener is
     * unregistered by other means.</p>
     *
     * <p>"unregistered by other means" refers to the listener being stopped after a message was
     * passed to {@link #onReply(ConversationMessage)} and {@link RegistrationAction#STOP_LISTENING} being
     * returned.</p>
     *
     * @param replies the replies which have been received
     */
    void onTimeout(@NotNull List<R> replies);

    /**
     * Defines the actions to take after receiving a reply in a {@link ConversationReplyListener}.
     */
    enum RegistrationAction {

        /**
         * Marks that the listener should continue listening for replies.
         */
        CONTINUE_LISTENING,

        /**
         * Marks that the listener should stop listening for replies.
         */
        STOP_LISTENING

    }
}