package me.kubbidev.nexuspowered.metadata;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import me.kubbidev.nexuspowered.metadata.type.BlockMetadataRegistry;
import me.kubbidev.nexuspowered.metadata.type.EntityMetadataRegistry;
import me.kubbidev.nexuspowered.metadata.type.PlayerMetadataRegistry;
import me.kubbidev.nexuspowered.metadata.type.WorldMetadataRegistry;
import me.kubbidev.nexuspowered.serialize.BlockPosition;
import me.kubbidev.nexuspowered.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The Metadata registries provided by helper.
 * <p>
 * These instances can be accessed through {@link Metadata}.
 */
final class StandardMetadataRegistries {

    public static final PlayerMetadataRegistry PLAYER = new PlayerRegistry();
    public static final EntityMetadataRegistry ENTITY = new EntityRegistry();
    public static final BlockMetadataRegistry  BLOCK  = new BlockRegistry();
    public static final WorldMetadataRegistry  WORLD  = new WorldRegistry();

    private static final MetadataRegistry<?>[] VALUES = new MetadataRegistry[]{
        PLAYER,
        ENTITY,
        BLOCK,
        WORLD
    };

    private StandardMetadataRegistries() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static MetadataRegistry<?>[] values() {
        return VALUES;
    }

    private static final class PlayerRegistry extends AbstractMetadataRegistry<UUID> implements PlayerMetadataRegistry {


        @Override
        public @NotNull MetadataMap provide(@NotNull Player player) {
            Objects.requireNonNull(player, "player");
            return provide(player.getUniqueId());
        }

        @Override
        public @NotNull Optional<MetadataMap> get(@NotNull Player player) {
            Objects.requireNonNull(player, "player");
            return get(player.getUniqueId());
        }

        @Override
        public <K> @NotNull Map<Player, K> getAllWithKey(@NotNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<Player, K> ret = ImmutableMap.builder();
            this.cache.forEach((uuid, map) -> map.get(key).ifPresent(t -> {
                Player player = Players.getNullable(uuid);
                if (player != null) {
                    ret.put(player, t);
                }
            }));
            return ret.build();
        }
    }

    private static final class EntityRegistry extends AbstractMetadataRegistry<UUID> implements EntityMetadataRegistry {

        @NotNull
        private static Optional<Entity> getEntity(UUID uuid) {
            Optional<Entity> entity = Optional.empty();
            for (World world : Bukkit.getWorlds()) {
                entity = world.getEntities().stream().filter(e -> e.getUniqueId().equals(uuid)).findFirst();
            }
            return entity;
        }

        @Override
        public @NotNull MetadataMap provide(@NotNull Entity entity) {
            Objects.requireNonNull(entity, "entity");
            return provide(entity.getUniqueId());
        }

        @Override
        public @NotNull Optional<MetadataMap> get(@NotNull Entity entity) {
            Objects.requireNonNull(entity, "entity");
            return get(entity.getUniqueId());
        }

        @Override
        public <K> @NotNull Map<Entity, K> getAllWithKey(@NotNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<Entity, K> ret = ImmutableMap.builder();
            this.cache.forEach(
                (uuid, map) -> map.get(key).ifPresent(t -> getEntity(uuid).ifPresent(e -> ret.put(e, t))));
            return ret.build();
        }
    }

    private static final class BlockRegistry extends AbstractMetadataRegistry<BlockPosition> implements
        BlockMetadataRegistry {

        @Override
        public @NotNull MetadataMap provide(@NotNull Block block) {
            Objects.requireNonNull(block, "block");
            return provide(BlockPosition.of(block));
        }

        @Override
        public @NotNull Optional<MetadataMap> get(@NotNull Block block) {
            Objects.requireNonNull(block, "block");
            return get(BlockPosition.of(block));
        }

        @Override
        public <K> @NotNull Map<BlockPosition, K> getAllWithKey(@NotNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<BlockPosition, K> ret = ImmutableMap.builder();
            this.cache.forEach((pos, map) -> map.get(key).ifPresent(t -> ret.put(pos, t)));
            return ret.build();
        }
    }

    private static final class WorldRegistry extends AbstractMetadataRegistry<UUID> implements WorldMetadataRegistry {

        @Override
        public @NotNull MetadataMap provide(@NotNull World world) {
            Objects.requireNonNull(world, "world");
            return provide(world.getUID());
        }

        @Override
        public @NotNull Optional<MetadataMap> get(@NotNull World world) {
            Objects.requireNonNull(world, "world");
            return get(world.getUID());
        }

        @Override
        public <K> @NotNull Map<World, K> getAllWithKey(@NotNull MetadataKey<K> key) {
            Objects.requireNonNull(key, "key");
            ImmutableMap.Builder<World, K> ret = ImmutableMap.builder();
            this.cache.forEach((uuid, map) -> map.get(key).ifPresent(t -> {
                World world = Bukkit.getWorld(uuid);
                if (world != null) {
                    ret.put(world, t);
                }
            }));
            return ret.build();
        }
    }
}