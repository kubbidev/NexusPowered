package com.kubbidev.nexuspowered.common;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The NexusEngine class represents the core engine of the Nexus system.
 * It extends the NexusPlugin interface, allowing the engine itself to be treated as a plugin.
 * The engine is responsible for managing child plugins and providing essential functionalities.
 *
 * @param <P> The type of the NexusPlugin used by the engine.
 */
public interface NexusEngine<P extends NexusEngine<P>> extends NexusPlugin<P> {

    /**
     * Enables the NexusEngine and all its associated functionalities.
     * This method is called during the initialization of the engine to set it up for use.
     *
     * @return true if the engine was successfully enabled, false otherwise.
     */
    boolean enableEngine();

    /**
     * Retrieves the set of child plugins registered with the NexusEngine.
     * Child plugins are plugins managed and controlled by the engine.
     *
     * @return A set containing all registered child plugins.
     */
    Set<NexusPlugin<?>> getChildPlugins();

    /**
     * Hooks a child plugin into the NexusEngine for management.
     * The engine will take responsibility for managing the lifecycle of the child plugin.
     *
     * @param plugin The NexusPlugin to be hooked as a child plugin by the engine.
     */
    void hookChild(@NotNull NexusPlugin<?> plugin);

    /**
     * Exception thrown when the API is requested before it has been loaded.
     */
    final class NotLoadedException extends IllegalStateException {
        private static final String MESSAGE = """
                The NexusEngine API isn't loaded yet!
                This could be because:
                  a) the NexusPowered plugin is not installed or it failed to enable
                  b) the plugin in the stacktrace does not declare a dependency on NexusPowered
                  c) the plugin in the stacktrace is retrieving the Engine before the plugin 'enable' phase
                     (call the #get method in onEnable, not the constructor!)
                """;

        /**
         * Creates a new instance of NotLoadedException with a predefined error message.
         */
        public NotLoadedException() {
            super(MESSAGE);
        }
    }
}