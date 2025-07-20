package me.kubbidev.nexuspowered.internal.exception.type;

import me.kubbidev.nexuspowered.internal.exception.InternalException;

public class PromiseChainException extends InternalException {

    public PromiseChainException(Throwable cause) {
        super("Promise chain", cause);
    }
}