package me.kubbidev.nexuspowered.setting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a map of states of a given realm of settings.
 *
 * @param <S> the setting type
 */
public final class SettingMap<S extends Setting<V>, V extends Setting.State> {
    private final SettingMapFactory<S, V> factory;

    // the index corresponds to the setting ordinal, the value corresponds to the state ordinal
    private final byte[] states;

    SettingMap(SettingMapFactory<S, V> factory, byte[] states) {
        this.factory = factory;
        this.states = states;
    }

    /**
     * Gets the state of a given setting.
     *
     * @param setting the setting
     * @return the state
     */
    public V get(S setting) {
        int stateOrdinal = Byte.toUnsignedInt(this.states[setting.ordinal()]);
        return this.factory.states[stateOrdinal];
    }

    /**
     * Sets the state of a given setting.
     *
     * @param setting the setting
     * @param state   the state to set
     * @return the previous state of the setting
     */
    public V set(S setting, V state) {
        V prev = get(setting);
        if (state == prev) {
            return state;
        }

        this.states[setting.ordinal()] = (byte) state.ordinal();
        return prev;
    }

    /**
     * Exports this {@link SettingMap} as a {@link Map}.
     *
     * @return a map
     */
    public Map<S, V> exportToMap() {
        Map<S, V> map = new HashMap<>();
        S[] settings = this.factory.settings;
        for (int i = 0; i < settings.length; i++) {
            S setting = settings[i];
            int stateOrdinal = Byte.toUnsignedInt(this.states[i]);
            map.put(setting, this.factory.states[stateOrdinal]);
        }
        return map;
    }

    /**
     * Returns if this map has any settings with states differing from the defaults.
     *
     * @return if this map differs from the defaults
     */
    public boolean isDifferentFromDefault() {
        return this.factory.isDifferentFromDefault(this.states);
    }

    /**
     * Encodes the state of each of the settings in this map to a byte array.
     *
     * @return the bytes
     */
    public byte[] encode() {
        return this.factory.encode(this.states);
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
     * @return a readable string representation of the map
     */
    @Override
    public String toString() {
        return exportToMap().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SettingMap<?, ?>)) {
            return false;
        }
        SettingMap<?, ?> other = (SettingMap<?, ?>) o;
        return this.factory.equals(other.factory) &&
                Arrays.equals(this.states, other.states);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.states);
    }
}