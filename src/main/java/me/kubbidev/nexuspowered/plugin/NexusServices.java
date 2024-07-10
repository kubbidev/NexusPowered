package me.kubbidev.nexuspowered.plugin;

import me.kubbidev.nexuspowered.hologram.BukkitHologramFactory;
import me.kubbidev.nexuspowered.hologram.HologramFactory;
import me.kubbidev.nexuspowered.hologram.individual.IndividualHologramFactory;
import me.kubbidev.nexuspowered.hologram.individual.PacketIndividualHologramFactory;
import me.kubbidev.nexuspowered.messaging.bungee.BungeeCord;
import me.kubbidev.nexuspowered.messaging.bungee.BungeeCordImpl;
import me.kubbidev.nexuspowered.signprompt.PacketSignPromptFactory;
import me.kubbidev.nexuspowered.signprompt.SignPromptFactory;

final class NexusServices {
    private NexusServices() {
    }

    static void setup(ExtendedJavaPlugin plugin) {
        plugin.provideService(HologramFactory.class, new BukkitHologramFactory());
        plugin.provideService(BungeeCord.class, new BungeeCordImpl(plugin));

        if (plugin.isPluginPresent("ProtocolLib")) {
            SignPromptFactory signPromptFactory = new PacketSignPromptFactory();
            plugin.provideService(SignPromptFactory.class, signPromptFactory);
            try {
                IndividualHologramFactory hologramFactory = new PacketIndividualHologramFactory();
                plugin.provideService(IndividualHologramFactory.class, hologramFactory);
            } catch (Throwable t) {
                // ignore??
            }
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