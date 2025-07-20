package me.kubbidev.nexuspowered.util;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for interacting with the server's {@link CommandMap} instance.
 */
public final class CommandMapUtil {

    private static final Constructor<PluginCommand> COMMAND_CONSTRUCTOR;

    static {
        Constructor<PluginCommand> commandConstructor;
        try {
            commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            commandConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        COMMAND_CONSTRUCTOR = commandConstructor;
    }

    private CommandMapUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Registers a CommandExecutor with the server.
     *
     * @param plugin  the plugin instance
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T>     the command executor class type
     * @return the command executor
     */
    @NotNull
    public static <T extends CommandExecutor> T registerCommand(@NotNull Plugin plugin, @NotNull T command,
                                                                @NotNull String... aliases) {
        return registerCommand(plugin, command, null, null, aliases);
    }

    /**
     * Registers a CommandExecutor with the server.
     *
     * @param plugin            the plugin instance
     * @param command           the command instance
     * @param permission        the command permission
     * @param description       the command description
     * @param aliases           the command aliases
     * @param <T>               the command executor class type
     * @return the command executor
     */
    @NotNull
    public static <T extends CommandExecutor> T registerCommand(@NotNull Plugin plugin, @NotNull T command,
                                                                String permission,
                                                                String description, @NotNull String... aliases) {
        Preconditions.checkArgument(aliases.length != 0, "No aliases");
        for (String alias : aliases) {
            try {
                PluginCommand cmd = COMMAND_CONSTRUCTOR.newInstance(alias, plugin);
                CommandMap commandMap = Bukkit.getCommandMap();

                String name = plugin.getPluginMeta().getName();
                commandMap.register(name, cmd);

                String aliasToLowerCase = alias.toLowerCase(Locale.ROOT);
                Map<String, Command> knownCommands = commandMap.getKnownCommands();
                knownCommands.put(name.toLowerCase(Locale.ROOT) + ":" + aliasToLowerCase, cmd);
                knownCommands.put(aliasToLowerCase, cmd);

                cmd.setLabel(aliasToLowerCase);
                if (permission != null) {
                    cmd.setPermission(permission);
                }
                if (description != null) {
                    cmd.setDescription(description);
                }

                cmd.setExecutor(command);
                if (command instanceof TabCompleter) {
                    cmd.setTabCompleter((TabCompleter) command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return command;
    }

    /**
     * Unregisters a CommandExecutor with the server.
     *
     * @param command the command instance
     * @param <T>     the command executor class type
     * @return the command executor
     */
    @NotNull
    public static <T extends CommandExecutor> T unregisterCommand(@NotNull T command) {
        CommandMap map = Bukkit.getCommandMap();
        try {
            Iterator<Command> iterator = map.getKnownCommands().values().iterator();
            while (iterator.hasNext()) {
                Command cmd = iterator.next();
                if (cmd instanceof PluginCommand) {
                    CommandExecutor executor = ((PluginCommand) cmd).getExecutor();
                    if (command == executor) {
                        cmd.unregister(map);
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not unregister command", e);
        }

        return command;
    }
}