package me.kubbidev.nexuspowered.metadata;

import me.kubbidev.nexuspowered.Events;
import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.metadata.type.BlockMetadataRegistry;
import me.kubbidev.nexuspowered.metadata.type.EntityMetadataRegistry;
import me.kubbidev.nexuspowered.metadata.type.PlayerMetadataRegistry;
import me.kubbidev.nexuspowered.metadata.type.WorldMetadataRegistry;
import me.kubbidev.nexuspowered.serialize.BlockPosition;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides access to {@link MetadataRegistry} instances bound to players, entities, blocks and worlds.
 */
public final class Metadata implements Runnable {
    private static final AtomicBoolean SETUP = new AtomicBoolean(false);

    @Override
    public void run() {
        for (MetadataRegistry<?> registry : StandardMetadataRegistries.values()) {
            registry.cleanup();
        }
    }

    public static void ensureSetup() {
        if (SETUP.get()) {
            return;
        }

        if (!SETUP.getAndSet(true)) {
            // remove player metadata when they leave the server
            Events.subscribe(PlayerQuitEvent.class, EventPriority.MONITOR)
                    .handler(e -> StandardMetadataRegistries.PLAYER.remove(e.getPlayer().getUniqueId()));

            // cache housekeeping task
            Schedulers.builder()
                    .async()
                    .afterAndEvery(1, TimeUnit.MINUTES)
                    .run(() -> {
                        for (MetadataRegistry<?> registry : StandardMetadataRegistries.values()) {
                            registry.cleanup();
                        }
                    });
        }
    }

    /**
     * Gets the {@link MetadataRegistry} for {@link Player}s.
     *
     * @return the {@link PlayerMetadataRegistry}
     */
    public static PlayerMetadataRegistry players() {
        ensureSetup();
        return StandardMetadataRegistries.PLAYER;
    }

    /**
     * Gets the {@link MetadataRegistry} for {@link Entity}s.
     *
     * @return the {@link EntityMetadataRegistry}
     */
    public static EntityMetadataRegistry entities() {
        ensureSetup();
        return StandardMetadataRegistries.ENTITY;
    }

    /**
     * Gets the {@link MetadataRegistry} for {@link Block}s.
     *
     * @return the {@link BlockMetadataRegistry}
     */
    public static BlockMetadataRegistry blocks() {
        ensureSetup();
        return StandardMetadataRegistries.BLOCK;
    }

    /**
     * Gets the {@link MetadataRegistry} for {@link World}s.
     *
     * @return the {@link WorldMetadataRegistry}
     */
    public static WorldMetadataRegistry worlds() {
        ensureSetup();
        return StandardMetadataRegistries.WORLD;
    }

    /**
     * Produces a {@link MetadataMap} for the given object.
     * <p>
     * A map will only be returned if the object is an instance of
     * {@link Player}, {@link UUID}, {@link Entity}, {@link Block} or {@link World}.
     *
     * @param o the object
     * @return a metadata map
     */
    @NotNull
    public static MetadataMap provide(@NotNull Object o) {
        Objects.requireNonNull(o, "obj");
        if (o instanceof Player) {
            return provideForPlayer(((Player) o));
        } else if (o instanceof UUID) {
            return provideForPlayer(((UUID) o));
        } else if (o instanceof Entity) {
            return provideForEntity(((Entity) o));
        } else if (o instanceof Block) {
            return provideForBlock(((Block) o));
        } else if (o instanceof World) {
            return provideForWorld(((World) o));
        } else {
            throw new IllegalArgumentException("Unknown object type: " + o.getClass());
        }
    }

    /**
     * Gets a {@link MetadataMap} for the given object, if one already exists and has
     * been cached in this registry.
     * <p>
     * A map will only be returned if the object is an instance of
     * {@link Player}, {@link UUID}, {@link Entity}, {@link Block} or {@link World}.
     *
     * @param o the object
     * @return a metadata map
     */
    @NotNull
    public static Optional<MetadataMap> get(@NotNull Object o) {
        Objects.requireNonNull(o, "obj");
        if (o instanceof Player) {
            return getForPlayer(((Player) o));
        } else if (o instanceof UUID) {
            return getForPlayer(((UUID) o));
        } else if (o instanceof Entity) {
            return getForEntity(((Entity) o));
        } else if (o instanceof Block) {
            return getForBlock(((Block) o));
        } else if (o instanceof World) {
            return getForWorld(((World) o));
        } else {
            throw new IllegalArgumentException("Unknown object type: " + o.getClass());
        }
    }

    @NotNull
    public static MetadataMap provideForPlayer(@NotNull UUID uuid) {
        return players().provide(uuid);
    }

    @NotNull
    public static MetadataMap provideForPlayer(@NotNull Player player) {
        return players().provide(player);
    }

    @NotNull
    public static Optional<MetadataMap> getForPlayer(@NotNull UUID uuid) {
        return players().get(uuid);
    }

    @NotNull
    public static Optional<MetadataMap> getForPlayer(@NotNull Player player) {
        return players().get(player);
    }

    @NotNull
    public static <T> Map<Player, T> lookupPlayersWithKey(@NotNull MetadataKey<T> key) {
        return players().getAllWithKey(key);
    }

    @NotNull
    public static MetadataMap provideForEntity(@NotNull UUID uuid) {
        return entities().provide(uuid);
    }

    @NotNull
    public static MetadataMap provideForEntity(@NotNull Entity entity) {
        return entities().provide(entity);
    }

    @NotNull
    public static Optional<MetadataMap> getForEntity(@NotNull UUID uuid) {
        return entities().get(uuid);
    }

    @NotNull
    public static Optional<MetadataMap> getForEntity(@NotNull Entity entity) {
        return entities().get(entity);
    }

    @NotNull
    public static <T> Map<Entity, T> lookupEntitiesWithKey(@NotNull MetadataKey<T> key) {
        return entities().getAllWithKey(key);
    }

    @NotNull
    public static MetadataMap provideForBlock(@NotNull BlockPosition block) {
        return blocks().provide(block);
    }

    @NotNull
    public static MetadataMap provideForBlock(@NotNull Block block) {
        return blocks().provide(block);
    }

    @NotNull
    public static Optional<MetadataMap> getForBlock(@NotNull BlockPosition block) {
        return blocks().get(block);
    }

    @NotNull
    public static Optional<MetadataMap> getForBlock(@NotNull Block block) {
        return blocks().get(block);
    }

    @NotNull
    public static <T> Map<BlockPosition, T> lookupBlocksWithKey(@NotNull MetadataKey<T> key) {
        return blocks().getAllWithKey(key);
    }

    @NotNull
    public static MetadataMap provideForWorld(@NotNull UUID uid) {
        return worlds().provide(uid);
    }

    @NotNull
    public static MetadataMap provideForWorld(@NotNull World world) {
        return worlds().provide(world);
    }

    @NotNull
    public static Optional<MetadataMap> getForWorld(@NotNull UUID uid) {
        return worlds().get(uid);
    }

    @NotNull
    public static Optional<MetadataMap> getForWorld(@NotNull World world) {
        return worlds().get(world);
    }

    @NotNull
    public static <T> Map<World, T> lookupWorldsWithKey(@NotNull MetadataKey<T> key) {
        return worlds().getAllWithKey(key);
    }

    private Metadata() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}