package me.kubbidev.nexuspowered.random;

/**
 * Represents an object which has a weight.
 */
public interface Weighted {

    /**
     * An instance of {@link Weigher} which uses the {@link #getWeight()} method
     * to determine weight.
     */
    Weigher<? super Weighted> WEIGHER = Weighted::getWeight;

    /**
     * Gets the weight of this entry.
     *
     * @return The weight
     */
    double getWeight();

}
