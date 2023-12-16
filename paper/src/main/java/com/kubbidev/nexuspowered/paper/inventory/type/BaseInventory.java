package com.kubbidev.nexuspowered.paper.inventory.type;

import com.kubbidev.nexuspowered.paper.inventory.components.InteractionModifier;
import com.kubbidev.nexuspowered.paper.inventory.components.InventoryAction;
import com.kubbidev.nexuspowered.paper.inventory.components.InventoryType;
import com.kubbidev.nexuspowered.paper.inventory.components.utils.InventoryFiller;
import com.kubbidev.nexuspowered.paper.inventory.components.execption.InventoryException;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Base class that every Inventory extends.
 * Contains all the basics for the Inventory to work.
 * Main and simplest implementation of this is {@link Inventory}.
 */
public abstract class BaseInventory implements InventoryHolder {

    private final JavaPlugin plugin;

    // Main inventory.
    private org.bukkit.inventory.Inventory inventory;
    private Component title;

    // Inventory filler.
    private final InventoryFiller filler = new InventoryFiller(this);

    private int rows = 1;

    // Inventory type, defaults to chest.
    private InventoryType inventoryType = InventoryType.CHEST;

    private final Map<Integer, InventoryItem> invItems; // Contains all items the Inventory will have.
    private final Map<Integer, InventoryAction<InventoryClickEvent>> slotActions; // Actions for specific slots.
    private final Set<InteractionModifier> interactionModifiers; // Interaction modifiers.

    private InventoryAction<InventoryClickEvent> defaultClickAction; // Action to execute when clicking on any item.
    private InventoryAction<InventoryClickEvent> defaultTopClickAction; // Action to execute when clicking on the top part of the Inventory only.
    private InventoryAction<InventoryClickEvent> playerInventoryAction; // Action to execute when clicking on the player Inventory.
    private InventoryAction<InventoryDragEvent> dragAction; // Action to execute when dragging the item on the Inventory.
    private InventoryAction<InventoryCloseEvent> closeInventoryAction; // Action to execute when Inventory closes.
    private InventoryAction<InventoryOpenEvent> openInventoryAction; // Action to execute when Inventory opens.
    private InventoryAction<InventoryClickEvent> outsideClickAction; // Action to execute when clicked outside the Inventory.

    // Whether the Inventory is updating.
    private boolean updating;

    // Whether should run the actions from the close and open methods.
    private boolean runCloseAction = true;
    private boolean runOpenAction = true;

    /**
     * The main constructor, using {@link String}.
     *
     * @param rows                 The amount of rows to use.
     * @param title                The Inventory title using {@link Component}.
     * @param interactionModifiers Modifiers to select which interactions are allowed.
     */
    public BaseInventory(JavaPlugin plugin, int rows, Component title, Set<InteractionModifier> interactionModifiers) {
        this.plugin = plugin;

        int finalRows = rows;
        if (!(rows >= 1 && rows <= 6))
            finalRows = 1;

        this.rows = finalRows;
        this.interactionModifiers = safeCopyOf(interactionModifiers);
        this.title = title;

        int inventorySize = rows * 9;

        this.inventory = Bukkit.createInventory(this, inventorySize, this.title);
        this.slotActions = new LinkedHashMap<>(inventorySize);
        this.invItems = new LinkedHashMap<>(inventorySize);
    }

    /**
     * Alternative constructor that takes {@link InventoryType} instead of rows number.
     *
     * @param inventoryType              The {@link InventoryType} to use.
     * @param title                The Inventory title using {@link String}.
     * @param interactionModifiers Modifiers to select which interactions are allowed.
     */
    public BaseInventory(JavaPlugin plugin, InventoryType inventoryType, Component title, Set<InteractionModifier> interactionModifiers) {
        this.plugin = plugin;
        this.inventoryType = inventoryType;
        this.interactionModifiers = safeCopyOf(interactionModifiers);
        this.title = title;

        int inventorySize = inventoryType.getLimit();

        this.inventory = Bukkit.createInventory(this, inventoryType.getInventoryType(), this.title);
        this.slotActions = new LinkedHashMap<>(inventorySize);
        this.invItems = new LinkedHashMap<>(inventorySize);
    }

