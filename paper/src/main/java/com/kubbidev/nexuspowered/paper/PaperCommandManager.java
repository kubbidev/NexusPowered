package com.kubbidev.nexuspowered.paper;

import com.kubbidev.nexuspowered.common.command.AbstractCommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PaperCommandManager<P extends PaperNexusPlugin<P>> extends AbstractCommandManager<P> {

    private static final String DEFAULT_DESCRIPTION = "A NexusPowered provided command.";
    private final P plugin;

    public PaperCommandManager(P plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void registerCommand(String label, com.kubbidev.nexuspowered.common.command.abstraction.Command<P, ?> command) {
        if (getCommands().put(label, command) == null) {

            PaperCommandExecutor executor = new PaperCommandExecutor(label, plugin);

            executor.setDescription(DEFAULT_DESCRIPTION);

            executor.setPermission(command.getPermission());
            executor.setUsage(command.getUsage());
            /*
                Register the command into the server command map
                And insert the command map into the command itself <- (don't know what it does but let's do it??)
            */
            CommandMap commandMap = plugin.getServer().getCommandMap();
            String prefixIdentifier = plugin.getId();

            executor.register(commandMap);
            commandMap.register(prefixIdentifier, executor);
        }
    }

    @Override
    public void unregisterCommand(String label) {
        if (getCommands().remove(label) != null) {

            // server command map
            CommandMap commandMap = plugin.getServer().getCommandMap();
            Map<String, Command> knownCommands = commandMap.getKnownCommands();

            for (String commandAlias : getAliases(knownCommands, label)) {
                Command bukkitCommand = commandMap.getCommand(commandAlias);

                if (bukkitCommand == null)
                    continue;

                bukkitCommand.unregister(commandMap);
                knownCommands.remove(commandAlias);
            }
        }
    }

    public static @NotNull Set<String> getAliases(@NotNull Map<String, Command> knownCommands, @NotNull String command) {

        for (Command knowCommand : knownCommands.values()) {
            String commandLabel = knowCommand.getLabel();

            List<String> aliases = knowCommand.getAliases();

            if (commandLabel.equalsIgnoreCase(command) || aliases.contains(command)) {

                Set<String> result = new HashSet<>();

                result.add(commandLabel);
                result.addAll(aliases);

                return result;
            }
        }
        return Collections.emptySet();
    }
}