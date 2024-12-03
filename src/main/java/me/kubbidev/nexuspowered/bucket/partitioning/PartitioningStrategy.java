package me.kubbidev.nexuspowered.bucket.partitioning;

import me.kubbidev.nexuspowered.bucket.Bucket;

/**
 * A function which determines the position of an object within a {@link Bucket}.
 *
 * <p>Functions will not necessarily return consistent results for subsequent
 * calls using the same parameters, as their behaviour usually depends heavily on
 * current bucket state.</p>
 *
 * @param <T> the object type
 */
@FunctionalInterface
public interface PartitioningStrategy<T> {

    /**
     * Calculates the index of the partition to use for the object.
     *
     * <p>The index must be within range of the buckets size.</p>
     *
     * @param object the object
     * @param bucket the bucket
     * @return the index
     */
    int allocate(T object, Bucket<T> bucket);
}