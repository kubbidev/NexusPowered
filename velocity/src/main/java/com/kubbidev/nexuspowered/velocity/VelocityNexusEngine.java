package com.kubbidev.nexuspowered.velocity;

import com.kubbidev.java.config.generic.adapter.ConfigurationAdapter;
import com.kubbidev.java.config.generic.key.ConfigKey;
import com.kubbidev.nexuspowered.common.NexusEngine;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.command.AbstractCommandManager;
import com.kubbidev.nexuspowered.common.commands.MainCommand;
import com.kubbidev.nexuspowered.common.commands.PluginsCommand;
import com.kubbidev.nexuspowered.common.engine.ConfigKeys;
import com.kubbidev.nexuspowered.common.engine.dependencies.DependencyManager;
import com.kubbidev.nexuspowered.common.engine.dependencies.DependencyManagerImpl;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerAdapter;
import com.kubbidev.nexuspowered.common.locale.LocaleMessage;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The central engine class for NexusPowered plugins in the Velocity Minecraft server platform.
 * This class provides access to the NexusPowered API and serves as a container for child plugins.
 */
public class VelocityNexusEngine extends VelocityNexusPlugin<VelocityNexusEngine> implements NexusEngine<VelocityNexusEngine> {

    // Static access
    private static VelocityNexusEngine instance = null;

    /**
     * Gets the static instance of the main class for NexusEngine.
     *
     * @return NexusEngine instance
     */
    public static @NotNull VelocityNexusEngine getInstance() {
        return instance;
    }

    private SchedulerAdapter scheduler;
    private DependencyManager dependencyManager;

    // Child plugins registered within this NexusEngine
    private final Set<NexusPlugin<?>> plugins = new HashSet<>();

    public VelocityNexusEngine() {
        instance = this;
    }

    @Override
    public boolean enableEngine() {
        // Send banner to console on starting
        LocaleMessage.STARTUP_BANNER.send(getConsoleSender(), this);

        this.scheduler = new VelocitySchedulerAdapter(this);
        this.dependencyManager = new DependencyManagerImpl(this);
        this.dependencyManager.loadDependencies(Set.of(
                // Default dependencies to load for now...
        ));

        return true; // You can provide implementation details here.
    }

    @Override
    public Set<NexusPlugin<?>> getChildPlugins() {
        return this.plugins;
    }

    @Override
    public void hookChild(@NotNull NexusPlugin<?> plugin) {
        this.plugins.add(plugin);
    }

    @Override
    protected void registerListeners(@NotNull Set<Object> listeners) {
        // Nothing to implement.
    }

    @Override
    public void load() {
        // Nothing to implement.
    }

    @Override
    public void enable() {
        // Nothing to implement.
    }

    @Override
    public void disable() {
        // close dependencies manager
        getDependencyManager().close();

        getSchedulerAdapter().shutdownScheduler();
        getSchedulerAdapter().shutdownExecutor();
    }

    @Override
    public void registerCommands(@NotNull AbstractCommandManager<VelocityNexusEngine> commandManager,
                                 @NotNull MainCommand<VelocityNexusEngine> mainCommand) {

        mainCommand.addChildren(new PluginsCommand<>());
    }

    @Override
    public @NotNull ConfigurationAdapter provideConfigurationAdapter() {
        return new VelocityConfigAdapter(resolvePath("config.yml"));
    }

    @Override
    public @NotNull List<? extends ConfigKey<?>> provideConfigKeys() {
        return ConfigKeys.getKeys();
    }

    @Override
    public @NotNull ConfigKey<String> provideDefaultLabel() {
        return ConfigKeys.DEFAULT_COMMAND_LABEL;
    }

    @Override
    public SchedulerAdapter getSchedulerAdapter() {
        return this.scheduler;
    }

    @Override
    public DependencyManager getDependencyManager() {
        return this.dependencyManager;
    }
}
