package me.kubbidev.nexuspowered.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class to cache custom name lookups for enum values.
 *
 * @param <E> the enum type
 */
public class EnumNamer<E extends Enum<E>> {
    /**
     * Function to convert an enum value's name to lowercase
     */
    public static final Function<Enum<?>, String> LOWER_CASE_NAME = e -> e.name().toLowerCase(Locale.ROOT);

    private final EnumMap<E, String> nameEnumMap;
    private final Function<? super E, String> namingFunction;

    /**
     * Constructs an {@link EnumNamer} with a mapping of predefined names and a naming function.
     *
     * @param enumClass      the class of the enum
     * @param definedNames   a map of predefined names
     * @param namingFunction function to generate names for enum values not in the map
     */
    public EnumNamer(Class<E> enumClass, Map<E, String> definedNames, Function<? super E, String> namingFunction) {
        this.nameEnumMap = new EnumMap<>(enumClass);
        this.namingFunction = namingFunction;

        for (E e : enumClass.getEnumConstants()) {
            this.nameEnumMap.put(e, definedNames.getOrDefault(e, namingFunction.apply(e)));
        }
    }

    /**
     * Constructs an {@link EnumNamer} with a naming function and no predefined names.
     *
     * @param enumClass      the class of the enum
     * @param namingFunction function to generate names for enum values
     */
    public EnumNamer(Class<E> enumClass, Function<? super E, String> namingFunction) {
        this(enumClass, Collections.emptyMap(), namingFunction);
    }

    /**
     * Retrieves the name associated with the given enum value.
     *
     * @param e the enum value
     * @return the name associated with the enum value
     */
    public String name(E e) {
        return this.nameEnumMap.getOrDefault(e, this.namingFunction.apply(e));
    }

}