    /**
     * Copy a set into an EnumSet, required because {@link EnumSet#copyOf(EnumSet)} throws an exception if the collection passed as argument is empty.
     *
     * @param set The set to be copied.
     * @return An EnumSet with the provided elements from the original set.
     */
    private EnumSet<InteractionModifier> safeCopyOf(Set<InteractionModifier> set) {
        if (set.isEmpty())
            return EnumSet.noneOf(InteractionModifier.class);
        else return EnumSet.copyOf(set);
    }

    /**
     * Gets the Inventory title as a {@link Component}.
     *
     * @return The Inventory title {@link Component}.
     */
    public Component title() {
        return this.title;
    }

    /**
     * Sets the {@link InventoryItem} to a specific slot on the Inventory.
     *
     * @param slot    The Inventory slot.
     * @param inventoryItem The {@link InventoryItem} to add to the slot.
     */
    public void setItem(int slot, InventoryItem inventoryItem) {
        validateSlot(slot);

        this.invItems.put(slot, inventoryItem);
    }

    /**
     * Removes the given {@link InventoryItem} from the Inventory.
     *
     * @param item The item to remove.
     */
    public void removeItem(InventoryItem item) {
        Optional<Map.Entry<Integer, InventoryItem>> entry = this.invItems.entrySet()
                .stream()
                .filter(it -> it.getValue().equals(item))
                .findFirst();

        entry.ifPresent(it -> {
            this.invItems.remove(it.getKey());
            this.inventory.remove(it.getValue().getItemStack());
        });
    }

    /**
     * Removes the given {@link ItemStack} from the Inventory.
     *
     * @param item The item to remove.
     */
    public void removeItem(ItemStack item) {
        Optional<Map.Entry<Integer, InventoryItem>> entry = this.invItems.entrySet()
                .stream()
                .filter(it -> it.getValue().getItemStack().equals(item))
                .findFirst();

        entry.ifPresent(it -> {
            this.invItems.remove(it.getKey());
            this.inventory.remove(item);
        });
    }

    /**
     * Removes the {@link InventoryItem} in the specific slot.
     *
     * @param slot The Inventory slot.
     */
    public void removeItem(int slot) {
        validateSlot(slot);

        this.invItems.remove(slot);
        this.inventory.setItem(slot, null);
    }

    /**
     * Alternative {@link #removeItem(int)} with cols and rows.
     *
     * @param row The row.
     * @param col The column.
     */
    public void removeItem(int row, int col) {
        removeItem(getSlotFromRowCol(row, col));
    }

    /**
     * Alternative {@link #setItem(int, InventoryItem)} to set item that takes a {@link List} of slots instead.
     *
     * @param slots   The slots in which the item should go.
     * @param inventoryItem The {@link InventoryItem} to add to the slots.
     */
    public void setItem(List<Integer> slots, InventoryItem inventoryItem) {
        for (int slot : slots) {
            setItem(slot, inventoryItem);
        }
    }

    /**
     * Alternative {@link #setItem(int, InventoryItem)} to set item that uses <i>ROWS</i> and <i>COLUMNS</i> instead of slots.
     *
     * @param row     The Inventory row number.
     * @param col     The Inventory column number.
     * @param inventoryItem The {@link InventoryItem} to add to the slot.
     */
    public void setItem(int row, int col, InventoryItem inventoryItem) {
        setItem(getSlotFromRowCol(row, col), inventoryItem);
    }

    /**
     * Adds {@link InventoryItem}s to the Inventory without specific slot.
     * It'll set the item to the next empty slot available.
     *
     * @param items Varargs for specifying the {@link InventoryItem}s.
     */
    public void addItem(InventoryItem... items) {
        addItem(false, items);
    }

