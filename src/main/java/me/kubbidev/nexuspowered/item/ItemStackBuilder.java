package me.kubbidev.nexuspowered.item;

import me.kubbidev.nexuspowered.menu.Item;
import me.kubbidev.nexuspowered.util.Text;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Easily construct {@link ItemStack} instances.
 */
@NotNullByDefault
public final class ItemStackBuilder {
    private static final ItemFlag[] ALL_FLAGS = new ItemFlag[]{
            ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
            ItemFlag.HIDE_DESTROYS,
            ItemFlag.HIDE_PLACED_ON
    };

    private final ItemStack itemStack;

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(new ItemStack(material)).hideAttributes();
    }

    public static ItemStackBuilder of(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack).hideAttributes();
    }

    public static ItemStackBuilder of(ConfigurationSection config) {
        return ItemStackReader.DEFAULT.read(config);
    }

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
    }

    public ItemStackBuilder transform(Consumer<ItemStack> is) {
        is.accept(this.itemStack);
        return this;
    }

    public ItemStackBuilder transformMeta(Consumer<ItemMeta> meta) {
        ItemMeta m = this.itemStack.getItemMeta();
        if (m != null) {
            meta.accept(m);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemStackBuilder name(Component name) {
        return transformMeta(meta -> meta.displayName(Text.removeItalic(name)));
    }

    public ItemStackBuilder type(Material material) {
        return transform(itemStack -> itemStack.withType(material));
    }

    public ItemStackBuilder lore(Component line) {
        return transformMeta(meta -> {
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(Text.removeItalic(line));
            meta.lore(lore);
        });
    }

    public ItemStackBuilder lore(Component... lines) {
        return transformMeta(meta -> {
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }

            for (Component line : lines) {
                lore.add(Text.removeItalic(line));
            }
        });
    }

    public ItemStackBuilder lore(Iterable<Component> lines) {
        return transformMeta(meta -> {
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            for (Component line : lines) {
                lore.add(Text.removeItalic(line));
            }
            meta.lore(lore);
        });
    }

    public ItemStackBuilder clearLore() {
        return transformMeta(meta -> meta.lore(new ArrayList<>()));
    }

    public ItemStackBuilder durability(int durability) {
        return transformMeta(meta -> ((Damageable) meta).setDamage(durability));
    }

    public ItemStackBuilder customModelData(@Nullable Integer customModelData) {
        return transformMeta(meta -> meta.setCustomModelData(customModelData));
    }

    public ItemStackBuilder amount(int amount) {
        return transform(itemStack -> itemStack.setAmount(amount));
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackBuilder enchant(Enchantment enchantment) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, 1));
    }

    public ItemStackBuilder clearEnchantments() {
        return transform(itemStack -> itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment));
    }

    public ItemStackBuilder flag(ItemFlag... flags) {
        return transformMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemStackBuilder unflag(ItemFlag... flags) {
        return transformMeta(meta -> meta.removeItemFlags(flags));
    }

    public ItemStackBuilder hideAttributes() {
        return flag(ALL_FLAGS);
    }

    public ItemStackBuilder showAttributes() {
        return unflag(ALL_FLAGS);
    }

    public ItemStackBuilder color(Color color) {
        return transformMeta(meta -> ((LeatherArmorMeta) meta).setColor(color));
    }

    public ItemStackBuilder breakable(boolean flag) {
        return transformMeta(meta -> meta.setUnbreakable(!flag));
    }

    public ItemStackBuilder apply(Consumer<ItemStackBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public Item.Builder buildItem() {
        return Item.builder(build());
    }

    public Item build(@Nullable Runnable handler) {
        return buildItem().bind(handler, ClickType.RIGHT, ClickType.LEFT).build();
    }

    public Item build(ClickType type, @Nullable Runnable handler) {
        return buildItem().bind(type, handler).build();
    }

    public Item build(@Nullable Runnable rightClick, @Nullable Runnable leftClick) {
        return buildItem().bind(ClickType.RIGHT, rightClick).bind(ClickType.LEFT, leftClick).build();
    }

    public Item buildFromMap(Map<ClickType, Runnable> handlers) {
        return buildItem().bindAllRunnables(handlers.entrySet()).build();
    }

    public Item buildConsumer(@Nullable Consumer<InventoryClickEvent> handler) {
        return buildItem().bind(handler, ClickType.RIGHT, ClickType.LEFT).build();
    }

    public Item buildConsumer(ClickType type, @Nullable Consumer<InventoryClickEvent> handler) {
        return buildItem().bind(type, handler).build();
    }

    public Item buildConsumer(@Nullable Consumer<InventoryClickEvent> rightClick, @Nullable Consumer<InventoryClickEvent> leftClick) {
        return buildItem().bind(ClickType.RIGHT, rightClick).bind(ClickType.LEFT, leftClick).build();
    }

    public Item buildFromConsumerMap(Map<ClickType, Consumer<InventoryClickEvent>> handlers) {
        return buildItem().bindAllConsumers(handlers.entrySet()).build();
    }
}