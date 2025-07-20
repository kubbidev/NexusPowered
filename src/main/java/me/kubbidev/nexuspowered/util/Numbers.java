package me.kubbidev.nexuspowered.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for parsing {@link Number}s, {@link Integer}s, {@link Long}s, {@link Float}s and {@link Double}s from
 * {@link String}s.
 */
@NotNullByDefault
public final class Numbers {

    // number

    private Numbers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static @Nullable Number parseNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return NumberFormat.getInstance().parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Optional<Number> parse(@NotNull String s) {
        return Optional.ofNullable(parseNullable(s));
    }

    // integer

    public static @Nullable Integer parseIntegerNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Optional<Integer> parseIntegerOpt(@NotNull String s) {
        return Optional.ofNullable(parseIntegerNullable(s));
    }

    public static OptionalInt parseInteger(@NotNull String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    // long

    public static @Nullable Long parseLongNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Optional<Long> parseLongOpt(@NotNull String s) {
        return Optional.ofNullable(parseLongNullable(s));
    }

    public static OptionalLong parseLong(@NotNull String s) {
        try {
            return OptionalLong.of(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }

    // float

    public static @Nullable Float parseFloatNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Optional<Float> parseFloatOpt(@NotNull String s) {
        return Optional.ofNullable(parseFloatNullable(s));
    }

    public static OptionalDouble parseFloat(@NotNull String s) {
        try {
            return OptionalDouble.of(Float.parseFloat(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // double

    public static @Nullable Double parseDoubleNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Optional<Double> parseDoubleOpt(@NotNull String s) {
        return Optional.ofNullable(parseDoubleNullable(s));
    }

    public static OptionalDouble parseDouble(@NotNull String s) {
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // byte

    public static @Nullable Byte parseByteNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Byte.parseByte(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Optional<Byte> parseByteOpt(@NotNull String s) {
        return Optional.ofNullable(parseByteNullable(s));
    }
}