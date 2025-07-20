package me.kubbidev.nexuspowered.gson.converter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
class ImmutableGsonConverter extends
    AbstractGsonConverter<ImmutableMap<String, Object>, ImmutableList<Object>, ImmutableSet<Object>> {

    public static final ImmutableGsonConverter INSTANCE = new ImmutableGsonConverter();

    private ImmutableGsonConverter() {
    }

    @Override
    protected MapBuilder<ImmutableMap<String, Object>, String, Object> newMapBuilder() {
        return new ImmutableMapBuilder<>();
    }

    @Override
    protected ListBuilder<ImmutableList<Object>, Object> newListBuilder() {
        return new ImmutableListBuilder<>();
    }

    @Override
    protected SetBuilder<ImmutableSet<Object>, Object> newSetBuilder() {
        return new ImmutableSetBuilder<>();
    }

    private static final class ImmutableMapBuilder<K, V> implements MapBuilder<ImmutableMap<K, V>, K, V> {

        private final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();

        @Override
        public void put(@Nullable K key, @Nullable V value) {
            if (key == null || value == null) {
                return;
            }
            this.builder.put(key, value);
        }

        @Override
        public ImmutableMap<K, V> build() {
            return this.builder.build();
        }
    }

    private static final class ImmutableListBuilder<E> implements ListBuilder<ImmutableList<E>, E> {

        private final ImmutableList.Builder<E> builder = ImmutableList.builder();

        @Override
        public void add(@Nullable E element) {
            if (element == null) {
                return;
            }
            this.builder.add(element);
        }

        @Override
        public ImmutableList<E> build() {
            return this.builder.build();
        }
    }

    private static final class ImmutableSetBuilder<E> implements SetBuilder<ImmutableSet<E>, E> {

        private final ImmutableSet.Builder<E> builder = ImmutableSet.builder();

        @Override
        public void add(@Nullable E element) {
            if (element == null) {
                return;
            }
            this.builder.add(element);
        }

        @Override
        public ImmutableSet<E> build() {
            return this.builder.build();
        }
    }

}