package com.kubbidev.nexuspowered.paper.inventory.builder.item;

import com.kubbidev.nexuspowered.paper.inventory.components.execption.InventoryException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Main ItemBuilder
 */
public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {


    /**
     * Constructor of the item builder
     *
     * @param itemStack The {@link ItemStack} of the item
     */
    ItemBuilder(ItemStack itemStack) {
        super(itemStack);
    }

    /**
     * Constructor of the item builder
     *
     * @param material The {@link Material} of the item
     */
    ItemBuilder(Material material) {
        super(material);
    }

    /**
     * Main method to create {@link ItemBuilder}
     *
     * @param itemStack The {@link ItemStack} you want to edit
     * @return A new {@link ItemBuilder}
     */
    public static ItemBuilder from(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }


    /**
     * Alternative method to create {@link ItemBuilder}
     *
     * @param material The {@link Material} you want to create an item from
     * @return A new {@link ItemBuilder}
     */
    public static ItemBuilder from(Material material) {
        return new ItemBuilder(material);
    }

    /**
     * Method for creating a {@link BannerBuilder} which will have BANNER specific methods
     *
     * @return A new {@link BannerBuilder}
     */
    public static BannerBuilder banner() {
        return new BannerBuilder();
    }

    /**
     * Method for creating a {@link BannerBuilder} which will have BANNER specific methods
     *
     * @param itemStack An existing BANNER {@link ItemStack}
     * @return A new {@link BannerBuilder}
     * @throws InventoryException if the item is not a BANNER
     */
    public static BannerBuilder banner(ItemStack itemStack) {
        return new BannerBuilder(itemStack);
    }

    /**
     * Method for creating a {@link BookBuilder} which will have {@link Material#WRITABLE_BOOK} /
     * {@link Material#WRITTEN_BOOK} specific methods
     *
     * @param itemStack an existing {@link Material#WRITABLE_BOOK} / {@link Material#WRITTEN_BOOK} {@link ItemStack}
     * @return A new {@link FireworkBuilder}
     * @throws InventoryException if the item type is not {@link Material#WRITABLE_BOOK}
     *                                                               or {@link Material#WRITTEN_BOOK}
     */
    public static BookBuilder book(ItemStack itemStack) {
        return new BookBuilder(itemStack);
    }

    /**
     * Method for creating a {@link FireworkBuilder} which will have {@link Material#FIREWORK_ROCKET} specific methods
     *
     * @return A new {@link FireworkBuilder}
     */
    public static FireworkBuilder firework() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_ROCKET));
    }

    /**
     * Method for creating a {@link FireworkBuilder} which will have {@link Material#FIREWORK_ROCKET} specific methods
     *
     * @param itemStack an existing {@link Material#FIREWORK_ROCKET} {@link ItemStack}
     * @return A new {@link FireworkBuilder}
     * @throws InventoryException if the item type is not {@link Material#FIREWORK_ROCKET}
     */
    public static FireworkBuilder firework(ItemStack itemStack) {
        return new FireworkBuilder(itemStack);
    }

    /**
     * Method for creating a {@link MapBuilder} which will have {@link Material#MAP} specific methods
     *
     * @return A new {@link MapBuilder}
     */
    public static MapBuilder map() {
        return new MapBuilder();
    }

    /**
     * Method for creating a {@link MapBuilder} which will have @link Material#MAP} specific methods
     *
     * @param itemStack An existing {@link Material#MAP} {@link ItemStack}
     * @return A new {@link MapBuilder}
     * @throws InventoryException if the item type is not {@link Material#MAP}
     */
    public static MapBuilder map(ItemStack itemStack) {
        return new MapBuilder(itemStack);
    }

    /**
     * Method for creating a {@link SkullBuilder} which will have PLAYER_HEAD specific methods
     *
     * @return A new {@link SkullBuilder}
     */
    public static SkullBuilder skull() {
        return new SkullBuilder();
    }

    /**
     * Method for creating a {@link SkullBuilder} which will have PLAYER_HEAD specific methods
     *
     * @param itemStack An existing PLAYER_HEAD {@link ItemStack}
     * @return A new {@link SkullBuilder}
     * @throws InventoryException if the item is not a player head
     */
    public static SkullBuilder skull(ItemStack itemStack) {
        return new SkullBuilder(itemStack);
    }

    /**
     * Method for creating a {@link FireworkBuilder} which will have {@link Material#FIREWORK_STAR} specific methods
     *
     * @return A new {@link FireworkBuilder}
     */
    public static FireworkBuilder star() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_STAR));
    }

    /**
     * Method for creating a {@link FireworkBuilder} which will have {@link Material#FIREWORK_STAR} specific methods
     *
     * @param itemStack an existing {@link Material#FIREWORK_STAR} {@link ItemStack}
     * @return A new {@link FireworkBuilder}
     * @throws InventoryException if the item type is not {@link Material#FIREWORK_STAR}
     */
    public static FireworkBuilder star(ItemStack itemStack) {
        return new FireworkBuilder(itemStack);
    }
}
