package me.kubbidev.nexuspowered.metadata;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a value wrapped in a {@link WeakReference}
 *
 * @param <T> the wrapped value type
 */
public final class WeakValue<T> implements TransientValue<T> {

    private final WeakReference<T> value;

    private WeakValue(T value) {
        this.value = new WeakReference<>(value);
    }

    public static <T> WeakValue<T> of(T value) {
        Objects.requireNonNull(value, "value");
        return new WeakValue<>(value);
    }

    public static <T> Supplier<WeakValue<T>> supplied(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");

        return () -> {
            T value = supplier.get();
            Objects.requireNonNull(value, "value");

            return new WeakValue<>(value);
        };
    }

    @Override
    public @Nullable T getOrNull() {
        return this.value.get();
    }

    @Override
    public boolean shouldExpire() {
        return this.value.get() == null;
    }

}