package me.kubbidev.nexuspowered.gson;

import com.google.gson.JsonElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object which can be serialized to JSON.
 *
 * <p>Classes which implement this interface should also implement a static "deserialize" method,
 * accepting {@link JsonElement} as the only parameter.</p>
 */
@FunctionalInterface
public interface GsonSerializable {

    /**
     * Gets the deserialization method for a given class.
     *
     * @param clazz the class
     * @return the deserialization method, if the class has one
     */
    static @Nullable Method getDeserializeMethod(@NotNull Class<?> clazz) {
        if (!GsonSerializable.class.isAssignableFrom(clazz)) {
            return null;
        }

        Method deserializeMethod;
        try {
            deserializeMethod = clazz.getDeclaredMethod("deserialize", JsonElement.class);
            deserializeMethod.setAccessible(true);
        } catch (Exception e) {
            return null;
        }

        if (!Modifier.isStatic(deserializeMethod.getModifiers())) {
            return null;
        }

        return deserializeMethod;
    }

    /**
     * Serializes the object to JSON.
     *
     * @return a json form of this object
     */
    @NotNull JsonElement serialize();
}