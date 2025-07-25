package me.kubbidev.nexuspowered.config.key;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import me.kubbidev.nexuspowered.config.adapter.ConfigurationAdapter;

public interface ConfigKeyFactory<T> {

    ConfigKeyFactory<Boolean>             BOOLEAN          = ConfigurationAdapter::getBoolean;
    ConfigKeyFactory<Integer>             INTEGER          = ConfigurationAdapter::getInteger;
    ConfigKeyFactory<Double>              DOUBLE           = ConfigurationAdapter::getDouble;
    ConfigKeyFactory<String>              STRING           = ConfigurationAdapter::getString;
    ConfigKeyFactory<List<String>>        STRING_LIST      = ConfigurationAdapter::getStringList;
    ConfigKeyFactory<String>              LOWERCASE_STRING = (adapter, path, def) -> adapter.getString(path, def)
        .toLowerCase(Locale.ROOT);
    ConfigKeyFactory<Map<String, String>> STRING_MAP       = (adapter, path, def) -> ImmutableMap.copyOf(
        adapter.getStringMap(path, ImmutableMap.of()));

    static <T> SimpleConfigKey<T> key(Function<ConfigurationAdapter, T> function) {
        return new SimpleConfigKey<>(function);
    }

    static <T> SimpleConfigKey<T> notReloadable(SimpleConfigKey<T> key) {
        key.setReloadable(false);
        return key;
    }

    static SimpleConfigKey<Boolean> booleanKey(String path, boolean def) {
        return key(new Bound<>(BOOLEAN, path, def));
    }

    static SimpleConfigKey<Integer> integerKey(String path, int def) {
        return key(new Bound<>(INTEGER, path, def));
    }

    static SimpleConfigKey<Double> doubleKey(String path, double def) {
        return key(new Bound<>(DOUBLE, path, def));
    }

    static SimpleConfigKey<String> stringKey(String path, String def) {
        return key(new Bound<>(STRING, path, def));
    }

    static SimpleConfigKey<List<String>> stringListKey(String path, List<String> def) {
        return key(new Bound<>(STRING_LIST, path, def));
    }

    static SimpleConfigKey<String> lowercaseStringKey(String path, String def) {
        return key(new Bound<>(LOWERCASE_STRING, path, def));
    }

    static SimpleConfigKey<Map<String, String>> mapKey(String path) {
        return key(new Bound<>(STRING_MAP, path, null));
    }

    /**
     * Extracts the value from the config.
     *
     * @param config the config
     * @param path   the path where the value is
     * @param def    the default value
     * @return the value
     */
    T getValue(ConfigurationAdapter config, String path, T def);

    /**
     * A {@link ConfigKeyFactory} bound to a given {@code path}.
     *
     * @param <T> the value type
     */
    class Bound<T> implements Function<ConfigurationAdapter, T> {

        private final ConfigKeyFactory<T> factory;
        private final String              path;
        private final T                   def;

        Bound(ConfigKeyFactory<T> factory, String path, T def) {
            this.factory = factory;
            this.path = path;
            this.def = def;
        }

        @Override
        public T apply(ConfigurationAdapter adapter) {
            return this.factory.getValue(adapter, this.path, this.def);
        }
    }

}