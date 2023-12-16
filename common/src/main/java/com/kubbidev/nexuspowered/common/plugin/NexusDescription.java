package com.kubbidev.nexuspowered.common.plugin;

import org.intellij.lang.annotations.Subst;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface NexusDescription {

    /**
     * Gets the qualified ID of the plugin.
     *
     * @return the plugin ID
     */
    @Subst("namespace") String getId();

    /**
     * Gets the name of the plugin.
     *
     * @return an {@link String} with the plugin name
     */
    String getPluginName();

    /**
     * Gets the version of the plugin.
     *
     * @return a {@link String} with the plugin version
     */
    String getPluginVersion();

    /**
     * Gets the description of the plugin.
     *
     * @return an {@link Optional} with the plugin description, may be empty
     */
    Optional<String> getPluginDescription();

    /**
     * Gets the url or website of the plugin.
     *
     * @return an {@link Optional} with the plugin url, may be empty
     */
    Optional<String> getUrl();

    /**
     * Gets the authors of the plugin.
     *
     * @return the plugin authors, may be empty
     */
    List<String> getAuthors();

    /**
     * Gets the source the plugin was loaded from.
     *
     * @return the source the plugin was loaded from or null if unknown
     */
    Path getSource();

    /**
     * Gets the data directory file for this plugin.
     *
     * @return The data directory file.
     */
    File getSourceDirectory();

    /**
     * Gets the brand name of the server on which the plugin is running.
     *
     * @return The brand name of the server.
     */
    String getServerBrand();

    /**
     * Gets the version of the server on which the plugin is running.
     *
     * @return The version of the server.
     */
    String getServerVersion();
}
