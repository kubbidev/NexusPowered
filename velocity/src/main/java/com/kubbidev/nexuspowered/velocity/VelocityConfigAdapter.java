package com.kubbidev.nexuspowered.velocity;

import com.google.common.base.Splitter;
import com.kubbidev.java.config.generic.adapter.ConfigurationAdapter;
import com.kubbidev.java.util.EnumUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class VelocityConfigAdapter implements ConfigurationAdapter {

    private final Path path;
    private ConfigurationNode root;

    public VelocityConfigAdapter(Path path) {
        this.path = path;
        reload();
    }

    protected ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path) {
        return YAMLConfigurationLoader.builder().setPath(path).build();
    }

    @Override
    public void reload() {
        ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(this.path);
        try {
            this.root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigurationNode resolvePath(String path) {
        if (this.root == null) {
            throw new RuntimeException("Config is not loaded.");
        }

        return this.root.getNode(Splitter.on('.').splitToList(path).toArray());
    }

    @Override
    public @NotNull String getString(String path, String def) {
        return resolvePath(path).getString(def);
    }

    @Override
    public int getInteger(String path, int def) {
        return resolvePath(path).getInt(def);
    }

    @Override
    public long getLong(String path, long def) {
        return resolvePath(path).getLong(def);
    }

    @Override
    public double getDouble(String path, double def) {
        return resolvePath(path).getDouble(def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return resolvePath(path).getBoolean(def);
    }

    @Override
    public @NotNull List<String> getStringList(String path, List<String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.isVirtual() || !node.isList()) {
            return def;
        }

        return node.getList(Object::toString);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Map<String, String> getStringMap(String path, Map<String, String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.isVirtual()) {
            return def;
        }

        Map<String, Object> m = (Map<String, Object>) node.getValue(Collections.emptyMap());
        return m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
    }

    @Override
    public <E extends Enum<E>> @NotNull E getEnum(String path, E def) {

        Class<E> clazz = def.getDeclaringClass();
        String value = resolvePath(path).getString();

        return Objects.requireNonNullElse(EnumUtil.getEnum(value, clazz), def);
    }
}