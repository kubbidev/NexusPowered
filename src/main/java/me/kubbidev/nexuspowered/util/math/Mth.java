package me.kubbidev.nexuspowered.util.math;

import java.util.Random;

/**
 * Utility for quickly performing Maths calculations.
 */
public final class Mth {
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{
            0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8,
            31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
    };

    private static final int SIN_BITS;
    private static final int SIN_MASK;
    private static final int SIN_COUNT;

    private static final double radToIndex;
    private static final double degToIndex;

    private static final double DISTANCE = 0.5;
    private static final double[] sin;
    private static final double[] cos;

    static {
        SIN_BITS = 12;
        SIN_MASK = ~(-1 << SIN_BITS);
        SIN_COUNT = SIN_MASK + 1;

        radToIndex = SIN_COUNT / (Math.PI * 2);
        degToIndex = SIN_COUNT / 360.0;

        sin = new double[SIN_COUNT];
        cos = new double[SIN_COUNT];

        for (int i = 0; i < SIN_COUNT; i++) {
            sin[i] = Math.sin((i + DISTANCE) / SIN_COUNT * (Math.PI * 2));
            cos[i] = Math.cos((i + DISTANCE) / SIN_COUNT * (Math.PI * 2));
        }

        for (int i = 0; i < 360; i += 90) {
            sin[(int) (i * degToIndex) & SIN_MASK] = Math.sin(i * Math.PI / 180.0);
            cos[(int) (i * degToIndex) & SIN_MASK] = Math.cos(i * Math.PI / 180.0);
        }
    }

    public static float normalizeYaw(float yaw) {
        yaw %= 360;
        if (yaw < -180.0f) {
            yaw += 360.0f;
        } else if (yaw > 180.0f) {
            yaw -= 360.0f;
        }
        return yaw;
    }

    /**
     * Returns the trigonometric sine of a radians.
     *
     * @param rad the radians to get a sine of
     * @return the sine of a radians
     */
    public static double sin(double rad) {
        return sin[(int) (rad * radToIndex) & SIN_MASK];
    }

    /**
     * Returns the trigonometric cosine of a radians.
     *
     * @param rad the radians to get a cosine of
     * @return the cosine of a radians
     */
    public static double cos(double rad) {
        return cos[(int) (rad * radToIndex) & SIN_MASK];
    }

    public static float sqrt(double a) {
        return (float) Math.sqrt(a);
    }

    public static int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    public static int ceil(double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    public static int square(int n) {
        return n * n;
    }

    public static double square(double n) {
        return n * n;
    }

    public static int squaredHypot(int a, int b) {
        return a * a + b * b;
    }

    public static double squaredHypot(double a, double b) {
        return a * a + b * b;
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }

    /**
     * {@return a random, uniformly distributed integer value in {@code
     * [min, max]}}
     *
     * @throws IllegalArgumentException if the range is empty (i.e. {@code
     * max < min})
     *
     * @param max the maximum value, inclusive
     * @param min the minimum value, inclusive
     */
    public static int nextBetween(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static int cantor(int x, int y, int z) {
        return cantor(x, cantor(y, z));
    }

    public static int cantor(int a, int b) {
        int ca = a >= 0 ? 2 * a : -2 * a - 1;
        int cb = b >= 0 ? 2 * b : -2 * b - 1;
        return (ca + cb + 1) * (ca + cb) / 2 + cb;
    }

    public static int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * {@return ceil(log < sub > 2 < / sub > ( { @ code value }))}
     *
     * <p>The vanilla implementation uses the de Bruijn sequence.
     *
     * @see Integer#numberOfLeadingZeros(int)
     *
     * @param value the input value
     */
    public static int ceilLog2(int value) {
        value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) value * 125613361L >> 27) & 31];
    }

    /**
     * {@return floor(log < sub > 2 < / sub > ( { @ code value }))}
     *
     * <p>The vanilla implementation uses the de Bruijn sequence.
     *
     * @see Integer#numberOfLeadingZeros(int)
     *
     * @param value the input value
     */
    public static int floorLog2(int value) {
        return ceilLog2(value) - (isPowerOfTwo(value) ? 0 : 1);
    }

    private Mth() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}