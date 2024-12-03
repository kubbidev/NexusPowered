package me.kubbidev.nexuspowered.command.argument;

import com.google.common.reflect.TypeToken;
import me.kubbidev.nexuspowered.Commands;
import me.kubbidev.nexuspowered.command.CommandInterruptException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents a command argument
 */
public interface Argument {

    /**
     * Gets the index of the argument.
     *
     * @return the index
     */
    int index();

    /**
     * Gets the value of the argument.
     *
     * @return the value
     */
    @NotNull
    Optional<String> value();

    @NotNull
    default <T> Optional<T> parse(@NotNull ArgumentParser<T> parser) {
        return parser.parse(this);
    }

    @NotNull
    default <T> T parseOrFail(@NotNull ArgumentParser<T> parser) throws CommandInterruptException {
        return parser.parseOrFail(this);
    }

    @NotNull
    default <T> Optional<T> parse(@NotNull TypeToken<T> type) {
        return Commands.parserRegistry().find(type).flatMap(this::parse);
    }

    @NotNull
    default <T> T parseOrFail(@NotNull TypeToken<T> type) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(type).orElse(null);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + type);
        }
        return parseOrFail(parser);
    }

    @NotNull
    default <T> Optional<T> parse(@NotNull Class<T> clazz) {
        return Commands.parserRegistry().find(clazz).flatMap(this::parse);
    }

    @NotNull
    default <T> T parseOrFail(@NotNull Class<T> clazz) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(clazz).orElse(null);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + clazz);
        }
        return parseOrFail(parser);
    }

    /**
     * Gets if the argument is present.
     *
     * @return true if present
     */
    boolean isPresent();

    /**
     * Asserts that the permission is present.
     */
    default void assertPresent() throws CommandInterruptException {
        CommandInterruptException.makeAssertion(isPresent(), "&cArgument at index " + index() + " is not present.");
    }
}