package me.kubbidev.nexuspowered.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Useless implementation of {@link Slot} to fulfill not-null contracts.
 *
 * @param menu the parent menu
 * @param id  the id of this slot
 */
public record DummySlot(Menu menu, int id) implements Slot {

    public DummySlot(@NotNull Menu menu, int id) {
        this.menu = menu;
        this.id = id;
    }

    @Override
    public @NotNull Menu menu() {
        return this.menu;
    }

    @Override
    public Slot applyFromItem(Item item) {
        return this;
    }

    @Override
    public @Nullable ItemStack getItem() {
        return null;
    }

    @Override
    public boolean hasItem() {
        return false;
    }

    @Override
    public @NotNull Slot setItem(@NotNull ItemStack item) {
        return this;
    }

    @Override
    public Slot clear() {
        return this;
    }

    @Override
    public @NotNull Slot clearItem() {
        return this;
    }

    @Override
    public @NotNull Slot clearBindings() {
        return this;
    }

    @Override
    public @NotNull Slot clearBindings(ClickType type) {
        return this;
    }

    @Override
    public @NotNull Slot bind(@NotNull ClickType type, @NotNull Consumer<InventoryClickEvent> handler) {
        return this;
    }

    @Override
    public @NotNull Slot bind(@NotNull ClickType type, @NotNull Runnable handler) {
        return this;
    }

    @Override
    public @NotNull Slot bind(@NotNull Consumer<InventoryClickEvent> handler, @NotNull ClickType... types) {
        return this;
    }

    @Override
    public @NotNull Slot bind(@NotNull Runnable handler, @NotNull ClickType... types) {
        return this;
    }

    @Override
    public @NotNull <T extends Runnable> Slot bindAllRunnables(@NotNull Iterable<Map.Entry<ClickType, T>> handlers) {
        return this;
    }

    @Override
    public @NotNull <T extends Consumer<InventoryClickEvent>> Slot bindAllConsumers(@NotNull Iterable<Map.Entry<ClickType, T>> handlers) {
        return this;
    }
}