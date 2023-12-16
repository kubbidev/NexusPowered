package com.kubbidev.nexuspowered.paper;

import com.kubbidev.java.config.generic.adapter.ConfigurationAdapter;
import com.kubbidev.java.util.EnumUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PaperConfigAdapter implements ConfigurationAdapter {

    private final File file;
    private YamlConfiguration configuration;

    public PaperConfigAdapter(File file) {
        this.file = file;
        reload();
    }

    @Override
    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public @NotNull String getString(String path, String def) {
        return this.configuration.getString(path, def);
    }

    @Override
    public int getInteger(String path, int def) {
        return this.configuration.getInt(path, def);
    }

    @Override
    public long getLong(String path, long def) {
        return this.configuration.getLong(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        return this.configuration.getDouble(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.configuration.getBoolean(path, def);
    }

    @Override
    public @NotNull List<String> getStringList(String path, List<String> def) {
        List<String> list = this.configuration.getStringList(path);
        return this.configuration.isSet(path) ? list : def;
    }

    @Override
    public @NotNull Map<String, String> getStringMap(String path, Map<String, String> def) {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection section = this.configuration.getConfigurationSection(path);
        if (section == null) {
            return def;
        }

        for (String key : section.getKeys(false)) {
            map.put(key, section.getString(key));
        }

        return map;
    }

    @Override
    public <E extends Enum<E>> @NotNull E getEnum(String path, E def) {

        Class<E> clazz = def.getDeclaringClass();
        String value = this.configuration.getString(path);

        return Objects.requireNonNullElse(EnumUtil.getEnum(value, clazz), def);
    }
}
