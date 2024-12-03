package me.kubbidev.nexuspowered.internal;

import me.kubbidev.nexuspowered.plugin.ExtendedJavaPlugin;

/**
 * Standalone plugin which provides the nexuspowered library at runtime for other plugins
 * on the server to use.
 */
@NexusImplementationPlugin
public final class StandalonePlugin extends ExtendedJavaPlugin {
    public StandalonePlugin() {
        getLogger().info("Initialized NexusPowered v" + this.getDescription().getVersion());
    }

    @Override
    public void load() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}