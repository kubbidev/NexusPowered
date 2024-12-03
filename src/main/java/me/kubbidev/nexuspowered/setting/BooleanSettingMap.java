package me.kubbidev.nexuspowered.setting;

import java.util.BitSet;

/**
 * Represents a map of states of a given realm of settings.
 *
 * @param <S> the setting type
 */
public final class BooleanSettingMap<S extends BooleanSetting> {
    private final BooleanSettingMapFactory<S> factory;
    private final BitSet bits;

    BooleanSettingMap(BooleanSettingMapFactory<S> factory, BitSet bits) {
        this.factory = factory;
        this.bits = bits;
    }

    /**
     * Gets the state of a given setting.
     *
     * @param setting the setting
     * @return the state
     */
    public boolean get(S setting) {
        return this.bits.get(setting.ordinal());
    }

    /**
     * Sets the state of a given setting.
     *
     * @param setting the setting
     * @param state the state to set
     * @return the previous state of the setting
     */
    public boolean set(S setting, boolean state) {
        if (state == get(setting)) {
            return state;
        }

        if (state) {
            this.bits.set(setting.ordinal());
        } else {
            this.bits.clear(setting.ordinal());
        }
        return !state;
    }

    /**
     * Toggles the state of a setting.
     *
     * @param setting the setting
     * @return the new state
     */
    public boolean toggle(S setting) {
        this.bits.flip(setting.ordinal());
        return get(setting);
    }

    /**
     * Returns if this map has any settings with states differing from the defaults.
     *
     * @return if this map differs from the defaults
     */
    public boolean isDifferentFromDefault() {
        return this.factory.isDifferentFromDefault(this.bits);
    }

    /**
     * Encodes the state of each of the settings in this map to a byte array.
     *
     * @return the bytes
     */
    public byte[] encode() {
        return this.bits.toByteArray();
    }

    /**
     * Encodes the state of each of the settings in this map to a string.
     *
     * @return the string
     */
    public String encodeToString() {
        return SettingMapFactory.ENCODING.encode(encode());
    }

    /**
     * Returns a readable string representation of the map.
     *
     * <p>Consists of a list of the ordinals of the setting set to a true state.</p>
     *
     * @return a readable string representation of the map
     */
    @Override
    public String toString() {
        return this.bits.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BooleanSettingMap<?>)) {
            return false;
        }
        BooleanSettingMap<?> other = (BooleanSettingMap<?>) o;
        return this.factory.equals(other.factory) &&
                this.bits.equals(other.bits);
    }

    @Override
    public int hashCode() {
        return this.bits.hashCode();
    }
}