package me.kubbidev.nexuspowered.util.math.intprovider;

import java.util.Random;

public interface IntProvider {
    int get(Random random);

    int getMin();

    int getMax();
}