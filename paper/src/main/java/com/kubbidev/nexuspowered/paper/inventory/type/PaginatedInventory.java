package com.kubbidev.nexuspowered.paper.inventory.type;

import com.kubbidev.nexuspowered.paper.inventory.components.InteractionModifier;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PaginatedInventory extends BaseInventory {

    // Saves the current page items and it's slot
    private final Map<Integer, Map<Integer, InventoryItem>> pagesItems;
    private final Map<Integer, InventoryItem> currentPage;

    private int pageNum = 0;

    /**
     * Main constructor to provide a way to create PaginatedInventory
     *
     * @param rows                 The amount of rows the Inventory should have
     * @param title                The Inventory's title using {@link String}
     * @param interactionModifiers A set containing what {@link InteractionModifier} this Inventory should have
     */
    public PaginatedInventory(JavaPlugin plugin, int rows, Component title, Set<InteractionModifier> interactionModifiers) {
        super(plugin, rows, title, interactionModifiers);
        int inventorySize = rows * 9;

        this.currentPage = new LinkedHashMap<>(inventorySize);

        // Create pages items and add default page 0
        this.pagesItems = new LinkedHashMap<>();
        this.pagesItems.computeIfAbsent(0, HashMap::new);
    }

    /**
     * Adds an {@link InventoryItem} to the given slot on the given page
     *
     * @param item The {@link InventoryItem} to add to the page
     * @param slot slot to add item
     * @param page to add the item on
     */
    public void setItem(InventoryItem item, int slot, int page) {
        Map<Integer, InventoryItem> itemPage = this.pagesItems.computeIfAbsent(page, HashMap::new);
        itemPage.put(slot, item);

        this.pagesItems.put(page, itemPage);
    }

    /**
     * Overridden {@link BaseInventory#update()} to use the paginated open
     */
    @Override
    public void update() {
        getInventory().clear();
        populateInventory();

        updatePage();
    }

    /**
     * Updates the page {@link InventoryItem} on the slot in the page
     * Can get the slot from {@link InventoryClickEvent#getSlot()}
     *
     * @param itemStack The new {@link ItemStack}
     * @param slot      The slot of the item to update
     */
    public void updatePageItem(ItemStack itemStack, int slot) {

        if (!this.currentPage.containsKey(slot))
            return;

        InventoryItem inventoryItem = this.currentPage.get(slot);

        inventoryItem.setItemStack(itemStack);
        getInventory().setItem(slot, inventoryItem.getItemStack());
    }

    /**
     * Alternative {@link #updatePageItem(ItemStack, int)} that uses <i>ROWS</i> and <i>COLUMNS</i> instead
     *
     * @param itemStack The new {@link ItemStack}
     * @param row       The row of the slot
     * @param col       The columns of the slot
     */
    public void updatePageItem(ItemStack itemStack, int row, int col) {
        updateItem(itemStack, getSlotFromRowCol(row, col));
    }

    /**
     * Overrides {@link BaseInventory#open(HumanEntity)} to use the paginated populator instead
     *
     * @param player The {@link HumanEntity} to open the Inventory to
     */
    @Override
    public void open(HumanEntity player) {
        open(player, 0);
    }

    /**
     * Specific open method for the Paginated Inventory
     * Uses {@link #populatePage()}
     *
     * @param player   The {@link HumanEntity} to open it to
     * @param openPage The specific page to open at
     */
    public void open(HumanEntity player, int openPage) {
        if (player.isSleeping())
            return;

        if (openPage < getPagesNum() || openPage >= 0)
            this.pageNum = openPage;
        this.currentPage.clear();

        getInventory().clear();

        populateInventory();
        populatePage();

        player.openInventory(getInventory());
    }

    /**
     * Overrides {@link BaseInventory#updateTitle(Component)} to use the paginated populator instead
     * Updates the title of the Inventory
     * <i>This method may cause LAG if used on a loop</i>
     *
     * @param title The title to set
     * @return The Inventory for easier use when declaring, works like a builder
     */
    @Override
    public BaseInventory updateTitle(Component title) {
        setUpdating(true);
        List<HumanEntity> viewers = new ArrayList<>(getInventory().getViewers());

        setInventory(Bukkit.createInventory(this, getInventory().getSize(), title));

        for (HumanEntity player : viewers) {
            open(player, getPageNum());
        }

        setUpdating(false);

        return this;
    }

    /**
     * Gets an immutable {@link Map} with all the current pages items
     *
     * @return The {@link Map} with all the {@link #currentPage}
     */
    public Map<Integer, InventoryItem> getCurrentPageItems() {
        return Collections.unmodifiableMap(this.currentPage);
    }


    /**
     * Gets the current page number
     *
     * @return The current page number
     */
    public int getCurrentPageNum() {
        return this.pageNum;
    }

    /**
     * Gets the next page number
     *
     * @return The next page number or {@link #pageNum} if no next is present
     */
    public int getNextPageNum() {
        if (this.pageNum + 1 >= getPagesNum())
            return this.pageNum;
        return this.pageNum + 1;
    }

    /**
     * Gets the previous page number
     *
     * @return The previous page number or {@link #pageNum} if no previous is present
     */
    public int getPrevPageNum() {
        if (this.pageNum == 0)
            return this.pageNum;
        return this.pageNum - 1;
    }

    /**
     * Gets if there is a next page
     *
     * @return False if there is no next page.
     */
    public boolean hasNext() {
        return this.pageNum + 1 < getPagesNum();
    }

    /**
     * Gets if there is a previous page
     *
     * @return False if there is no previous page.
     */
    public boolean hasPrevious() {
        return this.pageNum != 0;
    }

    /**
     * Goes to the next page
     *
     * @return False if there is no next page.
     */
    public boolean next() {
        if (this.pageNum + 1 >= getPagesNum())
            return false;

        this.pageNum++;
        updatePage();
        return true;
    }

    /**
     * Goes to the previous page if possible
     *
     * @return False if there is no previous page.
     */
    public boolean previous() {
        if (this.pageNum == 0)
            return false;

        this.pageNum--;
        updatePage();
        return true;
    }

    /**
     * Gets the page item for the Inventory listener
     *
     * @param slot The slot to get
     * @return The InventoryItem on that slot
     */
    public InventoryItem getPageItem(int slot) {
        return this.currentPage.get(slot);
    }

    /**
     * Gets the page item from page for the Inventory listener
     *
     * @param page page to get item
     * @param slot The slot to get
     * @return The InventoryItem on that slot on that page
     */
    public InventoryItem getPageItem(int slot, int page) {
        return this.pagesItems.get(page).get(slot);
    }

    /**
     * Gets the items in the page
     *
     * @param givenPage The page to get
     * @return A list with all the page items
     */
    private Map<Integer, InventoryItem> getPageNum(int givenPage) {
        return this.pagesItems.containsKey(givenPage) ? this.pagesItems.get(givenPage) : new HashMap<>();
    }

    /**
     * Gets the number of pages the Inventory has
     *
     * @return The pages number
     */
    public int getPagesNum() {
        return this.pagesItems.size();
    }

    /**
     * Populates the inventory with the page items
     */
    private void populatePage() {
        // Adds the paginated items to the page
        getPageNum(this.pageNum).forEach((slot, inventoryItem) -> {


            if (getInventoryItem(slot) != null || getInventory().getItem(slot) != null)
                return;

            this.currentPage.put(slot, inventoryItem);
            getInventory().setItem(slot, inventoryItem.getItemStack());
        });
    }

    /**
     * Gets the current page items to be used on other inventory types
     *
     * @return The {@link Map} with all the {@link #currentPage}
     */
    Map<Integer, InventoryItem> getMutableCurrentPageItems() {
        return this.currentPage;
    }

    /**
     * Clears the page content
     */
    void clearPage() {
        for (Map.Entry<Integer, InventoryItem> entry : this.currentPage.entrySet()) {
            getInventory().setItem(entry.getKey(), null);
        }
    }

    /**
     * Clears all previously added page items
     */
    public void clearPageItems(boolean update) {
        this.pagesItems.clear();

        if (update)
            update();
    }

    public void clearPageItems() {
        clearPageItems(false);
    }

    /**
     * Gets the page number
     *
     * @return The current page number
     */
    int getPageNum() {
        return this.pageNum;
    }

    /**
     * Sets the page number
     *
     * @param pageNum Sets the current page to be the specified number
     */
    void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * Updates the page content
     */
    void updatePage() {
        clearPage();
        populatePage();
    }
}
