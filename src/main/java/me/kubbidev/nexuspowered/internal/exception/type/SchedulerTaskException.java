package me.kubbidev.nexuspowered.internal.exception.type;

import me.kubbidev.nexuspowered.internal.exception.InternalException;

public class SchedulerTaskException extends InternalException {

    public SchedulerTaskException(Throwable cause) {
        super("Scheduler task", cause);
    }
}