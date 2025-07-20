package me.kubbidev.nexuspowered.bucket.factory;

import java.util.Set;
import java.util.function.Supplier;
import me.kubbidev.nexuspowered.bucket.Bucket;
import me.kubbidev.nexuspowered.bucket.partitioning.PartitioningStrategy;

/**
 * A set of methods for creating {@link Bucket}s.
 */
public final class BucketFactory {

    private BucketFactory() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static <E> Bucket<E> newBucket(int size, PartitioningStrategy<E> strategy, Supplier<Set<E>> setSupplier) {
        return new SetSuppliedBucket<>(size, strategy, setSupplier);
    }

    public static <E> Bucket<E> newHashSetBucket(int size, PartitioningStrategy<E> strategy) {
        return new HashSetBucket<>(size, strategy);
    }

    public static <E> Bucket<E> newSynchronizedHashSetBucket(int size, PartitioningStrategy<E> strategy) {
        return new SynchronizedHashSetBucket<>(size, strategy);
    }

    public static <E> Bucket<E> newConcurrentBucket(int size, PartitioningStrategy<E> strategy) {
        return new ConcurrentBucket<>(size, strategy);
    }

}