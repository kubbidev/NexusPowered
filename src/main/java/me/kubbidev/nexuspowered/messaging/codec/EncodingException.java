package me.kubbidev.nexuspowered.messaging.codec;

/**
 * Exception thrown if an error occurs whilst encoding/decoding a message.
 */
public class EncodingException extends RuntimeException {

    public EncodingException() {
    }

    public EncodingException(String message) {
        super(message);
    }

    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncodingException(Throwable cause) {
        super(cause);
    }
}