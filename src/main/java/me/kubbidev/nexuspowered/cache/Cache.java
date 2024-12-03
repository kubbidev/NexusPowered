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

    public static <T> Cache<T> suppliedBy(Supplier<T> supplier) {
        return new Cache<>(Objects.requireNonNull(supplier, "supplier"));
    }

    private final Supplier<T> supplier;
    private volatile T value = null;

    private Cache(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        T val = this.value;

        // double checked locking
        if (val == null) {
            synchronized (this) {
                val = this.value;
                if (val == null) {
                    val = this.supplier.get();
                    this.value = val;
                }
            }
        }

        return val;
    }

    public Optional<T> getIfPresent() {
        return Optional.ofNullable(this.value);
    }

    public void invalidate() {
        this.value = null;
    }
}