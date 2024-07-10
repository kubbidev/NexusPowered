package me.kubbidev.nexuspowered.hologram;

import com.google.gson.JsonElement;
import me.kubbidev.nexuspowered.Services;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.serialize.Position;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple hologram utility.
 */
public interface Hologram extends BaseHologram, GsonSerializable {

    /**
     * Creates and returns a new hologram
     *
     * <p>Note: the hologram will not be spawned automatically.</p>
     *
     * @param position the position of the hologram
     * @param lines the initial lines to display
     * @return the new hologram.
     */
    @NotNull
    static Hologram create(@NotNull Position position, @NotNull List<Component> lines) {
        return Services.load(HologramFactory.class).newHologram(position, lines);
    }

    static Hologram deserialize(JsonElement element) {
        return Services.load(HologramFactory.class).deserialize(element);
    }

    /**
     * Updates the lines displayed by this hologram.
     *
     * <p>This method does not refresh the actual hologram display. {@link #spawn()} must be called for these changes
     * to apply.</p>
     *
     * @param lines the new lines
     */
    void updateLines(@NotNull List<Component> lines);
}