package com.kubbidev.nexuspowered.common.util.duration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * A utility class for formatting durations into human-readable strings.
 * The class provides options for formatting durations in a long or concise format with a specified accuracy.
 */
public class DurationFormatter {

    /**
     * A pre-configured DurationFormatter instance that formats durations in a long format.
     */
    public static final DurationFormatter LONG = new DurationFormatter(false);

    /**
     * A pre-configured DurationFormatter instance that formats durations in a concise format.
     */
    public static final DurationFormatter CONCISE = new DurationFormatter(true);

    /**
     * A pre-configured DurationFormatter instance that formats durations in a concise format with low accuracy.
     */
    public static final DurationFormatter CONCISE_LOW_ACCURACY = new DurationFormatter(true, 3);

    /**
     * An array of ChronoUnits used to determine the units of the formatted duration.
     */
    private static final ChronoUnit[] UNITS = new ChronoUnit[]{
            ChronoUnit.YEARS,
            ChronoUnit.MONTHS,
            ChronoUnit.WEEKS,
            ChronoUnit.DAYS,
            ChronoUnit.HOURS,
            ChronoUnit.MINUTES,
            ChronoUnit.SECONDS
    };

    private final boolean concise;
    private final int accuracy;

    /**
     * Constructs a DurationFormatter with the specified format.
     *
     * @param concise Whether to format durations in a concise format.
     */
    public DurationFormatter(boolean concise) {
        this(concise, Integer.MAX_VALUE);
    }

    /**
     * Constructs a DurationFormatter with the specified format and accuracy.
     *
     * @param concise  Whether to format durations in a concise format.
     * @param accuracy The maximum number of units to show in the formatted duration.
     */
    public DurationFormatter(boolean concise, int accuracy) {
        this.concise = concise;
        this.accuracy = accuracy;
    }

    /**
     * Formats the provided duration as a plain string using legacy formatting codes.
     *
     * @param duration The duration to format.
     * @return The formatted duration as a plain string with legacy formatting codes.
     */
    public @NotNull Component format(@NotNull Duration duration) {
        long seconds = duration.getSeconds();
        int outputSize = 0;

        TextComponent.Builder builder = Component.text();

        for (ChronoUnit unit : UNITS) {

            long n = seconds / unit.getDuration().getSeconds();
            if (n > 0) {
                seconds -= unit.getDuration().getSeconds() * n;
                if (outputSize != 0) {
                    builder.appendSpace();
                }
                builder.append(formatPart(n, unit));
                outputSize++;
            }
            if (seconds <= 0 || outputSize >= this.accuracy) {
                break;
            }
        }

        if (outputSize == 0) {
            return formatPart(0, ChronoUnit.SECONDS);
        }
        return builder.build();
    }

    /**
     * Formats a part of the duration (e.g., years, months, days) as a string.
     *
     * @param amount The amount of the unit to format.
     * @param unit   The ChronoUnit representing the unit to format.
     * @return The formatted part of the duration as a string.
     */
    private @NotNull Component formatPart(long amount, @NotNull ChronoUnit unit) {
        String format = this.concise ? "short" : amount == 1 ? "singular" : "plural";
        String translationKey = "nexuspowered.duration.unit." + unit.name().toLowerCase(Locale.ROOT) + "." + format;
        return Component.translatable(translationKey, Component.text(amount));
    }
}
