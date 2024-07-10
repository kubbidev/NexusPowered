package me.kubbidev.nexuspowered.command.argument;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * A collection of {@link ArgumentParser}s
 */
public interface ArgumentParserRegistry {

    /**
     * Tries to find an argument parser for the given type.
     *
     * @param type the argument type
     * @param <T>  the type
     * @return an argument, if one was found
     */
    @NotNull
    <T> Optional<ArgumentParser<T>> find(@NotNull TypeToken<T> type);

    /**
     * Tries to find an argument parser for the given class.
     *
     * @param clazz the argument class
     * @param <T>   the class type
     * @return an argument, if one was found
     */
    @NotNull
    default <T> Optional<ArgumentParser<T>> find(@NotNull Class<T> clazz) {
        return find(TypeToken.of(clazz));
    }

    /**
     * Finds all known parsers for a given type.
     *
     * @param type the argument type
     * @param <T>  the type
     * @return a collection of argument parsers
     */
    @NotNull
    <T> Collection<ArgumentParser<T>> findAll(@NotNull TypeToken<T> type);

    /**
     * Finds all known parsers for a given class.
     *
     * @param clazz the argument class
     * @param <T>   the class type
     * @return a collection of argument parsers
     */
    @NotNull
    default <T> Collection<ArgumentParser<T>> findAll(@NotNull Class<T> clazz) {
        return findAll(TypeToken.of(clazz));
    }

    /**
     * Registers a new parser with the registry.
     *
     * @param type   the argument type
     * @param parser the parser
     * @param <T>    the type
     */
    <T> void register(@NotNull TypeToken<T> type, @NotNull ArgumentParser<T> parser);

    /**
     * Registers a new parser with the registry.
     *
     * @param clazz  the argument class
     * @param parser the parser
     * @param <T>    the class type
     */
    default <T> void register(@NotNull Class<T> clazz, @NotNull ArgumentParser<T> parser) {
        register(TypeToken.of(clazz), parser);
    }
}