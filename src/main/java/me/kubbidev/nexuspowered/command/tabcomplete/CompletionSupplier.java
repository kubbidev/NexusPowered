package me.kubbidev.nexuspowered.command.tabcomplete;

import me.kubbidev.nexuspowered.util.Predicates;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Functional interface representing a supplier of tab completions.
 */
@FunctionalInterface
public interface CompletionSupplier {

    /**
     * A CompletionSupplier that always returns an empty list.
     */
    CompletionSupplier EMPTY = partial -> Collections.emptyList();

    /**
     * Creates a {@link CompletionSupplier} that suggests completions starting with the given strings.
     *
     * @param strings The array of strings to use for completion suggestions.
     * @return A CompletionSupplier that filters the given strings by those starting with the input.
     */
    static CompletionSupplier startsWith(String... strings) {
        return startsWith(() -> Arrays.stream(strings));
    }

    /**
     * Creates a {@link CompletionSupplier} that suggests completions starting with the given collection of strings.
     *
     * @param strings The collection of strings to use for completion suggestions.
     * @return A CompletionSupplier that filters the given strings by those starting with the input.
     */
    static CompletionSupplier startsWith(Collection<String> strings) {
        return startsWith(strings::stream);
    }

    /**
     * Creates a {@link CompletionSupplier} that suggests completions starting with the given supplier of strings.
     *
     * @param stringsSupplier A supplier providing a stream of strings to be used for completion suggestions.
     * @return A CompletionSupplier that filters the supplied strings by those starting with the input.
     */
    static CompletionSupplier startsWith(Supplier<Stream<String>> stringsSupplier) {
        return partial -> stringsSupplier.get().filter(Predicates.startsWithIgnoreCase(partial)).collect(Collectors.toList());
    }

    /**
     * Creates a {@link CompletionSupplier} that suggests completions containing the given strings.
     *
     * @param strings The array of strings to use for completion suggestions.
     * @return A CompletionSupplier that filters the given strings by those containing the input.
     */
    static CompletionSupplier contains(String... strings) {
        return contains(() -> Arrays.stream(strings));
    }

    /**
     * Creates a {@link CompletionSupplier} that suggests completions containing the given collection of strings.
     *
     * @param strings The collection of strings to use for completion suggestions.
     * @return A CompletionSupplier that filters the given strings by those containing the input.
     */
    static CompletionSupplier contains(Collection<String> strings) {
        return contains(strings::stream);
    }

    /**
     * Creates a {@link CompletionSupplier} that suggests completions containing the given supplier of strings.
     *
     * @param stringsSupplier A supplier providing a stream of strings to be used for completion suggestions.
     * @return A CompletionSupplier that filters the supplied strings by those containing the input.
     */
    static CompletionSupplier contains(Supplier<Stream<String>> stringsSupplier) {
        return partial -> stringsSupplier.get().filter(Predicates.containsIgnoreCase(partial)).collect(Collectors.toList());
    }

    /**
     * Supplies a list of possible completions based on the given partial input.
     *
     * @param partial The partial input string to match completions against.
     * @return A list of matching completion suggestions.
     */
    List<String> supplyCompletions(String partial);
}