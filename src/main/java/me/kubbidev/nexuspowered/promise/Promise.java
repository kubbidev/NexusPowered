package me.kubbidev.nexuspowered.promise;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.kubbidev.nexuspowered.terminable.Terminable;
import me.kubbidev.nexuspowered.util.Delegates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object that acts as a proxy for a result that is initially unknown, usually because the computation of its value
 * is yet incomplete.
 *
 * <p>This interface carries similar method signatures to those of
 * {@link java.util.concurrent.CompletionStage} and {@link CompletableFuture}.</p>
 *
 * <p>However, a distinction is made between actions which are executed on
 * the main server thread vs asynchronously.</p>
 *
 * @param <V> the result type
 */
public interface Promise<V> extends Future<V>, Terminable {

    /**
     * Returns a new empty Promise
     *
     * <p>An empty promise can be 'completed' via the supply methods.</p>
     *
     * @param <U> the result type
     * @return a new empty promise
     */
    @NotNull
    static <U> Promise<U> empty() {
        return NexusPromise.empty();
    }

    /**
     * Returns a new base promise to be built on top of.
     *
     * @return a new promise
     */
    @NotNull
    static Promise<Void> start() {
        return NexusPromise.completed(null);
    }

    /**
     * Returns a Promise which is already completed with the given value.
     *
     * @param value the value
     * @param <U>   the result type
     * @return a new completed promise
     */
    @NotNull
    static <U> Promise<U> completed(@Nullable U value) {
        return NexusPromise.completed(value);
    }

    /**
     * Returns a Promise which is already completed with the given exception.
     *
     * @param exception the exception
     * @param <U>       the result type
     * @return the new completed promise
     */
    @NotNull
    static <U> Promise<U> exceptionally(@NotNull Throwable exception) {
        return NexusPromise.exceptionally(exception);
    }

