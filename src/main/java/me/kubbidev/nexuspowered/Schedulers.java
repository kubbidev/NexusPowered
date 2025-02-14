package me.kubbidev.nexuspowered;

import me.kubbidev.nexuspowered.interfaces.Delegate;
import me.kubbidev.nexuspowered.internal.LoaderUtils;
import me.kubbidev.nexuspowered.internal.exception.NexusExceptions;
import me.kubbidev.nexuspowered.promise.ThreadContext;
import me.kubbidev.nexuspowered.scheduler.NexusExecutors;
import me.kubbidev.nexuspowered.scheduler.Scheduler;
import me.kubbidev.nexuspowered.scheduler.Task;
import me.kubbidev.nexuspowered.scheduler.Ticks;
import me.kubbidev.nexuspowered.scheduler.builder.TaskBuilder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Provides common instances of {@link Scheduler}.
 */
@NotNullByDefault
public final class Schedulers {
    private static final Scheduler SYNC_SCHEDULER = new SyncScheduler();
    private static final Scheduler ASYNC_SCHEDULER = new AsyncScheduler();

    /**
     * Gets a scheduler for the given context.
     *
     * @param context the context
     * @return a scheduler
     */
    public static Scheduler get(ThreadContext context) {
        switch (context) {
            case SYNC:
                return sync();
            case ASYNC:
                return async();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Returns a "sync" scheduler, which executes tasks on the main server thread.
     *
     * @return a sync executor instance
     */
    public static Scheduler sync() {
        return SYNC_SCHEDULER;
    }

    /**
     * Returns an "async" scheduler, which executes tasks asynchronously.
     *
     * @return an async executor instance
     */
    public static Scheduler async() {
        return ASYNC_SCHEDULER;
    }

    /**
     * Gets Bukkit's scheduler.
     *
     * @return bukkit's scheduler
     */
    public static BukkitScheduler bukkit() {
        return Nexus.bukkitScheduler();
    }

    /**
     * Gets a {@link TaskBuilder} instance
     *
     * @return a task builder
     */
    public static TaskBuilder builder() {
        return TaskBuilder.newBuilder();
    }

    private static final class SyncScheduler implements Scheduler {

        @Override
        public void execute(@NotNull Runnable runnable) {
            NexusExecutors.sync().execute(runnable);
        }

        @NotNull
        @Override
        public ThreadContext getContext() {
            return ThreadContext.SYNC;
        }

        @NotNull
        @Override
        public Task runRepeating(@NotNull Consumer<Task> consumer, long delayTicks, long intervalTicks) {
            Objects.requireNonNull(consumer, "consumer");
            NexusTask task = new NexusTask(consumer);
            task.runTaskTimer(LoaderUtils.getPlugin(), delayTicks, intervalTicks);
            return task;
        }

        @NotNull
        @Override
        public Task runRepeating(@NotNull Consumer<Task> consumer, long delay, @NotNull TimeUnit delayUnit, long interval, @NotNull TimeUnit intervalUnit) {
            return runRepeating(consumer, Ticks.from(delay, delayUnit), Ticks.from(interval, intervalUnit));
        }
    }

    private static final class AsyncScheduler implements Scheduler {

        @Override
        public void execute(@NotNull Runnable runnable) {
            NexusExecutors.asyncNexus().execute(runnable);
        }

        @NotNull
        @Override
        public ThreadContext getContext() {
            return ThreadContext.ASYNC;
        }

        @NotNull
        @Override
        public Task runRepeating(@NotNull Consumer<Task> consumer, long delayTicks, long intervalTicks) {
            Objects.requireNonNull(consumer, "consumer");
            NexusTask task = new NexusTask(consumer);
            task.runTaskTimerAsynchronously(LoaderUtils.getPlugin(), delayTicks, intervalTicks);
            return task;
        }

        @NotNull
        @Override
        public Task runRepeating(@NotNull Consumer<Task> consumer, long delay, @NotNull TimeUnit delayUnit, long interval, @NotNull TimeUnit intervalUnit) {
            Objects.requireNonNull(consumer, "consumer");
            return new NexusAsyncTask(consumer, delay, delayUnit, interval, intervalUnit);
        }
    }

    private static class NexusTask extends BukkitRunnable implements Task, Delegate<Consumer<Task>> {
        private final Consumer<Task> backingTask;

        private final AtomicInteger counter = new AtomicInteger(0);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        private NexusTask(Consumer<Task> backingTask) {
            this.backingTask = backingTask;
        }

        @Override
        public void run() {
            if (this.cancelled.get()) {
                cancel();
                return;
            }

            try {
                this.backingTask.accept(this);
                this.counter.incrementAndGet();
            } catch (Throwable e) {
                NexusExceptions.reportScheduler(e);
            }

            if (this.cancelled.get()) {
                cancel();
            }
        }

        @Override
        public int getTimesRan() {
            return this.counter.get();
        }

        @Override
        public boolean stop() {
            return !this.cancelled.getAndSet(true);
        }

        @Override
        public int getBukkitId() {
            return getTaskId();
        }

        @Override
        public boolean isClosed() {
            return this.cancelled.get();
        }

        @Override
        public Consumer<Task> delegate() {
            return this.backingTask;
        }
    }

    private static class NexusAsyncTask implements Runnable, Task, Delegate<Consumer<Task>> {
        private final Consumer<Task> backingTask;
        private final ScheduledFuture<?> future;

        private final AtomicInteger counter = new AtomicInteger(0);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        private NexusAsyncTask(Consumer<Task> backingTask, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit) {
            this.backingTask = backingTask;
            this.future = NexusExecutors.asyncNexus().scheduleAtFixedRate(this, delayUnit.toNanos(delay), intervalUnit.toNanos(interval), TimeUnit.NANOSECONDS);
        }

        @Override
        public void run() {
            if (this.cancelled.get()) {
                return;
            }

            try {
                this.backingTask.accept(this);
                this.counter.incrementAndGet();
            } catch (Throwable e) {
                NexusExceptions.reportScheduler(e);
            }
        }

        @Override
        public int getTimesRan() {
            return this.counter.get();
        }

        @Override
        public boolean stop() {
            if (!this.cancelled.getAndSet(true)) {
                this.future.cancel(false);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int getBukkitId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isClosed() {
            return this.cancelled.get();
        }

        @Override
        public Consumer<Task> delegate() {
            return this.backingTask;
        }
    }

    private Schedulers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}