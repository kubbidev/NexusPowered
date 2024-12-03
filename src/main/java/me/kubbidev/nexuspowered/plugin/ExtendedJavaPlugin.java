package me.kubbidev.nexuspowered.plugin;

import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.Services;
import me.kubbidev.nexuspowered.config.KeyedConfiguration;
import me.kubbidev.nexuspowered.config.adapter.BukkitConfigAdapter;
import me.kubbidev.nexuspowered.config.adapter.ConfigurationAdapter;
import me.kubbidev.nexuspowered.config.key.ConfigKey;
import me.kubbidev.nexuspowered.internal.LoaderUtils;
import me.kubbidev.nexuspowered.scheduler.NexusExecutors;
import me.kubbidev.nexuspowered.terminable.composite.CompositeTerminable;
import me.kubbidev.nexuspowered.terminable.module.TerminableModule;
import me.kubbidev.nexuspowered.util.CommandMapUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * An "extended" JavaPlugin class.
 */
public abstract class ExtendedJavaPlugin extends JavaPlugin implements NexusPlugin {

    // the backing terminable registry
    private CompositeTerminable terminableRegistry;

    // are we the plugin that's providing nexuspowered?
    private boolean isLoaderPlugin;

    // Used by subclasses to perform logic for plugin load.
    public abstract void load();

    // Used by subclasses to perform logic for plugin enable.
    public abstract void enable();

    // Used by subclasses to perform logic for plugin disable.
    public abstract void disable();

    @Override
    public final void onLoad() {
        // LoaderUtils.getPlugin() has the side effect of caching the loader ref
        // do that nice and early. also store whether 'this' plugin is the loader.
        NexusPlugin loaderPlugin = LoaderUtils.getPlugin();
        this.isLoaderPlugin = this == loaderPlugin;

        this.terminableRegistry = CompositeTerminable.create();

        // call subclass
        load();
    }

    @Override
    public final void onEnable() {
        // schedule cleanup of the registry
        Schedulers.builder()
                .async()
                .after(10, TimeUnit.SECONDS)
                .every(30, TimeUnit.SECONDS)
                .run(this.terminableRegistry::cleanup)
                .bindWith(this.terminableRegistry);

        // setup services
        if (this.isLoaderPlugin) {
            NexusServices.setup(this);
        }

        // call subclass
        enable();
    }

    @Override
    public final void onDisable() {

        // call subclass
        disable();

        // terminate the registry
        this.terminableRegistry.closeAndReportException();

        if (this.isLoaderPlugin) {
            // shutdown the scheduler
            NexusExecutors.shutdown();
        }
    }

    @Override
    public <T extends AutoCloseable> @NotNull T bind(@NotNull T terminable) {
        return this.terminableRegistry.bind(terminable);
    }

    @Override
    public <T extends TerminableModule> @NotNull T bindModule(@NotNull T module) {
        return this.terminableRegistry.bindModule(module);
    }

    @Override
    public <T extends Listener> @NotNull T registerListener(@NotNull T listener) {
        Objects.requireNonNull(listener, "listener");
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    @Override
    public <T extends CommandExecutor> @NotNull T registerCommand(@NotNull T command, String permission, String permissionMessage, String description, @NotNull String... aliases) {
        return CommandMapUtil.registerCommand(this, command, permission, permissionMessage, description, aliases);
    }

    @Override
    public <T> @NotNull T getService(@NotNull Class<T> service) {
        return Services.load(service);
    }

    @Override
    public <T> @NotNull T provideService(@NotNull Class<T> clazz, @NotNull T instance, @NotNull ServicePriority priority) {
        return Services.provide(clazz, instance, this, priority);
    }

    @Override
    public <T> @NotNull T provideService(@NotNull Class<T> clazz, @NotNull T instance) {
        return provideService(clazz, instance, ServicePriority.Normal);
    }

    @Override
    public boolean isPluginPresent(@NotNull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getPlugin(@NotNull String name, @NotNull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    @Override
    public @NotNull File getBundledFile(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        File file = getRelativeFile(name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return file;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getRelativeFile(@NotNull String name) {
        getDataFolder().mkdirs();
        return new File(getDataFolder(), name);
    }

    @Override
    public @NotNull YamlConfiguration loadConfig(@NotNull String file) {
        Objects.requireNonNull(file, "file");
        return YamlConfiguration.loadConfiguration(getBundledFile(file));
    }

    @Override
    public @NotNull KeyedConfiguration loadKeyedConfig(@NotNull String file, @NotNull List<? extends ConfigKey<?>> keys) {
        Objects.requireNonNull(file, "file");
        return loadKeyedConfig(new BukkitConfigAdapter(this, getBundledFile(file)), keys);
    }

    @Override
    public @NotNull KeyedConfiguration loadKeyedConfig(@NotNull ConfigurationAdapter adapter, @NotNull List<? extends ConfigKey<?>> keys) {
        Objects.requireNonNull(adapter, "adapter");
        return new KeyedConfiguration(adapter, keys);
    }

    @Override
    public @NotNull File getJarFile() {
        return super.getFile();
    }

    @Override
    public @NotNull ClassLoader getClassloader() {
        return super.getClassLoader();
    }
}