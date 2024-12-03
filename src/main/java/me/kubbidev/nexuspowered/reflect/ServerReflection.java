package me.kubbidev.nexuspowered.reflect;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Utility methods for working with "versioned" server classes.
 *
 * <p>Internal classes within the Minecraft server and CraftBukkit are relocated at build time
 * to prevent developers from relying upon server internals. It is however sometimes useful to be
 * able to interact with these classes (via reflection).</p>
 */
public final class ServerReflection {

    /**
     * The nms prefix (without the version component)
     */
    public static final String NMS = "net.minecraft.server";

    /**
     * The obc prefix (without the version component)
     */
    public static final String OBC = "org.bukkit.craftbukkit";

    /**
     * The server's "nms" version
     */
    private static final String SERVER_VERSION;

    /**
     * The server's "nms" version
     */
    private static final NmsVersion NMS_VERSION;

    static {
        String serverVersion = "";
        // check we're dealing with a "CraftServer" and that the server isn't non-versioned.
        Class<?> server = Bukkit.getServer().getClass();
        if (server.getSimpleName().equals("CraftServer") && !server.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            String obcPackage = server.getPackage().getName();
            // check we're dealing with a craftbukkit implementation.
            if (obcPackage.startsWith("org.bukkit.craftbukkit.")) {
                // return the nms version.
                serverVersion = obcPackage.substring("org.bukkit.craftbukkit.".length());
            }
        }
        SERVER_VERSION = serverVersion;

        if (SERVER_VERSION.isEmpty()) {
            NMS_VERSION = NmsVersion.NONE;
        } else {
            NMS_VERSION = NmsVersion.valueOf(serverVersion);
        }
    }

    /**
     * Gets the server "nms" version.
     *
     * @return the server packaging version
     */
    @NotNull
    public static String getServerVersion() {
        return SERVER_VERSION;
    }

    /**
     * Gets the server "nms" version.
     *
     * @return the server packaging version
     */
    @NotNull
    public static NmsVersion getNmsVersion() {
        return NMS_VERSION;
    }

    /**
     * Prepends the versioned {@link #NMS} prefix to the given class name
     *
     * @param className the name of the class
     * @return the full class name
     */
    @NotNull
    public static String nms(String className) {
        return NMS_VERSION.nms(className);
    }

    /**
     * Prepends the versioned {@link #NMS} prefix to the given class name
     *
     * @param className the name of the class
     * @return the class represented by the full class name
     */
    @NotNull
    public static Class<?> nmsClass(String className) throws ClassNotFoundException {
        return NMS_VERSION.nmsClass(className);
    }

    /**
     * Prepends the versioned {@link #OBC} prefix to the given class name
     *
     * @param className the name of the class
     * @return the full class name
     */
    @NotNull
    public static String obc(String className) {
        return NMS_VERSION.obc(className);
    }

    /**
     * Prepends the versioned {@link #OBC} prefix to the given class name
     *
     * @param className the name of the class
     * @return the class represented by the full class name
     */
    @NotNull
    public static Class<?> obcClass(String className) throws ClassNotFoundException {
        return NMS_VERSION.obcClass(className);
    }

    private ServerReflection() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}