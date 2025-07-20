package me.kubbidev.nexuspowered.gson.converter;

import org.jetbrains.annotations.NotNullByDefault;

/**
 * Provides some default {@link GsonConverter}s.
 *
 * <p>Note that {@link GsonConverter#wrap(Object)} is not affected by the implementation type.</p>
 */
@NotNullByDefault
public class GsonConverters {

    /**
     * An implementation of {@link GsonConverter}, which returns immutable collections where applicable.
     */
    public static final GsonConverter IMMUTABLE = ImmutableGsonConverter.INSTANCE;

    /**
     * An implementation of {@link GsonConverter}, which returns mutable collections where applicable.
     */
    public static final GsonConverter MUTABLE = MutableGsonConverter.INSTANCE;

    private GsonConverters() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}