    /**
     * Adds {@link InventoryItem}s to the Inventory without specific slot.
     * It'll set the item to the next empty slot available.
     *
     * @param items        Varargs for specifying the {@link InventoryItem}s.
     * @param expandIfFull If true, expands the inventory if it is full
     *                     and there are more items to be added
     */
    public void addItem(boolean expandIfFull, InventoryItem... items) {

        List<InventoryItem> notAddedItems = new ArrayList<>();
        for (InventoryItem inventoryItem : items) {

            for (int slot = 0; slot < this.rows * 9; slot++) {
                if (this.invItems.get(slot) != null) {
                    if (slot == this.rows * 9 - 1) {
                        notAddedItems.add(inventoryItem);
                    }
                    continue;
                }

                this.invItems.put(slot, inventoryItem);
                break;
            }
        }

        if (!expandIfFull || this.rows >= 6 || notAddedItems.isEmpty() || this.inventoryType != InventoryType.CHEST) {
            return;
        }

        this.rows++;
        this.inventory = Bukkit.createInventory(this, this.rows * 9, this.title);

        update();
        addItem(true, notAddedItems.toArray(new InventoryItem[0]));
    }

    /**
     * Sets the {@link InventoryAction} of a default click on any item.
     * See {@link InventoryClickEvent}.
     *
     * @param defaultClickAction {@link InventoryAction} to resolve when any item is clicked.
     */
    public void setDefaultClickAction(InventoryAction<InventoryClickEvent> defaultClickAction) {
        this.defaultClickAction = defaultClickAction;
    }

    /**
     * Sets the {@link InventoryAction} of a default click on any item on the top part of the Inventory.
     * Top inventory being for example chests etc, instead of the {@link Player} inventory.
     * See {@link InventoryClickEvent}.
     *
     * @param defaultTopClickAction {@link InventoryAction} to resolve when clicking on the top inventory.
     */
    public void setDefaultTopClickAction(InventoryAction<InventoryClickEvent> defaultTopClickAction) {
        this.defaultTopClickAction = defaultTopClickAction;
    }

    public void setPlayerInventoryAction(InventoryAction<InventoryClickEvent> playerInventoryAction) {
        this.playerInventoryAction = playerInventoryAction;
    }

    /**
     * Sets the {@link InventoryAction} to run when clicking on the outside of the inventory.
     * See {@link InventoryClickEvent}.
     *
     * @param outsideClickAction {@link InventoryAction} to resolve when clicking outside of the inventory.
     */
    public void setOutsideClickAction(InventoryAction<InventoryClickEvent> outsideClickAction) {
        this.outsideClickAction = outsideClickAction;
    }

    /**
     * Sets the {@link InventoryAction} of a default drag action.
     * See {@link InventoryDragEvent}.
     *
     * @param dragAction {@link InventoryAction} to resolve.
     */
    public void setDragAction(InventoryAction<InventoryDragEvent> dragAction) {
        this.dragAction = dragAction;
    }

    /**
     * Sets the {@link InventoryAction} to run once the inventory is closed.
     * See {@link InventoryCloseEvent}.
     *
     * @param closeInventoryAction {@link InventoryAction} to resolve when the inventory is closed.
     */
    public void setCloseInventoryAction(InventoryAction<InventoryCloseEvent> closeInventoryAction) {
        this.closeInventoryAction = closeInventoryAction;
    }

    /**
     * Sets the {@link InventoryAction} to run when the Inventory opens.
     * See {@link InventoryOpenEvent}.
     *
     * @param openInventoryAction {@link InventoryAction} to resolve when opening the inventory.
     */
    public void setOpenInventoryAction(InventoryAction<InventoryOpenEvent> openInventoryAction) {
        this.openInventoryAction = openInventoryAction;
    }

    /**
     * Adds a {@link InventoryAction} for when clicking on a specific slot.
     * See {@link InventoryClickEvent}.
     *
     * @param slot       The slot that will trigger the {@link InventoryAction}.
     * @param slotAction {@link InventoryAction} to resolve when clicking on specific slots.
     */
    public void addSlotAction(int slot, InventoryAction<InventoryClickEvent> slotAction) {
        validateSlot(slot);

        this.slotActions.put(slot, slotAction);
    }

    /**
     * Alternative method for {@link #addSlotAction(int, InventoryAction)} to add a {@link InventoryAction} to a specific slot using <i>ROWS</i> and <i>COLUMNS</i> instead of slots.
     * See {@link InventoryClickEvent}.
     *
     * @param row        The row of the slot.
     * @param col        The column of the slot.
     * @param slotAction {@link InventoryAction} to resolve when clicking on the slot.
     */
    public void addSlotAction(int row, int col, InventoryAction<InventoryClickEvent> slotAction) {
        addSlotAction(getSlotFromRowCol(row, col), slotAction);
    }

