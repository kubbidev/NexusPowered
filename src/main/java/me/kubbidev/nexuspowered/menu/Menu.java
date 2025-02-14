package me.kubbidev.nexuspowered.menu;

import me.kubbidev.nexuspowered.Events;
import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.terminable.TerminableConsumer;
import me.kubbidev.nexuspowered.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

@NotNullByDefault
public class Menu implements InventoryHolder, TerminableConsumer {

    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();

    private final Inventory inventory;

    // A function used to build a fallback page when this page is closed.
    @Nullable
    private Function<Player, Menu> fallbackMenu = null;

    // This remains true until after #redraw is called for the first time
    private boolean firstDraw = true;
    private boolean valid = false;

    // Callbacks to be ran when the GUI is invalidated (closed). useful for cancelling tick tasks
    // Also contains the event handlers bound to this GUI, currently listening to events
    private final CompositeTerminable compositeTerminable = CompositeTerminable.create();

    /**
     * Create a new FastInv with a custom size.
     *
     * @param size The size of the inventory.
     */
    public Menu(int size) {
        this(owner -> Bukkit.createInventory(owner, size));
    }

    /**
     * Create a new FastInv with a custom size and title.
     *
     * @param size  The size of the inventory.
     * @param title The title (name) of the inventory.
     */
    public Menu(int size, String title) {
        this(owner -> Bukkit.createInventory(owner, size, title));
    }

    /**
     * Create a new FastInv with a custom type.
     *
     * @param type The type of the inventory.
     */
    public Menu(InventoryType type) {
        this(owner -> Bukkit.createInventory(owner, type));
    }

    /**
     * Create a new FastInv with a custom type and title.
     *
     * @param type  The type of the inventory.
     * @param title The title of the inventory.
     */
    public Menu(InventoryType type, String title) {
        this(owner -> Bukkit.createInventory(owner, type, title));
    }

    public Menu(Function<InventoryHolder, Inventory> inventoryFunction) {
        Objects.requireNonNull(inventoryFunction, "inventoryFunction");
        Inventory inv = inventoryFunction.apply(this);

        if (inv.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not FastInv, found: " + inv.getHolder());
        }

        this.inventory = inv;
    }

    /**
     * Places items on the Menu. Called when the Menu is opened.
     * <p>
     * Use {@link #isFirstDraw()} to determine if this is the first time redraw has been called.
     *
     * @param viewer of the current inventory
     */
    protected void redraw(Player viewer) {
    }

    protected void onOpen(InventoryOpenEvent event) {
    }

    protected void onClick(InventoryClickEvent event) {
    }

    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot.
     *
     * @param item The ItemStack to add
     */
    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot with a click handler.
     *
     * @param item    The item to add.
     * @param handler The click handler for the item.
     */
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a specific slot.
     *
     * @param slot The slot where to add the item.
     * @param item The item to add.
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
     *
     * @param slot    The slot where to add the item.
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.inventory.setItem(slot, item);

        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots.
     *
     * @param slotFrom Starting slot to add the item in.
     * @param slotTo   Ending slot to add the item in.
     * @param item     The item to add.
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots with a click handler.
     *
     * @param slotFrom Starting slot to put the item in.
     * @param slotTo   Ending slot to put the item in.
     * @param item     The item to add.
     * @param handler  The click handler for the item
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots.
     *
     * @param slots The slots where to add the item
     * @param item  The item to add.
     */
    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
     *
     * @param slots   The slots where to add the item
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Remove an {@link ItemStack} from the inventory.
     *
     * @param slot The slot where to remove the item
     */
    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    /**
     * Remove multiples {@link ItemStack} from the inventory.
     *
     * @param slots The slots where to remove the items
     */
    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    /**
     * Clear all {@link ItemStack} from the inventory.
     */
    public void clearItems() {
        this.inventory.clear();
        this.itemHandlers.clear();
    }

    public @Nullable Function<Player, Menu> getFallbackMenu() {
        return this.fallbackMenu;
    }

    public void setFallbackMenu(@Nullable Function<Player, Menu> fallbackMenu) {
        this.fallbackMenu = fallbackMenu;
    }

    /**
     * Add a handler to handle inventory open.
     *
     * @param openHandler The handler to add.
     */
    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    /**
     * Add a handler to handle inventory close.
     *
     * @param closeHandler The handler to add
     */
    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    /**
     * Add a handler to handle inventory click.
     *
     * @param clickHandler The handler to add.
     */
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    /**
     * Open the inventory to a player.
     *
     * @param player The player to open the menu.
     */
    public void open(Player player) {
        handleOpen(player);
    }

