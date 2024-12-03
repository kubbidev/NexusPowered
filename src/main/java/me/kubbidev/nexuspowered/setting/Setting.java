package me.kubbidev.nexuspowered.setting;

/**
 * Represents a setting.
 *
 * @param <V> the type of state the setting has
 */
public interface Setting<V extends Setting.State> {

    /**
     * The ordinal position of the setting.
     *
     * @return the ordinal value
     */
    int ordinal();

    /**
     * Returns the default state of this setting.
     *
     * @return the default state
     */
    V defaultState();

    /**
     * Represents the state of a setting.
     */
    interface State {

        /**
         * The ordinal position of the state.
         *
         * @return the ordinal value
         */
        int ordinal();

    }

}