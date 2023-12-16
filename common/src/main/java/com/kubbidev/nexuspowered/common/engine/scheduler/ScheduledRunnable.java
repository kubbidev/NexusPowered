package com.kubbidev.nexuspowered.common.engine.scheduler;

import com.kubbidev.nexuspowered.common.NexusPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * This class is provided as an easy way to handle scheduling tasks.
 */
public abstract class ScheduledRunnable implements Runnable {
    private SchedulerTask task;

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        checkScheduled();
        this.task.cancel();
    }

    /**
     * Executes the given task with a delay.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay the delay
     * @param unit  the unit of delay
     *
     * @throws IllegalStateException if this was already scheduled
     * @return the resultant task instance
     */
    public synchronized @NotNull SchedulerTask asyncLater(@NotNull NexusPlugin<?> plugin, long delay, TimeUnit unit) throws IllegalStateException {
        checkNotYetScheduled();
        return setupTask(plugin.getSchedulerAdapter().asyncLater(this, delay, unit));
    }

    /**
     * Executes the given task repeatedly at a given interval.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay the delay
     * @param unit  the unit of delay
     *
     * @throws IllegalStateException if this was already scheduled
     * @return the resultant task instance
     */
    public synchronized @NotNull SchedulerTask asyncRepeating(@NotNull NexusPlugin<?> plugin, long delay, TimeUnit unit) throws IllegalStateException {
        checkNotYetScheduled();
        return setupTask(plugin.getSchedulerAdapter().asyncRepeating(this, delay, unit));
    }

    private void checkNotYetScheduled() {
        if (task != null) {
            throw new IllegalStateException("Already scheduled");
        }
    }

    private void checkScheduled() {
        if (task == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private @NotNull SchedulerTask setupTask(@NotNull SchedulerTask task) {
        this.task = task;
        return task;
    }
}
