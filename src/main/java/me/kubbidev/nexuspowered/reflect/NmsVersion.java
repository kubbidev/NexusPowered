package me.kubbidev.nexuspowered.reflect;

import com.google.common.collect.ImmutableSet;
import me.kubbidev.nexuspowered.util.Indexing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * An enumeration of NMS versions.
 */
public enum NmsVersion {

    NONE {
        @Override
        protected String getPackageComponent() {
            return ".";
        }
    },

    v1_8_R1(
            MinecraftVersion.of(1, 8, 0)
    ),
    v1_8_R2(
            MinecraftVersion.of(1, 8, 3)
    ),
    v1_8_R3(
            MinecraftVersion.of(1, 8, 4),
            MinecraftVersion.of(1, 8, 5),
            MinecraftVersion.of(1, 8, 6),
            MinecraftVersion.of(1, 8, 7),
            MinecraftVersion.of(1, 8, 8)
    ),
    v1_9_R1(
            MinecraftVersion.of(1, 9, 0),
            MinecraftVersion.of(1, 9, 2)
    ),
    v1_9_R2(
            MinecraftVersion.of(1, 9, 4)
    ),
    v1_10_R1(
            MinecraftVersion.of(1, 10, 0),
            MinecraftVersion.of(1, 10, 2)
    ),
    v1_11_R1(
            MinecraftVersion.of(1, 11, 0),
            MinecraftVersion.of(1, 11, 1),
            MinecraftVersion.of(1, 11, 2)
    ),
    v1_12_R1(
            MinecraftVersion.of(1, 12, 0),
            MinecraftVersion.of(1, 12, 1),
            MinecraftVersion.of(1, 12, 2)
    ),
    v1_13_R1(
            MinecraftVersion.of(1, 13, 0)
    ),
    v1_13_R2(
            MinecraftVersion.of(1, 13, 1),
            MinecraftVersion.of(1, 13, 2)
    ),
    v1_14_R1(
            MinecraftVersion.of(1, 14, 0),
            MinecraftVersion.of(1, 14, 1),
            MinecraftVersion.of(1, 14, 2),
            MinecraftVersion.of(1, 14, 3),
            MinecraftVersion.of(1, 14, 4)
    ),
    v1_15_R1(
            MinecraftVersion.of(1, 15, 0),
            MinecraftVersion.of(1, 15, 1),
            MinecraftVersion.of(1, 15, 2)
    ),
    v1_16_R1(
            MinecraftVersion.of(1, 16, 0),
            MinecraftVersion.of(1, 16, 1)
    ),
    v1_16_R2(
            MinecraftVersion.of(1, 16, 2),
            MinecraftVersion.of(1, 16, 3)
    ),
    v1_16_R3(
            MinecraftVersion.of(1, 16, 4),
            MinecraftVersion.of(1, 16, 5)
    ),
    v1_17_R1(
            MinecraftVersion.of(1, 17, 0),
            MinecraftVersion.of(1, 17, 1)
    ),
    v1_18_R1(
            MinecraftVersion.of(1, 18, 0),
            MinecraftVersion.of(1, 18, 1),
            MinecraftVersion.of(1, 18, 2)
    ),
    v1_19_R1(
            MinecraftVersion.of(1, 19, 0),
            MinecraftVersion.of(1, 19, 1),
            MinecraftVersion.of(1, 19, 2)
    );

    private final Set<MinecraftVersion> minecraftVersions;

    private final String nmsPrefix;
    private final String obcPrefix;

    NmsVersion(MinecraftVersion... minecraftVersions) {
        this.minecraftVersions = ImmutableSet.copyOf(minecraftVersions);

        this.nmsPrefix = ServerReflection.NMS + getPackageComponent();
        this.obcPrefix = ServerReflection.OBC + getPackageComponent();
    }

    protected String getPackageComponent() {
        return "." + name() + ".";
    }

    /**
     * Gets the {@link MinecraftVersion}s that used this {@link NmsVersion}.
     *
     * @return the minecraft versions for this NMS version
     */
    public Set<MinecraftVersion> getMinecraftVersions() {
        return this.minecraftVersions;
    }

    /**
     * Prepends the versioned NMS prefix to the given class name
     *
     * @param className the name of the class
     * @return the full class name
     */
    @NotNull
    public String nms(String className) {
        return this.nmsPrefix + className;
    }

    /**
     * Prepends the versioned NMS prefix to the given class name
     *
     * @param className the name of the class
     * @return the class represented by the full class name
     */
    @NotNull
    public Class<?> nmsClass(String className) throws ClassNotFoundException {
        return Class.forName(nms(className));
    }

    /**
     * Prepends the versioned OBC prefix to the given class name
     *
     * @param className the name of the class
     * @return the full class name
     */
    @NotNull
    public String obc(String className) {
        return this.obcPrefix + className;
    }

    /**
     * Prepends the versioned OBC prefix to the given class name
     *
     * @param className the name of the class
     * @return the class represented by the full class name
     */
    @NotNull
    public Class<?> obcClass(String className) throws ClassNotFoundException {
        return Class.forName(obc(className));
    }

    private static final Map<MinecraftVersion, NmsVersion> MC_TO_NMS = Indexing.buildMultiple(values(), NmsVersion::getMinecraftVersions);

    /**
     * Gets the {@link NmsVersion} for the given {@link MinecraftVersion}.
     *
     * @param minecraftVersion the minecraft version
     * @return the nms version
     */
    @Nullable
    public static NmsVersion forMinecraftVersion(MinecraftVersion minecraftVersion) {
        return MC_TO_NMS.get(minecraftVersion);
    }

}