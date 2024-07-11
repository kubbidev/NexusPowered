package me.kubbidev.nexuspowered.menu.pattern;

import me.kubbidev.nexuspowered.item.ItemStackBuilder;
import me.kubbidev.nexuspowered.menu.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a mapping to be used in a {@link MenuPattern}
 */
@FunctionalInterface
public interface PatternMapping {
    PatternMapping EMPTY = () -> null;

    @NotNull
    static PatternMapping of(@NotNull Material type) {
        return () -> ItemStackBuilder.of(type).name(Component.empty()).build(null);
    }

    /**
     * Gets an item from the mapping which represents the given key.
     *
     * @param key the mapping key
     * @return an item if present, otherwise null
     */
    @Nullable
    Item get();
}