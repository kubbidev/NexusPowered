package com.kubbidev.nexuspowered.paper.inventory.type;

import com.kubbidev.nexuspowered.paper.inventory.components.InteractionModifier;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Inventory that does not clear its items once it's closed
 */
public class StorageInventory extends BaseInventory {

    /**
     * Main constructor for the StorageInventory
     *
     * @param rows                 The amount of rows the Inventory should have
     * @param title                The Inventory's title using {@link String}
     * @param interactionModifiers A set containing the {@link InteractionModifier} this Inventory should use
     */
    public StorageInventory(JavaPlugin plugin, int rows, Component title, Set<InteractionModifier> interactionModifiers) {
        super(plugin, rows, title, interactionModifiers);
    }

    /**
     * Adds {@link ItemStack} to the inventory straight, not the Inventory
     *
     * @param items Varargs with {@link ItemStack}s
     * @return An immutable {@link Map} with the left overs
     */
    public Map<Integer, ItemStack> addItem(ItemStack... items) {
        return Collections.unmodifiableMap(getInventory().addItem(items));
    }

    /**
     * Adds {@link ItemStack} to the inventory straight, not the Inventory
     *
     * @param items Varargs with {@link ItemStack}s
     * @return An immutable {@link Map} with the left overs
     */
    public Map<Integer, ItemStack> addItem(List<ItemStack> items) {
        return addItem(items.toArray(new ItemStack[0]));
    }

    /**
     * Overridden {@link BaseInventory#open(HumanEntity)} to prevent
     *
     * @param player The {@link HumanEntity} to open the Inventory to
     */
    @Override
    public void open(HumanEntity player) {
        if (player.isSleeping())
            return;

        populateInventory();
        player.openInventory(getInventory());
    }
}
