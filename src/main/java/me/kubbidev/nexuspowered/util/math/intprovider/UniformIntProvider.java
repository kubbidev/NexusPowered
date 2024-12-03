package me.kubbidev.nexuspowered.util.math.intprovider;

import me.kubbidev.nexuspowered.util.math.Mx;

import java.util.Random;

public class UniformIntProvider implements IntProvider {
    private final int min;
    private final int max;

    private UniformIntProvider(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @param min the minimum value, inclusive
     * @param max the maximum value, inclusive
     */
    public static UniformIntProvider create(int min, int max) {
        return new UniformIntProvider(min, max);
    }

    @Override
    public int get(Random random) {
        return Mx.nextBetween(random, this.min, this.max);
    }

    @Override
    public int getMin() {
        return this.min;
    }

    @Override
    public int getMax() {
        return this.max;
    }

    @Override
    public String toString() {
        return "[" + this.min + "-" + this.max + "]";
    }
}