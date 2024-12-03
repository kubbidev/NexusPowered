package me.kubbidev.nexuspowered.internal.exception;

import me.kubbidev.nexuspowered.Events;
import me.kubbidev.nexuspowered.interfaces.Delegate;
import me.kubbidev.nexuspowered.internal.exception.event.NexusExceptionEvent;
import me.kubbidev.nexuspowered.internal.exception.type.EventHandlerException;
import me.kubbidev.nexuspowered.internal.exception.type.PromiseChainException;
import me.kubbidev.nexuspowered.internal.exception.type.SchedulerTaskException;
import me.kubbidev.nexuspowered.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Central handler for exceptions that occur within user-written
 * Runnables and handlers running in nexuspowered.
 */
public final class NexusExceptions {
    private NexusExceptions() {
    }

    private static final ThreadLocal<AtomicBoolean> NOT_TODAY_STACK_OVERFLOW_EXCEPTION =
            ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    private static void log(InternalException exception) {
        // print to logger
        Log.severe(exception.getMessage(), exception);

        // call event
        AtomicBoolean firing = NOT_TODAY_STACK_OVERFLOW_EXCEPTION.get();
        if (firing.compareAndSet(false, true)) {
            try {
                Events.call(new NexusExceptionEvent(exception));
            } finally {
                firing.set(false);
            }
        }
    }

    public static void reportScheduler(Throwable throwable) {
        log(new SchedulerTaskException(throwable));
    }

    public static void reportPromise(Throwable throwable) {
        log(new PromiseChainException(throwable));
    }

    public static void reportEvent(Object event, Throwable throwable) {
        log(new EventHandlerException(throwable, event));
    }

    public static Runnable wrapSchedulerTask(Runnable runnable) {
        return new SchedulerWrappedRunnable(runnable);
    }

    private static class SchedulerWrappedRunnable implements Runnable, Delegate<Runnable> {
        private final Runnable delegate;

        public SchedulerWrappedRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                this.delegate.run();
            } catch (Throwable t) {
                reportScheduler(t);
            }
        }

        @Override
        public Runnable delegate() {
            return this.delegate;
        }
    }
}