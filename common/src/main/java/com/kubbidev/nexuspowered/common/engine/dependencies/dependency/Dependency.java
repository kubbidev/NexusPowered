package com.kubbidev.nexuspowered.common.engine.dependencies.dependency;

import com.kubbidev.nexuspowered.common.engine.dependencies.relocation.Relocation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * The dependency interface used to download an append dependencies to engine classpath.
 */
public interface Dependency {

    /**
     * Gets the dependency artifact id.
     *
     * @return the artifact id of the dependency.
     */
    String getArtifactId();

    /**
     * Gets the file name of the dependency.
     *
     * @param classifier at the end of the file name
     * @return a {@link String} pointing the file name of this dependency.
     */
    String getFileName(String classifier);

    /**
     * Gets the maven repository path used to download this dependency.
     *
     * @return a {@link String} path of this dependency.
     */
    String getMavenRepoPath();

    /**
     * Gets the check sum of the dependency.
     *
     * @return byte array check sum.
     */
    byte[] getChecksum();

    /**
     * Used to check if the downloaded dependency is the actual one provided by a plugin.
     *
     * @param hash of the downloaded dependency
     * @return true if the check sum matches, otherwise false.
     */
    boolean checksumMatches(byte[] hash);

    /**
     * Gets the relocations of this dependency.
     *
     * @return return relocations if present, otherwise empty list.
     */
    List<Relocation> getRelocations();

    /**
     * Creates a {@link MessageDigest} suitable for computing the checksums
     * of dependencies.
     *
     * @return the digest
     */
    static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
