package me.kubbidev.nexuspowered.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.kubbidev.nexuspowered.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;

/**
 * Utility methods for converting ItemStacks and Inventories to and from JSON.
 */
public final class Serializers {

    private Serializers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static JsonPrimitive serializeItemStack(ItemStack item) {
        return GsonBuilder.primitiveNonNull(InventorySerialization.encodeItemStackToString(item));
    }

    public static ItemStack deserializeItemstack(JsonElement data) {
        Preconditions.checkArgument(data.isJsonPrimitive());
        return InventorySerialization.decodeItemStack(data.getAsString());
    }

    public static JsonPrimitive serializeItemStacks(ItemStack[] items) {
        return GsonBuilder.primitiveNonNull(InventorySerialization.encodeItemStacksToString(items));
    }

    public static ItemStack[] deserializeItemStacks(JsonElement data) {
        Preconditions.checkArgument(data.isJsonPrimitive());
        return InventorySerialization.decodeItemStacks(data.getAsString());
    }
}