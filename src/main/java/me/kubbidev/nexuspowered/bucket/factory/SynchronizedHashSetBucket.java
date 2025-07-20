package me.kubbidev.nexuspowered.bucket.factory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import me.kubbidev.nexuspowered.bucket.AbstractBucket;
import me.kubbidev.nexuspowered.bucket.partitioning.PartitioningStrategy;

class SynchronizedHashSetBucket<E> extends AbstractBucket<E> {

    SynchronizedHashSetBucket(int size, PartitioningStrategy<E> partitioningStrategy) {
        super(size, partitioningStrategy);
    }

    @Override
    protected Set<E> createSet() {
        return Collections.synchronizedSet(new HashSet<>());
    }
}