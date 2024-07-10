package me.kubbidev.nexuspowered.command.argument;

import me.kubbidev.nexuspowered.command.CommandInterruptException;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

/**
 * Parses an argument from a String.
 *
 * @param <T> the value type
 */
@NotNullByDefault
public interface ArgumentParser<T> {

    static <T> ArgumentParser<T> of(Function<String, Optional<T>> parseFunction) {
        return parseFunction::apply;
    }

    static <T> ArgumentParser<T> of(Function<String, Optional<T>> parseFunction, Function<String, CommandInterruptException> generateExceptionFunction) {
        return new ArgumentParser<>() {
            @Override
            public Optional<T> parse(@NotNull String t) {
                return parseFunction.apply(t);
            }

            @Override
            public CommandInterruptException generateException(@NotNull String s) {
                return generateExceptionFunction.apply(s);
            }
        };
    }

    /**
     * Parses the value from a string.
     *
     * @param s the string
     * @return the value, if parsing was successful
     */
    Optional<T> parse(@NotNull String s);

    /**
     * Generates a {@link CommandInterruptException} for when parsing fails with the given content.
     *
     * @param s the string input
     * @return the exception
     */
    default CommandInterruptException generateException(@NotNull String s) {
        return new CommandInterruptException(Component.text("Unable to parse argument: " + s, NamedTextColor.RED));
    }

    /**
     * Generates a {@link CommandInterruptException} for when parsing fails due to the lack of an argument.
     *
     * @param missingArgumentIndex the missing argument index
     * @return the exception
     */
    default CommandInterruptException generateException(int missingArgumentIndex) {
        return new CommandInterruptException(Component.text("Argument at index " + missingArgumentIndex + " is missing.", NamedTextColor.RED));
    }

    /**
     * Parses the value from a string, throwing an interrupt exception if
     * parsing failed.
     *
     * @param s the string
     * @return the value
     */
    @NotNull
    default T parseOrFail(@NotNull String s) throws CommandInterruptException {
        Optional<T> ret = parse(s);
        if (ret.isEmpty()) {
            throw generateException(s);
        }
        return ret.get();
    }

    /**
     * Tries to parse the value from the argument.
     *
     * @param argument the argument
     * @return the value, if parsing was successful
     */
    @NotNull
    default Optional<T> parse(@NotNull Argument argument) {
        return argument.value().flatMap(this::parse);
    }

    /**
     * Parses the value from an argument, throwing an interrupt exception if
     * parsing failed.
     *
     * @param argument the argument
     * @return the value
     */
    @NotNull
    default T parseOrFail(@NotNull Argument argument) throws CommandInterruptException {
        Optional<String> value = argument.value();
        if (value.isEmpty()) {
            throw generateException(argument.index());
        }
        return parseOrFail(value.get());
    }

    /**
     * Creates a new parser which first tries to obtain a value from
     * this parser, then from another if the former was not successful.
     *
     * @param other the other parser
     * @return the combined parser
     */
    @NotNull
    default ArgumentParser<T> thenTry(@NotNull ArgumentParser<T> other) {
        ArgumentParser<T> first = this;
        return t -> {
            Optional<T> ret = first.parse(t);
            return ret.isPresent() ? ret : other.parse(t);
        };
    }
}