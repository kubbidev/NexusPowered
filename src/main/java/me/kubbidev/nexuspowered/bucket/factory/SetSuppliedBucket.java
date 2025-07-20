package me.kubbidev.nexuspowered.bucket.factory;

import java.util.Set;
import java.util.function.Supplier;
import me.kubbidev.nexuspowered.bucket.AbstractBucket;
import me.kubbidev.nexuspowered.bucket.partitioning.PartitioningStrategy;

class SetSuppliedBucket<E> extends AbstractBucket<E> {

    private final Supplier<Set<E>> setSupplier;

    SetSuppliedBucket(int size, PartitioningStrategy<E> strategy, Supplier<Set<E>> setSupplier) {
        super(size, strategy);
        this.setSupplier = setSupplier;
    }

    @Override
    protected Set<E> createSet() {
        return this.setSupplier.get();
    }
}