package me.kubbidev.nexuspowered.metadata.type;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.kubbidev.nexuspowered.metadata.MetadataKey;
import me.kubbidev.nexuspowered.metadata.MetadataMap;
import me.kubbidev.nexuspowered.metadata.MetadataRegistry;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link World}s.
 */
public interface WorldMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given world.
     *
     * @param world the world
     * @return a metadata map
     */
    @NotNull MetadataMap provide(@NotNull World world);

    /**
     * Gets a {@link MetadataMap} for the given world, if one already exists and has been cached in this registry.
     *
     * @param world the world
     * @return a metadata map, if present
     */
    @NotNull Optional<MetadataMap> get(@NotNull World world);

    /**
     * Gets a map of the worlds with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of worlds to key value
     */
    @NotNull <K> Map<World, K> getAllWithKey(@NotNull MetadataKey<K> key);
}