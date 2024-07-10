package me.kubbidev.nexuspowered.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Utility methods for parsing {@link Number}s, {@link Integer}s, {@link Long}s,
 * {@link Float}s and {@link Double}s from {@link String}s.
 */
public final class Numbers {

    // number

    @Nullable
    public static Number parseNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return NumberFormat.getInstance().parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    @NotNull
    public static Optional<Number> parse(@NotNull String s) {
        return Optional.ofNullable(parseNullable(s));
    }

    // integer

    @Nullable
    public static Integer parseIntegerNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static Optional<Integer> parseIntegerOpt(@NotNull String s) {
        return Optional.ofNullable(parseIntegerNullable(s));
    }

    @NotNull
    public static OptionalInt parseInteger(@NotNull String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    // long

    @Nullable
    public static Long parseLongNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static Optional<Long> parseLongOpt(@NotNull String s) {
        return Optional.ofNullable(parseLongNullable(s));
    }

    @NotNull
    public static OptionalLong parseLong(@NotNull String s) {
        try {
            return OptionalLong.of(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }

    // float

    @Nullable
    public static Float parseFloatNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static Optional<Float> parseFloatOpt(@NotNull String s) {
        return Optional.ofNullable(parseFloatNullable(s));
    }

    @NotNull
    public static OptionalDouble parseFloat(@NotNull String s) {
        try {
            return OptionalDouble.of(Float.parseFloat(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // double

    @Nullable
    public static Double parseDoubleNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static Optional<Double> parseDoubleOpt(@NotNull String s) {
        return Optional.ofNullable(parseDoubleNullable(s));
    }

    @NotNull
    public static OptionalDouble parseDouble(@NotNull String s) {
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // byte

    @Nullable
    public static Byte parseByteNullable(@NotNull String s) {
        Objects.requireNonNull(s);
        try {
            return Byte.parseByte(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static Optional<Byte> parseByteOpt(@NotNull String s) {
        return Optional.ofNullable(parseByteNullable(s));
    }

    private Numbers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}