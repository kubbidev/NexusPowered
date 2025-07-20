package me.kubbidev.nexuspowered.command;

import java.util.List;
import me.kubbidev.nexuspowered.command.context.CommandContext;
import me.kubbidev.nexuspowered.terminable.Terminable;
import me.kubbidev.nexuspowered.terminable.TerminableConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a command
 */
public interface Command extends Terminable {

    /**
     * Registers this command with the server, via the given plugin instance.
     *
     * @param aliases the aliases for the command
     */
    void register(@NotNull String... aliases);

    /**
     * Registers this command with the server, via the given plugin instance, and then binds it with the composite
     * terminable.
     *
     * @param consumer the terminable consumer to bind with
     * @param aliases  the aliases for the command
     */
    default void registerAndBind(@NotNull TerminableConsumer consumer, @NotNull String... aliases) {
        this.register(aliases);
        this.bindWith(consumer);
    }

    /**
     * Calls the command handler.
     *
     * @param context the contexts for the command
     */
    void call(@NotNull CommandContext<?> context) throws CommandInterruptException;

    /**
     * Calls the command tab completer.
     *
     * @param context the contexts for the command
     * @return a {@link List} with the completions
     */
    @Nullable List<String> callTabCompleter(@NotNull CommandContext<?> context) throws CommandInterruptException;
}