package me.kubbidev.nexuspowered.internal.exception.type;

import me.kubbidev.nexuspowered.internal.exception.InternalException;

public class EventHandlerException extends InternalException {

    public EventHandlerException(Throwable cause, Object event) {
        super("Event handler for " + event.getClass().getName(), cause);
    }
}