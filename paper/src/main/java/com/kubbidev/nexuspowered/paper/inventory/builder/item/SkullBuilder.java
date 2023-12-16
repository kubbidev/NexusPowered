package com.kubbidev.nexuspowered.paper.inventory.builder.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.kubbidev.nexuspowered.paper.inventory.components.execption.InventoryException;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * New builder for skull only, created to separate the specific features for skulls
 * Soon I'll add more useful features to this builder
 */
public final class SkullBuilder extends BaseItemBuilder<SkullBuilder> {

    SkullBuilder() {
        super(new ItemStack(Material.PLAYER_HEAD));
    }

    SkullBuilder(ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != Material.PLAYER_HEAD) {
            throw new InventoryException("SkullBuilder requires the material to be a PLAYER_HEAD/SKULL_ITEM!");
        }
    }

    /**
     * Sets skull owner via bukkit methods
     *
     * @param player {@link OfflinePlayer} to set skull of
     * @return {@link SkullBuilder}
     */
    public SkullBuilder owner(OfflinePlayer player) {
        if (getItemStack().getType() != Material.PLAYER_HEAD)
            return this;

        SkullMeta skullMeta = (SkullMeta) getMeta();

        skullMeta.setOwningPlayer(player);
        setMeta(skullMeta);
        return this;
    }

    /**
     * Sets skull owner via paper methods
     *
     * @param profile {@link PlayerProfile} to set skull of
     * @return {@link SkullBuilder}
     */
    public SkullBuilder owner(PlayerProfile profile) {
        if (getItemStack().getType() != Material.PLAYER_HEAD)
            return this;

        SkullMeta skullMeta = (SkullMeta) getMeta();

        skullMeta.setPlayerProfile(profile);
        setMeta(skullMeta);
        return this;
    }
}
