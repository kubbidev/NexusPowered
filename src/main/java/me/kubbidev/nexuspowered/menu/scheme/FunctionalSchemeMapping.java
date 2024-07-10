package me.kubbidev.nexuspowered.menu.scheme;

import com.google.common.collect.Range;
import me.kubbidev.nexuspowered.menu.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Implements {@link SchemeMapping} using a function.
 */
public final class FunctionalSchemeMapping implements SchemeMapping {
    private final IntFunction<Item> function;
    private final Range<Integer> validRange;

    public static @NotNull SchemeMapping of(@NotNull IntFunction<Item> function, @NotNull Range<Integer> validRange) {
        return new FunctionalSchemeMapping(function, validRange);
    }

    private FunctionalSchemeMapping(@NotNull IntFunction<Item> function, @NotNull Range<Integer> validRange) {
        this.function = Objects.requireNonNull(function, "function");
        this.validRange = Objects.requireNonNull(validRange, "validRange");
    }

    @Override
    public @Nullable Item getNullable(int key) {
        if (!hasMappingFor(key)) {
            return null;
        }
        return this.function.apply(key);
    }

    @Override
    public boolean hasMappingFor(int key) {
        return this.validRange.contains(key);
    }

    @Override
    public @NotNull SchemeMapping copy() {
        return this; // no need to make a copy, the backing data is immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionalSchemeMapping other)) {
            return false;
        }
        return this.function.equals(other.function)
                && this.validRange.equals(other.validRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.function, this.validRange);
    }
}