package me.kubbidev.nexuspowered.internal;

import me.kubbidev.nexuspowered.Nexus;
import me.kubbidev.nexuspowered.plugin.NexusPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides the instance which loaded the nexuspowered classes into the server.
 */
public final class LoaderUtils {
    private static NexusPlugin plugin = null;
    private static Thread mainThread = null;

    @NotNull
    public static synchronized NexusPlugin getPlugin() {
        if (plugin == null) {
            JavaPlugin p = JavaPlugin.getProvidingPlugin(LoaderUtils.class);
            if (!(p instanceof NexusPlugin)) {
                throw new IllegalStateException("NexusPowered providing plugin does not implement NexusPlugin: " + p.getClass().getName());
            }
            plugin = (NexusPlugin) p;
            Bukkit.getLogger().info("[NexusPowered] Implementation is now bound to - " + plugin.getClass().getName());
            setup();
        }

        return plugin;
    }

    /**
     * To be used for testing only.
     */
    public static synchronized void forceSetPlugin(NexusPlugin plugin) {
        LoaderUtils.plugin = plugin;
    }

    public static Set<Plugin> getNexusImplementationPlugins() {
        return Stream.concat(Stream.of(getPlugin()),
                Arrays.stream(Nexus.plugins().getPlugins()).filter(p -> p.getClass().isAnnotationPresent(NexusImplementationPlugin.class))
        ).collect(Collectors.toSet());
    }

    public static Set<NexusPlugin> getNexusPlugins() {
        return Stream.concat(Stream.of(getPlugin()),
                Arrays.stream(Nexus.plugins().getPlugins()).filter(p -> p instanceof NexusPlugin).map(p -> (NexusPlugin) p)
        ).collect(Collectors.toSet());
    }

    @NotNull
    public static synchronized Thread getMainThread() {
        if (mainThread == null) {
            if (Bukkit.getServer().isPrimaryThread()) {
                mainThread = Thread.currentThread();
            }
        }
        return mainThread;
    }

    // performs an initial setup for global handlers
    private static void setup() {
        // cache main thread in this class
        getMainThread();
    }

    private LoaderUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}