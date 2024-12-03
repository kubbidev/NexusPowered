package me.kubbidev.nexuspowered.internal.exception;

public abstract class InternalException extends RuntimeException {

    protected InternalException(String what, Throwable cause) {
        super("Exception occurred in a nexus " + what, cause);
    }
}