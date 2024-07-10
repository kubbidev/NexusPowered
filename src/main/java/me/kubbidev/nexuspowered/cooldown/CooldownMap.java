package me.kubbidev.nexuspowered.cooldown;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

/**
 * A self-populating map of cooldown instances
 *
 * @param <T> the type
 */
public interface CooldownMap<T> {

    /**
     * Creates a new collection with the cooldown properties defined by the base instance
     *
     * @param base the cooldown to base off
     * @return a new collection
     */
    @NotNull
    static <T> CooldownMap<T> create(@NotNull Cooldown base) {
        Objects.requireNonNull(base, "base");
        return new CooldownMapImpl<>(base);
    }

    /**
     * Gets the base cooldown
     *
     * @return the base cooldown
     */
    @NotNull
    Cooldown getBase();

    /**
     * Gets the internal cooldown instance associated with the given key.
     *
     * <p>The inline Cooldown methods in this class should be used to access the functionality of the cooldown as opposed
     * to calling the methods directly via the instance returned by this method.</p>
     *
     * @param key the key
     * @return a cooldown instance
     */
    @NotNull
    Cooldown get(@NotNull T key);

    void put(@NotNull T key, @NotNull Cooldown cooldown);

    /**
     * Gets the cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @NotNull
    Map<T, Cooldown> getAll();

    /* methods from Cooldown */

    default boolean test(@NotNull T key) {
        return get(key).test();
    }

    default boolean testSilently(@NotNull T key) {
        return get(key).testSilently();
    }

    default long elapsed(@NotNull T key) {
        return get(key).elapsed();
    }

    default void reset(@NotNull T key) {
        get(key).reset();
    }

    default long remainingMillis(@NotNull T key) {
        return get(key).remainingMillis();
    }

    default long remainingTime(@NotNull T key, @NotNull TimeUnit unit) {
        return get(key).remainingTime(unit);
    }

    @NotNull
    default OptionalLong getLastTested(@NotNull T key) {
        return get(key).getLastTested();
    }

    default void setLastTested(@NotNull T key, long time) {
        get(key).setLastTested(time);
    }

}