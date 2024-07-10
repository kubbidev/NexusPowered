package me.kubbidev.nexuspowered.scheduler.builder;

import me.kubbidev.nexuspowered.promise.Promise;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Builds instances of {@link Promise}, often combining parameters with
 * variables already known by this instance.
 */
public interface ContextualPromiseBuilder {

    @NotNull
    <T> Promise<T> supply(@NotNull Supplier<T> supplier);

    @NotNull
    <T> Promise<T> call(@NotNull Callable<T> callable);

    @NotNull
    Promise<Void> run(@NotNull Runnable runnable);

}