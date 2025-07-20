package me.kubbidev.nexuspowered.util;

import com.google.common.cache.CacheBuilder;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A simple expiring set implementation using Google caches
 *
 * @param <E> element type
 */
public class ExpiringSet<E> {

    private ExpiringSet() {
    }

    /**
     * An expiring set using Caffeine caches
     *
     * @param <E> the element type
     * @return a new expiring set
     */
    public static <E> Set<E> newExpiringSet(long duration, TimeUnit unit) {
        return Collections.newSetFromMap(
            CacheBuilder.newBuilder().expireAfterWrite(duration, unit).<E, Boolean>build().asMap());
    }
}