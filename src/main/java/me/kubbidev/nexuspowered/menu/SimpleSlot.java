package me.kubbidev.nexuspowered.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Basic implementation of {@link Slot}.
 */
public class SimpleSlot implements Slot {

    // the parent menu
    private final Menu menu;

    // the id of this slot
    private final int id;

    // the click handlers for this slot
    protected final Map<ClickType, Set<Consumer<InventoryClickEvent>>> handlers;

    public SimpleSlot(@NotNull Menu menu, int id) {
        this.menu = menu;
        this.id = id;
        this.handlers = Collections.synchronizedMap(new EnumMap<>(ClickType.class));
    }

    public void handle(@NotNull InventoryClickEvent event) {
        Set<Consumer<InventoryClickEvent>> handlers = this.handlers.get(event.getClick());
        if (handlers == null) {
            return;
        }
        for (Consumer<InventoryClickEvent> handler : handlers) {
            try {
                handler.accept(event);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public @NotNull Menu menu() {
        return this.menu;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public Slot applyFromItem(Item item) {
        Objects.requireNonNull(item, "item");
        setItem(item.itemStack());
        clearBindings();
        bindAllConsumers(item.handlers().entrySet());
        return this;
    }

    @Override
    public @Nullable ItemStack getItem() {
        return this.menu.getHandle().getItem(this.id);
    }

    @Override
    public boolean hasItem() {
        return getItem() != null;
    }

    @Override
    public @NotNull Slot setItem(@NotNull ItemStack item) {
        Objects.requireNonNull(item, "item");
        this.menu.getHandle().setItem(this.id, item);
        return this;
    }

    @Override
    public Slot clear() {
        clearItem();
        clearBindings();
        return this;
    }

    @Override
    public @NotNull Slot clearItem() {
        this.menu.getHandle().clear(this.id);
        return this;
    }

    @Override
    public @NotNull Slot clearBindings() {
        this.handlers.clear();
        return this;
    }

    @Override
    public @NotNull Slot clearBindings(ClickType type) {
        this.handlers.remove(type);
        return this;
    }

    @Override
    public @NotNull Slot bind(@NotNull ClickType type, @NotNull Consumer<InventoryClickEvent> handler) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
        this.handlers.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet()).add(handler);
        return this;
    }

    @Override
    public @NotNull Slot bind(@NotNull ClickType type, @NotNull Runnable handler) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
        this.handlers.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet()).add(Item.transformRunnable(handler));
        return this;
    }

    @Override
    public @NotNull Slot bind(@NotNull Consumer<InventoryClickEvent> handler, @NotNull ClickType... types) {
        for (ClickType type : types) {
            bind(type, handler);
        }
        return this;
    }

    @Override
    public @NotNull Slot bind(@NotNull Runnable handler, @NotNull ClickType... types) {
        for (ClickType type : types) {
            bind(type, handler);
        }
        return this;
    }

    @Override
    public @NotNull <T extends Runnable> Slot bindAllRunnables(@NotNull Iterable<Map.Entry<ClickType, T>> handlers) {
        Objects.requireNonNull(handlers, "handlers");
        for (Map.Entry<ClickType, T> handler : handlers) {
            bind(handler.getKey(), handler.getValue());
        }
        return this;
    }

    @Override
    public @NotNull <T extends Consumer<InventoryClickEvent>> Slot bindAllConsumers(@NotNull Iterable<Map.Entry<ClickType, T>> handlers) {
        Objects.requireNonNull(handlers, "handlers");
        for (Map.Entry<ClickType, T> handler : handlers) {
            bind(handler.getKey(), handler.getValue());
        }
        return this;
    }
}