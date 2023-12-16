package com.kubbidev.nexuspowered.common.platform;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Provides information about the platform NexusPowered is running on.
 */
public interface Platform {

    /**
     * Gets the type of platform NexusPowered is running on
     *
     * @return the type of platform NexusPowered is running on
     */
    Platform.@NotNull Type getType();

    /**
     * Gets the time when the plugin first started.
     *
     * @return the enable time
     */
    @NotNull Instant getStartTime();

    /**
     * Represents a type of platform which NexusPowered can run on.
     */
    enum Type {
        PAPER("Paper"),
        VELOCITY("Velocity");

        private final String friendlyName;

        Type(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        /**
         * Gets a readable name for the platform type.
         *
         * @return a readable name
         */
        public @NotNull String getFriendlyName() {
            return this.friendlyName;
        }
    }
}