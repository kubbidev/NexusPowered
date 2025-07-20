package me.kubbidev.nexuspowered.bucket.partitioning;

import java.util.concurrent.ThreadLocalRandom;
import me.kubbidev.nexuspowered.bucket.Bucket;
import me.kubbidev.nexuspowered.bucket.BucketPartition;

/**
 * Some standard partitioning strategies for use in {@link Bucket}s.
 */
public final class PartitioningStrategies {

    private PartitioningStrategies() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static <T> PartitioningStrategy<T> random() {
        return Strategies.RANDOM.cast();
    }

    public static <T> PartitioningStrategy<T> lowestSize() {
        return Strategies.LOWEST_SIZE.cast();
    }

    public static <T> PartitioningStrategy<T> nextInCycle() {
        return Strategies.NEXT_IN_CYCLE.cast();
    }

    public static <T> PartitioningStrategy<T> previousInCycle() {
        return Strategies.PREVIOUS_IN_CYCLE.cast();
    }

    private enum Strategies implements GenericPartitioningStrategy {
        RANDOM {
            @Override
            public int allocate(Bucket<?> bucket) {
                return ThreadLocalRandom.current().nextInt(bucket.getPartitionCount());
            }
        },
        LOWEST_SIZE {
            @Override
            public int allocate(Bucket<?> bucket) {
                int index = -1;
                int lowestSize = Integer.MAX_VALUE;

                for (BucketPartition<?> partition : bucket.getPartitions()) {
                    int size = partition.size();
                    int i = partition.getPartitionIndex();

                    if (size == 0) {
                        return i;
                    }

                    if (size < lowestSize) {
                        lowestSize = size;
                        index = i;
                    }
                }

                if (index == -1) {
                    throw new AssertionError();
                }
                return index;
            }
        },
        NEXT_IN_CYCLE {
            @Override
            public int allocate(Bucket<?> bucket) {
                return bucket.asCycle().next().getPartitionIndex();
            }
        },
        PREVIOUS_IN_CYCLE {
            @Override
            public int allocate(Bucket<?> bucket) {
                return bucket.asCycle().previous().getPartitionIndex();
            }
        }
    }

}