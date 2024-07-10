package me.kubbidev.nexuspowered.menu.scheme;

import me.kubbidev.nexuspowered.menu.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An empty menu scheme.
 */
final class EmptySchemeMapping implements SchemeMapping {

    @Override
    public @NotNull Optional<Item> get(int key) {
        return Optional.empty();
    }

    @Override
    public @Nullable Item getNullable(int key) {
        return null;
    }

    @Override
    public boolean hasMappingFor(int key) {
        return false;
    }

    @Override
    public @NotNull SchemeMapping copy() {
        return this; // no need to make a copy, this class is a singleton
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public boolean equals(Object o) {
        return o == this;
    }
}