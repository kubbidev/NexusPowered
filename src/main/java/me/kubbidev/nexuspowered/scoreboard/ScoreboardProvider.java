package me.kubbidev.nexuspowered.scoreboard;

import org.jetbrains.annotations.NotNull;

/**
 * An object which provides {@link Scoreboard} instances.
 */
public interface ScoreboardProvider {

    /**
     * Gets the scoreboard provided by this instance.
     *
     * @return the scoreboard
     */
    @NotNull
    Scoreboard getScoreboard();

}
