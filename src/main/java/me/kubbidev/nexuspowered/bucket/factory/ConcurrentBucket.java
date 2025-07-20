package me.kubbidev.nexuspowered.bucket.factory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.kubbidev.nexuspowered.bucket.AbstractBucket;
import me.kubbidev.nexuspowered.bucket.partitioning.PartitioningStrategy;

class ConcurrentBucket<E> extends AbstractBucket<E> {

    ConcurrentBucket(int size, PartitioningStrategy<E> strategy) {
        super(size, strategy);
    }

    @Override
    protected Set<E> createSet() {
        return ConcurrentHashMap.newKeySet();
    }
}