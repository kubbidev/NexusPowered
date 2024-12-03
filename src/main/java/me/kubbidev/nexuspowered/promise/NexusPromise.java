package me.kubbidev.nexuspowered.promise;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import me.kubbidev.nexuspowered.interfaces.Delegate;
import me.kubbidev.nexuspowered.internal.LoaderUtils;
import me.kubbidev.nexuspowered.internal.exception.NexusExceptions;
import me.kubbidev.nexuspowered.scheduler.NexusExecutors;
import me.kubbidev.nexuspowered.scheduler.Ticks;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of {@link Promise} using the server scheduler.
 *
 * @param <V> the result type
 */
final class NexusPromise<V> implements Promise<V> {

    @NotNull
    static <U> NexusPromise<U> empty() {
        return new NexusPromise<>();
    }

    @NotNull
    static <U> NexusPromise<U> completed(@Nullable U value) {
        return new NexusPromise<>(value);
    }

    @NotNull
    static <U> NexusPromise<U> exceptionally(@NotNull Throwable t) {
        return new NexusPromise<>(t);
    }

    @NotNull
    static <U> Promise<U> wrapFuture(@NotNull Future<U> future) {
        if (future instanceof CompletableFuture<?>) {
            return new NexusPromise<>(((CompletableFuture<U>) future).thenApply(Function.identity()));

        } else if (future instanceof CompletionStage<?>) {
            @SuppressWarnings("unchecked")
            CompletionStage<U> fut = (CompletionStage<U>) future;
            return new NexusPromise<>(fut.toCompletableFuture().thenApply(Function.identity()));

        } else if (future instanceof ListenableFuture<?>) {
            ListenableFuture<U> fut = (ListenableFuture<U>) future;
            NexusPromise<U> promise = empty();
            promise.supplied.set(true);

            Futures.addCallback(fut, new FutureCallback<U>() {
                @Override
                public void onSuccess(@Nullable U result) {
                    promise.complete(result);
                }

                @Override
                public void onFailure(@NotNull Throwable t) {
                    promise.completeExceptionally(t);
                }
            });

            return promise;
        }
        if (future.isDone()) {
            try {
                return completed(future.get());
            } catch (ExecutionException e) {
                return exceptionally(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Promise.supplyingExceptionallyAsync(future::get);
        }
    }

    /**
     * If the promise is currently being supplied
     */
    private final AtomicBoolean supplied = new AtomicBoolean(false);

    /**
     * If the execution of the promise is cancelled
     */
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    /**
     * The completable future backing this promise
     */
    @NotNull
    private final CompletableFuture<V> fut;

    private NexusPromise() {
        this.fut = new CompletableFuture<>();
    }

    private NexusPromise(@Nullable V v) {
        this.fut = CompletableFuture.completedFuture(v);
        this.supplied.set(true);
    }

    private NexusPromise(@NotNull Throwable t) {
        (this.fut = new CompletableFuture<>()).completeExceptionally(t);
        this.supplied.set(true);
    }

    private NexusPromise(@NotNull CompletableFuture<V> fut) {
        this.fut = Objects.requireNonNull(fut, "future");
        this.supplied.set(true);
        this.cancelled.set(fut.isCancelled());
    }

    /* utility methods */

    private void executeSync(@NotNull Runnable runnable) {
        if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
            NexusExceptions.wrapSchedulerTask(runnable).run();
        } else {
            NexusExecutors.sync().execute(runnable);
        }
    }

    private void executeAsync(@NotNull Runnable runnable) {
        NexusExecutors.asyncNexus().execute(runnable);
    }

    private void executeDelayedSync(@NotNull Runnable runnable, long delayTicks) {
        if (delayTicks <= 0) {
            executeSync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLater(LoaderUtils.getPlugin(), NexusExceptions.wrapSchedulerTask(runnable), delayTicks);
        }
    }

    private void executeDelayedAsync(@NotNull Runnable runnable, long delayTicks) {
        if (delayTicks <= 0) {
            executeAsync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(LoaderUtils.getPlugin(), NexusExceptions.wrapSchedulerTask(runnable), delayTicks);
        }
    }

    private void executeDelayedSync(@NotNull Runnable runnable, long delay, TimeUnit unit) {
        if (delay <= 0) {
            executeSync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLater(LoaderUtils.getPlugin(), NexusExceptions.wrapSchedulerTask(runnable), Ticks.from(delay, unit));
        }
    }

    private void executeDelayedAsync(@NotNull Runnable runnable, long delay, TimeUnit unit) {
        if (delay <= 0) {
            executeAsync(runnable);
        } else {
            NexusExecutors.asyncNexus().schedule(NexusExceptions.wrapSchedulerTask(runnable), delay, unit);
        }
    }

    private boolean complete(V value) {
        return !this.cancelled.get() && this.fut.complete(value);
    }

    private boolean completeExceptionally(@NotNull Throwable t) {
        return !this.cancelled.get() && this.fut.completeExceptionally(t);
    }

    private void markAsSupplied() {
        if (!this.supplied.compareAndSet(false, true)) {
            throw new IllegalStateException("Promise is already being supplied.");
        }
    }

    /* future methods */

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelled.set(true);
        return this.fut.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.fut.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.fut.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.fut.get();
    }

