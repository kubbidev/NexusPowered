package me.kubbidev.nexuspowered.event.functional;

/**
 * Represents when a expiry predicate should be tested relative to the handling of the event.
 */
public enum ExpiryTestStage {

    /**
     * The expiry predicate should be tested before the event is filtered or handled.
     */
    PRE,

    /**
     * The expiry predicate should be tested after the subscriptions filters have been evaluated.
     */
    POST_FILTER,

    /**
     * The expiry predicate should be tested after the event has been handled.
     */
    POST_HANDLE

}