package me.kubbidev.nexuspowered.util;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;

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

    private static CommandMap getCommandMap() {
        return Bukkit.getServer().getCommandMap();
    }

    private static Map<String, Command> getKnownCommandMap() {
        return getCommandMap().getKnownCommands();
    }

    /**
     * Registers a CommandExecutor with the server.
     *
     * @param plugin the plugin instance
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NotNull
    public static <T extends CommandExecutor> T registerCommand(@NotNull Plugin plugin, @NotNull T command, @NotNull String... aliases) {
        return registerCommand(plugin, command, null, null, null, aliases);
    }

    /**
     * Registers a CommandExecutor with the server.
     *
     * @param plugin the plugin instance
     * @param command the command instance
     * @param permission the command permission
     * @param permissionMessage the message sent when the sender doesn't the required permission
     * @param description the command description
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NotNull
    public static <T extends CommandExecutor> T registerCommand(@NotNull Plugin plugin, @NotNull T command, String permission, Component permissionMessage, String description, @NotNull String... aliases) {
        Preconditions.checkArgument(aliases.length != 0, "No aliases");
        for (String alias : aliases) {
            try {
                PluginCommand cmd = COMMAND_CONSTRUCTOR.newInstance(alias, plugin);

                getCommandMap().register(plugin.getPluginMeta().getName(), cmd);
                getKnownCommandMap().put(plugin.getPluginMeta().getName().toLowerCase() + ":" + alias.toLowerCase(), cmd);
                getKnownCommandMap().put(alias.toLowerCase(), cmd);
                cmd.setLabel(alias.toLowerCase());
                if (permission != null) {
                    cmd.setPermission(permission);
                    if (permissionMessage != null) {
                        //noinspection deprecation
                        cmd.permissionMessage(permissionMessage);
                    }
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
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NotNull
    public static <T extends CommandExecutor> T unregisterCommand(@NotNull T command) {
        CommandMap map = getCommandMap();
        try {
            Iterator<Command> iterator = getKnownCommandMap().values().iterator();
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

    private CommandMapUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}