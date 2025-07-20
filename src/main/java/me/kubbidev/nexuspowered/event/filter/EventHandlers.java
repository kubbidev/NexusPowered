package me.kubbidev.nexuspowered.event.filter;

import java.util.function.Consumer;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Defines standard event predicates for use in functional event handlers.
 */
@SuppressWarnings("unchecked")
public final class EventHandlers {

    private static final Consumer<? extends Cancellable> SET_CANCELLED   = e -> e.setCancelled(true);
    private static final Consumer<? extends Cancellable> UNSET_CANCELLED = e -> e.setCancelled(false);

    private EventHandlers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Returns a consumer which cancels the event
     *
     * @param <T> the event type
     * @return a consumer which cancels the event
     */
    public static @NotNull <T extends Cancellable> Consumer<T> cancel() {
        return (Consumer<T>) SET_CANCELLED;
    }

    /**
     * Returns a consumer which un-cancels the event
     *
     * @param <T> the event type
     * @return a consumer which un-cancels the event
     */
    public static @NotNull <T extends Cancellable> Consumer<T> uncancel() {
        return (Consumer<T>) UNSET_CANCELLED;
    }

}