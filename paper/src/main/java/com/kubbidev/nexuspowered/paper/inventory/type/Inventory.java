package com.kubbidev.nexuspowered.paper.inventory.type;

import com.kubbidev.nexuspowered.paper.inventory.builder.inventory.PaginatedBuilder;
import com.kubbidev.nexuspowered.paper.inventory.builder.inventory.SimpleBuilder;
import com.kubbidev.nexuspowered.paper.inventory.builder.inventory.StorageBuilder;
import com.kubbidev.nexuspowered.paper.inventory.components.InteractionModifier;
import com.kubbidev.nexuspowered.paper.inventory.components.InventoryType;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

/**
 * Standard Inventory implementation of {@link BaseInventory}
 */
public class Inventory extends BaseInventory {

    /**
     * Main constructor for the Inventory
     *
     * @param rows                 The amount of rows the Inventory should have
     * @param title                The Inventory's title using {@link String}
     * @param interactionModifiers A set containing the {@link InteractionModifier} this Inventory should use
     */
    public Inventory(JavaPlugin plugin, int rows, Component title, Set<InteractionModifier> interactionModifiers) {
        super(plugin, rows, title, interactionModifiers);
    }

    /**
     * Alternative constructor that takes both a {@link InventoryType} and a set of {@link InteractionModifier}
     *
     * @param inventoryType        The {@link InventoryType} to be used
     * @param title                The Inventory's title using {@link String}
     * @param interactionModifiers A set containing the {@link InteractionModifier} this Inventory should use
     */
    public Inventory(JavaPlugin plugin, InventoryType inventoryType, Component title, Set<InteractionModifier> interactionModifiers) {
        super(plugin, inventoryType, title, interactionModifiers);
    }

    /**
     * Creates a {@link SimpleBuilder} to build a {@link Inventory}
     *
     * @param type The {@link InventoryType} to be used
     * @return A {@link SimpleBuilder}
     */
    public static SimpleBuilder inventory(JavaPlugin plugin, InventoryType type) {
        return new SimpleBuilder(plugin, type);
    }

    /**
     * Creates a {@link SimpleBuilder} with CHEST as the {@link InventoryType}
     *
     * @return A CHEST {@link SimpleBuilder}
     */
    public static SimpleBuilder inventory(JavaPlugin plugin) {
        return inventory(plugin, InventoryType.CHEST);
    }

    /**
     * Creates a {@link StorageBuilder}.
     *
     * @return A CHEST {@link StorageBuilder}.
     */
    public static StorageBuilder storage(JavaPlugin plugin) {
        return new StorageBuilder(plugin);
    }

    /**
     * Creates a {@link PaginatedBuilder} to build a {@link PaginatedInventory}
     *
     * @return A {@link PaginatedBuilder}
     */
    public static PaginatedBuilder paginated(JavaPlugin plugin) {
        return new PaginatedBuilder(plugin);
    }
}
