package me.kubbidev.nexuspowered.setting;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;

/**
 * Creates and decodes {@link BooleanSettingMap}s for a given realm of settings.
 *
 * <p>It is safe to introduce additional settings, so long as the
 * ordinal values of existing ones are not affected.</p>
 *
 * @param <S> the setting type
 */
public final class BooleanSettingMapFactory<S extends BooleanSetting> {

    /**
     * Creates a new {@link BooleanSettingMapFactory} for the given {@link BooleanSetting} enum.
     *
     * <p>Factories should ideally be cached (stored in a static field) in the
     * setting enum and reused.</p>
     *
     * @param settingsEnum the setting class
     * @param <S> the class type
     * @return a new factory
     */
    public static <S extends Enum<S> & BooleanSetting> BooleanSettingMapFactory<S> create(Class<S> settingsEnum) {
        Objects.requireNonNull(settingsEnum, "settingsEnum");
        return create(settingsEnum.getEnumConstants());
    }

    /**
     * Creates a new {@link BooleanSettingMapFactory} for the given {@link BooleanSetting}s.
     *
     * <p>Factories should ideally be cached (stored in a static field) in the
     * setting class.</p>
     *
     * @param settings the settings
     * @param <S> the class type
     * @return a new factory
     */
    public static <S extends BooleanSetting> BooleanSettingMapFactory<S> create(S[] settings) {
        Objects.requireNonNull(settings, "settings");

        BitSet defaultBits = new BitSet();
        for (int i = 0; i < settings.length; i++) {
            S setting = settings[i];

            // ensure ordinal has been correctly implemented
            if (setting.ordinal() != i) {
                throw new IllegalArgumentException("The ordinal of setting " + setting + " does not equal its array index. ordinal=" + setting.ordinal() + ", index=" + i);
            }

            if (setting.defaultState()) {
                defaultBits.set(i);
            }
        }
        return new BooleanSettingMapFactory<>(settings, defaultBits);
    }

    private final S[] settings;
    private final BitSet defaultBits;

    private BooleanSettingMapFactory(S[] settings, BitSet defaultBits) {
        this.settings = settings;
        this.defaultBits = defaultBits;
    }

    /**
     * Gets the {@link BooleanSetting} instances.
     *
     * @return the setting instances
     */
    public S[] getSettings() {
        return Arrays.copyOf(this.settings, this.settings.length);
    }

    /**
     * Returns if the given set of bits differs from the defaults.
     *
     * @param bits the bits to compare
     * @return true if different
     */
    boolean isDifferentFromDefault(BitSet bits) {
        return !this.defaultBits.equals(bits);
    }

    /**
     * Creates a new {@link BooleanSettingMap}, with the default states set for each of the settings.
     *
     * @return the new map
     */
    public BooleanSettingMap<S> newMap() {
        return new BooleanSettingMap<>(this, BitSet.valueOf(this.defaultBits.toLongArray()));
    }

    /**
     * Decodes the given byte array to a {@link BooleanSettingMap}.
     *
     * <p>Operates on the reverse of {@link BooleanSettingMap#encode()}.</p>
     *
     * @param buf the byte array
     * @return the decoded map
     */
    public BooleanSettingMap<S> decode(byte[] buf) {
        BitSet bits = BitSet.valueOf(buf);
        if (bits.length() > this.settings.length) {
            bits.clear(this.settings.length, bits.length());
        }
        return new BooleanSettingMap<>(this, bits);
    }

    /**
     * Decodes the given string to a {@link BooleanSettingMap}.
     *
     * <p>Operates on the reverse of {@link BooleanSettingMap#encodeToString()}.</p>
     *
     * @param encodedString the string
     * @return the decoded map
     */
    public BooleanSettingMap<S> decode(String encodedString) {
        return decode(SettingMapFactory.ENCODING.decode(encodedString));
    }
}