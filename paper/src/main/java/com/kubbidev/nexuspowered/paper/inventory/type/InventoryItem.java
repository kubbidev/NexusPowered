package com.kubbidev.nexuspowered.paper.inventory.type;

import com.kubbidev.nexuspowered.paper.PaperNexusEngine;
import com.kubbidev.nexuspowered.paper.inventory.components.InventoryAction;
import com.kubbidev.nexuspowered.paper.util.PersistentDataUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * InventoryItem represents the {@link ItemStack} on the {@link Inventory}
 */
public class InventoryItem {

    public static final NamespacedKey NEXUS_INVENTORY_ITEM = PaperNexusEngine.getInstance().key("NEXUS-INVENTORY-ITEM");
    // Random UUID to identify the item when clicking
    private final UUID uniqueId = UUID.randomUUID();

    // Action to do when clicking on the item
    @Nullable
    private InventoryAction<InventoryClickEvent> action;

    // The ItemStack of the InventoryItem
    private ItemStack itemStack;


    /**
     * Main constructor of the InventoryItem
     *
     * @param itemStack The {@link ItemStack} to be used
     * @param action    The {@link InventoryAction} to run when clicking on the Item
     */
    public InventoryItem(ItemStack itemStack, @Nullable InventoryAction<InventoryClickEvent> action) {
        Validate.notNull(itemStack, "The ItemStack for the Inventory Item cannot be null!");

        this.action = action;
        this.itemStack = itemStack;
        // Sets the UUID to an NBT tag to be identifiable later
        PersistentDataUtils.setData(itemStack, InventoryItem.NEXUS_INVENTORY_ITEM, this.uniqueId);
    }

    /**
     * Secondary constructor with no action
     *
     * @param itemStack The ItemStack to be used
     */
    public InventoryItem(ItemStack itemStack) {
        this(itemStack, null);
    }

    /**
     * Alternate constructor that takes {@link Material} instead of an {@link ItemStack} but without a {@link InventoryAction}
     *
     * @param material The {@link Material} to be used when invoking class
     */
    public InventoryItem(Material material) {
        this(new ItemStack(material), null);
    }

    /**
     * Alternate constructor that takes {@link Material} instead of an {@link ItemStack}
     *
     * @param material The {@code Material} to be used when invoking class
     * @param action   The {@link InventoryAction} should be passed on {@link InventoryClickEvent}
     */
    public InventoryItem(Material material, InventoryAction<InventoryClickEvent> action) {
        this(new ItemStack(material), action);
    }

    /**
     * Replaces the {@link ItemStack} of the Inventory Item
     *
     * @param itemStack The new {@link ItemStack}
     */
    public void setItemStack(ItemStack itemStack) {
        Validate.notNull(itemStack, "The ItemStack for the Inventory Item cannot be null!");
        PersistentDataUtils.setData(itemStack, InventoryItem.NEXUS_INVENTORY_ITEM, this.uniqueId);

        this.itemStack = itemStack;
    }

    /**
     * Replaces the {@link InventoryAction} of the current Inventory Item
     *
     * @param action The new {@link InventoryAction} to set
     */
    public InventoryItem setAction(InventoryAction<InventoryClickEvent> action) {
        this.action = action;
        return this;
    }

    /**
     * Gets the InventoryItem's {@link ItemStack}
     *
     * @return The {@link ItemStack}
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * Gets the random {@link UUID} that was generated when the InventoryItem was made
     */
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    /**
     * Gets the {@link InventoryAction} to do when the player clicks on it
     */
    @Nullable
    public InventoryAction<InventoryClickEvent> getAction() {
        return this.action;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof InventoryItem item))
            return false;

        return item.uniqueId.equals(this.uniqueId);
    }
}
