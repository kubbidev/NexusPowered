package me.kubbidev.nexuspowered.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a slot in a {@link Menu}.
 * <p>
 * All changes made to this object are applied to the backing Menu instance, and vice versa.
 */
public interface Slot {

    /**
     * Gets the Menu this slot references.
     *
     * @return the parent menu
     */
    @NotNull
    Menu menu();

    /**
     * Gets the id of this slot.
     *
     * @return the id
     */
    int id();

    /**
     * Applies an item model to this slot.
     *
     * @param item the item
     * @return this slot
     */
    Slot applyFromItem(Item item);

    /**
     * Gets the item in this slot.
     *
     * @return the item in this slot
     */
    @Nullable
    ItemStack getItem();

    /**
     * Gets if this slot has an item.
     *
     * @return true if this slot has an item
     */
    boolean hasItem();

    /**
     * Sets the item in this slot.
     *
     * @param item the new item
     * @return this slot
     */
    @NotNull
    Slot setItem(@NotNull ItemStack item);

    /**
     * Clears all attributes of the slot.
     *
     * @return this slot
     */
    Slot clear();

    /**
     * Clears the item in this slot
     *
     * @return this slot
     */
    @NotNull
    Slot clearItem();

    /**
     * Clears all bindings on this slot.
     *
     * @return this slot
     */
    @NotNull
    Slot clearBindings();

    /**
     * Clears all bindings on this slot for a given click type.
     *
     * @return this slot
     */
    @NotNull
    Slot clearBindings(ClickType type);

    @NotNull
    Slot bind(@NotNull ClickType type, @NotNull Consumer<InventoryClickEvent> handler);

    @NotNull
    Slot bind(@NotNull ClickType type, @NotNull Runnable handler);

    @NotNull
    Slot bind(@NotNull Consumer<InventoryClickEvent> handler, @NotNull ClickType... types);

    @NotNull
    Slot bind(@NotNull Runnable handler, @NotNull ClickType... types);

    @NotNull
    <T extends Runnable> Slot bindAllRunnables(@NotNull Iterable<Map.Entry<ClickType, T>> handlers);

    @NotNull
    <T extends Consumer<InventoryClickEvent>> Slot bindAllConsumers(@NotNull Iterable<Map.Entry<ClickType, T>> handlers);
}