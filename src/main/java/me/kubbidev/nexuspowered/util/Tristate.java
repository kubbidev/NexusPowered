package me.kubbidev.nexuspowered.util;

import me.kubbidev.nexuspowered.setting.Setting;
import org.jetbrains.annotations.NotNull;

/**
 * Represents three different states of a setting.
 *
 * <p>Possible values:</p>
 * <p></p>
 * <ul>
 *     <li>{@link #TRUE} - a positive setting</li>
 *     <li>{@link #FALSE} - a negative (negated) setting</li>
 *     <li>{@link #UNDEFINED} - a non-existent setting</li>
 * </ul>
 */
public enum Tristate implements Setting.State {

    /**
     * A value indicating a positive setting
     */
    TRUE(true),

    /**
     * A value indicating a negative (negated) setting
     */
    FALSE(false),

    /**
     * A value indicating a non-existent setting
     */
    UNDEFINED(false);

    /**
     * Returns a {@link Tristate} from a boolean
     *
     * @param val the boolean value
     * @return {@link #TRUE} or {@link #FALSE}, if the value is <code>true</code> or <code>false</code>, respectively.
     */
    public static @NotNull Tristate of(boolean val) {
        return val ? TRUE : FALSE;
    }

    /**
     * Returns a {@link Tristate} from a nullable boolean.
     *
     * <p>Unlike {@link #of(boolean)}, this method returns {@link #UNDEFINED}
     * if the value is null.</p>
     *
     * @param val the boolean value
     * @return {@link #UNDEFINED}, {@link #TRUE} or {@link #FALSE}, if the value
     * is <code>null</code>, <code>true</code> or <code>false</code>, respectively.
     */
    public static @NotNull Tristate of(Boolean val) {
        return val == null ? UNDEFINED : val ? TRUE : FALSE;
    }

    private final boolean booleanValue;

    Tristate(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /**
     * Returns the value of the Tristate as a boolean.
     *
     * <p>A value of {@link #UNDEFINED} converts to false.</p>
     *
     * @return a boolean representation of the Tristate.
     */
    public boolean asBoolean() {
        return this.booleanValue;
    }
}