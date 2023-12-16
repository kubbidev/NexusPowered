package com.kubbidev.nexuspowered.paper.inventory.builder.inventory;

import com.kubbidev.nexuspowered.paper.inventory.components.InteractionModifier;
import com.kubbidev.nexuspowered.paper.inventory.components.InventoryType;
import com.kubbidev.nexuspowered.paper.inventory.components.execption.InventoryException;
import com.kubbidev.nexuspowered.paper.inventory.type.BaseInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The base for all the Inventory builders this is due to some limitations
 * where some builders will have unique features based on the Inventory type
 *
 * @param <G> The Type of {@link BaseInventory}
 */
@SuppressWarnings("unchecked")
public abstract class BaseInventoryBuilder<G extends BaseInventory, B extends BaseInventoryBuilder<G, B>> {

    protected final JavaPlugin plugin;

    private int rows = 1;

    private final EnumSet<InteractionModifier> interactionModifiers = EnumSet.noneOf(InteractionModifier.class);

    private Component title = null;
    private Consumer<G> consumer;

    public BaseInventoryBuilder(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets the rows for the Inventory
     * This will only work on CHEST {@link InventoryType}
     *
     * @param rows The amount of rows
     * @return The builder
     */
    public B rows(int rows) {
        this.rows = rows;
        return (B) this;
    }

    /**
     * Sets the title for the Inventory
     * This will be either a Component or a String
     *
     * @param title The Inventory title
     * @return The builder
     */
    public B title(Component title) {
        this.title = title;
        return (B) this;
    }

    /**
     * Disable item placement inside the Inventory
     *
     * @return The builder
     */
    public B disableItemPlace() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B) this;
    }

    /**
     * Disable item retrieval inside the Inventory
     *
     * @return The builder
     */
    public B disableItemTake() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B) this;
    }

    /**
     * Disable item swap inside the Inventory
     *
     * @return The builder
     */
    public B disableItemSwap() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B) this;
    }

    /**
     * Disable item drop inside the Inventory
     *
     * @return The builder
     */
    public B disableItemDrop() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return (B) this;
    }

    /**
     * Disable other Inventory actions
     * This option pretty much disables creating a clone stack of the item
     *
     * @return The builder
     */
    public B disableOtherActions() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B) this;
    }

    /**
     * Disable all the modifications of the Inventory, making it immutable by player interaction
     *
     * @return The builder
     */
    public B disableAllInteractions() {
        this.interactionModifiers.addAll(InteractionModifier.VALUES);
        return (B) this;
    }

    /**
     * Allows item placement inside the Inventory
     *
     * @return The builder
     */
    public B enableItemPlace() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B) this;
    }

    /**
     * Allow items to be taken from the Inventory
     *
     * @return The builder
     */
    public B enableItemTake() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B) this;
    }

    /**
     * Allows item swap inside the Inventory
     *
     * @return The builder
     */
    public B enableItemSwap() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B) this;
    }

    /**
     * Allows item drop inside the Inventory
     *
     * @return The builder
     */
    public B enableItemDrop() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return (B) this;
    }

    /**
     * Enable other Inventory actions
     * This option pretty much enables creating a clone stack of the item
     *
     * @return The builder
     */
    public B enableOtherActions() {
        this.interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B) this;
    }

    /**
     * Enable all modifications of the Inventory, making it completely mutable by player interaction
     *
     * @return The builder
     */
    public B enableAllInteractions() {
        this.interactionModifiers.clear();
        return (B) this;
    }

    /**
     * Applies anything to the Inventory once it's created
     * Can be pretty useful for setting up small things like default actions
     *
     * @param consumer A {@link Consumer} that passes the built Inventory
     * @return The builder
     */
    public B apply(Consumer<G> consumer) {
        this.consumer = consumer;
        return (B) this;
    }

    /**
     * Creates the given InventoryBase
     * Has to be abstract because each Inventory are different
     *
     * @return The new {@link BaseInventory}
     */
    public abstract G create();

    /**
     * Getter for the title
     *
     * @return The current title
     */
    protected Component getTitle() {
        if (this.title == null) {
            throw new InventoryException("Inventory title is missing!");
        }

        return this.title;
    }

    /**
     * Getter for the rows
     *
     * @return The amount of rows
     */
    protected int getRows() {
        return this.rows;
    }

    /**
     * Getter for the consumer
     *
     * @return The consumer
     */
    protected Consumer<G> getConsumer() {
        return this.consumer;
    }


    /**
     * Getter for the set of interaction modifiers
     * @return The set of {@link InteractionModifier}
     */
    protected Set<InteractionModifier> getModifiers() {
        return this.interactionModifiers;
    }
}
