package com.kubbidev.nexuspowered.paper.inventory.components.utils;

import com.kubbidev.nexuspowered.paper.inventory.components.InventoryType;
import com.kubbidev.nexuspowered.paper.inventory.components.execption.InventoryException;
import com.kubbidev.nexuspowered.paper.inventory.type.BaseInventory;
import com.kubbidev.nexuspowered.paper.inventory.type.InventoryItem;
import com.kubbidev.nexuspowered.paper.inventory.type.PaginatedInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryFiller {

    private final BaseInventory inventory;

    public InventoryFiller(BaseInventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Fills top portion of the Inventory
     *
     * @param item InventoryItem
     */
    public void fillTop(InventoryItem item) {
        fillTop(Collections.singletonList(item));
    }

    /**
     * Fills top portion of the Inventory with alternation
     *
     * @param invItems List of InventoryItems
     */
    public void fillTop(List<InventoryItem> invItems) {
        List<InventoryItem> items = repeatList(invItems);

        for (int i = 0; i < 9; i++) {
            if (!this.inventory.getInvItems().containsKey(i))
                this.inventory.setItem(i, items.get(i));
        }
    }

    /**
     * Fills bottom portion of the Inventory
     *
     * @param invItem InventoryItem
     */
    public void fillBottom(InventoryItem invItem) {
        fillBottom(Collections.singletonList(invItem));
    }

    /**
     * Fills bottom portion of the Inventory with alternation
     *
     * @param invItems InventoryItem
     */
    public void fillBottom(List<InventoryItem> invItems) {

        List<InventoryItem> items = repeatList(invItems);

        int rows = this.inventory.getRows();
        for (int i = 9; i > 0; i--) {

            if (this.inventory.getInvItems().get((rows * 9) - i) == null) {
                this.inventory.setItem((rows * 9) - i, items.get(i));
            }
        }
    }

    /**
     * Fills the outside section of the Inventory with a InventoryItem
     *
     * @param invItem InventoryItem
     */
    public void fillBorder(InventoryItem invItem) {
        fillBorder(Collections.singletonList(invItem));
    }

    /**
     * Fill empty slots with Multiple InventoryItems, goes through list and starts again
     *
     * @param invItems InventoryItems
     */
    public void fillBorder(List<InventoryItem> invItems) {
        int rows = this.inventory.getRows();
        if (rows <= 2)
            return;

        List<InventoryItem> items = repeatList(invItems);

        for (int i = 0; i < rows * 9; i++) {
            if ((i <= 8)
                    || (i >= (rows * 9) - 8) && (i <= (rows * 9) - 2)
                    || i % 9 == 0
                    || i % 9 == 8)
                this.inventory.setItem(i, items.get(i));

        }
    }

    /**
     * Fills rectangle from points within the Inventory
     *
     * @param rowFrom Row point 1
     * @param colFrom Col point 1
     * @param rowTo   Row point 2
     * @param colTo   Col point 2
     * @param invItem Item to fill with
     * @author Harolds
     */
    public void fillBetweenPoints(int rowFrom, int colFrom, int rowTo, int colTo, InventoryItem invItem) {
        fillBetweenPoints(rowFrom, colFrom, rowTo, colTo, Collections.singletonList(invItem));
    }

    /**
     * Fills rectangle from points within the Inventory
     *
     * @param rowFrom  Row point 1
     * @param colFrom  Col point 1
     * @param rowTo    Row point 2
     * @param colTo    Col point 2
     * @param inItems Item to fill with
     * @author Harolds
     */
    public void fillBetweenPoints(int rowFrom, int colFrom, int rowTo, int colTo, List<InventoryItem> inItems) {
        int minRow = Math.min(rowFrom, rowTo);
        int maxRow = Math.max(rowFrom, rowTo);
        int minCol = Math.min(colFrom, colTo);
        int maxCol = Math.max(colFrom, colTo);

        List<InventoryItem> items = repeatList(inItems);

        int rows = this.inventory.getRows();
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= 9; col++) {

                int slot = getSlotFromRowCol(row, col);
                if (!((row >= minRow && row <= maxRow) && (col >= minCol && col <= maxCol)))
                    continue;

                this.inventory.setItem(slot, items.get(slot));
            }
        }
    }

    /**
     * Sets an InventoryItem to fill up the entire inventory where there is no other item
     *
     * @param invItem The item to use as fill
     */
    public void fill(InventoryItem invItem) {
        fill(Collections.singletonList(invItem));
    }

    /**
     * Fill empty slots with Multiple InventoryItems, goes through list and starts again
     *
     * @param invItems InventoryItems
     */
    public void fill(List<InventoryItem> invItems) {
        if (this.inventory instanceof PaginatedInventory) {
            throw new InventoryException("Full filling an Inventory is not supported in a Paginated Inventory!");
        }

        InventoryType type = this.inventory.inventoryType();

        int fill;
        if (type == InventoryType.CHEST) {
            fill = this.inventory.getRows() * type.getLimit();
        } else {
            fill = type.getLimit();
        }

        List<InventoryItem> items = repeatList(invItems);
        for (int i = 0; i < fill; i++) {

            if (this.inventory.getInvItems().get(i) == null)
                this.inventory.setItem(i, items.get(i));
        }
    }

    /**
     * Repeats a list of items. Allows for alternating items
     * Stores references to existing objects -> Does not create new objects
     *
     * @param invItems List of items to repeat
     * @return New list
     */
    private List<InventoryItem> repeatList(List<InventoryItem> invItems) {
        List<InventoryItem> repeated = new ArrayList<>();

        Collections.nCopies(this.inventory.getRows() * 9, invItems).forEach(repeated::addAll);
        return repeated;
    }

    /**
     * Gets the slot from the row and col passed
     *
     * @param row The row
     * @param col The col
     * @return The new slot
     */
    private int getSlotFromRowCol(int row, int col) {
        return (col + (row - 1) * 9) - 1;
    }
}
