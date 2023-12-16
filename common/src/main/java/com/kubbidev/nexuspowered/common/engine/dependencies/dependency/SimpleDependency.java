package com.kubbidev.nexuspowered.common.engine.dependencies.dependency;

import com.google.common.collect.ImmutableList;
import com.kubbidev.nexuspowered.common.engine.dependencies.relocation.Relocation;

import java.util.*;

/**
 * The dependencies used by NexusPowered.
 */
public class SimpleDependency implements Dependency {

    public static final Dependency ASM = new SimpleDependency(
            "org.ow2.asm",
            "asm",
            "9.1",
            "zaTeRV+rSP8Ly3xItGOUR9TehZp6/DCglKmG8JNr66I="
    ),
    ASM_COMMONS = new SimpleDependency(
            "org.ow2.asm",
            "asm-commons",
            "9.1",
            "r8sm3B/BLAxKma2mcJCN2C4Y38SIyvXuklRplrRwwAw="
    ),
    JAR_RELOCATOR = new SimpleDependency(
            "me.lucko",
            "jar-relocator",
            "1.7",
            "b30RhOF6kHiHl+O5suNLh/+eAr1iOFEFLXhwkHHDu4I="
    ),
    CAFFEINE = new SimpleDependency(
            "com{}github{}ben-manes{}caffeine",
            "caffeine",
            "2.9.0",
            "VFMotEO3XLbTHfRKfL3m36GlN72E/dzRFH9B5BJiX2o=",
            Relocation.of("caffeine", "com{}github{}benmanes{}caffeine")
    ),
    MYSQL_DRIVER = new SimpleDependency(
            "mysql",
            "mysql-connector-java",
            "8.0.23",
            "/31bQCr9OcEnh0cVBaM6MEEDsjjsG3pE6JNtMynadTU=",
            Relocation.of("mysql", "com{}mysql")
    ),
    HIKARI = new SimpleDependency(
            "com{}zaxxer",
            "HikariCP",
            "4.0.3",
            "fAJK7/HBBjV210RTUT+d5kR9jmJNF/jifzCi6XaIxsk=",
            Relocation.of("hikari", "com{}zaxxer{}hikari")
    ),
    SLF4J_SIMPLE = new SimpleDependency(
            "org.slf4j",
            "slf4j-simple",
            "1.7.30",
            "i5J5y/9rn4hZTvrjzwIDm2mVAw7sAj7UOSh0jEFnD+4="
    ),
    SLF4J_API = new SimpleDependency(
            "org.slf4j",
            "slf4j-api",
            "1.7.30",
            "zboHlk0btAoHYUhcax6ML4/Z6x0ZxTkorA1/lRAQXFc="
    ),
    CONFIGURATE_CORE = new SimpleDependency(
            "org{}spongepowered",
            "configurate-core",
            "3.7.2",
            "XF2LzWLkSV0wyQRDt33I+gDlf3t2WzxH1h8JCZZgPp4=",
            Relocation.of("configurate", "ninja{}leaping{}configurate")
    ),
    CONFIGURATE_GSON = new SimpleDependency(
            "org{}spongepowered",
            "configurate-gson",
            "3.7.2",
            "9S/mp3Ig9De7NNd6+2kX+L4R90bHnAosSNVbFjrl7sM=",
            Relocation.of("configurate", "ninja{}leaping{}configurate")
    ),
    CONFIGURATE_YAML = new SimpleDependency(
            "org{}spongepowered",
            "configurate-yaml",
            "3.7.2",
            "OBfYn4nSMGZfVf2DoZhZq+G9TF1mODX/C5OOz/mkPmc=",
            Relocation.of("configurate", "ninja{}leaping{}configurate")
    ),
    CONFIGURATE_HOCON = new SimpleDependency(
            "org{}spongepowered",
            "configurate-hocon",
            "3.7.2",
            "GOORZlK1FKLzdIm7dKyyXtBdvk7Z89HARAd2H6NiWSY=",
            Relocation.of("configurate", "ninja{}leaping{}configurate"),
            Relocation.of("hocon", "com{}typesafe{}config")
    ),
    HOCON_CONFIG = new SimpleDependency(
            "com{}typesafe",
            "config",
            "1.4.1",
            "TAqn4iPHXIhAxB/Bg9TNMRgUCh7lA+PgjOZu0nlMlI8=",
            Relocation.of("hocon", "com{}typesafe{}config")
    );

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
