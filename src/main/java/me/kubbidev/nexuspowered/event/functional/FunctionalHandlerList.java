package me.kubbidev.nexuspowered.event.functional;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A functional builder which accumulates event handlers
 *
 * @param <T> the handled type
 * @param <R> the resultant subscription type
 */
public interface FunctionalHandlerList<T, R> {

    /**
     * Add a {@link Consumer} handler.
     *
     * @param handler the handler
     * @return this handler list
     */
    @NotNull
    FunctionalHandlerList<T, R> consumer(@NotNull Consumer<? super T> handler);

    /**
     * Add a {@link BiConsumer} handler.
     *
     * @param handler the handler
     * @return this handler list
     */
    @NotNull
    FunctionalHandlerList<T, R> biConsumer(@NotNull BiConsumer<R, ? super T> handler);

    /**
     * Builds and registers the Handler.
     *
     * @return a registered {@link R} instance.
     * @throws IllegalStateException if no handlers have been registered
     */
    @NotNull
    R register();

}