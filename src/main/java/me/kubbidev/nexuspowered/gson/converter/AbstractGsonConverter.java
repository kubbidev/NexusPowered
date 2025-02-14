package me.kubbidev.nexuspowered.gson.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@NotNullByDefault
abstract class AbstractGsonConverter<M extends Map<String, Object>, L extends List<Object>, S extends Set<Object>> implements GsonConverter {

    protected abstract MapBuilder<M, String, Object> newMapBuilder();

    protected abstract ListBuilder<L, Object> newListBuilder();

    protected abstract SetBuilder<S, Object> newSetBuilder();

    // gson --> standard java objects

    @Override
    public M unwrapObject(JsonObject object) {
        MapBuilder<M, String, Object> builder = newMapBuilder();
        for (Map.Entry<String, JsonElement> e : object.entrySet()) {
            builder.put(e.getKey(), unwrapElement(e.getValue()));
        }
        return builder.build();
    }

    @Override
    public L unwrapArray(JsonArray array) {
        ListBuilder<L, Object> builder = newListBuilder();
        for (JsonElement element : array) {
            builder.add(unwrapElement(element));
        }
        return builder.build();
    }

    @Override
    public S unwrapArrayToSet(JsonArray array) {
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
    @Nullable
    public Object unwrapElement(JsonElement element) {
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
        if (object instanceof JsonElement) {
            return ((JsonElement) object);
        } else if (object instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) object;
            JsonArray array = new JsonArray();
            for (Object o : iterable) {
                array.add(wrap(o));
            }
            return array;
        } else if (object instanceof Map<?, ?>) {
            Map<?, ?> map = ((Map<?, ?>) object);
            JsonObject obj = new JsonObject();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (e.getKey() instanceof String) {
                    String key = (String) e.getKey();
                    obj.add(key, wrap(e.getValue()));
                }
            }
            return obj;
        } else if (object instanceof String) {
            return new JsonPrimitive(((String) object));
        } else if (object instanceof Character) {
            return new JsonPrimitive(((Character) object));
        } else if (object instanceof Boolean) {
            return new JsonPrimitive(((Boolean) object));
        } else if (object instanceof Number) {
            return new JsonPrimitive(((Number) object));
        } else {
            throw new IllegalArgumentException("Unable to wrap object: " + object.getClass());
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