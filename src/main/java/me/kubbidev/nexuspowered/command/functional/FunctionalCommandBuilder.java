package me.kubbidev.nexuspowered.command.functional;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import java.util.function.Predicate;
import me.kubbidev.nexuspowered.command.Command;
import me.kubbidev.nexuspowered.command.context.CommandContext;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * Functional builder API for {@link Command}
 *
 * @param <T> the sender type
 */
@NotNullByDefault
public interface FunctionalCommandBuilder<T extends CommandSender> {

    // Default failure messages
    Component DEFAULT_NOT_OP_MESSAGE           = text("Only server operators are able to use this command.", RED);
    Component DEFAULT_NOT_PLAYER_MESSAGE       = text("Only players are able to use this command.", RED);
    Component DEFAULT_NOT_CONSOLE_MESSAGE      = text("This command is only available through the server console.",
        RED);
    Component DEFAULT_INVALID_USAGE_MESSAGE    = text("Invalid usage. Try: {0}.", RED);
    Component DEFAULT_INVALID_ARGUMENT_MESSAGE = text("Invalid argument '{0}' at index {1}.", RED);
    Component DEFAULT_INVALID_SENDER_MESSAGE   = text("You are not able to use this command.", RED);

    static FunctionalCommandBuilder<CommandSender> newBuilder() {
        return new FunctionalCommandBuilderImpl<>();
    }

    /**
     * Asserts that the sender has the specified permission, and sends them the default failure message if they don't
     * have permission.
     *
     * @param permission the permission to check for
     * @return the builder instance
     */
    FunctionalCommandBuilder<T> assertPermission(String permission);

    /**
     * Sets the command description to the specified one.
     *
     * @param description the command description
     * @return the builder instance
     */
    FunctionalCommandBuilder<T> description(String description);

    /**
     * Asserts that some function returns true.
     *
     * @param test the test to run
     * @return the builder instance
     */
    default FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> test) {
        return this.assertFunction(test, null);
    }

    /**
     * Asserts that some function returns true.
     *
     * @param test           the test to run
     * @param failureMessage the failure message if the test fails
     * @return the builder instance
     */
    FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> test,
                                               @Nullable Component failureMessage);

    /**
     * Asserts that the sender is op, and sends them the default failure message if they're not.
     *
     * @return the builder instance
     */
    default FunctionalCommandBuilder<T> assertOp() {
        return this.assertOp(DEFAULT_NOT_OP_MESSAGE);
    }

    /**
     * Asserts that the sender is op, and sends them the failure message if they don't have permission.
     *
     * @param failureMessage the failure message to send if they're not op
     * @return the builder instance
     */
    FunctionalCommandBuilder<T> assertOp(Component failureMessage);

    /**
     * Asserts that the sender is instance of Player, and sends them the default failure message if they're not.
     *
     * @return the builder instance
     */
    default FunctionalCommandBuilder<Player> assertPlayer() {
        return this.assertPlayer(DEFAULT_NOT_PLAYER_MESSAGE);
    }

    /**
     * Asserts that the sender is instance of Player, and sends them the failure message if they're not.
     *
     * @param failureMessage the failure message to send if they're not a player
     * @return the builder instance
     */
    FunctionalCommandBuilder<Player> assertPlayer(Component failureMessage);

    /**
     * Asserts that the sender is instance of ConsoleCommandSender, and sends them the default failure message if
     * they're not.
     *
     * @return the builder instance
     */
    default FunctionalCommandBuilder<ConsoleCommandSender> assertConsole() {
        return this.assertConsole(DEFAULT_NOT_CONSOLE_MESSAGE);
    }

    /**
     * Asserts that the sender is instance of ConsoleCommandSender, and sends them the failure message if they're not
     *
     * @param failureMessage the failure message to send if they're not console
     * @return the builder instance
     */
    FunctionalCommandBuilder<ConsoleCommandSender> assertConsole(Component failureMessage);

    /**
     * Asserts that the arguments match the given usage string.
     * <p>
     * Arguments should be separated by a " " space. Optional arguments are denoted by wrapping the argument name in
     * square quotes "[ ]"
     * <p>
     * The default failure message is sent if they didn't provide enough arguments.
     *
     * @param usage the usage string
     * @return the builder instance
     */
    default FunctionalCommandBuilder<T> assertUsage(String usage) {
        return this.assertUsage(usage, DEFAULT_INVALID_USAGE_MESSAGE);
    }

    /**
     * Asserts that the arguments match the given usage string.
     * <p>
     * Arguments should be separated by a " " space. Optional arguments are denoted by wrapping the argument name in
     * square quotes "[ ]"
     * <p>
     * The failure message is sent if they didn't provide enough arguments. "{0}" in this message will be replaced by
     * the usage for the command.
     *
     * @param usage          the usage string
     * @param failureMessage the failure message to send if the arguments to not match the usage
     * @return the builder instance
     */
    FunctionalCommandBuilder<T> assertUsage(String usage, Component failureMessage);

    /**
     * Tests a given argument with the provided predicate.
     * <p>
     * The default failure message is sent if the argument does not pass the predicate. If the argument is not present
     * at the given index,
     * <code>null</code> is passed to the predicate.
     *
     * @param index the index of the argument to test
     * @param test  the test predicate
     * @return the builder instance
     */
    default FunctionalCommandBuilder<T> assertArgument(int index, Predicate<@Nullable String> test) {
        return this.assertArgument(index, test, DEFAULT_INVALID_ARGUMENT_MESSAGE);
    }

    /**
     * Tests a given argument with the provided predicate.
     * <p>
     * The failure message is sent if the argument does not pass the predicate. If the argument is not present at the
     * given index,
     * <code>null</code> is passed to the predicate.
     * <p>
     * "{0}" and "{1}" will be replaced in the failure message with the index and actual argument value respectively.
     *
     * @param index          the index of the argument to test
     * @param test           the test predicate
     * @param failureMessage the failure message to send if the predicate fails
     * @return the builder instance
     */
    FunctionalCommandBuilder<T> assertArgument(int index, Predicate<@Nullable String> test, Component failureMessage);

    /**
     * Tests the sender with the provided predicate.
     * <p>
     * The default failure message is sent if the sender does not pass the predicate.
     *
     * @param test the test predicate
     * @return the builder instance
     */
    default FunctionalCommandBuilder<T> assertSender(Predicate<T> test) {
        return this.assertSender(test, DEFAULT_INVALID_SENDER_MESSAGE);
    }

    /**
     * Tests the sender with the provided predicate.
     * <p>
     * The failure message is sent if the sender does not pass the predicate.
     *
     * @param test           the test predicate
     * @param failureMessage the failure message to send if the predicate fails
     * @return the builder instance
     */
    FunctionalCommandBuilder<T> assertSender(Predicate<T> test, Component failureMessage);

    /**
     * Sets the tab handler to the provided one.
     *
     * @param tabHandler the tab handler
     * @return the builder instance
     */
    FunctionalCommandBuilder<T> tabHandler(FunctionalTabHandler<T> tabHandler);

    /**
     * Builds this {@link FunctionalCommandBuilder} into a {@link Command} instance.
     * <p>
     * The command will not be registered with the server until {@link Command#register(String...)} is called.
     *
     * @param handler the command handler
     * @return the command instance.
     */
    Command handler(FunctionalCommandHandler<T> handler);
}