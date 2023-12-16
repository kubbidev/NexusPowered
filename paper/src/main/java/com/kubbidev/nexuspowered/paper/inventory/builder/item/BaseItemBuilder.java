package com.kubbidev.nexuspowered.paper.inventory.builder.item;

import com.kubbidev.java.util.Pair;
import com.kubbidev.nexuspowered.paper.inventory.components.InventoryAction;
import com.kubbidev.nexuspowered.paper.inventory.type.InventoryItem;
import com.kubbidev.nexuspowered.paper.util.PersistentDataUtils;
import com.kubbidev.nexuspowered.common.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contains all the common methods for the future ItemBuilders
 *
 * @param <B> The ItemBuilder type so the methods can cast to the subtype
 */
@SuppressWarnings("unchecked")
public abstract class BaseItemBuilder<B extends BaseItemBuilder<B>> {

    private ItemStack itemStack;
    private ItemMeta meta;

    protected BaseItemBuilder(Material material) {
        Validate.notNull(material, "Material can't be null!");

        this.itemStack = new ItemStack(material);
        this.meta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
    }

    protected BaseItemBuilder(ItemStack itemStack) {
        Validate.notNull(itemStack, "Item can't be null!");

        this.itemStack = itemStack;
        this.meta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
    }

    /**
     * Sets the display name of the item using {@link Component}
     *
     * @param button The {@link Pair} button
     * @return {@link ItemBuilder}
     */
    public B button(Pair<Component, Integer> button) {

        name(button.getLeft());
        model(button.getRight());
        return (B) this;
    }

    /**
     * Sets the display name of the item using {@link Component}
     *
     * @param name The {@link Component} name
     * @return {@link ItemBuilder}
     */
    public B name(Component name) {
        this.meta.displayName(ComponentUtils.removeItalic(name));
        return (B) this;
    }

    /**
     * Sets the amount of items
     *
     * @param amount the amount of items
     * @return {@link ItemBuilder}
     */
    public B amount(int amount) {
        this.itemStack.setAmount(amount);
        return (B) this;
    }

    /**
     * Set the lore lines of an item
     *
     * @param lore Lore lines as varargs
     * @return {@link ItemBuilder}
     */
    public B lore(Component... lore) {
        return lore(Arrays.asList(lore));
    }

    /**
     * Set the lore lines of an item
     *
     * @param lore A {@link List} with the lore lines
     * @return {@link ItemBuilder}
     */
    public B lore(Component lore) {

        List<Component> oldLore = this.meta.lore();
        List<Component> newLore = new ArrayList<>(oldLore == null ? new ArrayList<>() : oldLore);
        newLore.add(ComponentUtils.removeItalic(lore));

        this.meta.lore(newLore);
        return (B) this;
    }

    /**
     * Set the lore lines of an item
     *
     * @param lore A {@link List} with the lore lines
     * @return {@link ItemBuilder}
     */
    public B lore(List<Component> lore) {
        this.meta.lore(lore.stream()
                .map(ComponentUtils::removeItalic)
                .toList());
        return (B) this;
    }

    /**
     * Consumer for freely adding to the lore
     *
     * @param lore A {@link Consumer} with the {@link List} of lore {@link Component}
     * @return {@link ItemBuilder}
     */
    public B lore(Consumer<List<Component>> lore) {


        List<Component> metaLore = this.meta.lore();
        List<Component> components = (metaLore == null) ? new ArrayList<>() : metaLore.stream()
                // The field is null by default ._.
                .map(ComponentUtils::removeItalic)
                .collect(Collectors.toList());

        lore.accept(components);
        return lore(components);
    }