    /**
     * Gets a specific {@link InventoryItem} on the slot.
     *
     * @param slot The slot of the item.
     * @return The {@link InventoryItem} on the introduced slot or {@code null} if doesn't exist.
     */
    public InventoryItem getInventoryItem(int slot) {
        return this.invItems.get(slot);
    }

    /**
     * Checks whether or not the Inventory is updating.
     *
     * @return Whether the Inventory is updating or not.
     */
    public boolean isUpdating() {
        return this.updating;
    }

    /**
     * Sets the updating status of the Inventory.
     *
     * @param updating Sets the Inventory to the updating status.
     */
    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    /**
     * Opens the Inventory for a {@link HumanEntity}.
     *
     * @param player The {@link HumanEntity} to open the Inventory to.
     */
    public void open(HumanEntity player) {
        open(player, true);
    }

    /**
     * Opens the Inventory for a {@link HumanEntity}.
     *
     * @param player The {@link HumanEntity} to open the Inventory to.
     * @param runOpenAction If should or not run the open action.
     */
    public void open(HumanEntity player, boolean runOpenAction) {

        if (player.isSleeping())
            return;

        this.runOpenAction = runOpenAction;
        this.inventory.clear();

        populateInventory();
        player.openInventory(this.inventory);

        this.runOpenAction = true;
    }

    /**
     * Closes the Inventory with a {@code 2 tick} delay (to prevent items from being taken from the {@link org.bukkit.inventory.Inventory}).
     *
     * @param player The {@link HumanEntity} to close the Inventory to.
     */
    public void close(HumanEntity player) {
        close(player, true);
    }

