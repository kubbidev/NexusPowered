package me.kubbidev.nexuspowered.cache;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * An expiring supplier extension.
 *
 * <p>The delegate supplier is only called on executions of {@link #get()} if the
 * result isn't already calculated.</p>
 *
 * @param <T> the supplied type
 */
public final class Expiring<T> implements Supplier<T> {

    private final    Supplier<T> supplier;
    private final    long        durationNanos;
    private volatile T           value;
    // when to expire. 0 means "not yet initialized".
    private volatile long        expirationNanos;

    private Expiring(Supplier<T> supplier, long duration, TimeUnit unit) {
        this.supplier = supplier;
        this.durationNanos = unit.toNanos(duration);
    }

    public static <T> Expiring<T> suppliedBy(Supplier<T> supplier, long duration, TimeUnit unit) {
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration > 0);

        return new Expiring<>(supplier, duration, unit);
    }

    @Override
    public T get() {
        long nanos = this.expirationNanos;
        long now = System.nanoTime();

        if (nanos == 0 || now - nanos >= 0) {
            synchronized (this) {
                if (nanos == this.expirationNanos) { // recheck for lost race
                    // compute the value using the delegate
                    T value = this.supplier.get();
                    this.value = value;

                    // reset expiration timer
                    nanos = now + this.durationNanos;
                    // In the very unlikely event that nanos is 0, set it to 1;
                    // no one will notice 1 ns of tardiness.
                    this.expirationNanos = (nanos == 0) ? 1 : nanos;
                    return value;
                }
            }
        }
        return this.value;
    }
}