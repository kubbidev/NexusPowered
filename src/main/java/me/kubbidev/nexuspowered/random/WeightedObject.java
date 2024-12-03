package me.kubbidev.nexuspowered.random;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a {@link Weighted} object.
 *
 * @param <T> the object type
 */
public final class WeightedObject<T> implements Weighted {

    @NotNull
    public static <T> WeightedObject<T> of(@NotNull T object, double weight) {
        return new WeightedObject<>(object, weight);
    }

    private final T object;
    private final double weight;

    private WeightedObject(T object, double weight) {
        Preconditions.checkArgument(weight >= 0, "weight cannot be negative");
        this.object = Objects.requireNonNull(object, "object");
        this.weight = weight;
    }

    @NotNull
    public T get() {
        return this.object;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WeightedObject<?>)) {
            return false;
        }
        WeightedObject<?> other = (WeightedObject<?>) o;
        return this.object.equals(other.object) && Double.compare(this.getWeight(), other.getWeight()) == 0;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.object.hashCode();
        result = result * PRIME + Long.hashCode(Double.doubleToLongBits(this.getWeight()));
        return result;
    }

    @Override
    public String toString() {
        return "WeightedObject(object=" + this.object + ", weight=" + this.getWeight() + ")";
    }
}