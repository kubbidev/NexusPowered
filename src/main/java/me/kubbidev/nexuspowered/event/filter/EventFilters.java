package me.kubbidev.nexuspowered.event.filter;

import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import java.util.function.Predicate;
import me.kubbidev.nexuspowered.metadata.Metadata;
import me.kubbidev.nexuspowered.metadata.MetadataKey;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Defines standard event predicates for use in functional event handlers.
 */
@SuppressWarnings("unchecked")
public final class EventFilters {

    private static final Predicate<? extends Cancellable>                        IGNORE_CANCELLED            = e -> !e.isCancelled();
    private static final Predicate<? extends Cancellable>                        IGNORE_UNCANCELLED          = Cancellable::isCancelled;
    private static final Predicate<? extends PlayerConnectionValidateLoginEvent> IGNORE_DISALLOWED_LOGIN     = PlayerConnectionValidateLoginEvent::isAllowed;
    private static final Predicate<? extends AsyncPlayerPreLoginEvent>           IGNORE_DISALLOWED_PRE_LOGIN = e ->
        e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED;

    private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_BLOCK = e ->
        !e.getFrom().getWorld().equals(e.getTo().getWorld())
            || e.getFrom().getBlockX() != e.getTo().getBlockX()
            || e.getFrom().getBlockZ() != e.getTo().getBlockZ()
            || e.getFrom().getBlockY() != e.getTo().getBlockY();

    private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_BLOCK_AND_Y = e ->
        !e.getFrom().getWorld().equals(e.getTo().getWorld())
            || e.getFrom().getBlockX() != e.getTo().getBlockX()
            || e.getFrom().getBlockZ() != e.getTo().getBlockZ();

    private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_CHUNK = e ->
        !e.getFrom().getWorld().equals(e.getTo().getWorld())
            || (e.getFrom().getBlockX() >> 4) != (e.getTo().getBlockX() >> 4)
            || (e.getFrom().getBlockZ() >> 4) != (e.getTo().getBlockZ() >> 4);

    private EventFilters() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Returns a predicate which only returns true if the event isn't cancelled.
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the event isn't cancelled
     */
    public static @NotNull <T extends Cancellable> Predicate<T> ignoreCancelled() {
        return (Predicate<T>) IGNORE_CANCELLED;
    }

    /**
     * Returns a predicate which only returns true if the event is cancelled.
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the event is cancelled
     */
    public static @NotNull <T extends Cancellable> Predicate<T> ignoreNotCancelled() {
        return (Predicate<T>) IGNORE_UNCANCELLED;
    }

    /**
     * Returns a predicate which only returns true if the login is allowed.
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the login is allowed
     */
    public static @NotNull <T extends PlayerConnectionValidateLoginEvent> Predicate<T> ignoreDisallowedLogin() {
        return (Predicate<T>) IGNORE_DISALLOWED_LOGIN;
    }

    /**
     * Returns a predicate which only returns true if the login is allowed.
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the login is allowed
     */
    public static @NotNull <T extends AsyncPlayerPreLoginEvent> Predicate<T> ignoreDisallowedPreLogin() {
        return (Predicate<T>) IGNORE_DISALLOWED_PRE_LOGIN;
    }

    /**
     * Returns a predicate which only returns true if the player has moved over a block.
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the player has moved over a block
     */
    public static @NotNull <T extends PlayerMoveEvent> Predicate<T> ignoreSameBlock() {
        return (Predicate<T>) IGNORE_SAME_BLOCK;
    }

    /**
     * Returns a predicate which only returns true if the player has moved over a block, not including movement directly
     * up and down. (so jumping wouldn't return true)
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the player has moved across a block border
     */
    public static @NotNull <T extends PlayerMoveEvent> Predicate<T> ignoreSameBlockAndY() {
        return (Predicate<T>) IGNORE_SAME_BLOCK_AND_Y;
    }

    /**
     * Returns a predicate which only returns true if the player has moved over a chunk border.
     *
     * @param <T> the event type
     * @return a predicate which only returns true if the player has moved over a chunk border
     */
    public static @NotNull <T extends PlayerMoveEvent> Predicate<T> ignoreSameChunk() {
        return (Predicate<T>) IGNORE_SAME_CHUNK;
    }

    /**
     * Returns a predicate which only returns true if the entity has a given metadata key.
     *
     * @param key the metadata key
     * @param <T> the event type
     * @return a predicate which only returns true if the entity has a given metadata key
     */
    public static @NotNull <T extends EntityEvent> Predicate<T> entityHasMetadata(MetadataKey<?> key) {
        return e -> Metadata.provideForEntity(e.getEntity()).has(key);
    }

    /**
     * Returns a predicate which only returns true if the player has a given metadata key.
     *
     * @param key the metadata key
     * @param <T> the event type
     * @return a predicate which only returns true if the player has a given metadata key
     */
    public static @NotNull <T extends PlayerEvent> Predicate<T> playerHasMetadata(MetadataKey<?> key) {
        return e -> Metadata.provideForPlayer(e.getPlayer()).has(key);
    }

    /**
     * Returns a predicate which only returns true if the player has the given permission
     *
     * @param permission the permission
     * @param <T>        the event type
     * @return a predicate which only returns true if the player has the given permission
     */
    public static @NotNull <T extends PlayerEvent> Predicate<T> playerHasPermission(String permission) {
        return e -> e.getPlayer().hasPermission(permission);
    }

}