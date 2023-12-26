package com.kubbidev.nexuspowered.common.engine.dependencies.dependency;

import com.google.common.collect.ImmutableList;
import com.kubbidev.nexuspowered.common.engine.dependencies.relocation.Relocation;

import java.util.*;

/**
 * The dependencies used by NexusPowered.
 */
public class SimpleDependency implements Dependency {

    private final String mavenRepoPath;
    private final String version;
    private final String artifactId;

    private final byte[] checksum;
    private final List<Relocation> relocations;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    public SimpleDependency(String groupId, String artifactId, String version, String checksum) {
        this(groupId, artifactId, version, checksum, new Relocation[0]);
    }

    public SimpleDependency(String groupId, String artifactId, String version, String checksum, Relocation... relocations) {
        this.mavenRepoPath = String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                version,
                rewriteEscaping(artifactId),
                version
        );
        this.version = version;
        this.artifactId = artifactId;
        this.checksum = Base64.getDecoder().decode(checksum);
        this.relocations = ImmutableList.copyOf(relocations);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    @Override
    public String getArtifactId() {
        return this.artifactId;
    }

    @Override
    public String getFileName(String classifier) {
        String name = artifactId.toLowerCase(Locale.ROOT).replace('_', '-');
        String extra = classifier == null || classifier.isEmpty()
                ? ""
                : "-" + classifier;

        return name + "-" + this.version + extra + ".jar";
    }

    @Override
    public String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    @Override
    public byte[] getChecksum() {
        return this.checksum;
    }

    @Override
    public boolean checksumMatches(byte[] hash) {
        return Arrays.equals(this.checksum, hash);
    }

    @Override
    public List<Relocation> getRelocations() {
        return this.relocations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleDependency that = (SimpleDependency) o;

        if (!Objects.equals(version, that.version)) return false;
        if (!Objects.equals(artifactId, that.artifactId)) return false;
        if (!Arrays.equals(checksum, that.checksum)) return false;

        return Objects.equals(relocations, that.relocations);
    }

    @Override
    public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(checksum);
        result = 31 * result + (relocations != null ? relocations.hashCode() : 0);
        return result;
    }
}
