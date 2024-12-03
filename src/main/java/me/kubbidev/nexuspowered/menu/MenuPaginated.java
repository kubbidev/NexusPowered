package me.kubbidev.nexuspowered.menu;

import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class MenuPaginated<T> extends Menu {
    private int page = 1;

    private IntFunction<ItemStack> prevPageItem;
    private IntFunction<ItemStack> nextPageItem;
    private int prevPageSlot = -1;
    private int nextPageSlot = -1;

    public MenuPaginated(int size) {
        super(size);
    }

    public MenuPaginated(int size, String title) {
        super(size, title);
    }

    public MenuPaginated(InventoryType type) {
        super(type);
    }

    public MenuPaginated(InventoryType type, String title) {
        super(type, title);
    }

    public MenuPaginated(Function<InventoryHolder, Inventory> inventoryFunction) {
        super(inventoryFunction);
    }

    /**
     * Gets a list containing all pages item, different from the actual page items.
     *
     * @return a {@link List} of {@link T} objects.
     */
    public abstract List<T> contents();

    /**
     * Retrieves a list of integers representing the slot indices
     * within the inventory where the content for the current page is displayed.
     *
     * @return a {@link List} of {@link Integer} objects, each representing an
     * inventory slot index used for displaying content on the current page.
     */
    public abstract List<Integer> contentSlots();

    /**
     * Set the item at the specified inventory slot to open the previous page.
     *
     * @param slot the inventory to set the item
     * @param item a function to get the item to set, with the page the item opens as parameter
     */
    public void prevPageItem(int slot, IntFunction<ItemStack> item) {
        if (slot < 0 || slot >= getInventory().getSize()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }

        this.prevPageSlot = slot;
        this.prevPageItem = item;
    }

    /**
     * Set the item at the specified inventory slot to open the previous page.
     *
     * @param slot the inventory to set the item
     * @param item the item to set
     */
    public void prevPageItem(int slot, ItemStack item) {
        prevPageItem(slot, page -> item);
    }

    /**
     * Set the item at the specified inventory slot to open the next page.
     *
     * @param slot the inventory to set the item
     * @param item a function to get the item to set, with the page the item opens as parameter
     */
    public void nextPageItem(int slot, IntFunction<ItemStack> item) {
        if (slot < 0 || slot >= getInventory().getSize()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }

        this.nextPageSlot = slot;
        this.nextPageItem = item;
    }

    /**
     * Set the item at the specified inventory slot to open the next page.
     *
     * @param slot the inventory to set the item
     * @param item the item to set
     */
    public void nextPageItem(int slot, ItemStack item) {
        nextPageItem(slot, page -> item);
    }

    /**
     * Called when the page is changed.
     *
     * @param page the new page
     */
    protected void onPageChange(int page) {
    }

    protected abstract void setItem(int slot, Player viewer, T item);

    @Override
    protected void redraw(Player viewer) {
        List<Integer> slots = new ArrayList<>(contentSlots());
        List<List<T>> pages = Lists.partition(contents(), slots.size());

        normalizePage(pages.size());
        if (!isFirstDraw()) {
            slots.forEach(this::removeItem);
        }

        List<T> currentPage = pages.isEmpty() ? new ArrayList<>() : pages.get(this.page - 1);
        for (T item : currentPage) {
            setItem(slots.remove(0), viewer, item);
        }

        drawPageItems(pages.size());
    }

    protected void drawPageItems(int maxPages) {
        if (this.page > 1 && this.prevPageItem != null) {
            setItem(this.prevPageSlot, this.prevPageItem.apply(this.page - 1),
                    e -> prevPage((Player) e.getWhoClicked()));

        } else if (this.prevPageSlot >= 0) {
            removeItem(this.prevPageSlot);
        }

        if (this.page < maxPages && this.nextPageItem != null) {
            setItem(this.nextPageSlot, this.nextPageItem.apply(this.page + 1),
                    e -> nextPage((Player) e.getWhoClicked()));

        } else if (this.nextPageSlot >= 0) {
            removeItem(this.nextPageSlot);
        }
    }

    protected void normalizePage(int maxPages) {
        this.page = Math.max(1, Math.min(this.page, maxPages));
    }

    public void nextPage(Player viewer) {
        drawPage(viewer, ++this.page);
    }

    public void prevPage(Player viewer) {
        drawPage(viewer, --this.page);
    }

    public void drawPage(Player viewer, int page) {
        this.page = page;
        redraw(viewer);
    }

    public int currentPage() {
        return this.page;
    }

    public boolean isFirstPage() {
        return this.page == 1;
    }

    public boolean isLastPage() {
        return this.page == maxPages();
    }

    public int maxPages() {
        return IntMath.divide(contents().size(), contentSlots().size(),
                RoundingMode.CEILING);
    }
}