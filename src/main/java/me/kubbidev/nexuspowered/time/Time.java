package me.kubbidev.nexuspowered.time;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A collection of time related utilities.
 */
public final class Time {

    private Time() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gets the current unix time in milliseconds.
     *
     * @return the current unix time
     */
    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Gets the current unix time in seconds.
     *
     * @return the current unix time
     */
    public static long nowSeconds() {
        return nowMillis() / 1000L;
    }

    /**
     * Gets the current unix time as an {@link Instant}.
     *
     * @return the current unix time
     */
    public static Instant now() {
        return Instant.ofEpochMilli(System.currentTimeMillis());
    }

    /**
     * Gets the difference between {@link #now() now} and another instant.
     *
     * @param other the other instant
     * @return the difference
     */
    public static Duration diffToNow(Instant other) {
        return Duration.between(now(), other).abs();
    }

    /**
     * Gets a {@link Duration} for a {@link TimeUnit} and amount.
     *
     * @param unit   the unit
     * @param amount the amount
     * @return the duration
     */
    public static Duration duration(TimeUnit unit, long amount) {
        Objects.requireNonNull(unit, "unit");
        return switch (unit) {
            case NANOSECONDS -> Duration.ofNanos(amount);
            case MICROSECONDS -> Duration.ofNanos(TimeUnit.MICROSECONDS.toNanos(amount));
            case MILLISECONDS -> Duration.ofMillis(amount);
            case SECONDS -> Duration.ofSeconds(amount);
            case MINUTES -> Duration.ofMinutes(amount);
            case HOURS -> Duration.ofHours(amount);
            case DAYS -> Duration.ofDays(amount);
        };
    }
}