    /**
     * Returns a Promise which represents the given future.
     *
     * <p>The implementation will make an attempt to wrap the future without creating a new process
     * to await the result (by casting to {@link java.util.concurrent.CompletionStage} or
     * {@link com.google.common.util.concurrent.ListenableFuture}).</p>
     *
     * <p>Calls to {@link #cancel() cancel} the returned promise will not affected the wrapped
     * future.</p>
     *
     * @param future the future to wrap
     * @param <U>    the result type
     * @return the new promise
     */
    @NotNull
    static <U> Promise<U> wrapFuture(@NotNull Future<U> future) {
        return NexusPromise.wrapFuture(future);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier.
     *
     * @param context  the type of executor to use to supply the promise
     * @param supplier the value supplier
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplying(@NotNull ThreadContext context, @NotNull Supplier<U> supplier) {
        Promise<U> p = empty();
        return p.supply(context, supplier);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier.
     *
     * @param supplier the value supplier
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingSync(@NotNull Supplier<U> supplier) {
        Promise<U> p = empty();
        return p.supplySync(supplier);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier.
     *
     * @param supplier the value supplier
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingAsync(@NotNull Supplier<U> supplier) {
        Promise<U> p = empty();
        return p.supplyAsync(supplier);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier, after the delay has elapsed.
     *
     * @param context    the type of executor to use to supply the promise
     * @param supplier   the value supplier
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingDelayed(@NotNull ThreadContext context, @NotNull Supplier<U> supplier,
                                           long delayTicks) {
        Promise<U> p = empty();
        return p.supplyDelayed(context, supplier, delayTicks);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier, after the delay has elapsed.
     *
     * @param context  the type of executor to use to supply the promise
     * @param supplier the value supplier
     * @param delay    the delay
     * @param unit     the unit of delay
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingDelayed(@NotNull ThreadContext context, @NotNull Supplier<U> supplier, long delay,
                                           @NotNull TimeUnit unit) {
        Promise<U> p = empty();
        return p.supplyDelayed(context, supplier, delay, unit);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier, after the delay has elapsed.
     *
     * @param supplier   the value supplier
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingDelayedSync(@NotNull Supplier<U> supplier, long delayTicks) {
        Promise<U> p = empty();
        return p.supplyDelayedSync(supplier, delayTicks);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier, after the delay has elapsed.
     *
     * @param supplier the value supplier
     * @param delay    the delay
     * @param unit     the unit of delay
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingDelayedSync(@NotNull Supplier<U> supplier, long delay, @NotNull TimeUnit unit) {
        Promise<U> p = empty();
        return p.supplyDelayedSync(supplier, delay, unit);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier, after the delay has elapsed.
     *
     * @param supplier   the value supplier
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingDelayedAsync(@NotNull Supplier<U> supplier, long delayTicks) {
        Promise<U> p = empty();
        return p.supplyDelayedAsync(supplier, delayTicks);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given supplier, after the delay has elapsed.
     *
     * @param supplier the value supplier
     * @param delay    the delay
     * @param unit     the unit of delay
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingDelayedAsync(@NotNull Supplier<U> supplier, long delay, @NotNull TimeUnit unit) {
        Promise<U> p = empty();
        return p.supplyDelayedAsync(supplier, delay, unit);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable.
     *
     * @param context  the type of executor to use to supply the promise
     * @param callable the value callable
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionally(@NotNull ThreadContext context, @NotNull Callable<U> callable) {
        Promise<U> p = empty();
        return p.supplyExceptionally(context, callable);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable.
     *
     * @param callable the value callable
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionallySync(@NotNull Callable<U> callable) {
        Promise<U> p = empty();
        return p.supplyExceptionallySync(callable);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable.
     *
     * @param callable the value callable
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionallyAsync(@NotNull Callable<U> callable) {
        Promise<U> p = empty();
        return p.supplyExceptionallyAsync(callable);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable, after the delay has elapsed.
     *
     * @param context    the type of executor to use to supply the promise
     * @param callable   the value callable
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayed(@NotNull ThreadContext context, @NotNull Callable<U> callable,
                                                        long delayTicks) {
        Promise<U> p = empty();
        return p.supplyExceptionallyDelayed(context, callable, delayTicks);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable, after the delay has elapsed.
     *
     * @param context  the type of executor to use to supply the promise
     * @param callable the value callable
     * @param delay    the delay
     * @param unit     the unit of delay
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayed(@NotNull ThreadContext context, @NotNull Callable<U> callable,
                                                        long delay, @NotNull TimeUnit unit) {
        Promise<U> p = empty();
        return p.supplyExceptionallyDelayed(context, callable, delay, unit);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable, after the delay has elapsed.
     *
     * @param callable   the value callable
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedSync(@NotNull Callable<U> callable, long delayTicks) {
        Promise<U> p = empty();
        return p.supplyExceptionallyDelayedSync(callable, delayTicks);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable, after the delay has elapsed.
     *
     * @param callable the value callable
     * @param delay    the delay
     * @param unit     the unit of delay
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedSync(@NotNull Callable<U> callable, long delay,
                                                            @NotNull TimeUnit unit) {
        Promise<U> p = empty();
        return p.supplyExceptionallyDelayedSync(callable, delay, unit);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable, after the delay has elapsed.
     *
     * @param callable   the value callable
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedAsync(@NotNull Callable<U> callable, long delayTicks) {
        Promise<U> p = empty();
        return p.supplyExceptionallyDelayedAsync(callable, delayTicks);
    }

    /**
     * Returns a new Promise, and schedules it's population via the given callable, after the delay has elapsed.
     *
     * @param callable the value callable
     * @param delay    the delay
     * @param unit     the unit of delay
     * @param <U>      the result type
     * @return the promise
     */
    @NotNull
    static <U> Promise<U> supplyingExceptionallyDelayedAsync(@NotNull Callable<U> callable, long delay,
                                                             @NotNull TimeUnit unit) {
        Promise<U> p = empty();
        return p.supplyExceptionallyDelayedAsync(callable, delay, unit);
    }

    /**
     * Attempts to cancel execution of this task.
     *
     * @return {@code false} if the task could not be cancelled, typically because it has already completed normally;
     * {@code true} otherwise
     */
    default boolean cancel() {
        return cancel(true);
    }

    /**
     * Returns the result value when complete, or throws an (unchecked) exception if completed exceptionally.
     *
     * <p>To better conform with the use of common functional forms, if a
     * computation involved in the completion of this Promise threw an exception, this method throws an (unchecked)
     * {@link CompletionException} with the underlying exception as its cause.</p>
     *
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed exceptionally or a completion computation threw an
     *                               exception
     */
    V join();

    /**
     * Returns the result value (or throws any encountered exception) if completed, else returns the given
     * valueIfAbsent.
     *
     * @param valueIfAbsent the value to return if not completed
     * @return the result value, if completed, else the given valueIfAbsent
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed exceptionally or a completion computation threw an
     *                               exception
     */
    V getNow(V valueIfAbsent);

    /**
     * Supplies the Promise's result.
     *
     * @param value the object to pass to the promise
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supply(@Nullable V value);

    /**
     * Supplies an exceptional result to the Promise.
     *
     * @param exception the exception to supply
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyException(@NotNull Throwable exception);

    /**
     * Schedules the supply of the Promise's result, via the given supplier.
     *
     * @param context  the type of executor to use to supply the promise
     * @param supplier the supplier
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    default Promise<V> supply(@NotNull ThreadContext context, @NotNull Supplier<V> supplier) {
        return switch (context) {
            case SYNC -> supplySync(supplier);
            case ASYNC -> supplyAsync(supplier);
        };
    }

    /**
     * Schedules the supply of the Promise's result, via the given supplier.
     *
     * @param supplier the supplier
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplySync(@NotNull Supplier<V> supplier);

    /**
     * Schedules the supply of the Promise's result, via the given supplier.
     *
     * @param supplier the supplier
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyAsync(@NotNull Supplier<V> supplier);

    /**
     * Schedules the supply of the Promise's result, via the given supplier, after the delay has elapsed.
     *
     * @param context    the type of executor to use to supply the promise
     * @param supplier   the supplier
     * @param delayTicks the delay in ticks
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    default Promise<V> supplyDelayed(@NotNull ThreadContext context, @NotNull Supplier<V> supplier, long delayTicks) {
        return switch (context) {
            case SYNC -> supplyDelayedSync(supplier, delayTicks);
            case ASYNC -> supplyDelayedAsync(supplier, delayTicks);
        };
    }

    /**
     * Schedules the supply of the Promise's result, via the given supplier, after the delay has elapsed.
     *
     * @param context  the type of executor to use to supply the promise
     * @param supplier the supplier
     * @param delay    the delay
     * @param unit     the unit of delay
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    default Promise<V> supplyDelayed(@NotNull ThreadContext context, @NotNull Supplier<V> supplier, long delay,
                                     @NotNull TimeUnit unit) {
        return switch (context) {
            case SYNC -> supplyDelayedSync(supplier, delay, unit);
            case ASYNC -> supplyDelayedAsync(supplier, delay, unit);
        };
    }

    /**
     * Schedules the supply of the Promise's result, via the given supplier, after the delay has elapsed.
     *
     * @param supplier   the supplier
     * @param delayTicks the delay in ticks
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyDelayedSync(@NotNull Supplier<V> supplier, long delayTicks);

    /**
     * Schedules the supply of the Promise's result, via the given supplier, after the delay has elapsed.
     *
     * @param supplier the supplier
     * @param delay    the delay
     * @param unit     the unit of delay
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyDelayedSync(@NotNull Supplier<V> supplier, long delay, @NotNull TimeUnit unit);

    /**
     * Schedules the supply of the Promise's result, via the given supplier, after the delay has elapsed.
     *
     * @param supplier   the supplier
     * @param delayTicks the delay in ticks
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyDelayedAsync(@NotNull Supplier<V> supplier, long delayTicks);

    /**
     * Schedules the supply of the Promise's result, via the given supplier, after the delay has elapsed.
     *
     * @param supplier the supplier
     * @param delay    the delay
     * @param unit     the unit of delay
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyDelayedAsync(@NotNull Supplier<V> supplier, long delay, @NotNull TimeUnit unit);

    /**
     * Schedules the supply of the Promise's result, via the given callable.
     *
     * @param context  the type of executor to use to supply the promise
     * @param callable the callable
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    default Promise<V> supplyExceptionally(@NotNull ThreadContext context, @NotNull Callable<V> callable) {
        return switch (context) {
            case SYNC -> supplyExceptionallySync(callable);
            case ASYNC -> supplyExceptionallyAsync(callable);
        };
    }

    /**
     * Schedules the supply of the Promise's result, via the given callable.
     *
     * @param callable the callable
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyExceptionallySync(@NotNull Callable<V> callable);

    /**
     * Schedules the supply of the Promise's result, via the given callable.
     *
     * @param callable the callable
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyExceptionallyAsync(@NotNull Callable<V> callable);

    /**
     * Schedules the supply of the Promise's result, via the given callable, after the delay has elapsed.
     *
     * @param context    the type of executor to use to supply the promise
     * @param callable   the callable
     * @param delayTicks the delay in ticks
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    default Promise<V> supplyExceptionallyDelayed(@NotNull ThreadContext context, @NotNull Callable<V> callable,
                                                  long delayTicks) {
        return switch (context) {
            case SYNC -> supplyExceptionallyDelayedSync(callable, delayTicks);
            case ASYNC -> supplyExceptionallyDelayedAsync(callable, delayTicks);
        };
    }

    /**
     * Schedules the supply of the Promise's result, via the given callable, after the delay has elapsed.
     *
     * @param context  the type of executor to use to supply the promise
     * @param callable the callable
     * @param delay    the delay
     * @param unit     the unit of delay
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    default Promise<V> supplyExceptionallyDelayed(@NotNull ThreadContext context, @NotNull Callable<V> callable,
                                                  long delay, @NotNull TimeUnit unit) {
        return switch (context) {
            case SYNC -> supplyExceptionallyDelayedSync(callable, delay, unit);
            case ASYNC -> supplyExceptionallyDelayedAsync(callable, delay, unit);
        };
    }

    /**
     * Schedules the supply of the Promise's result, via the given callable, after the delay has elapsed.
     *
     * @param callable   the callable
     * @param delayTicks the delay in ticks
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyExceptionallyDelayedSync(@NotNull Callable<V> callable, long delayTicks);

    /**
     * Schedules the supply of the Promise's result, via the given callable, after the delay has elapsed.
     *
     * @param callable the callable
     * @param delay    the delay
     * @param unit     the unit of delay
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyExceptionallyDelayedSync(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit);

    /**
     * Schedules the supply of the Promise's result, via the given callable, after the delay has elapsed.
     *
     * @param callable   the callable
     * @param delayTicks the delay in ticks
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyExceptionallyDelayedAsync(@NotNull Callable<V> callable, long delayTicks);

    /**
     * Schedules the supply of the Promise's result, via the given callable, after the delay has elapsed.
     *
     * @param callable the callable
     * @param delay    the delay
     * @param unit     the unit of delay
     * @return the same promise
     * @throws IllegalStateException if the promise is already being supplied, or has already been completed.
     */
    @NotNull
    Promise<V> supplyExceptionallyDelayedAsync(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function.
     *
     * @param context the type of executor to use to supply the promise
     * @param fn      the function to use to compute the value
     * @param <U>     the result type
     * @return the new promise
     */
    @NotNull
    default <U> Promise<U> thenApply(@NotNull ThreadContext context, @NotNull Function<? super V, ? extends U> fn) {
        return switch (context) {
            case SYNC -> thenApplySync(fn);
            case ASYNC -> thenApplyAsync(fn);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function.
     *
     * @param fn  the function to use to compute the value
     * @param <U> the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenApplySync(@NotNull Function<? super V, ? extends U> fn);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function.
     *
     * @param fn  the function to use to compute the value
     * @param <U> the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenApplyAsync(@NotNull Function<? super V, ? extends U> fn);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param context    the type of executor to use to supply the promise
     * @param fn         the function to use to compute the value
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the new promise
     */
    @NotNull
    default <U> Promise<U> thenApplyDelayed(@NotNull ThreadContext context,
                                            @NotNull Function<? super V, ? extends U> fn, long delayTicks) {
        return switch (context) {
            case SYNC -> thenApplyDelayedSync(fn, delayTicks);
            case ASYNC -> thenApplyDelayedAsync(fn, delayTicks);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param context the type of executor to use to supply the promise
     * @param fn      the function to use to compute the value
     * @param delay   the delay
     * @param unit    the unit of delay
     * @param <U>     the result type
     * @return the new promise
     */
    @NotNull
    default <U> Promise<U> thenApplyDelayed(@NotNull ThreadContext context,
                                            @NotNull Function<? super V, ? extends U> fn, long delay,
                                            @NotNull TimeUnit unit) {
        return switch (context) {
            case SYNC -> thenApplyDelayedSync(fn, delay, unit);
            case ASYNC -> thenApplyDelayedAsync(fn, delay, unit);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param fn         the function to use to compute the value
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenApplyDelayedSync(@NotNull Function<? super V, ? extends U> fn, long delayTicks);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param fn    the function to use to compute the value
     * @param delay the delay
     * @param unit  the unit of delay
     * @param <U>   the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenApplyDelayedSync(@NotNull Function<? super V, ? extends U> fn, long delay,
                                        @NotNull TimeUnit unit);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param fn         the function to use to compute the value
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenApplyDelayedAsync(@NotNull Function<? super V, ? extends U> fn, long delayTicks);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param fn    the function to use to compute the value
     * @param delay the delay
     * @param unit  the unit of delay
     * @param <U>   the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenApplyDelayedAsync(@NotNull Function<? super V, ? extends U> fn, long delay,
                                         @NotNull TimeUnit unit);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action.
     *
     * @param context the type of executor to use to supply the promise
     * @param action  the action to perform before completing the returned future
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAccept(@NotNull ThreadContext context, @NotNull Consumer<? super V> action) {
        return switch (context) {
            case SYNC -> thenAcceptSync(action);
            case ASYNC -> thenAcceptAsync(action);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action.
     *
     * @param action the action to perform before completing the returned future
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAcceptSync(@NotNull Consumer<? super V> action) {
        return thenApplySync(Delegates.consumerToFunction(action));
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action.
     *
     * @param action the action to perform before completing the returned future
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAcceptAsync(@NotNull Consumer<? super V> action) {
        return thenApplyAsync(Delegates.consumerToFunction(action));
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action, after the delay has elapsed.
     *
     * @param context    the type of executor to use to supply the promise
     * @param action     the action to perform before completing the returned future
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAcceptDelayed(@NotNull ThreadContext context, @NotNull Consumer<? super V> action,
                                            long delayTicks) {
        return switch (context) {
            case SYNC -> thenAcceptDelayedSync(action, delayTicks);
            case ASYNC -> thenAcceptDelayedAsync(action, delayTicks);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action, after the delay has elapsed.
     *
     * @param context the type of executor to use to supply the promise
     * @param action  the action to perform before completing the returned future
     * @param delay   the delay
     * @param unit    the unit of delay
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAcceptDelayed(@NotNull ThreadContext context, @NotNull Consumer<? super V> action,
                                            long delay, @NotNull TimeUnit unit) {
        return switch (context) {
            case SYNC -> thenAcceptDelayedSync(action, delay, unit);
            case ASYNC -> thenAcceptDelayedAsync(action, delay, unit);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action, after the delay has elapsed.
     *
     * @param action     the action to perform before completing the returned future
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAcceptDelayedSync(@NotNull Consumer<? super V> action, long delayTicks) {
        return thenApplyDelayedSync(Delegates.consumerToFunction(action), delayTicks);
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action, after the delay has elapsed.
     *
     * @param action the action to perform before completing the returned future
     * @param delay  the delay
     * @param unit   the unit of delay
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAcceptDelayedSync(@NotNull Consumer<? super V> action, long delay,
                                                @NotNull TimeUnit unit) {
        return thenApplyDelayedSync(Delegates.consumerToFunction(action), delay, unit);
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action, after the delay has elapsed.
     *
     * @param action     the action to perform before completing the returned future
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAcceptDelayedAsync(@NotNull Consumer<? super V> action, long delayTicks) {
        return thenApplyDelayedAsync(Delegates.consumerToFunction(action), delayTicks);
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given action, after the delay has elapsed.
     *
     * @param action the action to perform before completing the returned future
     * @param delay  the delay
     * @param unit   the unit of delay
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenAcceptDelayedAsync(@NotNull Consumer<? super V> action, long delay,
                                                 @NotNull TimeUnit unit) {
        return thenApplyDelayedAsync(Delegates.consumerToFunction(action), delay, unit);
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task.
     *
     * @param context the type of executor to use to supply the promise
     * @param action  the action to run before completing the returned future
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRun(@NotNull ThreadContext context, @NotNull Runnable action) {
        return switch (context) {
            case SYNC -> thenRunSync(action);
            case ASYNC -> thenRunAsync(action);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task.
     *
     * @param action the action to run before completing the returned future
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRunSync(@NotNull Runnable action) {
        return thenApplySync(Delegates.runnableToFunction(action));
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task.
     *
     * @param action the action to run before completing the returned future
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRunAsync(@NotNull Runnable action) {
        return thenApplyAsync(Delegates.runnableToFunction(action));
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task, after the delay has
     * elapsed.
     *
     * @param context    the type of executor to use to supply the promise
     * @param action     the action to run before completing the returned future
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRunDelayed(@NotNull ThreadContext context, @NotNull Runnable action, long delayTicks) {
        return switch (context) {
            case SYNC -> thenRunDelayedSync(action, delayTicks);
            case ASYNC -> thenRunDelayedAsync(action, delayTicks);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task, after the delay has
     * elapsed.
     *
     * @param context the type of executor to use to supply the promise
     * @param action  the action to run before completing the returned future
     * @param delay   the delay
     * @param unit    the unit of delay
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRunDelayed(@NotNull ThreadContext context, @NotNull Runnable action, long delay,
                                         @NotNull TimeUnit unit) {
        return switch (context) {
            case SYNC -> thenRunDelayedSync(action, delay, unit);
            case ASYNC -> thenRunDelayedAsync(action, delay, unit);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task, after the delay has
     * elapsed.
     *
     * @param action     the action to run before completing the returned future
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRunDelayedSync(@NotNull Runnable action, long delayTicks) {
        return thenApplyDelayedSync(Delegates.runnableToFunction(action), delayTicks);
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task, after the delay has
     * elapsed.
     *
     * @param action the action to run before completing the returned future
     * @param delay  the delay
     * @param unit   the unit of delay
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRunDelayedSync(@NotNull Runnable action, long delay, @NotNull TimeUnit unit) {
        return thenApplyDelayedSync(Delegates.runnableToFunction(action), delay, unit);
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task, after the delay has
     * elapsed.
     *
     * @param action     the action to run before completing the returned future
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRunDelayedAsync(@NotNull Runnable action, long delayTicks) {
        return thenApplyDelayedAsync(Delegates.runnableToFunction(action), delayTicks);
    }

    /**
     * Returns a new Promise that, when this promise completes normally, executes the given task, after the delay has
     * elapsed.
     *
     * @param action the action to run before completing the returned future
     * @param delay  the delay
     * @param unit   the unit of delay
     * @return the new promise
     */
    @NotNull
    default Promise<Void> thenRunDelayedAsync(@NotNull Runnable action, long delay, @NotNull TimeUnit unit) {
        return thenApplyDelayedAsync(Delegates.runnableToFunction(action), delay, unit);
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function.
     *
     * @param context the type of executor to use to supply the promise
     * @param fn      the function to use to compute the value
     * @param <U>     the result type
     * @return the new promise
     */
    @NotNull
    default <U> Promise<U> thenCompose(@NotNull ThreadContext context,
                                       @NotNull Function<? super V, ? extends Promise<U>> fn) {
        return switch (context) {
            case SYNC -> thenComposeSync(fn);
            case ASYNC -> thenComposeAsync(fn);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function.
     *
     * @param fn  the function to use to compute the value
     * @param <U> the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenComposeSync(@NotNull Function<? super V, ? extends Promise<U>> fn);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function.
     *
     * @param fn  the function to use to compute the value
     * @param <U> the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenComposeAsync(@NotNull Function<? super V, ? extends Promise<U>> fn);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param context    the type of executor to use to supply the promise
     * @param fn         the function to use to compute the value
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the new promise
     */
    @NotNull
    default <U> Promise<U> thenComposeDelayedSync(@NotNull ThreadContext context,
                                                  @NotNull Function<? super V, ? extends Promise<U>> fn,
                                                  long delayTicks) {
        return switch (context) {
            case SYNC -> thenComposeDelayedSync(fn, delayTicks);
            case ASYNC -> thenComposeDelayedAsync(fn, delayTicks);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param context the type of executor to use to supply the promise
     * @param fn      the function to use to compute the value
     * @param delay   the delay
     * @param unit    the unit of delay
     * @param <U>     the result type
     * @return the new promise
     */
    @NotNull
    default <U> Promise<U> thenComposeDelayedSync(@NotNull ThreadContext context,
                                                  @NotNull Function<? super V, ? extends Promise<U>> fn, long delay,
                                                  @NotNull TimeUnit unit) {
        return switch (context) {
            case SYNC -> thenComposeDelayedSync(fn, delay, unit);
            case ASYNC -> thenComposeDelayedAsync(fn, delay, unit);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param fn         the function to use to compute the value
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenComposeDelayedSync(@NotNull Function<? super V, ? extends Promise<U>> fn, long delayTicks);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param fn    the function to use to compute the value
     * @param delay the delay
     * @param unit  the unit of delay
     * @param <U>   the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenComposeDelayedSync(@NotNull Function<? super V, ? extends Promise<U>> fn, long delay,
                                          @NotNull TimeUnit unit);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param fn         the function to use to compute the value
     * @param delayTicks the delay in ticks
     * @param <U>        the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenComposeDelayedAsync(@NotNull Function<? super V, ? extends Promise<U>> fn, long delayTicks);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's result as the
     * argument to the given function, after the delay has elapsed.
     *
     * @param fn    the function to use to compute the value
     * @param delay the delay
     * @param unit  the unit of delay
     * @param <U>   the result type
     * @return the new promise
     */
    @NotNull
    <U> Promise<U> thenComposeDelayedAsync(@NotNull Function<? super V, ? extends Promise<U>> fn, long delay,
                                           @NotNull TimeUnit unit);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function. Otherwise, if this promise completes normally, then the returned promise also
     * completes normally with the same value.
     *
     * @param context the type of executor to use to supply the promise
     * @param fn      the function to use to compute the value of the returned Promise, if this promise completed
     *                exceptionally
     * @return the new promise
     */
    @NotNull
    default Promise<V> exceptionally(@NotNull ThreadContext context, @NotNull Function<Throwable, ? extends V> fn) {
        return switch (context) {
            case SYNC -> exceptionallySync(fn);
            case ASYNC -> exceptionallyAsync(fn);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function. Otherwise, if this promise completes normally, then the returned promise also
     * completes normally with the same value.
     *
     * @param fn the function to use to compute the value of the returned Promise, if this promise completed
     *           exceptionally
     * @return the new promise
     */
    @NotNull
    Promise<V> exceptionallySync(@NotNull Function<Throwable, ? extends V> fn);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function. Otherwise, if this promise completes normally, then the returned promise also
     * completes normally with the same value.
     *
     * @param fn the function to use to compute the value of the returned Promise, if this promise completed
     *           exceptionally
     * @return the new promise
     */
    @NotNull
    Promise<V> exceptionallyAsync(@NotNull Function<Throwable, ? extends V> fn);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function, after the delay has elapsed. Otherwise, if this promise completes normally,
     * then the returned promise also completes normally with the same value.
     *
     * @param context    the type of executor to use to supply the promise
     * @param fn         the function to use to compute the value of the returned Promise, if this promise completed
     *                   exceptionally
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    default Promise<V> exceptionallyDelayed(@NotNull ThreadContext context,
                                            @NotNull Function<Throwable, ? extends V> fn, long delayTicks) {
        return switch (context) {
            case SYNC -> exceptionallyDelayedSync(fn, delayTicks);
            case ASYNC -> exceptionallyDelayedAsync(fn, delayTicks);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function, after the delay has elapsed. Otherwise, if this promise completes normally,
     * then the returned promise also completes normally with the same value.
     *
     * @param context the type of executor to use to supply the promise
     * @param fn      the function to use to compute the value of the returned Promise, if this promise completed
     *                exceptionally
     * @param delay   the delay
     * @param unit    the unit of delay
     * @return the new promise
     */
    @NotNull
    default Promise<V> exceptionallyDelayed(@NotNull ThreadContext context,
                                            @NotNull Function<Throwable, ? extends V> fn, long delay,
                                            @NotNull TimeUnit unit) {
        return switch (context) {
            case SYNC -> exceptionallyDelayedSync(fn, delay, unit);
            case ASYNC -> exceptionallyDelayedAsync(fn, delay, unit);
        };
    }

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function, after the delay has elapsed. Otherwise, if this promise completes normally,
     * then the returned promise also completes normally with the same value.
     *
     * @param fn         the function to use to compute the value of the returned Promise, if this promise completed
     *                   exceptionally
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    Promise<V> exceptionallyDelayedSync(@NotNull Function<Throwable, ? extends V> fn, long delayTicks);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function, after the delay has elapsed. Otherwise, if this promise completes normally,
     * then the returned promise also completes normally with the same value.
     *
     * @param fn    the function to use to compute the value of the returned Promise, if this promise completed
     *              exceptionally
     * @param delay the delay
     * @param unit  the unit of delay
     * @return the new promise
     */
    @NotNull
    Promise<V> exceptionallyDelayedSync(@NotNull Function<Throwable, ? extends V> fn, long delay,
                                        @NotNull TimeUnit unit);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function, after the delay has elapsed. Otherwise, if this promise completes normally,
     * then the returned promise also completes normally with the same value.
     *
     * @param fn         the function to use to compute the value of the returned Promise, if this promise completed
     *                   exceptionally
     * @param delayTicks the delay in ticks
     * @return the new promise
     */
    @NotNull
    Promise<V> exceptionallyDelayedAsync(@NotNull Function<Throwable, ? extends V> fn, long delayTicks);

    /**
     * Returns a new Promise that, when this promise completes normally, is executed with this promise's exception as
     * the argument to the given function, after the delay has elapsed. Otherwise, if this promise completes normally,
     * then the returned promise also completes normally with the same value.
     *
     * @param fn    the function to use to compute the value of the returned Promise, if this promise completed
     *              exceptionally
     * @param delay the delay
     * @param unit  the unit of delay
     * @return the new promise
     */
    @NotNull
    Promise<V> exceptionallyDelayedAsync(@NotNull Function<Throwable, ? extends V> fn, long delay,
                                         @NotNull TimeUnit unit);


    /**
     * Returns a {@link CompletableFuture} maintaining the same completion properties as this Promise.
     * <p>
     * A Promise implementation that does not choose to interoperate with CompletableFutures may throw
     * {@code UnsupportedOperationException}.
     *
     * @return the CompletableFuture
     * @throws UnsupportedOperationException if this implementation does not interoperate with CompletableFuture
     */
    CompletableFuture<V> toCompletableFuture();

}