package me.kubbidev.nexuspowered.internal.exception.event;

import com.google.common.base.Preconditions;
import me.kubbidev.nexuspowered.internal.exception.InternalException;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when an internal exception occurs.
 */
public class NexusExceptionEvent extends Event {

    private static final HandlerList       HANDLER_LIST = new HandlerList();
    private final        InternalException exception;

    public NexusExceptionEvent(InternalException exception) {
        super(!Bukkit.isPrimaryThread());
        this.exception = Preconditions.checkNotNull(exception, "exception");
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public InternalException getException() {
        return this.exception;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}