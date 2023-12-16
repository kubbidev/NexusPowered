package com.kubbidev.nexuspowered.common.command.abstraction;

import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.command.spec.Argument;
import com.kubbidev.nexuspowered.common.command.spec.CommandSpec;
import com.kubbidev.nexuspowered.common.command.util.ArgumentList;
import com.kubbidev.nexuspowered.common.sender.Sender;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * An abstract command class
 */
public abstract class Command<P extends NexusPlugin<P>, T> {

    /**
     * The name of the command. Should be properly capitalised.
     */
    private final String name;

    /**
     * The permission required to use this command. Nullable.
     */
    private final @Nullable String permission;

    /**
     * If this command is intended for players only.
     */
    private final boolean playersOnly;

    /**
     * The commands specification.
     * <p>
     * Contains details about usage, description, etc
     */
    private final CommandSpec spec;

    /**
     * A predicate used for testing the size of the arguments list passed to this command
     */
    private final Predicate<Integer> argumentCheck;

    public Command(String name, @Nullable String permission, boolean playersOnly, CommandSpec spec, Predicate<Integer> argumentCheck) {
        this.name = name;
        this.permission = permission;
        this.playersOnly = playersOnly;
        this.spec = spec;
        this.argumentCheck = argumentCheck;
    }

    /**
     * Gets the short name of this command
     *
     * <p>The result should be appropriately capitalised.</p>
     *
     * @return the command name
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Gets the commands spec.
     *
     * @return the command spec
     */
    public @NotNull CommandSpec getSpec() {
        return this.spec;
    }

    /**
     * Gets the permission required by this command, if present
     *
     * @return the command permission
     */
    public @Nullable String getPermission() {
        return this.permission;
    }

    /**
     * Check if this command is intended for players only.
     *
     * @return true if the command is for players only, false otherwise.
     */
    public boolean isPlayersOnly() {
        return this.playersOnly;
    }

    /**
     * Gets the predicate used to validate the number of arguments provided to
     * the command on execution
     *
     * @return the argument checking predicate
     */
    public @NotNull Predicate<Integer> getArgumentCheck() {
        return this.argumentCheck;
    }

    /**
     * Gets the commands description.
     *
     * @return the description
     */
    public Component getDescription() {
        return getSpec().description();
    }

    /**
     * Gets the usage of this command.
     * Will only return a non empty result for main commands.
     *
     * @return the usage of this command.
     */
    public String getUsage() {
        String usage = getSpec().usage();
        return usage == null ? "" : usage;
    }

    /**
     * Gets the arguments required by this command
     *
     * @return the commands arguments
     */
    public Optional<List<Argument>> getArgs() {
        return Optional.ofNullable(getSpec().args());
    }

    // Main execution method for the command.
    public abstract void execute(P plugin, Sender sender, T target, ArgumentList args, String label) throws CommandException;

    // Tab completion method - default implementation is provided as some commands do not provide tab completions.
    public List<String> tabComplete(P plugin, Sender sender, ArgumentList args) {
        return Collections.emptyList();
    }

    /**
     * Sends a brief command usage message to the Sender.
     * If this command has child commands, the children are listed. Otherwise, a basic usage message is sent.
     *
     * @param sender the sender to send the usage to
     * @param label the label used when executing the command
     */
    public abstract void sendUsage(Sender sender, String label);

    /**
     * Sends a detailed command usage message to the Sender.
     * If this command has child commands, nothing is sent. Otherwise, a detailed messaging containing a description
     * and argument usage is sent.
     *
     * @param sender the sender to send the usage to
     * @param label the label used when executing the command
     */
    public abstract void sendDetailedUsage(Sender sender, String label);

    /**
     * Returns true if the sender is authorised to use this command
     *
     * Commands with children are likely to override this method to check for permissions based upon whether
     * a sender has access to any sub commands.
     *
     * @param sender the sender
     * @return true if the sender has permission to use this command
     */
    public boolean isAuthorized(Sender sender) {
        return this.getPermission() == null || sender.hasPermission(getPermission());
    }

    /**
     * Gets if this command should be displayed in command listings, or "hidden"
     *
     * @return if the command should be displayed
     */
    public boolean shouldDisplay() {
        return true;
    }
}