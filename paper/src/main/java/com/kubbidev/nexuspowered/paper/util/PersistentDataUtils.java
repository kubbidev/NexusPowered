package com.kubbidev.nexuspowered.paper.util;

import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class PersistentDataUtils {

    private PersistentDataUtils() {
        throw new AssertionError("No com.kubbidev.nexuspowered.paper.util.PersistentDataUtils instances for you!");
    }

    public static @Nullable <Z> Z getData(PersistentDataHolder holder, PersistentDataType<?, Z> type, NamespacedKey key) {
        return getData(holder.getPersistentDataContainer(), type, key);
    }

    public static void setData(PersistentDataHolder holder, NamespacedKey key, Object value) {
        setData(holder.getPersistentDataContainer(), key, value);

        if (holder instanceof BlockState) {
            ((BlockState) holder).update();
        }
    }

    public static void removeData(PersistentDataHolder holder, NamespacedKey key) {
        removeData(holder.getPersistentDataContainer(), key);

        if (holder instanceof BlockState) {
            ((BlockState) holder).update();
        }
    }

    public static boolean hasData(PersistentDataHolder holder, NamespacedKey key) {
        return hasData(holder.getPersistentDataContainer(), key);
    }

    public static @Nullable String getStringData(PersistentDataHolder holder, NamespacedKey key) {
        return getStringData(holder.getPersistentDataContainer(), key);
    }

    public static int getIntData(PersistentDataHolder holder, NamespacedKey key) {
        return getIntData(holder.getPersistentDataContainer(), key);
    }

    public static long getLongData(PersistentDataHolder holder, NamespacedKey key) {
        return getLongData(holder.getPersistentDataContainer(), key);
    }

    public static double getDoubleData(PersistentDataHolder holder, NamespacedKey key) {
        return getDoubleData(holder.getPersistentDataContainer(), key);
    }

    public static boolean getBooleanData(PersistentDataHolder holder, NamespacedKey key) {
        return getBooleanData(holder.getPersistentDataContainer(), key);
    }

    public static @Nullable <Z> Z getData(ItemStack item, PersistentDataType<?, Z> type, NamespacedKey key) {
        return getData(item.getItemMeta(), type, key);
    }

    public static void setData(ItemStack item, NamespacedKey key, Object value) {
        item.editMeta(itemMeta -> setData(itemMeta, key, value));
    }

    public static void removeData(ItemStack item, NamespacedKey key) {
        item.editMeta(itemMeta -> removeData(itemMeta, key));
    }

    public static boolean hasData(ItemStack item, NamespacedKey key) {
        return hasData(item.getItemMeta(), key);
    }

    public static @Nullable String getStringData(ItemStack item, NamespacedKey key) {
        return getStringData(item.getItemMeta(), key);
    }

    public static int getIntData(ItemStack item, NamespacedKey key) {
        return getIntData(item.getItemMeta(), key);
    }

    public static long getLongData(ItemStack item, NamespacedKey key) {
        return getLongData(item.getItemMeta(), key);
    }

    public static double getDoubleData(ItemStack item, NamespacedKey key) {
        return getDoubleData(item.getItemMeta(), key);
    }

    public static boolean getBooleanData(ItemStack item, NamespacedKey key) {
        return getBooleanData(item.getItemMeta(), key);
    }

    public static @Nullable <Z> Z getData(PersistentDataContainer container, PersistentDataType<?, Z> type, NamespacedKey key) {
        if (container.has(key, type)) {
            return container.get(key, type);
        }
        return null;
    }

    public static void setData(PersistentDataContainer container, NamespacedKey key, Object value) {
        if (value instanceof Boolean) {
            container.set(key, PersistentDataType.BOOLEAN, (boolean) value);
        } else if (value instanceof Double) {
            container.set(key, PersistentDataType.DOUBLE, (double) value);
        } else if (value instanceof Integer) {
            container.set(key, PersistentDataType.INTEGER, (int) value);
        } else if (value instanceof Long) {
            container.set(key, PersistentDataType.LONG, (long) value);
        } else {
            container.set(key, PersistentDataType.STRING, value.toString());
        }
    }

    public static void removeData(PersistentDataContainer container, NamespacedKey key) {
        container.remove(key);
    }

    public static boolean hasData(PersistentDataContainer container, NamespacedKey key) {
        return container.has(key);
    }

    public static @Nullable String getStringData(PersistentDataContainer container, NamespacedKey key) {
        return getData(container, PersistentDataType.STRING, key);
    }

    public static int getIntData(PersistentDataContainer container, NamespacedKey key) {
        Integer o = getData(container, PersistentDataType.INTEGER, key);
        if (o == null)
            return 0;

        return o;
    }

    public static long getLongData(PersistentDataContainer container, NamespacedKey key) {
        Long o = getData(container, PersistentDataType.LONG, key);
        if (o == null)
            return 0L;

        return o;
    }

    public static double getDoubleData(PersistentDataContainer container, NamespacedKey key) {
        Double o = getData(container, PersistentDataType.DOUBLE, key);
        if (o == null)
            return 0.0;

        return o;
    }

    public static boolean getBooleanData(PersistentDataContainer container, NamespacedKey key) {
        return Boolean.TRUE.equals(getData(container, PersistentDataType.BOOLEAN, key));
    }
}
