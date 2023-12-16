package com.kubbidev.nexuspowered.common;

import com.kubbidev.java.classpath.ClassPathAppender;
import com.kubbidev.java.config.Configuration;
import com.kubbidev.java.config.generic.adapter.ConfigurationAdapter;
import com.kubbidev.java.config.generic.key.ConfigKey;
import com.kubbidev.java.logging.LoggerAdapter;
import com.kubbidev.nexuspowered.common.command.AbstractCommandManager;
import com.kubbidev.nexuspowered.common.commands.MainCommand;
import com.kubbidev.nexuspowered.common.engine.dependencies.DependencyManager;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerAdapter;
import com.kubbidev.nexuspowered.common.locale.TranslationManager;
import com.kubbidev.nexuspowered.common.platform.Platform;
import com.kubbidev.nexuspowered.common.plugin.NexusDescription;
import com.kubbidev.nexuspowered.common.plugin.NexusServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * The NexusPlugin interface represents a plugin within the NexusPowered system.
 * Plugins extend this class to integrate with the NexusPowered functionality.
 *
 * @param <P> The type of the NexusPlugin used.
 */
public interface NexusPlugin<P extends NexusPlugin<P>> extends NexusDescription, NexusServer, Platform {

    /**
     * Owner of the NexusPowered project
     */
    String TM = "kubbidev";

    String PREFIX = "Nexus";

    String NEXUSPOWERED = "NexusPowered";
    String APRIL_FIRST = "NexSUSPowered";

    /**
     * Gets if this plugin is the engine or not.
     *
     * @return true if this is the engine, otherwise false.
     */
    boolean isEngine();

    /**
     * Gets the {@link NexusEngine} class instance running on the platform.
     *
     * @return The engine running on the platform.
     */
    NexusEngine<?> provideEngine();

    // Lifecycle

    /**
     * Reloads the configuration for this plugin.
     * This method is called to refresh the plugin's configuration from its configuration file.
     */
    void reload();

    /**
     * Called during the loading phase of the plugin.
     * Implementations should use this method to set up any necessary resources and configurations.
     */
    void load();

    /**
     * Called during the enabling phase of the plugin.
     * Implementations should use this method to enable the plugin's features and functionalities.
     */
    void enable();

    /**
     * Called during the disabling phase of the plugin.
     * Implementations should use this method to clean up any resources and perform necessary cleanup tasks.
     */
    void disable();

    /**
     * Registers commands for this plugin.
     *
     * @param commandManager The CommandManager instance of this plugin.
     * @param mainCommand The MainCommand instance of this plugin.
     */
    void registerCommands(@NotNull AbstractCommandManager<P> commandManager, @NotNull MainCommand<P> mainCommand);

    /**
     * Provides the configuration adapter for this plugin.
     * The configuration adapter is used to handle the configuration of this plugin.
     *
     * @return The ConfigurationAdapter instance for this plugin's configuration.
     */
    @NotNull ConfigurationAdapter provideConfigurationAdapter();

    /**
     * Provides a list of ConfigKey objects that define the configuration keys for this plugin.
     *
     * @return A list of ConfigKey objects representing the configuration keys for this plugin.
     */
    @NotNull List<? extends ConfigKey<?>> provideConfigKeys();

    /**
     * Provides a string ConfigKey that define the default command label for the main command for this plugin.
     *
     * @return A string ConfigKey representing the default command label for the main command for this plugin.
     */
    @NotNull ConfigKey<String> provideDefaultLabel();

    /**
     * Retrieves the author(s) of this plugin as a comma-separated string.
     * If the plugin has no specified authors, the default author (TM) is returned.
     *
     * @return The author(s) of this plugin as a comma-separated string.
     */
    default String getAuthor() {
        return getAuthors().isEmpty() ? NexusPlugin.TM : String.join(", ", getAuthors());
    }

    // Providing methods

    /**
     * Retrieves the logger for this plugin.
     *
     * @return The PluginLogger instance for this plugin.
     */
    LoggerAdapter getLoggerAdapter();

    /**
     * Retrieves the SchedulerAdapter associated with this plugin.
     * The SchedulerAdapter is responsible for managing scheduled tasks and timing within the plugin.
     *
     * @return The SchedulerAdapter instance associated with this plugin.
     */
    SchedulerAdapter getSchedulerAdapter();

    /**
     * Retrieves the dependency manager for this plugin.
     *
     * @return The DependencyManager associated with this plugin.
     */
    DependencyManager getDependencyManager();

    /**
     * Retrieves the configuration object for this plugin.
     *
     * @return The Configuration instance for this plugin.
     */
    Configuration getConfiguration();

    /**
     * Retrieves the command manager for the plugin.
     *
     * @return The AbstractCommandManager instance for this plugin.
     */
    AbstractCommandManager<P> getCommandManager();

    /**
     * Retrieves the class path appender for this plugin.
     *
     * @return The ClassPathAppender instance for this plugin.
     */
    ClassPathAppender getClassPathAppender();

    /**
     * Retrieves the TranslationManager for this plugin.
     * The TranslationManager is responsible for managing plugin translations and language files.
     *
     * @return The TranslationManager instance for this plugin.
     */
    TranslationManager getTranslationManager();

    /**
     * Retrieves the load latch for this plugin.
     * The load latch is a CountDownLatch that is used to manage the plugin's loading process.
     *
     * @return The CountDownLatch instance for this plugin's loading process.
     */
    CountDownLatch getLoadLatch();

    /**
     * Retrieves the enable latch for this plugin.
     * The enable latch is a CountDownLatch that is used to manage the plugin's enabling process.
     *
     * @return The CountDownLatch instance for this plugin's enabling process.
     */
    CountDownLatch getEnableLatch();

    /**
     * Retrieves an InputStream for a resource file located within the plugin's resources.
     *
     * @param path The path to the resource file.
     * @return An InputStream for the resource file, or null if the resource was not found.
     */
    default @Nullable InputStream getResourceStream(@NotNull String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    /**
     * Resolves a file path within the plugin's configuration directory.
     * If the file does not exist, it creates it based on a template in the resources directory.
     *
     * @param fileName The name of the file to resolve.
     * @return The resolved Path for the file.
     */
    default @NotNull Path resolvePath(@NotNull String fileName) {
        Path configFile = getSource().resolve(fileName);

        // If the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e) {
                // ignore
            }
            try (InputStream is = getResourceStream(fileName)) {
                if (is != null) {
                    Files.copy(is, configFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configFile;
    }

    static @NotNull String getEngineName() {
        LocalDate date = LocalDate.now();
        if (date.getMonth() == Month.APRIL && date.getDayOfMonth() == 1) {
            return APRIL_FIRST;
        }
        return NEXUSPOWERED;
    }
}
