package com.kubbidev.nexuspowered.paper.event;

import com.kubbidev.nexuspowered.common.NexusPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public final class EventManager {

    private EventManager() {
        throw new AssertionError("No com.kubbidev.nexuspowered.paper.event.EventManager instances for you!");
    }

    public static void callSync(@NotNull Event event, NexusPlugin<?> plugin) {
        plugin.getSchedulerAdapter().executeSync(() -> callEvent(event));
    }

    public static void callAsync(@NotNull Event event, NexusPlugin<?> plugin) {
        plugin.getSchedulerAdapter().executeAsync(() -> callEvent(event));
    }

    private static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
