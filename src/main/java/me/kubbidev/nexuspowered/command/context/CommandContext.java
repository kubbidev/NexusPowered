package me.kubbidev.nexuspowered.command.context;

import com.google.common.collect.ImmutableList;
import me.kubbidev.nexuspowered.command.argument.Argument;
import me.kubbidev.nexuspowered.util.Players;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the context for a given command execution.
 *
 * @param <T> the sender type
 */
public interface CommandContext<T extends CommandSender> {

    /**
     * Gets the sender who executed the command.
     *
     * @return the sender who executed the command
     */
    @NotNull
    T sender();

    /**
     * Sends a message to the {@link #sender()}.
     *
     * @param message the message to send
     */
    default void reply(String... message) {
        Players.msg(sender(), message);
    }

    /**
     * Gets an immutable list of the supplied arguments.
     *
     * @return an immutable list of the supplied arguments
     */
    @NotNull
    ImmutableList<String> args();

    /**
     * Gets the argument at a the given index.
     *
     * @param index the index
     * @return the argument
     */
    @NotNull
    Argument arg(int index);

    /**
     * Gets the argument at the given index.
     * Returns null if no argument is present at that index.
     *
     * @param index the index
     * @return the argument, or null if one was not present
     */
    @Nullable
    String rawArg(int index);

    /**
     * Gets the command label which was used to execute this command.
     *
     * @return the command label which was used to execute this command
     */
    @NotNull
    String label();

    /**
     * Gets the aliases of the command.
     *
     * @return the aliases of the command
     */
    @NotNull
    ImmutableList<String> aliases();
}