package com.kubbidev.nexuspowered.paper.inventory.builder.item;

import com.kubbidev.nexuspowered.paper.inventory.components.execption.InventoryException;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

/**
 * Item builder for {@link Material#MAP} only
 */
public class MapBuilder extends BaseItemBuilder<MapBuilder> {

    private static final Material MAP = Material.MAP;

    MapBuilder() {
        super(new ItemStack(MAP));
    }

    MapBuilder(ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != MAP) {
            throw new InventoryException("MapBuilder requires the material to be a MAP!");
        }
    }

    /**
     * Sets the map color. A custom map color will alter the display of the map
     * in an inventory slot.
     *
     * @param color the color to set
     * @return {@link MapBuilder}
     */
    @Override
    public MapBuilder color(Color color) {
        MapMeta mapMeta = (MapMeta) getMeta();

        mapMeta.setColor(color);
        setMeta(mapMeta);
        return this;
    }

    /**
     * Sets the location name. A custom map color will alter the display of the
     * map in an inventory slot.
     *
     * @param name the name to set
     * @return {@link MapMeta}
     */
    public MapBuilder locationName(Component name) {
        MapMeta mapMeta = (MapMeta) getMeta();

        mapMeta.displayName(name);
        setMeta(mapMeta);
        return this;
    }

    /**
     * Sets if this map is scaling or not.
     *
     * @param scaling true to scale
     * @return {@link MapMeta}
     */
    public MapBuilder scaling(boolean scaling) {
        MapMeta mapMeta = (MapMeta) getMeta();

        mapMeta.setScaling(scaling);
        setMeta(mapMeta);
        return this;
    }

    /**
     * Sets the associated map. This is used to determine what map is displayed.
     *
     * <p>
     * The implementation <b>may</b> allow null to clear the associated map, but
     * this is not required and is liable to generate a new (undefined) map when
     * the item is first used.
     *
     * @param view the map to set
     * @return {@link MapBuilder}
     */
    public MapBuilder view(MapView view) {
        MapMeta mapMeta = (MapMeta) getMeta();

        mapMeta.setMapView(view);
        setMeta(mapMeta);
        return this;
    }
}
