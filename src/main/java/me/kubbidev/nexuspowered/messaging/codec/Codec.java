package me.kubbidev.nexuspowered.messaging.codec;

/**
 * An object responsible for encoding and decoding a given type of message.
 *
 * @param <M> the message type
 */
public interface Codec<M> {

    /**
     * Encodes the message.
     *
     * @param message the message object
     * @return the encoded form
     * @throws EncodingException if encoding failed
     */
    byte[] encode(M message) throws EncodingException;

    /**
     * Decodes the message.
     *
     * @param buf the encoded message
     * @return the decoded object
     * @throws EncodingException if decoding failed
     */
    M decode(byte[] buf) throws EncodingException;

}