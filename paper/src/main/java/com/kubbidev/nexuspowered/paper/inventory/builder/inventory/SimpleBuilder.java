package com.kubbidev.nexuspowered.paper.inventory.builder.inventory;

import com.kubbidev.nexuspowered.paper.inventory.components.InventoryType;
import com.kubbidev.nexuspowered.paper.inventory.type.Inventory;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * The simple Inventory builder is used for creating a {@link Inventory}
 */
public final class SimpleBuilder extends BaseInventoryBuilder<Inventory, SimpleBuilder> {

    private InventoryType inventoryType;

    /**
     * Main constructor
     *
     * @param inventoryType The {@link InventoryType} to default to
     */
    public SimpleBuilder(JavaPlugin plugin, InventoryType inventoryType) {
        super(plugin);
        this.inventoryType = inventoryType;
    }

    /**
     * Sets the {@link InventoryType} to use on the Inventory
     * This method is unique to the simple Inventory
     *
     * @param inventoryType The {@link InventoryType}
     * @return The current builder
     */
    public SimpleBuilder type(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
        return this;
    }

    /**
     * Creates a new {@link Inventory}
     *
     * @return A new {@link Inventory}
     */
    @Override
    public Inventory create() {
        Inventory inventory;
        Component title = getTitle();

        if (this.inventoryType == InventoryType.CHEST) {
            inventory = new Inventory(this.plugin, getRows(), title, getModifiers());
        } else {
            inventory = new Inventory(this.plugin, this.inventoryType, title, getModifiers());
        }

        Consumer<Inventory> consumer = getConsumer();
        if (consumer != null)
            consumer.accept(inventory);

        return inventory;
    }
}
