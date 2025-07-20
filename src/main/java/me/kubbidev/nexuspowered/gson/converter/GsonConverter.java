package me.kubbidev.nexuspowered.gson.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * A utility for converting between GSON's {@link JsonElement} family of classes, and standard Java types.
 *
 * <p>All conversions are deep, meaning for collections, contained values are also converted.</p>
 */
@NotNullByDefault
public interface GsonConverter {

    /**
     * Converts a {@link JsonObject} to a {@link Map}.
     *
     * @param object the json object
     * @return a new map
     */
    Map<String, Object> unwrapObject(JsonObject object);

    /**
     * Converts a {@link JsonArray} to a {@link List}.
     *
     * @param array the json array
     * @return a new list
     */
    List<Object> unwrapArray(JsonArray array);

    /**
     * Converts a {@link JsonArray} to a {@link Set}.
     *
     * @param array the json array
     * @return a new set
     */
    Set<Object> unwrapArrayToSet(JsonArray array);

    /**
     * Extracts the underlying {@link Object} from an {@link JsonPrimitive}.
     *
     * @param primitive the json primitive
     * @return the underlying object
     */
    Object unwarpPrimitive(JsonPrimitive primitive);

    /**
     * Converts a {@link JsonElement} to a {@link Object}.
     *
     * @param element the json element
     * @return the object
     */
    @Nullable Object unwrapElement(JsonElement element);

    /**
     * Tries to wrap an object to a {@link JsonElement}.
     *
     * <p>Supported types: {@link String}, {@link Number}, {@link Boolean},
     * {@link Character}, {@link Iterable}, and {@link Map}, where the key is a {@link String}.</p>
     *
     * @param object the object to wrap
     * @return the new json element
     */
    JsonElement wrap(Object object);
}