package me.kubbidev.nexuspowered.scheduler;

import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for converting between Minecraft game ticks and standard durations.
 */
public final class Ticks {

    // the number of ticks which occur in a second - this is a server implementation detail
    public static final int TICKS_PER_SECOND        = 20;
    // the number of milliseconds in a second - constant
    public static final int MILLISECONDS_PER_SECOND = 1000;
    // the number of milliseconds in a tick - assuming the server runs at a perfect tick rate
    public static final int MILLISECONDS_PER_TICK   = MILLISECONDS_PER_SECOND / TICKS_PER_SECOND;

    private Ticks() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Converts a duration in a certain unit of time to ticks.
     *
     * <p><code>Ticks.from(duration)</code> returns the number of ticks <b>from</b> the given duration.</p>
     *
     * @param duration the duration of time
     * @param unit     the unit the duration is in
     * @return the number of ticks which represent the duration
     */
    public static long from(long duration, @NotNull TimeUnit unit) {
        return unit.toMillis(duration) / MILLISECONDS_PER_TICK;
    }

    /**
     * Converts ticks to a duration in a certain unit of time.
     *
     * <p><code>Ticks.to(ticks)</code> converts the number of ticks <b>to</b> a duration.</p>
     *
     * @param ticks the number of ticks
     * @param unit  the unit to return the duration in
     * @return a duration value in the given unit, representing the number of ticks
     */
    public static long to(long ticks, @NotNull TimeUnit unit) {
        return unit.convert(ticks * MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS);
    }

}