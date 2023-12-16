package com.kubbidev.nexuspowered.paper;

import com.kubbidev.java.config.generic.adapter.ConfigurationAdapter;
import com.kubbidev.java.config.generic.key.ConfigKey;
import com.kubbidev.nexuspowered.paper.listeners.InteractionModifierListener;
import com.kubbidev.nexuspowered.paper.listeners.InventoryListener;
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
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The central engine class for NexusPowered plugins in the Paper Minecraft server platform.
 * This class provides access to the NexusPowered API and serves as a container for child plugins.
 */
public class PaperNexusEngine extends PaperNexusPlugin<PaperNexusEngine> implements NexusEngine<PaperNexusEngine> {

    // Static access
    private static PaperNexusEngine instance = null;

    /**
     * Gets the static instance of the main class for NexusEngine.
     *
     * @return NexusEngine instance
     */
    public static @NotNull PaperNexusEngine getInstance() {
        return instance;
    }

    private SchedulerAdapter scheduler;
    private DependencyManager dependencyManager;

    // Child plugins registered within this NexusEngine
    private final Set<NexusPlugin<?>> plugins = new HashSet<>();

    public PaperNexusEngine() {
        instance = this;
    }

    /**
     * Loads the NexusEngine.
     *
     * @return true if the engine is successfully loaded, false otherwise.
     */
    @Override
    public boolean enableEngine() {
        // Send banner to console on starting
        LocaleMessage.STARTUP_BANNER.send(getConsoleSender(), this);

        this.scheduler = new PaperSchedulerAdapter(this);
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
    protected void registerListeners(@NotNull Set<Listener> listeners) {
        listeners.add(new InteractionModifierListener());
        listeners.add(new InventoryListener());
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
    public void registerCommands(@NotNull AbstractCommandManager<PaperNexusEngine> commandManager,
                                 @NotNull MainCommand<PaperNexusEngine> mainCommand) {
        // Register commands...
        mainCommand.addChildren(new PluginsCommand<>());
    }

    @Override
    public @NotNull ConfigurationAdapter provideConfigurationAdapter() {
        return new PaperConfigAdapter(resolvePath("config.yml").toFile());
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
