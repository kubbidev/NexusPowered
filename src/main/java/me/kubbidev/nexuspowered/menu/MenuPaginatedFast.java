package me.kubbidev.nexuspowered.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MenuPaginatedFast extends MenuPaginated<ItemStack> {
    private final List<ItemStack> contentItems = new ArrayList<>();

    private Consumer<InventoryClickEvent> contentHandler;
    private List<Integer> contentSlots;

    public MenuPaginatedFast(int size) {
        this(owner -> Bukkit.createInventory(owner, size));
    }

    public MenuPaginatedFast(int size, String title) {
        this(owner -> Bukkit.createInventory(owner, size, title));
    }

    public MenuPaginatedFast(InventoryType type) {
        this(owner -> Bukkit.createInventory(owner, type));
    }

    public MenuPaginatedFast(InventoryType type, String title) {
        this(owner -> Bukkit.createInventory(owner, type, title));
    }

    public MenuPaginatedFast(Function<InventoryHolder, Inventory> inventoryFunction) {
        super(inventoryFunction);
        this.contentSlots = IntStream.range(0, Math.max(9, getInventory().getSize() - 9))
                .boxed()
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemStack> contents() {
        return this.contentItems;
    }

    /**
     * Add an item to the paginated content with a click handler, the item will be added to the next available slot.
     *
     * @param item the item to add
     */
    public void addContent(ItemStack item) {
        this.contentItems.add(item);
    }

    /**
     * Add a list of items to the paginated content with click handlers, the items will be added to the next available slots.
     *
     * @param content the list of items to add
     */
    public void addContent(Collection<ItemStack> content) {
        Objects.requireNonNull(content, "content");
        this.contentItems.addAll(content);
    }

    /**
     * Set the item at the specified index of the paginated content, with a click handler.
     *
     * @param index the slot index
     * @param item  the item to set
     */
    public void setContent(int index, ItemStack item) {
        this.contentItems.set(index, item);
    }

    /**
     * Set the list of items as the paginated content, with click handlers. The previous content will be cleared.
     *
     * @param content the list of items to set
     */
    public void setContent(Collection<ItemStack> content) {
        Objects.requireNonNull(content, "content");
        clearContent();
        addContent(content);
    }

    /**
     * Clear the paginated content and the associated click handlers.
     */
    public void clearContent() {
        this.contentItems.clear();
    }

    public @Nullable Consumer<InventoryClickEvent> getContentHandler() {
        return this.contentHandler;
    }

    public void setContentHandler(@Nullable Consumer<InventoryClickEvent> handler) {
        this.contentHandler = handler;
    }

    @Override
    public List<Integer> contentSlots() {
        return this.contentSlots;
    }

    /**
     * Specify the slots of the inventory that will be used to display the paginated content.
     *
     * @param contentSlots the slots of the inventory to use
     */
    public void setContentSlots(List<Integer> contentSlots) {
        this.contentSlots = Objects.requireNonNull(contentSlots, "contentSlots");
    }

    @Override
    protected void setItem(int slot, Player viewer, ItemStack item) {
        setItem(slot, item, getContentHandler());
    }
}
