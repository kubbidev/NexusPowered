package me.kubbidev.nexuspowered.scheduler.builder;

import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.promise.Promise;
import me.kubbidev.nexuspowered.promise.ThreadContext;
import me.kubbidev.nexuspowered.scheduler.Task;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

class TaskBuilderImpl implements TaskBuilder {
    static final TaskBuilder INSTANCE = new TaskBuilderImpl();

    private final TaskBuilder.ThreadContextual sync;
    private final ThreadContextual async;

    private TaskBuilderImpl() {
        this.sync = new ThreadContextualBuilder(ThreadContext.SYNC);
        this.async = new ThreadContextualBuilder(ThreadContext.ASYNC);
    }

    @NotNull
    @Override
    public TaskBuilder.ThreadContextual sync() {
        return this.sync;
    }

    @NotNull
    @Override
    public TaskBuilder.ThreadContextual async() {
        return this.async;
    }

    private static final class ThreadContextualBuilder implements TaskBuilder.ThreadContextual {
        private final ThreadContext context;
        private final ContextualPromiseBuilder instant;

        ThreadContextualBuilder(ThreadContext context) {
            this.context = context;
            this.instant = new ContextualPromiseBuilderImpl(context);
        }

        @NotNull
        @Override
        public ContextualPromiseBuilder now() {
            return this.instant;
        }

        @NotNull
        @Override
        public DelayedTick after(long ticks) {
            return new DelayedTickBuilder(this.context, ticks);
        }

        @NotNull
        @Override
        public DelayedTime after(long duration, @NotNull TimeUnit unit) {
            return new DelayedTimeBuilder(this.context, duration, unit);
        }

        @NotNull
        @Override
        public ContextualTaskBuilder afterAndEvery(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, ticks, ticks);
        }

        @NotNull
        @Override
        public ContextualTaskBuilder afterAndEvery(long duration, @NotNull TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, duration, unit, duration, unit);
        }

        @NotNull
        @Override
        public ContextualTaskBuilder every(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, 0, ticks);
        }

        @NotNull
        @Override
        public ContextualTaskBuilder every(long duration, @NotNull TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, 0, TimeUnit.NANOSECONDS, duration, unit);
        }
    }

    private static class DelayedTickBuilder implements DelayedTick {
        private final ThreadContext context;
        private final long delay;

        public DelayedTickBuilder(ThreadContext context, long delay) {
            this.context = context;
            this.delay = delay;
        }

        @NotNull
        @Override
        public <T> Promise<T> supply(@NotNull Supplier<T> supplier) {
            return Schedulers.get(this.context).supplyLater(supplier, this.delay);
        }

        @NotNull
        @Override
        public <T> Promise<T> call(@NotNull Callable<T> callable) {
            return Schedulers.get(this.context).callLater(callable, this.delay);
        }

        @NotNull
        @Override
        public Promise<Void> run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).runLater(runnable, this.delay);
        }

        @NotNull
        @Override
        public ContextualTaskBuilder every(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, this.delay, ticks);
        }
    }

    private static class DelayedTimeBuilder implements DelayedTime {
        private final ThreadContext context;

        private final long delay;
        private final TimeUnit delayUnit;

        public DelayedTimeBuilder(ThreadContext context, long delay, TimeUnit delayUnit) {
            this.context = context;
            this.delay = delay;
            this.delayUnit = delayUnit;
        }

        @NotNull
        @Override
        public <T> Promise<T> supply(@NotNull Supplier<T> supplier) {
            return Schedulers.get(this.context).supplyLater(supplier, this.delay, this.delayUnit);
        }

        @NotNull
        @Override
        public <T> Promise<T> call(@NotNull Callable<T> callable) {
            return Schedulers.get(this.context).callLater(callable, this.delay, this.delayUnit);
        }

        @NotNull
        @Override
        public Promise<Void> run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).runLater(runnable, this.delay, this.delayUnit);
        }

        @NotNull
        @Override
        public ContextualTaskBuilder every(long duration, TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, this.delay, this.delayUnit, duration, unit);
        }
    }

    private static class ContextualPromiseBuilderImpl implements ContextualPromiseBuilder {
        private final ThreadContext context;

        public ContextualPromiseBuilderImpl(ThreadContext context) {
            this.context = context;
        }

        @NotNull
        @Override
        public <T> Promise<T> supply(@NotNull Supplier<T> supplier) {
            return Schedulers.get(this.context).supply(supplier);
        }

        @NotNull
        @Override
        public <T> Promise<T> call(@NotNull Callable<T> callable) {
            return Schedulers.get(this.context).call(callable);
        }

        @NotNull
        @Override
        public Promise<Void> run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).run(runnable);
        }
    }

    private static class ContextualTaskBuilderTickImpl implements ContextualTaskBuilder {
        private final ThreadContext context;

        private final long delay;
        private final long interval;

        public ContextualTaskBuilderTickImpl(ThreadContext context, long delay, long interval) {
            this.context = context;
            this.delay = delay;
            this.interval = interval;
        }

        @NotNull
        @Override
        public Task consume(@NotNull Consumer<Task> consumer) {
            return Schedulers.get(this.context).runRepeating(consumer, this.delay, this.interval);
        }

        @NotNull
        @Override
        public Task run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).runRepeating(runnable, this.delay, this.interval);
        }
    }

    private static class ContextualTaskBuilderTimeImpl implements ContextualTaskBuilder {
        private final ThreadContext context;

        private final long delay;
        private final TimeUnit delayUnit;

        private final long interval;
        private final TimeUnit intervalUnit;

        public ContextualTaskBuilderTimeImpl(ThreadContext context, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit) {
            this.context = context;
            this.delay = delay;
            this.delayUnit = delayUnit;
            this.interval = interval;
            this.intervalUnit = intervalUnit;
        }

        @NotNull
        @Override
        public Task consume(@NotNull Consumer<Task> consumer) {
            return Schedulers.get(this.context).runRepeating(consumer, this.delay, this.delayUnit, this.interval, this.intervalUnit);
        }

        @NotNull
        @Override
        public Task run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).runRepeating(runnable, this.delay, this.delayUnit, this.interval, this.intervalUnit);
        }
    }
}