    /**
     * Get borders of the inventory. If the inventory size is under 27, all slots are returned.
     *
     * @return inventory borders
     */
    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9
                || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    /**
     * Get corners of the inventory.
     *
     * @return inventory corners
     */
    public int[] getCorners() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10)
                || i == 17 || i == size - 18
                || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    public boolean isFirstDraw() {
        return this.firstDraw;
    }

    /**
     * Returns true unless this {@link Menu} has been invalidated, through being closed, or the player leaving.
     *
     * @return true unless this Menu has been invalidated.
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Get the Bukkit inventory.
     *
     * @return The Bukkit inventory.
     */
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public <T extends AutoCloseable> @NotNull T bind(@NotNull T terminable) {
        return this.compositeTerminable.bind(terminable);
    }

    void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        this.openHandlers.forEach(c -> c.accept(e));
    }

    void handleClose(InventoryCloseEvent e) {
        onClose(e);

        this.closeHandlers.forEach(c -> c.accept(e));
        invalidate();
    }

    void handleClick(InventoryClickEvent e) {
        onClick(e);

        this.clickHandlers.forEach(c -> c.accept(e));

        Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }

    private void handleOpen(Player viewer) {
        if (this.valid) {
            throw new IllegalStateException("Menu is already opened.");
        }
        this.firstDraw = true;
        try {
            redraw(viewer);
        } catch (Exception e) {
            e.printStackTrace();
            invalidate();
            return;
        }

        this.firstDraw = false;
        startListening(viewer);
        viewer.openInventory(this.inventory);
        this.valid = true;
    }

    private void invalidate() {
        this.valid = false;

        // stop listening
        this.compositeTerminable.closeAndReportException();

        // clear all items from the Menu, just in case the menu didn't close properly.
        clearItems();
    }

    /**
     * Registers the event handlers for {@link Menu}s.
     */
    private void startListening(Player viewer) {
        Events.merge(Player.class)
                .bindEvent(PlayerDeathEvent.class, PlayerDeathEvent::getEntity)
                .bindEvent(PlayerQuitEvent.class, PlayerEvent::getPlayer)
                .bindEvent(PlayerChangedWorldEvent.class, PlayerEvent::getPlayer)
                .bindEvent(PlayerTeleportEvent.class, PlayerEvent::getPlayer)
                .filter(p -> p.equals(viewer))
                .filter(p -> isValid())
                .handler(p -> invalidate())
                .bindWith(this);

        Events.subscribe(InventoryClickEvent.class)
                .filter(e -> e.getWhoClicked().equals(viewer))
                .handler(e -> {
                    boolean wasCancelled = e.isCancelled();
                    e.setCancelled(true);

                    if (!isValid()) {
                        viewer.closeInventory();
                        return;
                    }

                    if (!e.getInventory().equals(this.inventory)) {
                        return;
                    }

                    handleClick(e);

                    // This prevents un-canceling the event if another plugin canceled it before
                    if (!wasCancelled && !e.isCancelled()) {
                        e.setCancelled(false);
                    }
                })
                .bindWith(this);


        Events.subscribe(InventoryOpenEvent.class)
                .filter(e -> e.getPlayer().equals(viewer))
                .handler(e -> {
                    if (!e.getInventory().equals(this.inventory) && isValid()) {
                        invalidate();
                    } else {
                        handleOpen(e);
                    }
                })
                .bindWith(this);

        Events.subscribe(InventoryCloseEvent.class)
                .filter(e -> e.getPlayer().equals(viewer))
                .filter(e -> isValid())
                .handler(e -> {
                    handleClose(e);

                    if (!e.getInventory().equals(this.inventory)) {
                        return;
                    }

                    Function<Player, Menu> fallback = this.fallbackMenu;
                    if (fallback == null) {
                        return;
                    }

                    // Open at a delay
                    Schedulers.sync().runLater(() -> {
                        if (!viewer.isOnline()) {
                            return;
                        }
                        Menu fallbackMenu = fallback.apply(viewer);
                        if (fallbackMenu == null) {
                            throw new IllegalStateException("Fallback function " + fallback + " returned null");
                        }
                        if (fallbackMenu.valid) {
                            throw new IllegalStateException("Fallback function " + fallback + " produced a Menu " + fallbackMenu + " which is already open");
                        }
                        fallbackMenu.open(viewer);
                    }, 1L);
                })
                .bindWith(this);
    }
}