package me.kubbidev.nexuspowered.terminable.module;

import me.kubbidev.nexuspowered.terminable.Terminable;
import me.kubbidev.nexuspowered.terminable.TerminableConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * A terminable module is a class which manipulates and constructs a number
 * of {@link Terminable}s.
 */
public interface TerminableModule {

    /**
     * Performs the tasks to setup this module
     *
     * @param consumer the terminable consumer
     */
    void setup(@NotNull TerminableConsumer consumer);

    /**
     * Registers this terminable with a terminable consumer
     *
     * @param consumer the terminable consumer
     */
    default void bindModuleWith(@NotNull TerminableConsumer consumer) {
        consumer.bindModule(this);
    }

}