package me.kubbidev.nexuspowered.gson.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
class MutableGsonConverter extends AbstractGsonConverter<HashMap<String, Object>, ArrayList<Object>, HashSet<Object>> {

    public static final MutableGsonConverter INSTANCE = new MutableGsonConverter();

    private MutableGsonConverter() {
    }

    @Override
    protected MapBuilder<HashMap<String, Object>, String, Object> newMapBuilder() {
        return new MutableMapBuilder<>();
    }

    @Override
    protected ListBuilder<ArrayList<Object>, Object> newListBuilder() {
        return new MutableListBuilder<>();
    }

    @Override
    protected SetBuilder<HashSet<Object>, Object> newSetBuilder() {
        return new MutableSetBuilder<>();
    }

    private static final class MutableMapBuilder<K, V> implements MapBuilder<HashMap<K, V>, K, V> {

        private final HashMap<K, V> builder = new HashMap<>();

        @Override
        public void put(@Nullable K key, @Nullable V value) {
            if (key != null && value != null) {
                this.builder.put(key, value);
            }
        }

        @Override
        public HashMap<K, V> build() {
            return this.builder;
        }
    }

    private static final class MutableListBuilder<E> implements ListBuilder<ArrayList<E>, E> {

        private final ArrayList<E> builder = new ArrayList<>();

        @Override
        public void add(@Nullable E element) {
            if (element != null) {
                this.builder.add(element);
            }
        }

        @Override
        public ArrayList<E> build() {
            return this.builder;
        }
    }

    private static final class MutableSetBuilder<E> implements SetBuilder<HashSet<E>, E> {

        private final HashSet<E> builder = new HashSet<>();

        @Override
        public void add(@Nullable E element) {
            if (element != null) {
                this.builder.add(element);
            }
        }

        @Override
        public HashSet<E> build() {
            return this.builder;
        }
    }
}