    /**
     * Enchants the {@link ItemStack}
     *
     * @param enchantment            The {@link Enchantment} to add
     * @param level                  The level of the {@link Enchantment}
     * @param ignoreLevelRestriction If should or not ignore it
     * @return {@link ItemBuilder}
     */
    public B enchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        this.meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return (B) this;
    }

    /**
     * Enchants the {@link ItemStack}
     *
     * @param enchantment The {@link Enchantment} to add
     * @param level       The level of the {@link Enchantment}
     * @return {@link ItemBuilder}
     */
    public B enchant(Enchantment enchantment, int level) {
        return enchant(enchantment, level, true);
    }

    /**
     * Enchants the {@link ItemStack}
     *
     * @param enchantment The {@link Enchantment} to add
     * @return {@link ItemBuilder}
     */
    public B enchant(Enchantment enchantment) {
        return enchant(enchantment, 1, true);
    }

    /**
     * Enchants the {@link ItemStack} with the specified map where the value
     * is the level of the key's enchantment
     *
     * @param enchantments Enchantments to add
     * @param ignoreLevelRestriction If level restriction should be ignored
     * @return {@link ItemBuilder}
     */
    public B enchant(Map<Enchantment, Integer> enchantments, boolean ignoreLevelRestriction) {
        enchantments.forEach((enchantment, level) -> enchant(enchantment, level, ignoreLevelRestriction));
        return (B) this;
    }

    /**
     * Enchants the {@link ItemStack} with the specified map where the value
     * is the level of the key's enchantment
     *
     * @param enchantments Enchantments to add
     * @return {@link ItemBuilder}
     */
    public B enchant(Map<Enchantment, Integer> enchantments) {
        return enchant(enchantments, true);
    }

    /**
     * Disenchants a certain {@link Enchantment} from the {@link ItemStack}
     *
     * @param enchantment The {@link Enchantment} to remove
     * @return {@link ItemBuilder}
     */
    public B disenchant(Enchantment enchantment) {
        this.itemStack.removeEnchantment(enchantment);
        return (B) this;
    }

    /**
     * Add an {@link ItemFlag} to the item
     *
     * @param flags The {@link ItemFlag} to add
     * @return {@link ItemBuilder}
     */
    public B flags(ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return (B) this;
    }

    /**
     * Makes the {@link ItemStack} unbreakable
     *
     * @return {@link ItemBuilder}
     */
    public B unbreakable() {
        return unbreakable(true);
    }

    /**
     * Sets the item as unbreakable
     *
     * @param unbreakable If should or not be unbreakable
     * @return {@link ItemBuilder}
     */
    public B unbreakable(boolean unbreakable) {
        this.meta.setUnbreakable(unbreakable);
        return (B) this;
    }

    /**
     * Makes the {@link ItemStack} glow
     *
     * @return {@link ItemBuilder}
     */
    public B glow() {
        return glow(true);
    }

    /**
     * Adds or removes the {@link ItemStack} glow
     *
     * @param glow Should the item glow
     * @return {@link ItemBuilder}
     */
    public B glow(boolean glow) {
        if (glow) {
            this.meta.addEnchant(Enchantment.LURE, 1, false);
            this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            return (B) this;
        }

        for (Enchantment enchantment : meta.getEnchants().keySet()) {
            this.meta.removeEnchant(enchantment);
        }

        return (B) this;
    }

    /**
     * Consumer for applying {@link PersistentDataContainer} to the item
     * This method will only work on versions above 1.14
     *
     * @param consumer The {@link Consumer} with the PDC
     * @return {@link ItemBuilder}
     */
    public B pdc(Consumer<PersistentDataContainer> consumer) {
        consumer.accept(this.meta.getPersistentDataContainer());
        return (B) this;
    }

    /**
     * Sets the custom model data of the item
     * Added in 1.13
     *
     * @param modelData The custom model data from the resource pack
     * @return {@link ItemBuilder}
     */
    public B model(int modelData) {
        this.meta.setCustomModelData(modelData);
        return (B) this;
    }

    /**
     * Color an {@link ItemStack}
     *
     * @param color color
     * @return {@link B}
     * @see LeatherArmorMeta#setColor(Color)
     * @see org.bukkit.inventory.meta.MapMeta#setColor(Color)
     */
    public B color(Color color) {
        if (getMeta() instanceof LeatherArmorMeta armorMeta) {

            armorMeta.setColor(color);
            setMeta(armorMeta);
        }
        if (getMeta() instanceof FireworkEffectMeta effectMeta) {

            effectMeta.setEffect(FireworkEffect.builder().withColor(color).build());
            setMeta(effectMeta);
        }

        return (B) this;
    }

    /**
     * Sets NBT tag to the {@link ItemStack}
     *
     * @param key   The NBT key
     * @param value The NBT value
     * @return {@link ItemBuilder}
     */
    public B setNbt(NamespacedKey key, Object value) {
        this.itemStack.setItemMeta(this.meta);
        this.meta = this.itemStack.getItemMeta();

        PersistentDataUtils.setData(this.meta, key, value);

        return (B) this;
    }

    /**
     * Removes NBT tag from the {@link ItemStack}
     *
     * @param key The NBT key
     * @return {@link ItemBuilder}
     */
    public B removeNbt(NamespacedKey key) {

        this.itemStack.setItemMeta(this.meta);
        this.meta = this.itemStack.getItemMeta();

        PersistentDataUtils.removeData(this.meta, key);

        return (B) this;
    }

    /**
     * Builds the item into {@link ItemStack}
     *
     * @return The fully built {@link ItemStack}
     */
    public ItemStack build() {
        this.itemStack.setItemMeta(this.meta);
        return this.itemStack;
    }

    /**
     * Creates a {@link InventoryItem} instead of an {@link ItemStack}
     *
     * @return A {@link InventoryItem} with no {@link InventoryAction}
     */
    public InventoryItem asInventoryItem() {
        return new InventoryItem(build());
    }

    /**
     * Creates a {@link InventoryItem} instead of an {@link ItemStack}
     *
     * @param action The {@link InventoryAction} to apply to the item
     * @return A {@link InventoryItem} with {@link InventoryAction}
     */
    public InventoryItem asInventoryItem(InventoryAction<InventoryClickEvent> action) {
        return new InventoryItem(build(), action);
    }

    /**
     * Package private getter for extended builders
     *
     * @return The ItemStack
     */
    protected ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * Package private setter for the extended builders
     *
     * @param itemStack The ItemStack
     */
    protected void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Package private getter for extended builders
     *
     * @return The ItemMeta
     */
    protected ItemMeta getMeta() {
        return this.meta;
    }

    /**
     * Package private setter for the extended builders
     *
     * @param meta The ItemMeta
     */
    protected void setMeta(ItemMeta meta) {
        this.meta = meta;
    }
}