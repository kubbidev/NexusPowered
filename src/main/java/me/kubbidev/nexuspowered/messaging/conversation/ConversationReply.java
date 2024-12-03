package me.kubbidev.nexuspowered.messaging.conversation;

import me.kubbidev.nexuspowered.promise.Promise;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Encapsulates the reply to a incoming message in a {@link ConversationChannel}.
 *
 * @param <R> the reply type
 */
public final class ConversationReply<R extends ConversationMessage> {
    private static final ConversationReply<?> NO_REPLY = new ConversationReply<>(null);

    /**
     * Returns an object indicating that no reply should be sent.
     *
     * @param <R> the reply type
     * @return a "no reply" marker
     */
    @SuppressWarnings("unchecked")
    public static <R extends ConversationMessage> ConversationReply<R> noReply() {
        return (ConversationReply<R>) NO_REPLY;
    }

    /**
     * Creates a new {@link ConversationReply}.
     *
     * @param reply the reply message
     * @param <R>   the type
     * @return the new reply encapsulation
     */
    public static <R extends ConversationMessage> ConversationReply<R> of(R reply) {
        Objects.requireNonNull(reply, "reply");
        return new ConversationReply<>(Promise.completed(reply));
    }

    /**
     * Creates a new {@link ConversationReply}.
     *
     * <p>Bear in mind the reply will only be send once the future completes. The timeout value on
     * "other other end" may need to take this into account.</p>
     *
     * @param futureReply the future reply
     * @param <R>         the type
     * @return the new reply encapsulation
     */
    public static <R extends ConversationMessage> ConversationReply<R> ofCompletableFuture(CompletableFuture<R> futureReply) {
        Objects.requireNonNull(futureReply, "futureReply");
        return new ConversationReply<>(Promise.wrapFuture(futureReply));
    }

    /**
     * Creates a new {@link ConversationReply}.
     *
     * <p>Bear in mind the reply will only be send once the future completes. The timeout value on
     * "other other end" may need to take this into account.</p>
     *
     * @param promiseReply the future reply
     * @param <R>          the type
     * @return the new reply encapsulation
     */
    public static <R extends ConversationMessage> ConversationReply<R> ofPromise(Promise<R> promiseReply) {
        Objects.requireNonNull(promiseReply, "promiseReply");
        return new ConversationReply<>(promiseReply);
    }

    private final Promise<R> reply;

    private ConversationReply(Promise<R> reply) {
        this.reply = reply;
    }

    /**
     * Gets if this object actually contains a reply.
     *
     * @return if the object has a reply
     */
    public boolean hasReply() {
        return this.reply != null;
    }

    /**
     * Gets the reply.
     *
     * @return the reply
     * @throws IllegalStateException if this object doesn't {@link #hasReply() have a reply}
     */
    @NotNull
    public Promise<R> getReply() {
        if (this.reply == null) {
            throw new IllegalStateException("No reply present");
        }
        return this.reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConversationReply<?>)) {
            return false;
        }
        ConversationReply<?> other = (ConversationReply<?>) o;
        return Objects.equals(this.reply, other.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.reply);
    }

    @Override
    public String toString() {
        return this.reply != null ? String.format("ConversationReply[%s]", this.reply) : "ConversationReply.noReply";
    }

}