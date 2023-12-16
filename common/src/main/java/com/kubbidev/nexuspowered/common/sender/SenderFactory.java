package com.kubbidev.nexuspowered.common.sender;

import com.kubbidev.java.util.TriState;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Factory class to make a thread-safe sender instance
 *
 * @param <P> the plugin type
 * @param <T> the command sender type
 */
public abstract class SenderFactory<P extends NexusPlugin<P>, T> implements AutoCloseable {
    private final P plugin;

    public SenderFactory(P plugin) {
        this.plugin = plugin;
    }

    protected P getPlugin() {
        return this.plugin;
    }

    protected abstract UUID getUniqueId(T sender);

    protected abstract String getName(T sender);

    protected abstract void sendMessage(T sender, Component message);

    protected abstract void sendMessage(T sender, String message);

    protected abstract TriState getPermissionValue(T sender, String node);

    protected abstract boolean hasPermission(T sender, String node);

    protected abstract void performCommand(T sender, String command);

    protected abstract boolean isConsole(T sender);

    protected abstract Locale getLocale(T sender);

    protected boolean consoleHasAllPermissions() {
        return true;
    }

    public final Sender wrap(T sender) {
        Objects.requireNonNull(sender, "sender");
        return new AbstractSender<>(this.plugin, this, sender);
    }

    @Override
    public void close() {

    }
}