package com.kubbidev.nexuspowered.paper.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;

public final class PaperReflection {

    private PaperReflection() {
        throw new AssertionError("No com.kubbidev.nexuspowered.paper.util.PaperReflection instances for you!");
    }

    public static final String SERVER_VERSION_STRING;

    static {
        SERVER_VERSION_STRING = getNMSVersion().orElseThrow(() -> new RuntimeException("Failed to get NMS version"));
    }

    public static @NotNull <E> E getHandle(@NotNull Player player, @NotNull Class<E> eClass) throws ReflectiveOperationException {

        Method getHandleMethod = PaperReflection.getBukkitClass("entity.CraftPlayer")
                .getDeclaredMethod("getHandle");

        getHandleMethod.setAccessible(true);

        return eClass.cast(getHandleMethod.invoke(player));
    }

    public static @NotNull Class<?> getBukkitClass(@NotNull String clazz) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION_STRING + "." + clazz);
    }

    public static @NotNull Class<?> getNMSClass(@NotNull String clazz, @NotNull String fullClassName) throws ClassNotFoundException {
        try {
            return Class.forName("net.minecraft.server." + SERVER_VERSION_STRING + "." + clazz);
        } catch (ClassNotFoundException ignored) {
            return Class.forName(fullClassName);
        }
    }

    private static @NotNull Optional<String> getNMSVersion() {
        String propertyVersion = System.getProperty("sr.nms.version");
        if (propertyVersion != null) {
            return Optional.of(propertyVersion);
        }

        try {
            Object bukkitServer = Class.forName("org.bukkit.Bukkit").getMethod("getServer").invoke(null);

            if (bukkitServer == null) {
                return Optional.empty();
            }

            String serverPackage = bukkitServer.getClass().getPackage().getName();
            return Optional.of(serverPackage.substring(serverPackage.lastIndexOf('.') + 1));

        } catch (ReflectiveOperationException ignored) {
            return Optional.empty();
        }
    }
}
