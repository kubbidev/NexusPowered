package me.kubbidev.nexuspowered.bucket;

import java.util.Set;

/**
 * Represents a partition of elements within a {@link Bucket}.
 *
 * @param <E> the element type
 */
public interface BucketPartition<E> extends Set<E> {

    /**
     * Gets the index of this partition within the bucket
     *
     * @return the index
     */
    int getPartitionIndex();
}