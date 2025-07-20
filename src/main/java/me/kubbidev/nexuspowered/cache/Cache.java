package me.kubbidev.nexuspowered.cache;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A cached supplier extension.
 *
 * <p>The delegate supplier is only called on executions of {@link #get()} if the
 * result is not cached. Subsequent calls will block until the value is calculated.</p>
 *
 * @param <T> the supplied type
 */
public final class Cache<T> implements Supplier<T> {

    private final    Supplier<T> supplier;
    private volatile T           value = null;

    private Cache(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Cache<T> suppliedBy(Supplier<T> supplier) {
        return new Cache<>(Objects.requireNonNull(supplier, "supplier"));
    }

    @Override
    public T get() {
        T value = this.value;

        // double checked locking
        if (value == null) {
            synchronized (this) {
                value = this.value;
                if (value == null) {
                    value = this.supplier.get();
                    this.value = value;
                }
            }
        }

        return value;
    }

    public void invalidate() {
        this.value = null;
    }

    public Optional<T> getIfPresent() {
        return Optional.ofNullable(this.value);
    }
}