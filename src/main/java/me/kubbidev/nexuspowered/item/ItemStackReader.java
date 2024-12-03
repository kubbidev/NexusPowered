package me.kubbidev.nexuspowered.item;

import com.google.common.collect.Iterables;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

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
                    parseData(config).ifPresent(isb::data);
                    parseName(config).map(variableReplacer::replace).ifPresent(isb::name);
                    parseLore(config).map(variableReplacer::replace).ifPresent(isb::lore);
                });
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

    protected OptionalInt parseData(ConfigurationSection config) {
        if (config.contains("data")) {
            return OptionalInt.of(config.getInt("data"));
        }
        return OptionalInt.empty();
    }

    protected Optional<String> parseName(ConfigurationSection config) {
        if (config.contains("name")) {
            return Optional.of(config.getString("name"));
        }
        return Optional.empty();
    }

    protected Optional<List<String>> parseLore(ConfigurationSection config) {
        if (config.contains("lore")) {
            return Optional.of(config.getStringList("lore"));
        }
        return Optional.empty();
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