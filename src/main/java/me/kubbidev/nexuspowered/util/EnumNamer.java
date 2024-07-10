package me.kubbidev.nexuspowered.util;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Small utility to cache custom name lookups for enum values.
 *
 * @param <E> the enum type
 */
public class EnumNamer<E extends Enum<E>> {
    public static final Function<Enum<?>, String> LOWER_CASE_NAME = value -> value.name().toLowerCase(Locale.ROOT);

    private final String[] names;
    private final Function<? super E, String> namingFunction;

    public EnumNamer(Class<E> enumClass, Map<? super E, String> definedNames, Function<? super E, String> namingFunction) {
        E[] values = enumClass.getEnumConstants();
        this.names = new String[values.length];
        for (E value : values) {
            String name = definedNames.get(value);
            if (name == null) {
                name = namingFunction.apply(value);
            }
            this.names[value.ordinal()] = name;
        }
        this.namingFunction = namingFunction;
    }

    public EnumNamer(Class<E> enumClass, Function<? super E, String> namingFunction) {
        this(enumClass, Collections.emptyMap(), namingFunction);
    }

    public String name(E value) {
        int ordinal = value.ordinal();
        // support the Bukkit-Forge hack where enum constants are added at runtime...
        if (ordinal >= this.names.length) {
            return this.namingFunction.apply(value);
        }
        return this.names[ordinal];
    }

}