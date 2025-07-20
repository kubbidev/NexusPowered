package me.kubbidev.nexuspowered.cooldown;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * A self-populating, composed map of cooldown instances
 *
 * @param <I> input type
 * @param <O> internal type
 */
public interface ComposedCooldownMap<I, O> {

    /**
     * Creates a new collection with the cooldown properties defined by the base instance.
     *
     * @param base the cooldown to base off
     * @return a new collection
     */
    static @NotNull <I, O> ComposedCooldownMap<I, O> create(@NotNull Cooldown base,
                                                            @NotNull Function<I, O> composeFunction) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(composeFunction, "composeFunction");
        return new ComposedCooldownMapImpl<>(base, composeFunction);
    }

    /**
     * Gets the base cooldown.
     *
     * @return the base cooldown
     */
    @NotNull Cooldown getBase();

    /**
     * Gets the internal cooldown instance associated with the given key.
     *
     * <p>The inline Cooldown methods in this class should be used to access the functionality of the cooldown as
     * opposed to calling the methods directly via the instance returned by this method.</p>
     *
     * @param key the key
     * @return a cooldown instance
     */
    @NotNull Cooldown get(@NotNull I key);

    void put(@NotNull O key, @NotNull Cooldown cooldown);

    /**
     * Gets the cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @NotNull Map<O, Cooldown> getAll();

    /* methods from Cooldown */

    default boolean test(@NotNull I key) {
        return this.get(key).test();
    }

    default boolean testSilently(@NotNull I key) {
        return this.get(key).testSilently();
    }

    default long elapsed(@NotNull I key) {
        return this.get(key).elapsed();
    }

    default void reset(@NotNull I key) {
        this.get(key).reset();
    }

    default long remainingMillis(@NotNull I key) {
        return this.get(key).remainingMillis();
    }

    default long remainingTime(@NotNull I key, @NotNull TimeUnit unit) {
        return this.get(key).remainingTime(unit);
    }

    default @NotNull OptionalLong getLastTested(@NotNull I key) {
        return this.get(key).getLastTested();
    }

    default void setLastTested(@NotNull I key, long time) {
        this.get(key).setLastTested(time);
    }

}