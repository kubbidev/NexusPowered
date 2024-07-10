package me.kubbidev.nexuspowered.hologram;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.serialize.Position;
import me.kubbidev.nexuspowered.util.Text;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A object which can create {@link Hologram}s.
 */
public interface HologramFactory {

    /**
     * Creates a new hologram.
     *
     * @param position the position of the hologram
     * @param lines the initial lines to display
     * @return the new hologram
     */
    @NotNull
    Hologram newHologram(@NotNull Position position, @NotNull List<Component> lines);

    /**
     * Deserializes a hologram instance from its {@link GsonSerializable serialized} form.
     *
     * @param element the data
     * @return the hologram
     */
    @NotNull
    default Hologram deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("position"));
        Preconditions.checkArgument(object.has("lines"));

        Position position = Position.deserialize(object.get("position"));
        JsonArray lineArray = object.get("lines").getAsJsonArray();
        List<Component> lines = new ArrayList<>();
        for (JsonElement e : lineArray) {
            lines.add(Text.fromGson(e.getAsString()));
        }

        return newHologram(position, lines);
    }
}