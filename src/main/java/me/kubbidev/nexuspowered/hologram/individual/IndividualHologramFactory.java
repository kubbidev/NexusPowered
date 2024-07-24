package me.kubbidev.nexuspowered.hologram.individual;

import me.kubbidev.nexuspowered.serialize.Position;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A object which can create {@link IndividualHologram}s.
 */
@ApiStatus.Experimental
public interface IndividualHologramFactory {

    /**
     * Creates a new hologram.
     *
     * @param position the position of the hologram
     * @param lines the lines to display
     * @return the new hologram
     */
    @NotNull
    IndividualHologram newHologram(@NotNull Position position, @NotNull List<HologramLine> lines);

}