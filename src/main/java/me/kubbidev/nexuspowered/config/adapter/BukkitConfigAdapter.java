package me.kubbidev.nexuspowered.config.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.kubbidev.nexuspowered.plugin.NexusPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class BukkitConfigAdapter implements ConfigurationAdapter {

    private final NexusPlugin       plugin;
    private final File              file;
    private       YamlConfiguration configuration;

    public BukkitConfigAdapter(NexusPlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        this.reload();
    }

    @Override
    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public String getString(String path, String def) {
        return this.configuration.getString(path, def);
    }

    @Override
    public int getInteger(String path, int def) {
        return this.configuration.getInt(path, def);
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
    public List<String> getStringList(String path, List<String> def) {
        List<String> list = this.configuration.getStringList(path);
        return this.configuration.isSet(path) ? list : def;
    }

    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
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
    public NexusPlugin getPlugin() {
        return this.plugin;
    }
}