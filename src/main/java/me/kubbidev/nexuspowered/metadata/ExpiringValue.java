package me.kubbidev.nexuspowered.metadata;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a value which will expire in the future
 *
 * @param <T> the wrapped value type
 */
public final class ExpiringValue<T> implements TransientValue<T> {

    private final T    value;
    private final long expireAt;

    private ExpiringValue(T value, long millis) {
        this.value = value;
        this.expireAt = System.currentTimeMillis() + millis;
    }

    public static <T> ExpiringValue<T> of(T value, long duration, TimeUnit unit) {
        Preconditions.checkArgument(duration >= 0, "duration must be >= 0");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(unit, "unit");

        long millis = unit.toMillis(duration);
        return new ExpiringValue<>(value, millis);
    }

    public static <T> Supplier<ExpiringValue<T>> supplied(Supplier<? extends T> supplier, long duration,
                                                          TimeUnit unit) {
        Preconditions.checkArgument(duration >= 0, "duration must be >= 0");
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(unit, "unit");

        long millis = unit.toMillis(duration);

        return () -> {
            T value = supplier.get();
            Objects.requireNonNull(value, "value");

            return new ExpiringValue<>(value, millis);
        };
    }

    @Override
    public @Nullable T getOrNull() {
        return shouldExpire() ? null : this.value;
    }

    @Override
    public boolean shouldExpire() {
        return System.currentTimeMillis() > this.expireAt;
    }

}