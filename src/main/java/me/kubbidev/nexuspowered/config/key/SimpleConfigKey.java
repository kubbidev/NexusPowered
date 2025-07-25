package me.kubbidev.nexuspowered.config.key;

import java.util.function.Function;
import me.kubbidev.nexuspowered.config.adapter.ConfigurationAdapter;

/**
 * Basic {@link ConfigKey} implementation.
 *
 * @param <T> the value type
 */
public class SimpleConfigKey<T> implements ConfigKey<T> {

    private final Function<? super ConfigurationAdapter, ? extends T> function;

    private int     ordinal    = -1;
    private boolean reloadable = true;

    SimpleConfigKey(Function<? super ConfigurationAdapter, ? extends T> function) {
        this.function = function;
    }

    @Override
    public T get(ConfigurationAdapter adapter) {
        return this.function.apply(adapter);
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public boolean reloadable() {
        return this.reloadable;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }
}