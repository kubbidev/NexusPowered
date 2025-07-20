package me.kubbidev.nexuspowered.command;

import java.util.function.Consumer;
import org.bukkit.command.CommandSender;

/**
 * Exception thrown when the handling of a command should be interrupted.
 *
 * <p>This exception is silently swallowed by the command processing handler.</p>
 */
public class CommandInterruptException extends Exception {

    private final Consumer<CommandSender> action;

    public CommandInterruptException(Consumer<CommandSender> action) {
        this.action = action;
    }

    public CommandInterruptException(String message) {
        this.action = cs -> cs.sendMessage(message);
    }

    /**
     * Makes an assertion about a condition.
     *
     * <p>When used inside a command, command processing will be gracefully halted
     * if the condition is not true.</p>
     *
     * @param condition the condition
     * @param failMsg   the message to send to the player if the assertion fails
     * @throws CommandInterruptException if the assertion fails
     */
    public static void makeAssertion(boolean condition, String failMsg) throws CommandInterruptException {
        if (!condition) {
            throw new CommandInterruptException(failMsg);
        }
    }

    public Consumer<CommandSender> getAction() {
        return this.action;
    }
}