    @Override
    public V get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.fut.get(timeout, unit);
    }

    @Override
    public V join() {
        return this.fut.join();
    }

    @Override
    public V getNow(V valueIfAbsent) {
        return this.fut.getNow(valueIfAbsent);
    }

    @Override
    public CompletableFuture<V> toCompletableFuture() {
        return this.fut.thenApply(Function.identity());
    }

    @Override
    public void close() {
        cancel();
    }

    @Override
    public boolean isClosed() {
        return isCancelled();
    }

    /* implementation */

    @NotNull
    @Override
    public Promise<V> supply(@Nullable V value) {
        markAsSupplied();
        complete(value);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyException(@NotNull Throwable exception) {
        markAsSupplied();
        completeExceptionally(exception);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplySync(@NotNull Supplier<V> supplier) {
        markAsSupplied();
        executeSync(new SupplyRunnable(supplier));
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyAsync(@NotNull Supplier<V> supplier) {
        markAsSupplied();
        executeAsync(new SupplyRunnable(supplier));
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyDelayedSync(@NotNull Supplier<V> supplier, long delayTicks) {
        markAsSupplied();
        executeDelayedSync(new SupplyRunnable(supplier), delayTicks);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyDelayedSync(@NotNull Supplier<V> supplier, long delay, @NotNull TimeUnit unit) {
        markAsSupplied();
        executeDelayedSync(new SupplyRunnable(supplier), delay, unit);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyDelayedAsync(@NotNull Supplier<V> supplier, long delayTicks) {
        markAsSupplied();
        executeDelayedAsync(new SupplyRunnable(supplier), delayTicks);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyDelayedAsync(@NotNull Supplier<V> supplier, long delay, @NotNull TimeUnit unit) {
        markAsSupplied();
        executeDelayedAsync(new SupplyRunnable(supplier), delay, unit);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyExceptionallySync(@NotNull Callable<V> callable) {
        markAsSupplied();
        executeSync(new ThrowingSupplyRunnable(callable));
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyExceptionallyAsync(@NotNull Callable<V> callable) {
        markAsSupplied();
        executeAsync(new ThrowingSupplyRunnable(callable));
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyExceptionallyDelayedSync(@NotNull Callable<V> callable, long delayTicks) {
        markAsSupplied();
        executeDelayedSync(new ThrowingSupplyRunnable(callable), delayTicks);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyExceptionallyDelayedSync(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit) {
        markAsSupplied();
        executeDelayedSync(new ThrowingSupplyRunnable(callable), delay, unit);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyExceptionallyDelayedAsync(@NotNull Callable<V> callable, long delayTicks) {
        markAsSupplied();
        executeDelayedAsync(new ThrowingSupplyRunnable(callable), delayTicks);
        return this;
    }

    @NotNull
    @Override
    public Promise<V> supplyExceptionallyDelayedAsync(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit) {
        markAsSupplied();
        executeDelayedAsync(new ThrowingSupplyRunnable(callable), delay, unit);
        return this;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenApplySync(@NotNull Function<? super V, ? extends U> fn) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeSync(new ApplyRunnable<>(promise, fn, value));
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenApplyAsync(@NotNull Function<? super V, ? extends U> fn) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeAsync(new ApplyRunnable<>(promise, fn, value));
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenApplyDelayedSync(@NotNull Function<? super V, ? extends U> fn, long delayTicks) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ApplyRunnable<>(promise, fn, value), delayTicks);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenApplyDelayedSync(@NotNull Function<? super V, ? extends U> fn, long delay, @NotNull TimeUnit unit) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ApplyRunnable<>(promise, fn, value), delay, unit);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenApplyDelayedAsync(@NotNull Function<? super V, ? extends U> fn, long delayTicks) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ApplyRunnable<>(promise, fn, value), delayTicks);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenApplyDelayedAsync(@NotNull Function<? super V, ? extends U> fn, long delay, @NotNull TimeUnit unit) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ApplyRunnable<>(promise, fn, value), delay, unit);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenComposeSync(@NotNull Function<? super V, ? extends Promise<U>> fn) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeSync(new ComposeRunnable<>(promise, fn, value, true));
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenComposeAsync(@NotNull Function<? super V, ? extends Promise<U>> fn) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeAsync(new ComposeRunnable<>(promise, fn, value, false));
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenComposeDelayedSync(@NotNull Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ComposeRunnable<>(promise, fn, value, true), delayTicks);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenComposeDelayedSync(@NotNull Function<? super V, ? extends Promise<U>> fn, long delay, @NotNull TimeUnit unit) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ComposeRunnable<>(promise, fn, value, true), delay, unit);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenComposeDelayedAsync(@NotNull Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ComposeRunnable<>(promise, fn, value, false), delayTicks);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public <U> Promise<U> thenComposeDelayedAsync(@NotNull Function<? super V, ? extends Promise<U>> fn, long delay, @NotNull TimeUnit unit) {
        NexusPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ComposeRunnable<>(promise, fn, value, false), delay, unit);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public Promise<V> exceptionallySync(@NotNull Function<Throwable, ? extends V> fn) {
        NexusPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeSync(new ExceptionallyRunnable<>(promise, fn, t));
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public Promise<V> exceptionallyAsync(@NotNull Function<Throwable, ? extends V> fn) {
        NexusPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeAsync(new ExceptionallyRunnable<>(promise, fn, t));
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public Promise<V> exceptionallyDelayedSync(@NotNull Function<Throwable, ? extends V> fn, long delayTicks) {
        NexusPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedSync(new ExceptionallyRunnable<>(promise, fn, t), delayTicks);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public Promise<V> exceptionallyDelayedSync(@NotNull Function<Throwable, ? extends V> fn, long delay, @NotNull TimeUnit unit) {
        NexusPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedSync(new ExceptionallyRunnable<>(promise, fn, t), delay, unit);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public Promise<V> exceptionallyDelayedAsync(@NotNull Function<Throwable, ? extends V> fn, long delayTicks) {
        NexusPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedAsync(new ExceptionallyRunnable<>(promise, fn, t), delayTicks);
            }
        });
        return promise;
    }

    @NotNull
    @Override
    public Promise<V> exceptionallyDelayedAsync(@NotNull Function<Throwable, ? extends V> fn, long delay, @NotNull TimeUnit unit) {
        NexusPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedAsync(new ExceptionallyRunnable<>(promise, fn, t), delay, unit);
            }
        });
        return promise;
    }

    /* delegating behaviour runnables */

    private final class ThrowingSupplyRunnable implements Runnable, Delegate<Callable<V>> {
        private final Callable<V> supplier;

        private ThrowingSupplyRunnable(Callable<V> supplier) {
            this.supplier = supplier;
        }

        public Callable<V> delegate() {
            return this.supplier;
        }

        @Override
        public void run() {
            if (NexusPromise.this.cancelled.get()) {
                return;
            }
            try {
                NexusPromise.this.fut.complete(this.supplier.call());
            } catch (Throwable t) {
                NexusExceptions.reportPromise(t);
                NexusPromise.this.fut.completeExceptionally(t);
            }
        }
    }

    private final class SupplyRunnable implements Runnable, Delegate<Supplier<V>> {
        private final Supplier<V> supplier;

        private SupplyRunnable(Supplier<V> supplier) {
            this.supplier = supplier;
        }

        public Supplier<V> delegate() {
            return this.supplier;
        }

        @Override
        public void run() {
            if (NexusPromise.this.cancelled.get()) {
                return;
            }
            try {
                NexusPromise.this.fut.complete(this.supplier.get());
            } catch (Throwable t) {
                NexusExceptions.reportPromise(t);
                NexusPromise.this.fut.completeExceptionally(t);
            }
        }
    }

    private final class ApplyRunnable<U> implements Runnable, Delegate<Function<? super V, ? extends U>> {
        private final NexusPromise<U> promise;
        private final Function<? super V, ? extends U> function;
        private final V value;

        private ApplyRunnable(NexusPromise<U> promise, Function<? super V, ? extends U> function, V value) {
            this.promise = promise;
            this.function = function;
            this.value = value;
        }

        public Function<? super V, ? extends U> delegate() {
            return this.function;
        }

        @Override
        public void run() {
            if (NexusPromise.this.cancelled.get()) {
                return;
            }
            try {
                this.promise.complete(this.function.apply(this.value));
            } catch (Throwable t) {
                NexusExceptions.reportPromise(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

    private final class ComposeRunnable<U> implements Runnable, Delegate<Function<? super V, ? extends Promise<U>>> {
        private final NexusPromise<U> promise;
        private final Function<? super V, ? extends Promise<U>> function;
        private final V value;
        private final boolean sync;

        private ComposeRunnable(NexusPromise<U> promise, Function<? super V, ? extends Promise<U>> function, V value, boolean sync) {
            this.promise = promise;
            this.function = function;
            this.value = value;
            this.sync = sync;
        }

        public Function<? super V, ? extends Promise<U>> delegate() {
            return this.function;
        }

        @Override
        public void run() {
            if (NexusPromise.this.cancelled.get()) {
                return;
            }
            try {
                Promise<U> p = this.function.apply(this.value);
                if (p == null) {
                    this.promise.complete(null);
                } else {
                    if (this.sync) {
                        p.thenAcceptSync(this.promise::complete);
                    } else {
                        p.thenAcceptAsync(this.promise::complete);
                    }
                }
            } catch (Throwable t) {
                NexusExceptions.reportPromise(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

    private final class ExceptionallyRunnable<U> implements Runnable, Delegate<Function<Throwable, ? extends U>> {
        private final NexusPromise<U> promise;
        private final Function<Throwable, ? extends U> function;
        private final Throwable t;

        private ExceptionallyRunnable(NexusPromise<U> promise, Function<Throwable, ? extends U> function, Throwable t) {
            this.promise = promise;
            this.function = function;
            this.t = t;
        }

        public Function<Throwable, ? extends U> delegate() {
            return this.function;
        }

        @Override
        public void run() {
            if (NexusPromise.this.cancelled.get()) {
                return;
            }
            try {
                this.promise.complete(this.function.apply(this.t));
            } catch (Throwable t) {
                NexusExceptions.reportPromise(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

}