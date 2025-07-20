package me.kubbidev.nexuspowered.cooldown;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * A self-populating map of cooldown instances
 *
 * @param <T> the type
 */
public interface CooldownMap<T> {

    /**
     * Creates a new collection.
     *
     * @return a new collection
     */
    static @NotNull <T> CooldownMap<T> create() {
        return new CooldownMapImpl<>();
    }

    /**
     * Gets the internal cooldown instance associated with the given key.
     *
     * <p>The inline Cooldown methods in this class should be used to access the functionality of the cooldown as
     * opposed to calling the methods directly via the instance returned by this method.</p>
     *
     * @param key the key
     * @return a cooldown instance
     */
    @NotNull Optional<Cooldown> get(T key);

    void put(@NotNull T key, @NotNull Cooldown cooldown);

    /**
     * Gets the cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @NotNull Map<T, Cooldown> getAll();

    /* Methods from Cooldown */

    default boolean test(T key) {
        return this.get(key).map(Cooldown::test).orElse(true);
    }

    default boolean testSilently(T key) {
        return this.get(key).map(Cooldown::test).orElse(true);
    }

    default long elapsed(T key) {
        return this.get(key).map(Cooldown::elapsed).orElse(0L);
    }

    default void reset(T key) {
        this.get(key).ifPresent(Cooldown::reset);
    }

    default long remainingMillis(T key) {
        return this.get(key).map(Cooldown::remainingMillis).orElse(0L);
    }

    default long remainingTime(T key, TimeUnit unit) {
        return this.get(key).map(cooldown -> cooldown.remainingTime(unit)).orElse(0L);
    }

    default OptionalLong getLastTested(T key) {
        return get(key).map(Cooldown::getLastTested).orElse(OptionalLong.empty());
    }

    default void setLastTested(T key, long time) {
        this.get(key).ifPresent(cooldown -> cooldown.setLastTested(time));
    }

}