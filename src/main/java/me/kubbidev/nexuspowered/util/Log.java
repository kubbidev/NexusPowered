package me.kubbidev.nexuspowered.util;

import me.kubbidev.nexuspowered.internal.LoaderUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Utility for quickly accessing a logger instance without using {@link Bukkit#getLogger()}
 */
public final class Log {

    public static void info(@NotNull String s) {
        LoaderUtils.getPlugin().getLogger().info(s);
    }

    public static void warn(@NotNull String s) {
        LoaderUtils.getPlugin().getLogger().warning(s);
    }

    public static void severe(@NotNull String s) {
        LoaderUtils.getPlugin().getLogger().severe(s);
    }

    public static void warn(@NotNull String s, Throwable t) {
        LoaderUtils.getPlugin().getLogger().log(Level.WARNING, s, t);
    }

    public static void severe(@NotNull String s, Throwable t) {
        LoaderUtils.getPlugin().getLogger().log(Level.SEVERE, s, t);
    }

    private Log() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}