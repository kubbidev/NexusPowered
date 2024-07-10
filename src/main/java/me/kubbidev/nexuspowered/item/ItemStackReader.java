package me.kubbidev.nexuspowered.item;

import com.google.common.collect.Iterables;
import me.kubbidev.nexuspowered.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                    parseName(config).map(variableReplacer::replace).ifPresent(isb::name);
                    parseLore(config).map(variableReplacer::replace).ifPresent(isb::lore);
                });
    }

    protected Material parseMaterial(ConfigurationSection config) {
        return parseMaterial(Objects.requireNonNull(config.getString("material"), "Could not found ItemStack type"));
    }

    protected Material parseMaterial(String name) {
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to parse material '" + name + "'");
        }
    }

    protected Optional<Component> parseName(ConfigurationSection config) {
        if (config.contains("name")) {
            return Optional.of(Text.fromGson(config.getString("name")));
        }
        return Optional.empty();
    }

    protected Optional<List<Component>> parseLore(ConfigurationSection config) {
        if (config.contains("lore")) {
            return Optional.of(Text.fromGson(config.getStringList("lore")));
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
         * Replace variables in the input {@code component}.
         *
         * @param component the string
         * @return the replaced string
         */
        Component replace(Component component);

        /**
         * Replaces variables in the input {@code list} of {@link Component}s.
         *
         * @param list the list
         * @return the replaced list
         */
        default Iterable<Component> replace(List<Component> list) {
            return Iterables.transform(list, this::replace);
        }
    }
}