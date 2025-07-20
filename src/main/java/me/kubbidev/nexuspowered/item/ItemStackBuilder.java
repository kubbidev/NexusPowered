package me.kubbidev.nexuspowered.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNullByDefault;

/**
 * Easily construct {@link ItemStack} instances.
 */
@NotNullByDefault
public final class ItemStackBuilder {

    private static final ItemFlag[] ALL_FLAGS = new ItemFlag[]{
        ItemFlag.HIDE_ENCHANTS,
        ItemFlag.HIDE_ATTRIBUTES,
        ItemFlag.HIDE_UNBREAKABLE,
        ItemFlag.HIDE_DESTROYS,
        ItemFlag.HIDE_PLACED_ON,
        ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
        ItemFlag.HIDE_DYE,
        ItemFlag.HIDE_ARMOR_TRIM,
        ItemFlag.HIDE_STORED_ENCHANTS,
    };

    private ItemStack itemStack;

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
    }

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(ItemStack.of(material)).hideAttributes();
    }

    public static ItemStackBuilder of(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack).hideAttributes();
    }

    public static ItemStackBuilder of(ConfigurationSection config) {
        return ItemStackReader.DEFAULT.read(config);
    }

    public ItemStackBuilder operate(UnaryOperator<ItemStack> operator) {
        this.itemStack = operator.apply(this.itemStack);
        return this;
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
        return this.transformMeta(meta -> meta.displayName(name));
    }

    public ItemStackBuilder type(Material material) {
        return this.operate(itemStack -> itemStack.withType(material));
    }

    public ItemStackBuilder lore(Component line) {
        return this.transformMeta(meta -> {
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(line);
            meta.lore(lore);
        });
    }

    public ItemStackBuilder clearLore() {
        return this.transformMeta(meta -> meta.lore(new ArrayList<>()));
    }

    public ItemStackBuilder durability(int durability) {
        return this.transformMeta(meta -> {
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(durability);
            }
        });
    }

    public ItemStackBuilder modelData(int modelData) {
        return this.transformMeta(meta -> meta.setCustomModelData(modelData));
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
}