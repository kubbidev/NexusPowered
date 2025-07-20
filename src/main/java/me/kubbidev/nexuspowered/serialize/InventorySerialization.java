package me.kubbidev.nexuspowered.serialize;

import org.bukkit.inventory.ItemStack;

public final class InventorySerialization {

    private InventorySerialization() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static byte[] encodeItemStack(ItemStack item) {
        return item.serializeAsBytes();
    }

    public static String encodeItemStackToString(ItemStack item) {
        return Base64Util.encode(encodeItemStack(item));
    }

    public static ItemStack decodeItemStack(byte[] buf) {
        return ItemStack.deserializeBytes(buf);
    }

    public static ItemStack decodeItemStack(String data) {
        return decodeItemStack(Base64Util.decode(data));
    }

    public static byte[] encodeItemStacks(ItemStack[] items) {
        return ItemStack.serializeItemsAsBytes(items);
    }

    public static String encodeItemStacksToString(ItemStack[] items) {
        return Base64Util.encode(encodeItemStacks(items));
    }

    public static ItemStack[] decodeItemStacks(byte[] buf) {
        return ItemStack.deserializeItemsFromBytes(buf);
    }

    public static ItemStack[] decodeItemStacks(String data) {
        return decodeItemStacks(Base64Util.decode(data));
    }
}