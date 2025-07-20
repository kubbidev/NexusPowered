package me.kubbidev.nexuspowered.interfaces;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that knows it's own type parameter.
 *
 * @param <T> the type
 */
@FunctionalInterface
public interface TypeAware<T> {

    @NotNull TypeToken<T> getType();
}