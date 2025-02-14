package me.kubbidev.nexuspowered.plugin;

import me.kubbidev.nexuspowered.scoreboard.PacketScoreboardProvider;
import me.kubbidev.nexuspowered.scoreboard.ScoreboardProvider;

final class NexusServices {
    private NexusServices() {
    }

    static void setup(ExtendedJavaPlugin plugin) {
        if (plugin.isPluginPresent("ProtocolLib")) {
            PacketScoreboardProvider scoreboardProvider = new PacketScoreboardProvider(plugin);
            plugin.provideService(ScoreboardProvider.class, scoreboardProvider);
            plugin.provideService(PacketScoreboardProvider.class, scoreboardProvider);
        }
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