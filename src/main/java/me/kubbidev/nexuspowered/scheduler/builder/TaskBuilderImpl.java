package me.kubbidev.nexuspowered.scheduler.builder;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.promise.Promise;
import me.kubbidev.nexuspowered.promise.ThreadContext;
import me.kubbidev.nexuspowered.scheduler.Task;
import org.jetbrains.annotations.NotNull;

class TaskBuilderImpl implements TaskBuilder {

    static final TaskBuilder INSTANCE = new TaskBuilderImpl();

    private final TaskBuilder.ThreadContextual sync;
    private final ThreadContextual             async;

    private TaskBuilderImpl() {
        this.sync = new ThreadContextualBuilder(ThreadContext.SYNC);
        this.async = new ThreadContextualBuilder(ThreadContext.ASYNC);
    }

    @Override
    public @NotNull TaskBuilder.ThreadContextual sync() {
        return this.sync;
    }

    @Override
    public @NotNull TaskBuilder.ThreadContextual async() {
        return this.async;
    }

    private static final class ThreadContextualBuilder implements TaskBuilder.ThreadContextual {

        private final ThreadContext            context;
        private final ContextualPromiseBuilder instant;

        ThreadContextualBuilder(ThreadContext context) {
            this.context = context;
            this.instant = new ContextualPromiseBuilderImpl(context);
        }

        @Override
        public @NotNull ContextualPromiseBuilder now() {
            return this.instant;
        }

        @Override
        public @NotNull DelayedTick after(long ticks) {
            return new DelayedTickBuilder(this.context, ticks);
        }

        @Override
        public @NotNull DelayedTime after(long duration, @NotNull TimeUnit unit) {
            return new DelayedTimeBuilder(this.context, duration, unit);
        }

        @Override
        public @NotNull ContextualTaskBuilder afterAndEvery(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, ticks, ticks);
        }

        @Override
        public @NotNull ContextualTaskBuilder afterAndEvery(long duration, @NotNull TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, duration, unit, duration, unit);
        }

        @Override
        public @NotNull ContextualTaskBuilder every(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, 0, ticks);
        }

        @Override
        public @NotNull ContextualTaskBuilder every(long duration, @NotNull TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, 0, TimeUnit.NANOSECONDS, duration, unit);
        }
    }

    private record DelayedTickBuilder(ThreadContext context, long delay) implements DelayedTick {

        @Override
        public @NotNull <T> Promise<T> supply(@NotNull Supplier<T> supplier) {
            return Schedulers.get(this.context).supplyLater(supplier, this.delay);
        }

        @Override
        public @NotNull <T> Promise<T> call(@NotNull Callable<T> callable) {
            return Schedulers.get(this.context).callLater(callable, this.delay);
        }

        @Override
        public @NotNull Promise<Void> run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).runLater(runnable, this.delay);
        }

        @Override
        public @NotNull ContextualTaskBuilder every(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, this.delay, ticks);
        }
    }

    private record DelayedTimeBuilder(ThreadContext context, long delay, TimeUnit delayUnit) implements DelayedTime {

        @Override
        public @NotNull <T> Promise<T> supply(@NotNull Supplier<T> supplier) {
            return Schedulers.get(this.context).supplyLater(supplier, this.delay, this.delayUnit);
        }

        @Override
        public @NotNull <T> Promise<T> call(@NotNull Callable<T> callable) {
            return Schedulers.get(this.context).callLater(callable, this.delay, this.delayUnit);
        }

        @Override
        public @NotNull Promise<Void> run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).runLater(runnable, this.delay, this.delayUnit);
        }

        @Override
        public @NotNull ContextualTaskBuilder every(long duration, TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, this.delay, this.delayUnit, duration, unit);
        }
    }

    private record ContextualPromiseBuilderImpl(ThreadContext context) implements ContextualPromiseBuilder {

        @Override
        public <T> @NotNull Promise<T> supply(@NotNull Supplier<T> supplier) {
            return Schedulers.get(this.context).supply(supplier);
        }

        @Override
        public <T> @NotNull Promise<T> call(@NotNull Callable<T> callable) {
            return Schedulers.get(this.context).call(callable);
        }

        @Override
        public @NotNull Promise<Void> run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).run(runnable);
        }
    }

    private record ContextualTaskBuilderTickImpl(ThreadContext context, long delay, long interval) implements
        ContextualTaskBuilder {

        @Override
        public @NotNull Task consume(@NotNull Consumer<Task> consumer) {
            return Schedulers.get(this.context).runRepeating(consumer, this.delay, this.interval);
        }

        @Override
        public @NotNull Task run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context).runRepeating(runnable, this.delay, this.interval);
        }
    }

    private record ContextualTaskBuilderTimeImpl(ThreadContext context, long delay, TimeUnit delayUnit, long interval,
                                                 TimeUnit intervalUnit) implements ContextualTaskBuilder {

        @Override
        public @NotNull Task consume(@NotNull Consumer<Task> consumer) {
            return Schedulers.get(this.context)
                .runRepeating(consumer, this.delay, this.delayUnit, this.interval, this.intervalUnit);
        }

        @Override
        public @NotNull Task run(@NotNull Runnable runnable) {
            return Schedulers.get(this.context)
                .runRepeating(runnable, this.delay, this.delayUnit, this.interval, this.intervalUnit);
        }
    }
}