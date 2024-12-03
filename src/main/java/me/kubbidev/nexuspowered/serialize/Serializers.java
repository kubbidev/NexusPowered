package me.kubbidev.nexuspowered.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Utility methods for converting ItemStacks and Inventories to and from JSON.
 */
public final class Serializers {

    public static JsonPrimitive serializeItemStack(ItemStack item) {
        return JsonBuilder.primitiveNonNull(InventorySerialization.encodeItemStackToString(item));
    }

    public static ItemStack deserializeItemstack(JsonElement data) {
        Preconditions.checkArgument(data.isJsonPrimitive());
        return InventorySerialization.decodeItemStack(data.getAsString());
    }

    public static JsonPrimitive serializeItemStacks(ItemStack[] items) {
        return JsonBuilder.primitiveNonNull(InventorySerialization.encodeItemStacksToString(items));
    }

    public static JsonPrimitive serializeInventory(Inventory inventory) {
        return JsonBuilder.primitiveNonNull(InventorySerialization.encodeInventoryToString(inventory));
    }

    public static ItemStack[] deserializeItemStacks(JsonElement data) {
        Preconditions.checkArgument(data.isJsonPrimitive());
        return InventorySerialization.decodeItemStacks(data.getAsString());
    }

    public static Inventory deserializeInventory(JsonElement data, String title) {
        Preconditions.checkArgument(data.isJsonPrimitive());
        return InventorySerialization.decodeInventory(data.getAsString(), title);
    }

    private Serializers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}