package me.kubbidev.nexuspowered.serialize;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class InventorySerialization {

    public static byte[] encodeItemStack(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeObject(item);
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeItemStackToString(ItemStack item) {
        return Base64Util.encode(encodeItemStack(item));
    }

    public static ItemStack decodeItemStack(byte[] buf) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                return (ItemStack) dataInput.readObject();
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack decodeItemStack(String data) {
        return decodeItemStack(Base64Util.decode(data));
    }

    public static byte[] encodeItemStacks(ItemStack[] items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeInt(items.length);
                for (ItemStack item : items) {
                    dataOutput.writeObject(item);
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeItemStacksToString(ItemStack[] items) {
        return Base64Util.encode(encodeItemStacks(items));
    }

    public static ItemStack[] decodeItemStacks(byte[] buf) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                ItemStack[] items = new ItemStack[dataInput.readInt()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = (ItemStack) dataInput.readObject();
                }
                return items;
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack[] decodeItemStacks(String data) {
        return decodeItemStacks(Base64Util.decode(data));
    }

    public static byte[] encodeInventory(Inventory inventory) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeInt(inventory.getSize());
                for (int i = 0; i < inventory.getSize(); i++) {
                    dataOutput.writeObject(inventory.getItem(i));
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeInventoryToString(Inventory inventory) {
        return Base64Util.encode(encodeInventory(inventory));
    }

    public static Inventory decodeInventory(byte[] buf, String title) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                Inventory inventory = Bukkit.createInventory(null, dataInput.readInt(), title);
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, (ItemStack) dataInput.readObject());
                }
                return inventory;
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Inventory decodeInventory(String data, String title) {
        return decodeInventory(Base64Util.decode(data), title);
    }

    private InventorySerialization() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}