    /**
     * Closes the Inventory with a {@code 2 tick} delay (to prevent items from being taken from the {@link org.bukkit.inventory.Inventory}).
     *
     * @param player      The {@link HumanEntity} to close the Inventory to.
     * @param closeAction If should or not run the close action.
     */
    public void close(HumanEntity player, boolean closeAction) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {

            this.runCloseAction = closeAction;

            player.closeInventory();
            this.runCloseAction = true;
        }, 10);
    }

    /**
     * Updates the Inventory for all the {@link org.bukkit.inventory.Inventory} views.
     */
    public void update() {
        this.inventory.clear();

        populateInventory();
        for (HumanEntity viewer : new ArrayList<>(this.inventory.getViewers()))
            ((Player) viewer).updateInventory();
    }

    /**
     * Updates the title of the Inventory.
     * <i>This method may cause LAG if used on a loop</i>.
     *
     * @param title The title to set.
     * @return The Inventory for easier use when declaring, works like a builder.
     */
    public BaseInventory updateTitle(Component title) {
        this.updating = true;
        List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());

        this.inventory = this.plugin.getServer().createInventory(this, this.inventory.getSize(), title);

        for (HumanEntity player : viewers) {
            open(player);
        }

        this.updating = false;
        this.title = title;
        return this;
    }

    /**
     * Updates the specified item in the Inventory at runtime, without creating a new {@link InventoryItem}.
     *
     * @param itemStack The {@link ItemStack} to replace in the original one in the {@link InventoryItem}.
     * @param slot      The slot of the item to update.
     */
    public void updateItem(ItemStack itemStack, int slot) {

        InventoryItem inventoryItem = this.invItems.get(slot);
        if (inventoryItem == null) {
            updateItem(new InventoryItem(itemStack), slot);
            return;
        }

        inventoryItem.setItemStack(itemStack);
        updateItem(inventoryItem, slot);
    }

    /**
     * Alternative {@link #updateItem(ItemStack, int)} that takes <i>ROWS</i> and <i>COLUMNS</i> instead of slots.
     *
     * @param itemStack The {@link ItemStack} to replace in the original one in the {@link InventoryItem}.
     * @param row       The row of the slot.
     * @param col       The columns of the slot.
     */
    public void updateItem(ItemStack itemStack, int row, int col) {
        updateItem(itemStack, getSlotFromRowCol(row, col));
    }

    /**
     * Alternative {@link #updateItem(ItemStack, int)} but creates a new {@link InventoryItem}.
     *
     * @param item The {@link InventoryItem} to replace in the original.
     * @param slot The slot of the item to update.
     */
    public void updateItem(InventoryItem item, int slot) {
        this.invItems.put(slot, item);
        this.inventory.setItem(slot, item.getItemStack());
    }

    /**
     * Alternative {@link #updateItem(InventoryItem, int)} that takes <i>ROWS</i> and <i>COLUMNS</i> instead of slots.
     *
     * @param item The {@link InventoryItem} to replace in the original.
     * @param row  The row of the slot.
     * @param col  The columns of the slot.
     */
    public void updateItem(InventoryItem item, int row, int col) {
        updateItem(item, getSlotFromRowCol(row, col));
    }

    /**
     * Disable item placement inside the Inventory.
     *
     * @return The BaseInventory..
     */
    public BaseInventory disableItemPlace() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }

    /**
     * Disable item retrieval inside the Inventory.
     *
     * @return The BaseInventory..
     */
    public BaseInventory disableItemTake() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }

    /**
     * Disable item swap inside the Inventory.
     *
     * @return The BaseInventory..
     */
    public BaseInventory disableItemSwap() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }

    /**
     * Disable item drop inside the Inventory
     *
     * @return The BaseInventory
     */
    public BaseInventory disableItemDrop() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }

    /**
     * Disable other Inventory actions
     * This option pretty much disables creating a clone stack of the item
     *
     * @return The BaseInventory
     */
    public BaseInventory disableOtherActions() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }

    /**
     * Disable all the modifications of the Inventory, making it immutable by player interaction.
     *
     * @return The BaseInventory..
     */
    public BaseInventory disableAllInteractions() {
        this.interactionModifiers.addAll(InteractionModifier.VALUES);
        return this;
    }

    /**
     * Allows item placement inside the Inventory.
     *
     * @return The BaseInventory..
     */
    public BaseInventory enableItemPlace() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }

    /**
     * Allow items to be taken from the Inventory.
     *
     * @return The BaseInventory..
     */
    public BaseInventory enableItemTake() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }

    /**
     * Allows item swap inside the Inventory.
     *
     * @return The BaseInventory..
     */
    public BaseInventory enableItemSwap() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }

    /**
     * Allows item drop inside the Inventory
     *
     * @return The BaseInventory
     */
    public BaseInventory enableItemDrop() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }

    /**
     * Enable other Inventory actions
     * This option pretty much enables creating a clone stack of the item
     *
     * @return The BaseInventory
     */
    public BaseInventory enableOtherActions() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }

    /**
     * Enable all modifications of the Inventory, making it completely mutable by player interaction.
     *
     * @return The BaseInventory
     */
    public BaseInventory enableAllInteractions() {
        this.interactionModifiers.clear();
        return this;
    }

    /**
     * Check if item placement is allowed inside this Inventory.
     *
     * @return True if item placement is allowed for this Inventory.
     */
    public boolean canPlaceItems() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
    }

    /**
     * Check if item retrieval is allowed inside this Inventory.
     *
     * @return True if item retrieval is allowed inside this Inventory.
     */
    public boolean canTakeItems() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
    }

    /**
     * Check if item swap is allowed inside this Inventory.
     *
     * @return True if item swap is allowed for this Inventory.
     */
    public boolean canSwapItems() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
    }

    /**
     * Check if item drop is allowed inside this Inventory
     *
     * @return True if item drop is allowed for this Inventory
     */
    public boolean canDropItems() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
    }

    /**
     * Check if any other actions are allowed in this Inventory
     *
     * @return True if other actions are allowed
     */
    public boolean allowsOtherActions() {
        return !this.interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
    }

    /**
     * Gets the {@link InventoryFiller} that it's used for filling up the Inventory in specific ways.
     *
     * @return The {@link InventoryFiller}.
     */
    public InventoryFiller getFiller() {
        return this.filler;
    }

    /**
     * Gets an immutable {@link Map} with all the Inventory items.
     *
     * @return The {@link Map} with all the {@link #invItems}.
     */
    public Map<Integer, InventoryItem> getInvItems() {
        return this.invItems;
    }

    /**
     * Gets the main {@link org.bukkit.inventory.Inventory} of this Inventory.
     *
     * @return Gets the {@link org.bukkit.inventory.Inventory} from the holder.
     */
    @NotNull
    @Override
    public org.bukkit.inventory.Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets the amount of {@link #rows}.
     *
     * @return The {@link #rows} of the Inventory.
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Gets the {@link InventoryType} in use.
     *
     * @return The {@link InventoryType}.
     */
    public InventoryType inventoryType() {
        return this.inventoryType;
    }

    /**
     * Gets the default click resolver.
     */
    @Nullable
    public InventoryAction<InventoryClickEvent> getDefaultClickAction() {
        return this.defaultClickAction;
    }

    /**
     * Gets the default top click resolver.
     */
    @Nullable
    public InventoryAction<InventoryClickEvent> getDefaultTopClickAction() {
        return this.defaultTopClickAction;
    }

    /**
     * Gets the player inventory action.
     */
    @Nullable
    public InventoryAction<InventoryClickEvent> getPlayerInventoryAction() {
        return this.playerInventoryAction;
    }

    /**
     * Gets the default drag resolver.
     */
    @Nullable
    public InventoryAction<InventoryDragEvent> getDragAction() {
        return this.dragAction;
    }

    /**
     * Gets the close inventory resolver.
     */
    @Nullable
    public InventoryAction<InventoryCloseEvent> getCloseInventoryAction() {
        return this.closeInventoryAction;
    }

    /**
     * Gets the open inventory resolver.
     */
    @Nullable
    public InventoryAction<InventoryOpenEvent> getOpenInventoryAction() {
        return this.openInventoryAction;
    }

    /**
     * Gets the resolver for the outside click.
     */
    @Nullable
    public InventoryAction<InventoryClickEvent> getOutsideClickAction() {
        return this.outsideClickAction;
    }

    /**
     * Gets the action for the specified slot.
     *
     * @param slot The slot clicked.
     */
    @Nullable
    public InventoryAction<InventoryClickEvent> getSlotAction(int slot) {
        return this.slotActions.get(slot);
    }

    /**
     * Populates the Inventory with it's items.
     */
    void populateInventory() {
        for (Map.Entry<Integer, InventoryItem> entry : this.invItems.entrySet()) {
            this.inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }
    }

    public boolean shouldRunCloseAction() {
        return this.runCloseAction;
    }

    public boolean shouldRunOpenAction() {
        return this.runOpenAction;
    }

    /**
     * Gets the slot from the row and column passed.
     *
     * @param row The row.
     * @param col The column.
     * @return The slot needed.
     */
    public int getSlotFromRowCol(int row, int col) {
        return ((row - 1) * 9) + (col - 1);
    }

    /**
     * Gets the row and column from the slot passed.
     *
     * @param slot The slot.
     * @return An array containing the row and column.
     */
    public int[] getRowColFromSlot(int slot) {
        int[] rowCol = new int[2];

        rowCol[0] = (slot / 9) + 1;  // Calculating the row
        rowCol[1] = (slot % 9) + 1;  // Calculating the column

        return rowCol;
    }

    /**
     * Sets the new inventory of the Inventory.
     *
     * @param inventory The new inventory.
     */
    public void setInventory(org.bukkit.inventory.Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Checks if the slot introduces is a valid slot.
     *
     * @param slot The slot to check.
     */
    private void validateSlot(int slot) {
        int limit = this.inventoryType.getLimit();

        if (this.inventoryType == InventoryType.CHEST) {
            if (slot < 0 || slot >= this.rows * limit) throwInvalidSlot(slot);
            return;
        }

        if (slot < 0 || slot > limit)
            throwInvalidSlot(slot);
    }

    /**
     * Throws an exception if the slot is invalid.
     *
     * @param slot The specific slot to display in the error message.
     */
    private void throwInvalidSlot(int slot) {
        if (this.inventoryType == InventoryType.CHEST) {
            throw new InventoryException("Slot " + slot + " is not valid for the inventory type - " + this.inventoryType + " and rows - " + this.rows + "!");
        }

        throw new InventoryException("Slot " + slot + " is not valid for the inventory type - " + this.inventoryType + "!");
    }
}