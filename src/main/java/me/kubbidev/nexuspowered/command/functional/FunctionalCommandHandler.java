package me.kubbidev.nexuspowered.command.functional;

import me.kubbidev.nexuspowered.command.Command;
import me.kubbidev.nexuspowered.command.CommandInterruptException;
import me.kubbidev.nexuspowered.command.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNullByDefault;

/**
 * Represents a handler for a {@link Command}.
 *
 * @param <T> the sender type
 */
@FunctionalInterface
@NotNullByDefault
public interface FunctionalCommandHandler<T extends CommandSender> {

    /**
     * Executes the handler using the given command context.
     *
     * @param context the command context
     */
    void handle(CommandContext<T> context) throws CommandInterruptException;
}