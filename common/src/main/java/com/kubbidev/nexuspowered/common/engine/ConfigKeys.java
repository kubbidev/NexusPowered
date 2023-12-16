package com.kubbidev.nexuspowered.common.engine;

import com.kubbidev.java.config.generic.KeyedConfiguration;
import com.kubbidev.java.config.generic.key.ConfigKey;
import com.kubbidev.java.config.generic.key.SimpleConfigKey;

import java.util.List;

import static com.kubbidev.java.config.generic.key.ConfigKeyFactory.stringKey;

public final class ConfigKeys {
    private ConfigKeys() {}

    /**
     * Default command label used when registering the main plugin command and his children.
     */
    public static final ConfigKey<String> DEFAULT_COMMAND_LABEL = stringKey("default-command-label", "undefined");

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(ConfigKeys.class);

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}
