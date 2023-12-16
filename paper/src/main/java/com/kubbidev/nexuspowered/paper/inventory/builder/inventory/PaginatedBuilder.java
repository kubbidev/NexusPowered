package com.kubbidev.nexuspowered.paper.inventory.builder.inventory;

import com.kubbidev.nexuspowered.paper.inventory.type.PaginatedInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * Inventory builder for creating a {@link PaginatedInventory}
 */
public class PaginatedBuilder extends BaseInventoryBuilder<PaginatedInventory, PaginatedBuilder> {

    public PaginatedBuilder(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Creates a new {@link PaginatedInventory}
     *
     * @return A new {@link PaginatedInventory}
     */
    @Override
    public PaginatedInventory create() {
        PaginatedInventory inventory = new PaginatedInventory(this.plugin, getRows(), getTitle(), getModifiers());
        Consumer<PaginatedInventory> consumer = getConsumer();

        if (consumer != null)
            consumer.accept(inventory);

        return inventory;
    }
}
