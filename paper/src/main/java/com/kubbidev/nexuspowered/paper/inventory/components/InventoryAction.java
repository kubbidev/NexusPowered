package com.kubbidev.nexuspowered.paper.inventory.components;

import org.bukkit.event.Event;

@FunctionalInterface
public interface InventoryAction<T extends Event> {

    /**
     * Executes the event passed to it
     *
     * @param event Inventory action
     */
    void execute(T event);
}
