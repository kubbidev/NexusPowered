package com.kubbidev.nexuspowered.paper.inventory;

import com.kubbidev.nexuspowered.paper.inventory.type.BaseInventory;
import org.bukkit.entity.Player;

public interface InventoryHolder<T extends BaseInventory> {

    T getInventory();

    default void update() {
        getInventory().update();
    }

    default void open(Player player) {
        getInventory().open(player);
    }
}
