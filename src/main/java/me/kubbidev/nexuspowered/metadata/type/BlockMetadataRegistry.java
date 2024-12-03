package me.kubbidev.nexuspowered.metadata.type;

import me.kubbidev.nexuspowered.metadata.MetadataKey;
import me.kubbidev.nexuspowered.metadata.MetadataMap;
import me.kubbidev.nexuspowered.metadata.MetadataRegistry;
import me.kubbidev.nexuspowered.serialize.BlockPosition;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link Block}s.
 */
public interface BlockMetadataRegistry extends MetadataRegistry<BlockPosition> {

    /**
     * Produces a {@link MetadataMap} for the given block.
     *
     * @param block the block
     * @return a metadata map
     */
    @NotNull
    MetadataMap provide(@NotNull Block block);

    /**
     * Gets a {@link MetadataMap} for the given block, if one already exists and has
     * been cached in this registry.
     *
     * @param block the block
     * @return a metadata map, if present
     */
    @NotNull
    Optional<MetadataMap> get(@NotNull Block block);

    /**
     * Gets a map of the blocks with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of blocks to key value
     */
    @NotNull
    <K> Map<BlockPosition, K> getAllWithKey(@NotNull MetadataKey<K> key);

}