package me.kubbidev.nexuspowered.hologram.individual;

import me.kubbidev.nexuspowered.Services;
import me.kubbidev.nexuspowered.hologram.BaseHologram;
import me.kubbidev.nexuspowered.serialize.Position;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@ApiStatus.Experimental
public interface IndividualHologram extends BaseHologram {

    /**
     * Creates and returns a new individual hologram
     *
     * <p>Note: the hologram will not be spawned automatically.</p>
     *
     * @param position the position of the hologram
     * @param lines the initial lines to display
     * @return the new hologram.
     */
    @NotNull
    static IndividualHologram create(@NotNull Position position, @NotNull List<HologramLine> lines) {
        return Services.load(IndividualHologramFactory.class).newHologram(position, lines);
    }

    /**
     * Updates the lines displayed by this hologram
     *
     * <p>This method does not refresh the actual hologram display. {@link #spawn()} must be called for these changes
     * to apply.</p>
     *
     * @param lines the new lines
     */
    void updateLines(@NotNull List<HologramLine> lines);

    /**
     * Returns a copy of the available viewers of the hologram.
     *
     * @return a {@link Set} of players.
     */
    @NotNull
    Set<Player> getViewers();

    /**
     * Adds a viewer to the hologram.
     *
     * @param player the player
     */
    void addViewer(@NotNull Player player);

    /**
     * Removes a viewer from the hologram.
     *
     * @param player the player
     */
    void removeViewer(@NotNull Player player);

    /**
     * Check if there are any viewers for the hologram.
     *
     * @return any viewers
     */
    default boolean hasViewers() {
        return !this.getViewers().isEmpty();
    }
}