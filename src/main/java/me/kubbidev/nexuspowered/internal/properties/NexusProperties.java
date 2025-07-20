package me.kubbidev.nexuspowered.internal.properties;

import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

/**
 * NexusPowered properties.
 */
public final class NexusProperties {

    /**
     * Property for specifying whether debug mode is enabled.
     */
    public static final Property<Boolean> DEBUG = property("debug", Boolean::parseBoolean, false);

    private NexusProperties() {
    }

    /**
     * Creates a new property.
     *
     * @param name         the property name
     * @param parser       the value parser
     * @param defaultValue the default value
     * @param <T>          the value type
     * @return a property
     */
    public static <T> Property<T> property(String name, Function<String, T> parser, @Nullable T defaultValue) {
        return NexusPropertiesImpl.property(name, parser, defaultValue);
    }

    /**
     * A property.
     *
     * @param <T> the value type
     */
    @FunctionalInterface
    public interface Property<T> {

        /**
         * Gets the value.
         *
         * @return the value
         */
        @Nullable T value();
    }
}