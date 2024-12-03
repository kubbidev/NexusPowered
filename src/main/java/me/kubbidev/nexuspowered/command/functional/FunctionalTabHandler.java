package me.kubbidev.nexuspowered.command.functional;

import me.kubbidev.nexuspowered.command.CommandInterruptException;
import me.kubbidev.nexuspowered.command.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface FunctionalTabHandler<T extends CommandSender> {

    /**
     * Executes the tab completer using the given command context and returns the completions.
     *
     * @param context the command context
     * @return a {@link List} with the completions
     */
    @Nullable
    List<String> handle(CommandContext<T> context) throws CommandInterruptException;

}