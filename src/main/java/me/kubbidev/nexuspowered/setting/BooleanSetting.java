package me.kubbidev.nexuspowered.setting;

/**
 * Represents an individual setting with a boolean value.
 */
public interface BooleanSetting {

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
    default boolean defaultState() {
        return false;
    }

}
