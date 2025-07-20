package me.kubbidev.nexuspowered.metadata.type;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.kubbidev.nexuspowered.metadata.MetadataKey;
import me.kubbidev.nexuspowered.metadata.MetadataMap;
import me.kubbidev.nexuspowered.metadata.MetadataRegistry;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link Entity}s.
 */
public interface EntityMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given entity.
     *
     * @param entity the entity
     * @return a metadata map
     */
    @NotNull MetadataMap provide(@NotNull Entity entity);

    /**
     * Gets a {@link MetadataMap} for the given entity, if one already exists and has been cached in this registry.
     *
     * @param entity the entity
     * @return a metadata map, if present
     */
    @NotNull Optional<MetadataMap> get(@NotNull Entity entity);

    /**
     * Gets a map of the entities with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of entities to key value
     */
    @NotNull <K> Map<Entity, K> getAllWithKey(@NotNull MetadataKey<K> key);
}