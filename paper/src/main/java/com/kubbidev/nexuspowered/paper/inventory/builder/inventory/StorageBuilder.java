package com.kubbidev.nexuspowered.paper.inventory.builder.inventory;

import com.kubbidev.nexuspowered.paper.inventory.type.StorageInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * The simple Inventory builder is used for creating a {@link StorageInventory}
 */
public final class StorageBuilder extends BaseInventoryBuilder<StorageInventory, StorageBuilder> {

    public StorageBuilder(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Creates a new {@link StorageInventory}
     *
     * @return A new {@link StorageInventory}
     */
    @Override
    public StorageInventory create() {
        StorageInventory inventory = new StorageInventory(this.plugin, getRows(), getTitle(), getModifiers());

        Consumer<StorageInventory> consumer = getConsumer();
        if (consumer != null)
            consumer.accept(inventory);

        return inventory;
    }
}
