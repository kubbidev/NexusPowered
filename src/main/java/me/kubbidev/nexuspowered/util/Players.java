package me.kubbidev.nexuspowered.util;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.kubbidev.nexuspowered.Nexus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of Player related utilities
 */
@NotNullByDefault
public final class Players {

    public static final float DEFAULT_PLAYER_WALKING_SPEED = 0.2f;
    public static final float DEFAULT_PLAYER_FLYING_SPEED  = 0.1f;

    private Players() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gets a player by uuid.
     *
     * @param uuid the uuid
     * @return a player, or null
     */
    public static @Nullable Player getNullable(UUID uuid) {
        return Nexus.server().getPlayer(uuid);
    }

    /**
     * Gets a player by uuid.
     *
     * @param uuid the uuid
     * @return an optional player
     */
    public static Optional<Player> get(UUID uuid) {
        return Optional.ofNullable(getNullable(uuid));
    }

    /**
     * Gets a player by username.
     *
     * @param username the players username
     * @return the player, or null
     */
    public static @Nullable Player getNullable(String username) {
        return Nexus.server().getPlayerExact(username);
    }

    /**
     * Gets a player by username.
     *
     * @param username the players username
     * @return an optional player
     */
    public static Optional<Player> get(String username) {
        return Optional.ofNullable(getNullable(username));
    }

    /**
     * Gets all players on the server.
     *
     * @return all players on the server
     */
    @SuppressWarnings("unchecked")
    public static Collection<Player> all() {
        return (Collection<Player>) Bukkit.getOnlinePlayers();
    }

    /**
     * Gets a stream of all players on the server.
     *
     * @return a stream of all players on the server
     */
    public static Stream<Player> stream() {
        return all().stream();
    }

    /**
     * Applies a given action to all players on the server
     *
     * @param consumer the action to apply
     */
    public static void forEach(Consumer<Player> consumer) {
        all().forEach(consumer);
    }

    /**
     * Gets a stream of all players within a given radius of a point
     *
     * @param center the point
     * @param radius the radius
     * @return a stream of players
     */
    public static Stream<Player> streamInRange(Location center, double radius) {
        return center.getWorld().getNearbyEntities(center, radius, radius, radius).stream()
            .filter(e -> e instanceof Player)
            .map(e -> ((Player) e));
    }

    /**
     * Applies an action to all players within a given radius of a point
     *
     * @param center   the point
     * @param radius   the radius
     * @param consumer the action to apply
     */
    public static void forEachInRange(Location center, double radius, Consumer<Player> consumer) {
        streamInRange(center, radius).forEach(consumer);
    }

    public static OfflinePlayer getOfflineNullable(UUID uuid) {
        return Nexus.server().getOfflinePlayer(uuid);
    }

    public static Optional<OfflinePlayer> getOffline(UUID uuid) {
        return Optional.of(getOfflineNullable(uuid));
    }

    public static OfflinePlayer getOfflineNullable(String username) {
        return Nexus.server().getOfflinePlayer(username);
    }

    public static Optional<OfflinePlayer> getOffline(String username) {
        return Optional.of(getOfflineNullable(username));
    }

    public static Collection<OfflinePlayer> allOffline() {
        return ImmutableList.copyOf(Bukkit.getOfflinePlayers());
    }

    public static Stream<OfflinePlayer> streamOffline() {
        return Arrays.stream(Bukkit.getOfflinePlayers());
    }

    public static void forEachOffline(Consumer<OfflinePlayer> consumer) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            consumer.accept(player);
        }
    }

    public static void resetWalkSpeed(Player player) {
        player.setWalkSpeed(DEFAULT_PLAYER_WALKING_SPEED);
    }

    public static void resetFlySpeed(Player player) {
        player.setFlySpeed(DEFAULT_PLAYER_FLYING_SPEED);
    }

    public static boolean hasPermission(Permissible entity, String... permissions) {
        return Arrays.stream(permissions).anyMatch(entity::hasPermission);
    }

    public static boolean hasPermission(Permissible entity, Collection<String> permissions) {
        return permissions.stream().anyMatch(entity::hasPermission);
    }
}