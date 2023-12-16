package com.kubbidev.nexuspowered.paper.listeners;

import com.google.common.base.Preconditions;
import com.kubbidev.nexuspowered.paper.inventory.components.InteractionModifier;
import com.kubbidev.nexuspowered.paper.inventory.type.BaseInventory;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Listener that apply default inventory {@link InteractionModifier InteractionModifier}s to all inventory
 */
public final class InteractionModifierListener implements Listener {

    /**
     * Handles any click on inventory, applying all {@link InteractionModifier InteractionModifier} as required
     *
     * @param event The InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseInventory inventory))
            return;

        // if player is trying to do a disabled action, cancel it
        if ((!inventory.canPlaceItems() && isPlaceItemEvent(event)) ||
                (!inventory.canTakeItems() && isTakeItemEvent(event)) ||
                (!inventory.canSwapItems() && isSwapItemEvent(event)) ||
                (!inventory.canDropItems() && isDropItemEvent(event)) ||
                (!inventory.allowsOtherActions() && isOtherEvent(event))
        ) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    /**
     * Handles any item drag on inventory, applying all {@link InteractionModifier InteractionModifier} as required
     *
     * @param event The InventoryDragEvent
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseInventory inventory))
            return;

        // if players are allowed to place items on the inventory, or player is not dragging on inventory, return
        if (inventory.canPlaceItems() || !isDraggingOnInventory(event))
            return;

        // cancel the interaction
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }

    /**
     * Checks if what is happening on the {@link InventoryClickEvent} is take an item from the inventory
     *
     * @param event The InventoryClickEvent
     * @return True if the {@link InventoryClickEvent} is for taking an item from the inventory
     */
    private boolean isTakeItemEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();

        InventoryAction action = event.getAction();

        // magic logic, simplified version of https://paste.helpch.at/tizivomeco.cpp
        if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER || inventory.getType() == InventoryType.PLAYER) {
            return false;
        }

        return action == InventoryAction.MOVE_TO_OTHER_INVENTORY || isTakeAction(action);
    }

    /**
     * Checks if what is happening on the {@link InventoryClickEvent} is place an item on the inventory
     *
     * @param event The InventoryClickEvent
     * @return True if the {@link InventoryClickEvent} is for placing an item from the inventory
     */
    private boolean isPlaceItemEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();

        InventoryAction action = event.getAction();

        // shift click on item in player inventory
        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                && clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER
                && inventory.getType() != clickedInventory.getType()) {
            return true;
        }

        // normal click on inventory empty slot with item on cursor
        return isPlaceAction(action)
                && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER)
                && inventory.getType() != InventoryType.PLAYER;
    }

    /**
     * Checks if what is happening on the {@link InventoryClickEvent} is swap any item with an item from the inventory
     *
     * @param event The InventoryClickEvent
     * @return True if the {@link InventoryClickEvent} is for swapping any item with an item from the inventory
     */
    private boolean isSwapItemEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();

        return isSwapAction(action)
                && (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER)
                && inventory.getType() != InventoryType.PLAYER;
    }

    private boolean isDropItemEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();

        return isDropAction(action)
                && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
    }

    private boolean isOtherEvent(InventoryClickEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");

        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();

        return isOtherAction(action)
                && (clickedInventory != null || inventory.getType() != InventoryType.PLAYER);
    }

    /**
     * Checks if any item is being dragged on the inventory
     *
     * @param event The InventoryDragEvent
     * @return True if the {@link InventoryDragEvent} is for dragging an item inside the inventory
     */
    private boolean isDraggingOnInventory(InventoryDragEvent event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        int topSlots = event.getView().getTopInventory().getSize();
        // is dragging on any top inventory slot
        return event.getRawSlots().stream().anyMatch(slot -> slot < topSlots);
    }

    private boolean isTakeAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_TAKE_ACTIONS.contains(action);
    }

    private boolean isPlaceAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_PLACE_ACTIONS.contains(action);
    }

    private boolean isSwapAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_SWAP_ACTIONS.contains(action);
    }

    private boolean isDropAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return ITEM_DROP_ACTIONS.contains(action);
    }

    private boolean isOtherAction(InventoryAction action) {
        Preconditions.checkNotNull(action, "action cannot be null");
        return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
    }

    /**
     * Holds all the actions that should be considered "take" actions
     */
    private static final Set<InventoryAction> ITEM_TAKE_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.COLLECT_TO_CURSOR, InventoryAction.HOTBAR_SWAP, InventoryAction.MOVE_TO_OTHER_INVENTORY));

    /**
     * Holds all the actions that should be considered "place" actions
     */
    private static final Set<InventoryAction> ITEM_PLACE_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL));

    /**
     * Holds all actions relating to swapping items
     */
    private static final Set<InventoryAction> ITEM_SWAP_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.HOTBAR_SWAP, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.HOTBAR_MOVE_AND_READD));

    /**
     * Holds all actions relating to dropping items
     */
    private static final Set<InventoryAction> ITEM_DROP_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ALL_CURSOR));
}