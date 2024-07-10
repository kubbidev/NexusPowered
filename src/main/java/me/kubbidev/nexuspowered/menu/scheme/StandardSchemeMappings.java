package me.kubbidev.nexuspowered.menu.scheme;

import com.google.common.collect.Range;
import me.kubbidev.nexuspowered.item.ItemStackBuilder;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.Arrays;

/**
 * Contains a number of default {@link SchemeMapping}s.
 */
@NotNullByDefault
public final class StandardSchemeMappings {
    private static final Range<Integer> COLORED_MATERIAL_RANGE = Range.closed(0, 15);

    private static final String[] BLOCK_COLORS = {
            "WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY",
            "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK",
    };

    public static final SchemeMapping STAINED_GLASS
            = forColoredMaterial("_STAINED_GLASS_PANE");

    public static final SchemeMapping STAINED_GLASS_BLOCK
            = forColoredMaterial("_STAINED_GLASS");

    public static final SchemeMapping HARDENED_CLAY
            = forColoredMaterial("_TERRACOTTA");

    public static final SchemeMapping WOOL
            = forColoredMaterial("_WOOL");

    public static final SchemeMapping EMPTY = new EmptySchemeMapping();

    private static SchemeMapping forColoredMaterial(String modernSuffix) {
        Material[] materials = Arrays.stream(BLOCK_COLORS)
                .map(color -> Material.valueOf(color + modernSuffix))
                .toArray(Material[]::new);

        return FunctionalSchemeMapping.of(
                data -> ItemStackBuilder.of(materials[data]).name(Component.text("", NamedTextColor.WHITE)).build(null),
                COLORED_MATERIAL_RANGE
        );
    }

    private StandardSchemeMappings() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}