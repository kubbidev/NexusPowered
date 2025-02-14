package me.kubbidev.nexuspowered;

import me.kubbidev.nexuspowered.internal.LoaderUtils;
import me.kubbidev.nexuspowered.plugin.NexusPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Base class for nexuspowered, which mainly just proxies calls to {@link Bukkit#getServer()} for convenience.
 */
@NotNullByDefault
public final class Nexus {

    /**
     * Gets the plugin which is "hosting" nexuspowered.
     *
     * @return the host plugin
     */
    public static NexusPlugin hostPlugin() {
        return LoaderUtils.getPlugin();
    }

    public static Server server() {
        return Bukkit.getServer();
    }

    public static ConsoleCommandSender console() {
        return server().getConsoleSender();
    }

    public static PluginManager plugins() {
        return server().getPluginManager();
    }

    public static ServicesManager services() {
        return server().getServicesManager();
    }

    public static BukkitScheduler bukkitScheduler() {
        return server().getScheduler();
    }

    @Nullable
    public static <T> T serviceNullable(Class<T> clazz) {
        return Services.get(clazz).orElse(null);
    }

    public static <T> Optional<T> service(Class<T> clazz) {
        return Services.get(clazz);
    }

    public static void executeCommand(String command) {
        server().dispatchCommand(console(), command);
    }

    @Nullable
    public static World worldNullable(String name) {
        return server().getWorld(name);
    }

    public static Optional<World> world(String name) {
        return Optional.ofNullable(worldNullable(name));
    }

    private Nexus() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}