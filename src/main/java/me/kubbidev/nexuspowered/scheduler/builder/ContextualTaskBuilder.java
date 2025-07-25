package me.kubbidev.nexuspowered.scheduler.builder;

import java.util.function.Consumer;
import me.kubbidev.nexuspowered.scheduler.Scheduler;
import me.kubbidev.nexuspowered.scheduler.Task;
import org.jetbrains.annotations.NotNull;

/**
 * Queues execution of tasks using {@link Scheduler}, often combining parameters with variables already known by this
 * instance.
 */
public interface ContextualTaskBuilder {

    @NotNull
    Task consume(@NotNull Consumer<Task> consumer);

    @NotNull
    Task run(@NotNull Runnable runnable);
}