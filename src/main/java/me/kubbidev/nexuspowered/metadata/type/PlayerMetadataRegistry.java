package me.kubbidev.nexuspowered.metadata.type;

import me.kubbidev.nexuspowered.metadata.MetadataKey;
import me.kubbidev.nexuspowered.metadata.MetadataMap;
import me.kubbidev.nexuspowered.metadata.MetadataRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link Player}s.
 */
public interface PlayerMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given player.
     *
     * @param player the player
     * @return a metadata map
     */
    @NotNull
    MetadataMap provide(@NotNull Player player);

    /**
     * Gets a {@link MetadataMap} for the given player, if one already exists and has
     * been cached in this registry.
     *
     * @param player the player
     * @return a metadata map, if present
     */
    @NotNull
    Optional<MetadataMap> get(@NotNull Player player);

    /**
     * Gets a map of the players with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of players to key value
     */
    @NotNull
    <K> Map<Player, K> getAllWithKey(@NotNull MetadataKey<K> key);

}