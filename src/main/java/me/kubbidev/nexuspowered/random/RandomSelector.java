package me.kubbidev.nexuspowered.random;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * A tool to randomly select elements from collections.
 *
 * @param <E> the element type
 */
public interface RandomSelector<E> {

    /**
     * Creates a uniform selector which picks elements randomly.
     *
     * @param elements the elements to pick from
     * @param <E>      the element type
     * @return the selector instance
     */
    static <E> RandomSelector<E> uniform(Collection<E> elements) {
        return RandomSelectorImpl.uniform(elements);
    }

    /**
     * Creates a weighted selector which picks elements according to the value of their {@link Weighted#getWeight()}.
     *
     * @param elements the elements to pick from
     * @param <E>      the element type
     * @return the selector instance
     */
    static <E extends Weighted> RandomSelector<E> weighted(Collection<E> elements) {
        return weighted(elements, Weighted.WEIGHER);
    }

    /**
     * Creates a weighted selector which picks elements using their weight, according to the weigher function.
     *
     * @param elements the elements to pick from
     * @param <E>      the element type
     * @return the selector instance
     */
    static <E> RandomSelector<E> weighted(Collection<E> elements, Weigher<? super E> weigher) {
        return RandomSelectorImpl.weighted(elements, weigher);
    }

    /**
     * Randomly pick an element.
     *
     * @param random the random instance to use for selection
     * @return an element
     */
    E pick(Random random);

    /**
     * Randomly pick an element.
     *
     * @return an element
     */
    default E pick() {
        return pick(ThreadLocalRandom.current());
    }

    /**
     * Returns an effectively unlimited stream of random elements from this selector.
     *
     * @param random the random instance to use for selection
     * @return a stream of elements
     */
    Stream<E> stream(Random random);

    /**
     * Returns an effectively unlimited stream of random elements from this selector.
     *
     * @return a stream of elements
     */
    default Stream<E> stream() {
        return stream(ThreadLocalRandom.current());
    }
}