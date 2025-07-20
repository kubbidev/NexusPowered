package me.kubbidev.nexuspowered.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.gson.GsonBuilder;
import org.bukkit.util.Vector;

/**
 * Utility for serializing and deserializing Vector instances
 */
public final class VectorSerializers {

    private VectorSerializers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static JsonObject serialize(Vector vector) {
        return GsonBuilder.object()
            .add("x", vector.getX())
            .add("y", vector.getY())
            .add("z", vector.getZ())
            .build();
    }

    public static Vector deserialize(JsonElement element) {
        return new Vector(
            element.getAsJsonObject().get("x").getAsLong(),
            element.getAsJsonObject().get("y").getAsLong(),
            element.getAsJsonObject().get("z").getAsLong()
        );
    }
}