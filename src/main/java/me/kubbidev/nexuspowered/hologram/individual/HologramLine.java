package me.kubbidev.nexuspowered.hologram.individual;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * Represents a line in a hologram.
 */
@ApiStatus.Experimental
public interface HologramLine {

    /**
     * Returns a new {@link HologramLine.Builder}.
     *
     * @return a new builder
     */
    static HologramLine.Builder builder() {
        return new Builder();
    }

    /**
     * Returns a hologram line that doesn't change between players.
     *
     * @param text the text to display
     * @return the line
     */
    @NotNull
    static HologramLine fixed(@NotNull Component text) {
        return viewer -> text;
    }

    @NotNull
    static HologramLine fromFunction(@NotNull Function<Player, Component> function) {
        return function::apply;
    }

    /**
     * Gets the string representation of the line, for the given player.
     *
     * @param viewer the player
     * @return the line
     */
    @NotNull
    Component resolve(Player viewer);

    final class Builder {
        private final ImmutableList.Builder<HologramLine> lines = ImmutableList.builder();

        private Builder() {

        }

        public Builder line(HologramLine line) {
            this.lines.add(line);
            return this;
        }

        public Builder lines(Iterable<? extends HologramLine> lines) {
            this.lines.addAll(lines);
            return this;
        }

        public Builder line(Component line) {
            return line(HologramLine.fixed(line));
        }

        public Builder fromFunction(@NotNull Function<Player, Component> function) {
            return line(HologramLine.fromFunction(function));
        }

        public List<HologramLine> build() {
            return this.lines.build();
        }
    }

}