package me.kubbidev.nexuspowered.plugin;

import me.kubbidev.nexuspowered.config.KeyedConfiguration;
import me.kubbidev.nexuspowered.config.adapter.ConfigurationAdapter;
import me.kubbidev.nexuspowered.config.key.ConfigKey;
import me.kubbidev.nexuspowered.terminable.TerminableConsumer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public interface NexusPlugin extends Plugin, TerminableConsumer {

    /**
     * Register a listener with the server.
     *
     * <p>{@link me.kubbidev.nexuspowered.Events} should be used instead of this method in most cases.</p>
     *
     * @param listener the listener to register
     * @param <T>      the listener class type
     * @return the listener
     */
    @NotNull
    <T extends Listener> T registerListener(@NotNull T listener);

    /**
     * Registers a CommandExecutor with the server.
     *
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T>     the command executor class type
     * @return the command executor
     */
    @NotNull
    default <T extends CommandExecutor> T registerCommand(@NotNull T command, @NotNull String... aliases) {
        return registerCommand(command, null, null, null, aliases);
    }

    /**
     * Registers a CommandExecutor with the server.
     *
     * @param command           the command instance
     * @param permission        the command permission
     * @param permissionMessage the message sent when the sender doesn't the required permission
     * @param description       the command description
     * @param aliases           the command aliases
     * @param <T>               the command executor class type
     * @return the command executor
     */
    @NotNull
    <T extends CommandExecutor> T registerCommand(@NotNull T command, String permission, String permissionMessage, String description, @NotNull String... aliases);

    /**
     * Gets a service provided by the ServiceManager.
     *
     * @param service the service class
     * @param <T>     the class type
     * @return the service
     */
    @NotNull
    <T> T getService(@NotNull Class<T> service);

    /**
     * Provides a service to the ServiceManager, bound to this plugin.
     *
     * @param clazz    the service class
     * @param instance the instance
     * @param priority the priority to register the service at
     * @param <T>      the service class type
     * @return the instance
     */
    @NotNull
    <T> T provideService(@NotNull Class<T> clazz, @NotNull T instance, @NotNull ServicePriority priority);

    /**
     * Provides a service to the ServiceManager, bound to this plugin at {@link ServicePriority#Normal}.
     *
     * @param clazz    the service class
     * @param instance the instance
     * @param <T>      the service class type
     * @return the instance
     */
    @NotNull
    <T> T provideService(@NotNull Class<T> clazz, @NotNull T instance);

    /**
     * Gets if a given plugin is enabled.
     *
     * @param name the name of the plugin
     * @return if the plugin is enabled
     */
    boolean isPluginPresent(@NotNull String name);

    /**
     * Gets a plugin instance for the given plugin name.
     *
     * @param name        the name of the plugin
     * @param pluginClass the main plugin class
     * @param <T>         the main class type
     * @return the plugin
     */
    @Nullable
    <T> T getPlugin(@NotNull String name, @NotNull Class<T> pluginClass);

    /**
     * Gets a bundled file from the plugins resource folder.
     *
     * <p>If the file is not present, a version of it it copied from the jar.</p>
     *
     * @param name the name of the file
     * @return the file
     */
    @NotNull
    File getBundledFile(@NotNull String name);

    /**
     * Loads a config file from a file name.
     *
     * <p>Behaves in the same was as {@link #getBundledFile(String)} when the file is not present.</p>
     *
     * @param file the name of the file
     * @return the config instance
     */
    @NotNull
    YamlConfiguration loadConfig(@NotNull String file);

    /**
     * Loads a keyed config file from an {@link ConfigurationAdapter}.
     *
     * <p>Behaves in the same was as {@link #getBundledFile(String)} when the file is not present.</p>
     *
     * @param file the name of the file
     * @param keys the config key entry list
     * @return the keyed config instance
     */
    @NotNull
    KeyedConfiguration loadKeyedConfig(@NotNull String file, @NotNull List<? extends ConfigKey<?>> keys);

    /**
     * Loads a keyed config file from an {@link ConfigurationAdapter}.
     *
     * @param adapter the adapter of the config
     * @param keys    the config key entry list
     * @return the keyed config instance
     */
    @NotNull
    KeyedConfiguration loadKeyedConfig(@NotNull ConfigurationAdapter adapter, @NotNull List<? extends ConfigKey<?>> keys);

    /**
     * Gets the plugin jar file instance.
     *
     * @return the jar file
     */
    @NotNull
    File getJarFile();

    /**
     * Gets the plugin's class loader.
     *
     * @return the class loader
     */
    @NotNull
    ClassLoader getClassloader();
}