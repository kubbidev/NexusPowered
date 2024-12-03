package me.kubbidev.nexuspowered.config.adapter;

import me.kubbidev.nexuspowered.plugin.NexusPlugin;

import java.util.List;
import java.util.Map;

public interface ConfigurationAdapter {

    NexusPlugin getPlugin();

    void reload();

    String getString(String path, String def);

    int getInteger(String path, int def);

    double getDouble(String path, double def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path, List<String> def);

    Map<String, String> getStringMap(String path, Map<String, String> def);

}