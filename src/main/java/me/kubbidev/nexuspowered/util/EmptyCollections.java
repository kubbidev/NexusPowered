package me.kubbidev.nexuspowered.util;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Empty collections that do not throw {@link UnsupportedOperationException} on mutate operations.
 */
public final class EmptyCollections {

    private static final EmptyList<?>   LIST = new EmptyList<>();
    private static final EmptySet<?>    SET  = new EmptySet<>();
    private static final EmptyMap<?, ?> MAP  = new EmptyMap<>();

    private EmptyCollections() {
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> list() {
        return (List<E>) LIST;
    }

    @SuppressWarnings("unchecked")
    public static <E> Set<E> set() {
        return (Set<E>) SET;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> map() {
        return (Map<K, V>) MAP;
    }

    private static final class EmptyList<E> extends AbstractList<E> {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public E get(int index) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public E set(int index, E element) {
            return null;
        }

        @Override
        public void add(int index, E element) {

        }

        @Override
        public E remove(int index) {
            throw new IndexOutOfBoundsException();
        }
    }

    private static final class EmptySet<E> extends AbstractSet<E> {

        @Override
        public @NotNull Iterator<E> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean add(E e) {
            return true;
        }
    }

    private static final class EmptyMap<K, V> extends AbstractMap<K, V> {

        @Override
        public @NotNull Set<Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }

        @Override
        public V put(K key, V value) {
            return null;
        }
    }

}