package me.kubbidev.nexuspowered.menu;

import com.google.common.collect.ImmutableMap;
import me.kubbidev.nexuspowered.util.Delegates;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The initial model of a clickable item in a {@link Menu}. Immutable.
 *
 * @param handlers  the click handlers for this item
 * @param itemStack the backing item stack
 */
public record Item(Map<ClickType, Consumer<InventoryClickEvent>> handlers, ItemStack itemStack) {

    @NotNull
    public static Item.Builder builder(@NotNull ItemStack itemStack) {
        return new Builder(itemStack);
    }

    public Item(@NotNull Map<ClickType, Consumer<InventoryClickEvent>> handlers, @NotNull ItemStack itemStack) {
        this.handlers = ImmutableMap.copyOf(Objects.requireNonNull(handlers, "handlers"));
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
    }

    /**
     * Gets the click handlers for this Item.
     *
     * @return the click handlers
     */
    @Override
    @NotNull
    public Map<ClickType, Consumer<InventoryClickEvent>> handlers() {
        return this.handlers;
    }

    /**
     * Gets the ItemStack backing this Item.
     *
     * @return the backing item stack
     */
    @Override
    @NotNull
    public ItemStack itemStack() {
        return this.itemStack;
    }

    /**
     * Aids creation of {@link Item} instances.
     */
    public static final class Builder {
        private final ItemStack itemStack;
        private final Map<ClickType, Consumer<InventoryClickEvent>> handlers;

        private Builder(@NotNull ItemStack itemStack) {
            this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
            this.handlers = new HashMap<>();
        }

        @NotNull
        public Builder bind(@NotNull ClickType type, @Nullable Consumer<InventoryClickEvent> handler) {
            Objects.requireNonNull(type, "type");
            if (handler != null) {
                this.handlers.put(type, handler);
            } else {
                this.handlers.remove(type);
            }
            return this;
        }

        @NotNull
        public Builder bind(@NotNull ClickType type, @Nullable Runnable handler) {
            Objects.requireNonNull(type, "type");
            if (handler != null) {
                this.handlers.put(type, transformRunnable(handler));
            } else {
                this.handlers.remove(type);
            }
            return this;
        }

        @NotNull
        public Builder bind(@Nullable Consumer<InventoryClickEvent> handler, @NotNull ClickType... types) {
            for (ClickType type : types) {
                bind(type, handler);
            }
            return this;
        }

        @NotNull
        public Builder bind(@Nullable Runnable handler, @NotNull ClickType... types) {
            for (ClickType type : types) {
                bind(type, handler);
            }
            return this;
        }

        @NotNull
        public <T extends Runnable> Builder bindAllRunnables(@NotNull Iterable<Map.Entry<ClickType, T>> handlers) {
            Objects.requireNonNull(handlers, "handlers");
            for (Map.Entry<ClickType, T> handler : handlers) {
                bind(handler.getKey(), handler.getValue());
            }
            return this;
        }

        @NotNull
        public <T extends Consumer<InventoryClickEvent>> Builder bindAllConsumers(@NotNull Iterable<Map.Entry<ClickType, T>> handlers) {
            Objects.requireNonNull(handlers, "handlers");
            for (Map.Entry<ClickType, T> handler : handlers) {
                bind(handler.getKey(), handler.getValue());
            }
            return this;
        }

        @NotNull
        public Item build() {
            return new Item(this.handlers, this.itemStack);
        }
    }

    @NotNull
    public static Consumer<InventoryClickEvent> transformRunnable(@NotNull Runnable runnable) {
        return Delegates.runnableToConsumer(runnable);
    }
}