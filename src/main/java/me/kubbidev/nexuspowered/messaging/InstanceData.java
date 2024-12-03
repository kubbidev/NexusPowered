package me.kubbidev.nexuspowered.messaging;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Provides information about the current server instance.
 */
public interface InstanceData {

    /**
     * Gets the unique ID of the current server instance.
     *
     * @return the id of the server
     */
    @NotNull
    String getId();

    /**
     * Gets the groups this server is a member of.
     *
     * @return this instance's groups
     */
    @NotNull
    Set<String> getGroups();

}