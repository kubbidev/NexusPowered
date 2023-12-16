package com.kubbidev.nexuspowered.paper.listeners;

import com.kubbidev.nexuspowered.paper.inventory.components.InventoryAction;
import com.kubbidev.nexuspowered.paper.inventory.type.BaseInventory;
import com.kubbidev.nexuspowered.paper.inventory.type.InventoryItem;
import com.kubbidev.nexuspowered.paper.inventory.type.PaginatedInventory;
import com.kubbidev.nexuspowered.paper.util.PersistentDataUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public final class InventoryListener implements Listener {

    /**
     * Handles what happens when a player clicks on the inventory
     *
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseInventory inventory))
            return;

        // Executes the outside click action
        InventoryAction<InventoryClickEvent> outsideClickAction = inventory.getOutsideClickAction();
        if (outsideClickAction != null && event.getClickedInventory() == null) {
            outsideClickAction.execute(event);
            return;
        }

        if (event.getClickedInventory() == null)
            return;

        // Default click action and checks weather or not there is a default action and executes it
        InventoryAction<InventoryClickEvent> defaultTopClick = inventory.getDefaultTopClickAction();
        if (defaultTopClick != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            defaultTopClick.execute(event);
        }

        // Default click action and checks weather or not there is a default action and executes it
        InventoryAction<InventoryClickEvent> playerInventoryClick = inventory.getPlayerInventoryAction();
        if (playerInventoryClick != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            playerInventoryClick.execute(event);
        }

        // Default click action and checks weather or not there is a default action and executes it
        InventoryAction<InventoryClickEvent> defaultClick = inventory.getDefaultClickAction();
        if (defaultClick != null)
            defaultClick.execute(event);

        // Slot action and checks weather or not there is a slot action and executes it
        InventoryAction<InventoryClickEvent> slotAction = inventory.getSlotAction(event.getSlot());
        if (slotAction != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            slotAction.execute(event);
        }

        InventoryItem item;

        // Checks whether it's a paginated inventory or not
        if (inventory instanceof PaginatedInventory paginatedInventory) {

            // Gets the inventory item from the added items or the page items
            item = paginatedInventory.getInventoryItem(event.getSlot());
            if (item == null)
                item = paginatedInventory.getPageItem(event.getSlot());

        } else {
            // The clicked inventory Item
            item = inventory.getInventoryItem(event.getSlot());
        }

        if (!isInventoryItem(event.getCurrentItem(), item))
            return;

        // Executes the action of the item
        InventoryAction<InventoryClickEvent> itemAction = item.getAction();
        if (itemAction != null)
            itemAction.execute(event);
    }

    /**
     * Handles what happens when a player clicks on the inventory
     *
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseInventory inventory))
            return;

        // Default click action and checks weather or not there is a default action and executes it
        InventoryAction<InventoryDragEvent> dragAction = inventory.getDragAction();
        if (dragAction != null)
            dragAction.execute(event);
    }

    /**
     * Handles what happens when the inventory is closed
     *
     * @param event The InventoryCloseEvent
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseInventory inventory))
            return;

        // The inventory action for closing
        InventoryAction<InventoryCloseEvent> closeAction = inventory.getCloseInventoryAction();
        // Checks if there is or not an action set and executes it
        if (closeAction != null && !inventory.isUpdating() && inventory.shouldRunCloseAction())
            closeAction.execute(event);
    }

    /**
     * Handles what happens when the inventory is opened
     *
     * @param event The InventoryOpenEvent
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseInventory inventory))
            return;

        // The inventory action for opening
        InventoryAction<InventoryOpenEvent> openAction = inventory.getOpenInventoryAction();
        // Checks if there is or not an action set and executes it
        if (openAction != null && !inventory.isUpdating() && inventory.shouldRunOpenAction())
            openAction.execute(event);
    }

    /**
     * Checks if the item is or not a inventory item
     *
     * @param currentItem The current item clicked
     * @param item     The inventory item in the slot
     * @return Whether it is or not a inventory item
     */
    private boolean isInventoryItem(ItemStack currentItem, InventoryItem item) {
        if (currentItem == null || item == null)
            return false;

        // Checks whether the Item is truly a inventory Item
        String nbt = PersistentDataUtils.getStringData(currentItem, InventoryItem.NEXUS_INVENTORY_ITEM);
        if (nbt == null)
            return false;
        return nbt.equals(item.getUniqueId().toString());
    }
}