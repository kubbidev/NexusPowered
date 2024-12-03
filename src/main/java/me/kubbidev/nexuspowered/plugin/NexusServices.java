package me.kubbidev.nexuspowered.plugin;

final class NexusServices {
    private NexusServices() {
    }

    static void setup(ExtendedJavaPlugin plugin) {
    }

    private static boolean classExists(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}