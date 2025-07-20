package me.kubbidev.nexuspowered.util;

public final class EnumUniverse {

    private EnumUniverse() {
    }

    public static <E extends Enum<E>> E getEnumByOrdinal(Enum<?> value, E[] enums) {
        return getEnumByOrdinal(value.ordinal(), enums);
    }

    public static <E extends Enum<E>> E getEnumByOrdinal(int ordinal, E[] enums) {
        if (ordinal < enums.length) {
            return enums[ordinal];
        } else {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }
    }
}
