package me.kubbidev.nexuspowered.cache;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A lazy supplier extension.
 *
 * <p>The delegate supplier is only called on the first execution of {@link #get()}.
 * The result is then cached and returned for all subsequent calls.</p>
 *
 * @param <T> the supplied type
 */
public final class Lazy<T> implements Supplier<T> {

    private volatile Supplier<T> supplier;
    private volatile boolean     initialized = false;
    private          T           value;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    private Lazy(T value) {
        this.value = value;
        this.initialized = true;
    }

    public static <T> Lazy<T> of(T value) {
        return new Lazy<>(Objects.requireNonNull(value, "value"));
    }

    public static <T> Lazy<T> suppliedBy(Supplier<T> supplier) {
        return new Lazy<>(Objects.requireNonNull(supplier, "supplier"));
    }

    @Override
    public T get() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    // compute the value using the delegate
                    T value = this.supplier.get();

                    this.value = value;
                    this.initialized = true;

                    // release the delegate supplier to the gc
                    this.supplier = null;
                    return value;
                }
            }
        }
        return this.value;
    }
}