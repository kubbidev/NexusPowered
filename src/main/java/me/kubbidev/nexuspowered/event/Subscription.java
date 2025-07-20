package me.kubbidev.nexuspowered.event;

import me.kubbidev.nexuspowered.terminable.Terminable;

/**
 * Represents a subscription to a given (set of) event(s).
 */
public interface Subscription extends Terminable {

    /**
     * Gets whether the handler is active
     *
     * @return if the handler is active
     */
    boolean isActive();

    /**
     * Gets the number of times the handler has been called
     *
     * @return the number of times the handler has been called
     */
    long getCallCounter();

    /**
     * Unregisters the handler
     *
     * @return true if the handler wasn't already unregistered
     */
    boolean unregister();

    @Override
    default void close() {
        this.unregister();
    }

}