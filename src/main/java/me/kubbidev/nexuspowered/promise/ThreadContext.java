package me.kubbidev.nexuspowered.promise;

import me.kubbidev.nexuspowered.internal.LoaderUtils;

/**
 * Represents the two main types of {@link Thread} on the server.
 */
public enum ThreadContext {

    /**
     * Represents the main "server" thread
     */
    SYNC,

    /**
     * Represents anything which isn't the {@link #SYNC} thread.
     */
    ASYNC;

    public static ThreadContext forCurrentThread() {
        return forThread(Thread.currentThread());
    }

    public static ThreadContext forThread(Thread thread) {
        return thread == LoaderUtils.getMainThread() ? SYNC : ASYNC;
    }
}