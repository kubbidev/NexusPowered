package me.kubbidev.nexuspowered.bucket.factory;

import java.util.HashSet;
import java.util.Set;
import me.kubbidev.nexuspowered.bucket.AbstractBucket;
import me.kubbidev.nexuspowered.bucket.partitioning.PartitioningStrategy;

class HashSetBucket<E> extends AbstractBucket<E> {

    HashSetBucket(int size, PartitioningStrategy<E> partitioningStrategy) {
        super(size, partitioningStrategy);
    }

    @Override
    protected Set<E> createSet() {
        return new HashSet<>();
    }
}