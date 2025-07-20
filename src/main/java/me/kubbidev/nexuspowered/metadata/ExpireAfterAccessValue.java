package me.kubbidev.nexuspowered.metadata;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a value which will expire a set amount of time after the last access
 *
 * @param <T> the wrapped value type
 */
public class ExpireAfterAccessValue<T> implements TransientValue<T> {

    private final T    value;
    private final long millis;
    private       long expireAt;

    private ExpireAfterAccessValue(T value, long millis) {
        this.value = value;
        this.millis = millis;
        this.expireAt = System.currentTimeMillis() + this.millis;
    }

    public static <T> ExpireAfterAccessValue<T> of(T value, long duration, TimeUnit unit) {
        Preconditions.checkArgument(duration >= 0, "duration must be >= 0");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(unit, "unit");

        long millis = unit.toMillis(duration);
        return new ExpireAfterAccessValue<>(value, millis);
    }

    public static <T> Supplier<ExpireAfterAccessValue<T>> supplied(Supplier<? extends T> supplier, long duration,
                                                                   TimeUnit unit) {
        Preconditions.checkArgument(duration >= 0, "duration must be >= 0");
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(unit, "unit");

        long millis = unit.toMillis(duration);

        return () -> {
            T value = supplier.get();
            Objects.requireNonNull(value, "value");

            return new ExpireAfterAccessValue<>(value, millis);
        };
    }

    @Nullable
    @Override
    public T getOrNull() {
        if (shouldExpire()) {
            return null;
        }

        // reset expiry time
        this.expireAt = System.currentTimeMillis() + this.millis;
        return this.value;
    }

    @Override
    public boolean shouldExpire() {
        return System.currentTimeMillis() > this.expireAt;
    }

}