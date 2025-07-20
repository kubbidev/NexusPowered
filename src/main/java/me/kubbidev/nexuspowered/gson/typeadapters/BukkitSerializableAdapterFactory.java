package me.kubbidev.nexuspowered.gson.typeadapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public final class BukkitSerializableAdapterFactory implements TypeAdapterFactory {

    public static final BukkitSerializableAdapterFactory INSTANCE = new BukkitSerializableAdapterFactory();

    private BukkitSerializableAdapterFactory() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> clazz = type.getRawType();

        if (!ConfigurationSerializable.class.isAssignableFrom(clazz)) {
            return null;
        }

        return (TypeAdapter<T>) new Adapter(gson);
    }

    private static final class Adapter extends TypeAdapter<ConfigurationSerializable> {

        private static final Type RAW_OUTPUT_TYPE = new TypeToken<Map<String, Object>>() {
        }.getType();
        private final        Gson gson;

        private Adapter(Gson gson) {
            this.gson = gson;
        }

        @Override
        public void write(JsonWriter out, ConfigurationSerializable value) {
            if (value == null) {
                this.gson.toJson(null, RAW_OUTPUT_TYPE, out);
                return;
            }
            Map<String, Object> serialized = value.serialize();

            Map<String, Object> map = new LinkedHashMap<>(serialized.size() + 1);
            map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
                ConfigurationSerialization.getAlias(value.getClass()));
            map.putAll(serialized);

            this.gson.toJson(map, RAW_OUTPUT_TYPE, out);
        }

        @Override
        public ConfigurationSerializable read(JsonReader in) {
            Map<String, Object> map = this.gson.fromJson(in, RAW_OUTPUT_TYPE);
            if (map == null) {
                return null;
            }
            this.deserializeChildren(map);
            return ConfigurationSerialization.deserializeObject(map);
        }

        private void deserializeChildren(Map<String, Object> map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> value = (Map<String, Object>) entry.getValue();
                        this.deserializeChildren(value);

                        if (value.containsKey("==")) {
                            entry.setValue(ConfigurationSerialization.deserializeObject(value));
                        }

                    } catch (Exception e) {
                        // ignore
                    }
                }

                if (entry.getValue() instanceof Number) {
                    entry.setValue(((Number) entry.getValue()).doubleValue());
                }
            }
        }
    }

}