package me.kubbidev.nexuspowered.scheduler;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.kubbidev.nexuspowered.promise.Promise;
import me.kubbidev.nexuspowered.promise.ThreadContext;
import me.kubbidev.nexuspowered.util.Delegates;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for scheduling tasks
 */
public interface Scheduler extends Executor {

    /**
     * Gets the context this scheduler operates in.
     *
     * @return the context
     */
    @NotNull ThreadContext getContext();

    /**
     * Compute the result of the passed supplier.
     *
     * @param supplier the supplier
     * @param <T>      the return type
     * @return a Promise which will return the result of the computation
     */
    @NotNull
    default <T> Promise<T> supply(@NotNull Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return Promise.supplying(getContext(), supplier);
    }

    /**
     * Compute the result of the passed callable.
     *
     * @param callable the callable
     * @param <T>      the return type
     * @return a Promise which will return the result of the computation
     */
    @NotNull
    default <T> Promise<T> call(@NotNull Callable<T> callable) {
        Objects.requireNonNull(callable, "callable");
        return Promise.supplying(getContext(), Delegates.callableToSupplier(callable));
    }

    /**
     * Execute the passed runnable.
     *
     * @param runnable the runnable
     * @return a Promise which will return when the runnable is complete
     */
    @NotNull
    default Promise<Void> run(@NotNull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        return Promise.supplying(getContext(), Delegates.runnableToSupplier(runnable));
    }

    /**
     * Compute the result of the passed supplier at some point in the future.
     *
     * @param supplier   the supplier
     * @param delayTicks the delay in ticks before calling the supplier
     * @param <T>        the return type
     * @return a Promise which will return the result of the computation
     */
    @NotNull
    default <T> Promise<T> supplyLater(@NotNull Supplier<T> supplier, long delayTicks) {
        Objects.requireNonNull(supplier, "supplier");
        return Promise.supplyingDelayed(getContext(), supplier, delayTicks);
    }

    /**
     * Compute the result of the passed supplier at some point in the future.
     *
     * @param supplier the supplier
     * @param delay    the delay to wait before calling the supplier
     * @param unit     the unit of delay
     * @param <T>      the return type
     * @return a Promise which will return the result of the computation
     */
    @NotNull
    default <T> Promise<T> supplyLater(@NotNull Supplier<T> supplier, long delay, @NotNull TimeUnit unit) {
        Objects.requireNonNull(supplier, "supplier");
        return Promise.supplyingDelayed(getContext(), supplier, delay, unit);
    }

    /**
     * Compute the result of the passed callable at some point in the future.
     *
     * @param callable   the callable
     * @param delayTicks the delay in ticks before calling the supplier
     * @param <T>        the return type
     * @return a Promise which will return the result of the computation
     */
    @NotNull
    default <T> Promise<T> callLater(@NotNull Callable<T> callable, long delayTicks) {
        Objects.requireNonNull(callable, "callable");
        return Promise.supplyingDelayed(getContext(), Delegates.callableToSupplier(callable), delayTicks);
    }

    /**
     * Compute the result of the passed callable at some point in the future.
     *
     * @param callable the callable
     * @param delay    the delay to wait before calling the supplier
     * @param unit     the unit of delay
     * @param <T>      the return type
     * @return a Promise which will return the result of the computation
     */
    @NotNull
    default <T> Promise<T> callLater(@NotNull Callable<T> callable, long delay, @NotNull TimeUnit unit) {
        Objects.requireNonNull(callable, "callable");
        return Promise.supplyingDelayed(getContext(), Delegates.callableToSupplier(callable), delay, unit);
    }

    /**
     * Execute the passed runnable at some point in the future.
     *
     * @param runnable   the runnable
     * @param delayTicks the delay in ticks before calling the supplier
     * @return a Promise which will return when the runnable is complete
     */
    @NotNull
    default Promise<Void> runLater(@NotNull Runnable runnable, long delayTicks) {
        Objects.requireNonNull(runnable, "runnable");
        return Promise.supplyingDelayed(getContext(), Delegates.runnableToSupplier(runnable), delayTicks);
    }

    /**
     * Execute the passed runnable at some point in the future.
     *
     * @param runnable the runnable
     * @param delay    the delay to wait before calling the supplier
     * @param unit     the unit of delay
     * @return a Promise which will return when the runnable is complete
     */
    @NotNull
    default Promise<Void> runLater(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        Objects.requireNonNull(runnable, "runnable");
        return Promise.supplyingDelayed(getContext(), Delegates.runnableToSupplier(runnable), delay, unit);
    }

    /**
     * Schedule a repeating task to run.
     *
     * @param consumer      the task to run
     * @param delayTicks    the delay before the task begins
     * @param intervalTicks the interval at which the task will repeat
     * @return a task instance
     */
    @NotNull Task runRepeating(@NotNull Consumer<Task> consumer, long delayTicks, long intervalTicks);

    /**
     * Schedule a repeating task to run.
     *
     * @param consumer     the task to run
     * @param delay        the delay before the task begins
     * @param delayUnit    the unit of delay
     * @param interval     the interval at which the task will repeat
     * @param intervalUnit the
     * @return a task instance
     */
    @NotNull Task runRepeating(@NotNull Consumer<Task> consumer, long delay, @NotNull TimeUnit delayUnit, long interval,
                               @NotNull TimeUnit intervalUnit);

    /**
     * Schedule a repeating task to run.
     *
     * @param runnable      the task to run
     * @param delayTicks    the delay before the task begins
     * @param intervalTicks the interval at which the task will repeat
     * @return a task instance
     */
    @NotNull
    default Task runRepeating(@NotNull Runnable runnable, long delayTicks, long intervalTicks) {
        return runRepeating(Delegates.runnableToConsumer(runnable), delayTicks, intervalTicks);
    }

    /**
     * Schedule a repeating task to run.
     *
     * @param runnable     the task to run
     * @param delay        the delay before the task begins
     * @param delayUnit    the unit of delay
     * @param interval     the interval at which the task will repeat
     * @param intervalUnit the
     * @return a task instance
     */
    @NotNull
    default Task runRepeating(@NotNull Runnable runnable, long delay, @NotNull TimeUnit delayUnit, long interval,
                              @NotNull TimeUnit intervalUnit) {
        return runRepeating(Delegates.runnableToConsumer(runnable), delay, delayUnit, interval, intervalUnit);
    }
}