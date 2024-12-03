package me.kubbidev.nexuspowered.datatree;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GsonDataTree implements DataTree {
    private final JsonElement element;

    public GsonDataTree(JsonElement element) {
        this.element = Objects.requireNonNull(element, "element");
    }

    public JsonElement getElement() {
        return this.element;
    }

    @Override
    public @NotNull DataTree resolve(@NotNull Object... path) {
        if (path.length == 0) {
            return this;
        }

        JsonElement o = this.element;
        for (int i = 0; i < path.length; i++) {
            Object p = path[i];

            if (p instanceof String) {
                String memberName = (String) p;
                JsonObject obj = o.getAsJsonObject();
                if (!obj.has(memberName)) {
                    throw new IllegalArgumentException("Object " + obj + " does not have member: " + memberName);
                }
                o = obj.get(memberName);
            } else if (p instanceof Number) {
                o = o.getAsJsonArray().get(((Number) p).intValue());
            } else {
                throw new IllegalArgumentException("Unknown path node at index " + i + ": " + p);
            }
        }
        return new GsonDataTree(o);
    }

    @Override
    public @NotNull Stream<? extends Map.Entry<String, ? extends DataTree>> asObject() {
        return this.element.getAsJsonObject().entrySet().stream()
                .map(entry -> Maps.immutableEntry(entry.getKey(), new GsonDataTree(entry.getValue())));
    }

    @Override
    public @NotNull Stream<? extends DataTree> asArray() {
        return StreamSupport.stream(this.element.getAsJsonArray().spliterator(), false)
                .map(GsonDataTree::new);
    }

    @Override
    public @NotNull Stream<? extends Map.Entry<Integer, ? extends DataTree>> asIndexedArray() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<Map.Entry<Integer, GsonDataTree>>() {
            private final Iterator<JsonElement> iterator = GsonDataTree.this.element.getAsJsonArray().iterator();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            @Override
            public Map.Entry<Integer, GsonDataTree> next() {
                return Maps.immutableEntry(this.index++, new GsonDataTree(this.iterator.next()));
            }
        }, Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
    }

    @Override
    public @NotNull String asString() {
        return this.element.getAsString();
    }

    @Override
    public @NotNull Number asNumber() {
        return this.element.getAsNumber();
    }

    @Override
    public int asInt() {
        return this.element.getAsInt();
    }

    @Override
    public double asDouble() {
        return this.element.getAsDouble();
    }

    @Override
    public boolean asBoolean() {
        return this.element.getAsBoolean();
    }
}