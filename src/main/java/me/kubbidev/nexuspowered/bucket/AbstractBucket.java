package me.kubbidev.nexuspowered.bucket;

import com.google.common.collect.ImmutableList;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import me.kubbidev.nexuspowered.bucket.partitioning.PartitioningStrategy;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract implementation of {@link Bucket}.
 *
 * @param <E> the element type
 */
public abstract class AbstractBucket<E> extends AbstractSet<E> implements Bucket<E> {

    /**
     * The function used to partition objects
     */
    protected final PartitioningStrategy<E>           strategy;
    /**
     * The number of partitions in this bucket
     */
    protected final int                               size;
    /**
     * The content in the bucket
     */
    protected final Set<E>                            content;
    /**
     * The partitions in the bucket
     */
    protected final ImmutableList<Set<E>>             partitions;
    /**
     * A view of the {@link #partitions} list - with all contained values wrapped by {@link SetView}.
     */
    protected final ImmutableList<BucketPartition<E>> partitionView;
    /**
     * A cycle of the partitions in this bucket
     */
    private final   Cycle<BucketPartition<E>>         partitionCycle;

    protected AbstractBucket(int size, PartitioningStrategy<E> strategy) {
        this.strategy = strategy;

        this.size = size;
        this.content = createSet();

        ImmutableList.Builder<Set<E>> sets = ImmutableList.builder();
        ImmutableList.Builder<BucketPartition<E>> views = ImmutableList.builder();

        for (int i = 0; i < size; i++) {
            Set<E> set = createSet();
            sets.add(set);

            SetView view = new SetView(set, i);
            views.add(view);
        }

        this.partitions = sets.build();
        this.partitionView = views.build();
        this.partitionCycle = Cycle.of(this.partitionView);
    }

    /**
     * Supplies the set instances to use for each partition in the bucket
     *
     * @return a new set
     */
    protected abstract Set<E> createSet();

    @Override
    public int getPartitionCount() {
        return this.size;
    }

    @Override
    public @NotNull BucketPartition<E> getPartition(int index) {
        return this.partitionView.get(index);
    }

    @Override
    public @NotNull List<BucketPartition<E>> getPartitions() {
        return this.partitionView;
    }

    @Override
    public @NotNull Cycle<BucketPartition<E>> asCycle() {
        return this.partitionCycle;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("Buckets do not accept null elements.");
        }

        if (!this.content.add(e)) {
            return false;
        }

        this.partitions.get(this.strategy.allocate(e, this)).add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!this.content.remove(o)) {
            return false;
        }

        for (Set<E> partition : this.partitions) {
            partition.remove(o);
        }

        return true;
    }

    @Override
    public void clear() {
        for (Set<E> partition : this.partitions) {
            partition.clear();
        }
        this.content.clear();
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return new BucketIterator();
    }

    // just delegate to the backing content set

    @Override
    public int size() {
        return this.content.size();
    }

    @Override
    public boolean isEmpty() {
        return this.content.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.content.contains(o);
    }


    /**
     * Class used to wrap the result of {@link Bucket}'s {@link #iterator()} method.
     * <p>
     * This wrapping overrides the #remove method, and ensures that when removed, elements are also removed from their
     * backing partition.
     */
    private final class BucketIterator implements Iterator<E> {

        private final Iterator<E> delegate = AbstractBucket.this.content.iterator();
        private       E           current;

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public E next() {
            // track the iterators cursor to handle #remove calls
            this.current = this.delegate.next();
            return this.current;
        }

        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }

            // remove from the global collection
            this.delegate.remove();

            // also remove the element from it's contained partition
            for (Set<E> partition : AbstractBucket.this.partitions) {
                partition.remove(this.current);
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            this.delegate.forEachRemaining(action);
        }
    }

    /**
     * Class used to wrap the backing sets returned by {@link #getPartition(int)}.
     * <p>
     * This wrapping prevents add operations, and propagates calls with remove objects back to the parent bucket.
     */
    private final class SetView extends AbstractSet<E> implements BucketPartition<E> {

        private final Set<E> backing;
        private final int    index;

        private SetView(Set<E> backing, int index) {
            this.backing = backing;
            this.index = index;
        }

        @Override
        public int getPartitionIndex() {
            return this.index;
        }

        @Override
        public @NotNull Iterator<E> iterator() {
            return new SetViewIterator(this.backing.iterator());
        }

        @Override
        public boolean remove(Object o) {
            if (!this.backing.remove(o)) {
                return false;
            }

            // also remove from the bucket content set
            AbstractBucket.this.content.remove(o);
            return true;
        }

        @Override
        public void clear() {
            // remove the content of the backing from the bucket content set
            AbstractBucket.this.content.removeAll(this.backing);
            // then clear the backing
            this.backing.clear();
        }

        // just delegate

        @Override
        public int size() {
            return this.backing.size();
        }

        @Override
        public boolean isEmpty() {
            return this.backing.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.backing.contains(o);
        }

        @Override
        public Object @NotNull [] toArray() {
            return this.backing.toArray();
        }

        @Override
        public <T> T @NotNull [] toArray(T @NotNull [] a) {
            return this.backing.toArray(a);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return this.backing.containsAll(c);
        }

        @Override
        public int hashCode() {
            return this.backing.hashCode();
        }
    }

    /**
     * Wrapping around {@link SetView}'s iterators, to propagate calls to the #remove method to the parent bucket.
     */
    private final class SetViewIterator implements Iterator<E> {

        private final Iterator<E> delegate;
        private       E           current;

        private SetViewIterator(Iterator<E> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public E next() {
            // track the iterators cursor to handle #remove calls
            this.current = this.delegate.next();
            return this.current;
        }

        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }

            // remove from the backing partition
            this.delegate.remove();

            // also remove from the bucket content set
            AbstractBucket.this.content.remove(this.current);
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            this.delegate.forEachRemaining(action);
        }
    }
}