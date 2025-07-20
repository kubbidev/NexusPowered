package me.kubbidev.nexuspowered.gson.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
abstract class AbstractGsonConverter<M extends Map<String, Object>, L extends List<Object>, S extends Set<Object>> implements
    GsonConverter {

    protected abstract MapBuilder<M, String, Object> newMapBuilder();

    protected abstract ListBuilder<L, Object> newListBuilder();

    protected abstract SetBuilder<S, Object> newSetBuilder();

    // gson --> standard java objects

    @Override
    public @NotNull M unwrapObject(JsonObject object) {
        MapBuilder<M, String, Object> builder = newMapBuilder();
        for (Map.Entry<String, JsonElement> e : object.entrySet()) {
            builder.put(e.getKey(), unwrapElement(e.getValue()));
        }
        return builder.build();
    }

    @Override
    public @NotNull L unwrapArray(JsonArray array) {
        ListBuilder<L, Object> builder = newListBuilder();
        for (JsonElement element : array) {
            builder.add(unwrapElement(element));
        }
        return builder.build();
    }

    @Override
    public @NotNull S unwrapArrayToSet(JsonArray array) {
        SetBuilder<S, Object> builder = newSetBuilder();
        for (JsonElement element : array) {
            builder.add(unwrapElement(element));
        }
        return builder.build();
    }

    @Override
    public Object unwarpPrimitive(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        } else if (primitive.isNumber()) {
            return primitive.getAsNumber();
        } else if (primitive.isString()) {
            return primitive.getAsString();
        } else {
            throw new IllegalArgumentException("Unknown primitive type: " + primitive);
        }
    }

    @Override
    public @Nullable Object unwrapElement(JsonElement element) {
        if (element.isJsonNull()) {
            return null;
        } else if (element.isJsonArray()) {
            return unwrapArray(element.getAsJsonArray());
        } else if (element.isJsonObject()) {
            return unwrapObject(element.getAsJsonObject());
        } else if (element.isJsonPrimitive()) {
            return unwarpPrimitive(element.getAsJsonPrimitive());
        } else {
            throw new IllegalArgumentException("Unknown element type: " + element);
        }
    }

    // standard collections --> gson

    @Override
    public JsonElement wrap(Object object) {
        switch (object) {
            case JsonElement element -> {
                return element;
            }
            case Iterable<?> iterable -> {
                JsonArray array = new JsonArray();
                for (Object o : iterable) {
                    array.add(wrap(o));
                }
                return array;
            }
            case Map<?, ?> map -> {
                JsonObject obj = new JsonObject();
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    if (e.getKey() instanceof String key) {
                        obj.add(key, wrap(e.getValue()));
                    }
                }
                return obj;
            }
            case String s -> {
                return new JsonPrimitive(s);
            }
            case Character c -> {
                return new JsonPrimitive(c);
            }
            case Boolean b -> {
                return new JsonPrimitive(b);
            }
            case Number number -> {
                return new JsonPrimitive(number);
            }
            default -> throw new IllegalArgumentException("Unable to wrap object: " + object.getClass());
        }
    }

    protected interface MapBuilder<M extends Map<K, V>, K, V> {

        void put(@Nullable K key, @Nullable V value);

        M build();
    }

    protected interface ListBuilder<L extends List<E>, E> {

        void add(@Nullable E element);

        L build();
    }

    protected interface SetBuilder<S extends Set<E>, E> {

        void add(@Nullable E element);

        S build();
    }

}