package me.kubbidev.nexuspowered.item;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Utility for creating {@link ItemStackBuilder}s from {@link ConfigurationSection config} files.
 */
public class ItemStackReader {

    /**
     * The default helper {@link ItemStackReader} implementation.
     */
    public static final ItemStackReader DEFAULT = new ItemStackReader();

    // allow subclassing
    protected ItemStackReader() {

    }

    /**
     * Reads an {@link ItemStackBuilder} from the given config.
     *
     * @param config the config to read from
     * @return the item
     */
    public final ItemStackBuilder read(ConfigurationSection config) {
        return read(config, VariableReplacer.NOOP);
    }

    /**
     * Reads an {@link ItemStackBuilder} from the given config.
     *
     * @param config           the config to read from
     * @param variableReplacer the variable replacer to use to replace variables in the name and lore.
     * @return the item
     */
    public ItemStackBuilder read(ConfigurationSection config, VariableReplacer variableReplacer) {
        return ItemStackBuilder.of(parseMaterial(config))
            .apply(isb -> {
                parseModelData(config).ifPresent(isb::modelData);
                parseName(config).map(variableReplacer::replace).map(this::parseComponent).ifPresent(isb::name);
                parseLore(config).map(variableReplacer::replace).map(this::parseComponent).ifPresent(isb::lore);
            });
    }

    protected Component parseComponent(String rawString) {
        return GsonComponentSerializer.gson().deserialize(rawString);
    }

    protected Material parseMaterial(ConfigurationSection config) {
        return parseMaterial(Objects.requireNonNull(config.getString("material"), "Could not found ItemStack type"));
    }

    protected Material parseMaterial(String name) {
        try {
            return Material.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to parse material '" + name + "'");
        }
    }

    protected OptionalInt parseModelData(ConfigurationSection config) {
        return config.contains("model_data") ? OptionalInt.of(config.getInt("model_data")) : OptionalInt.empty();
    }

    protected Optional<String> parseName(ConfigurationSection config) {
        return config.contains("name") ? Optional.ofNullable(config.getString("name")) : Optional.empty();
    }

    protected Optional<String> parseLore(ConfigurationSection config) {
        return config.contains("lore") ? Optional.ofNullable(config.getString("lore")) : Optional.empty();
    }

    /**
     * Function for replacing variables in item names and lores.
     */
    @FunctionalInterface
    public interface VariableReplacer {

        /**
         * No-op instance.
         */
        VariableReplacer NOOP = s -> s;

        /**
         * Replace variables in the input {@code string}.
         *
         * @param string the string
         * @return the replaced string
         */
        String replace(String string);

        /**
         * Replaces variables in the input {@code list} of {@link String}s.
         *
         * @param list the list
         * @return the replaced list
         */
        default Iterable<String> replace(List<String> list) {
            return Iterables.transform(list, this::replace);
        }
    }
}