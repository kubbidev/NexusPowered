package me.kubbidev.nexuspowered.util.math;

import java.util.Random;
import java.util.UUID;

/**
 * Utility for quickly performing Maths calculations.
 */
public final class Mth {

    public static final  long    UNSIGNED_32BIT_MASK              = 0xFFFFFFFFL;
    private static final long    HALF_PI_RADIANS_SINE_TABLE_INDEX = 0x4000L;
    private static final long    UUID_VERSION_CLEAR_MASK          = 0xFFFFFFFFFFFF0FFFL;
    private static final long    UUID_VARIANT_CLEAR_MASK          = 0x3FFFFFFFFFFFFFFFL;
    private static final int     SIN_BITS                         = 12;
    private static final int     SIN_MASK                         = ~(-1 << SIN_BITS);
    private static final int     SIN_COUNT                        = SIN_MASK + 1;
    public static final  float   EPSILON                          = 1.0E-06f;
    private static final float   RAD_FULL                         = (float) (Math.PI * 2);
    private static final float   DEG_FULL                         = (float) (360.0);
    private static final float   RAD_TO_INDEX                     = SIN_COUNT / RAD_FULL;
    private static final float   DEG_TO_INDEX                     = SIN_COUNT / DEG_FULL;
    private static final float   DISTANCE                         = 0.5f;
    private static final float[] SIN                              = new float[SIN_COUNT];
    private static final float[] COS                              = new float[SIN_COUNT];

    static {
        for (int i = 0; i < SIN_COUNT; i++) {
            SIN[i] = (float) Math.sin((i + DISTANCE) / SIN_COUNT * RAD_FULL);
            COS[i] = (float) Math.cos((i + DISTANCE) / SIN_COUNT * RAD_FULL);
        }

        for (int i = 0; i < 360; i += 90) { // Override
            SIN[(int) (i * DEG_TO_INDEX) & SIN_MASK] = (float) Math.sin(i * Math.PI / 180);
            COS[(int) (i * DEG_TO_INDEX) & SIN_MASK] = (float) Math.cos(i * Math.PI / 180);
        }
    }

    private Mth() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Returns the trigonometric sine of a radians.
     *
     * @param rad the radians to get a sine of
     * @return the sine of a radians
     */
    public static float sin(double rad) {
        return SIN[(int) (rad * RAD_TO_INDEX) & SIN_MASK];
    }

    /**
     * Returns the trigonometric cosine of a radians.
     *
     * @param rad the radians to get a cosine of
     * @return the cosine of a radians
     */
    public static float cos(double rad) {
        return COS[(int) (rad * RAD_TO_INDEX) & SIN_MASK];
    }

    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    public static int floor(float value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static int floor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static long lfloor(double value) {
        long l = (long) value;
        return value < l ? l - 1L : l;
    }

    public static int ceil(float value) {
        int i = (int) value;
        return value > i ? i + 1 : i;
    }

    public static int ceil(double value) {
        int i = (int) value;
        return value > i ? i + 1 : i;
    }

    public static long lceil(double value) {
        long l = (long) value;
        return value > l ? l + 1L : l;
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * {@return a random, uniformly distributed integer value in {@code [min, max]}} If the range is empty (i.e.
     * {@code max < min}), it returns {@code min}.
     *
     * @param max the maximum value, inclusive
     * @param min the minimum value, inclusive
     */
    public static int nextInt(Random random, int min, int max) {
        return min >= max ? min : random.nextInt(max - min + 1) + min;
    }

    public static float nextFloat(Random random, float min, float max) {
        return min >= max ? min : random.nextFloat() * (max - min) + min;
    }

    public static double nextDouble(Random random, double min, double max) {
        return min >= max ? min : random.nextDouble() * (max - min) + min;
    }

    public static boolean approximatelyEquals(float a, float b) {
        return Math.abs(b - a) < EPSILON;
    }

    public static boolean approximatelyEquals(double a, double b) {
        return Math.abs(b - a) < EPSILON;
    }

    public static boolean isMultipleOf(int a, int b) {
        return a % b == 0;
    }

    public static byte packDegrees(float degrees) {
        return (byte) floor(degrees * 256f / 360f);
    }

    /**
     * Wraps an angle in degrees to the interval {@code [-180, 180)}.
     */
    public static float wrapDegrees(float value) {
        float f = value % 360f;
        if (f >= 180f) {
            f -= 360f;
        }

        if (f < -180f) {
            f += 360f;
        }

        return f;
    }

    /**
     * Wraps an angle in degrees to the interval {@code [-180, 180)}.
     */
    public static double wrapDegrees(double degrees) {
        double d = degrees % 360;
        if (d >= 180) {
            d -= 360;
        }

        if (d < -180) {
            d += 360;
        }

        return d;
    }

    public static float subtractAngles(float start, float end) {
        return wrapDegrees(end - start);
    }

    /**
     * Clamps {@code value}, as an angle, between {@code mean - delta} and {@code mean + delta} degrees.
     *
     * @param delta the maximum difference allowed from the mean, must not be negative
     * @param mean  the mean value of the clamp angle range
     * @param value the value to clamp
     * @return the clamped {@code value}
     */
    public static float clampAngle(float value, float mean, float delta) {
        float f = subtractAngles(value, mean);
        float g = clamp(f, -delta, delta);
        return mean - g;
    }

    public static float fractionalPart(float value) {
        return value - floor(value);
    }

    public static double fractionalPart(double value) {
        return value - lfloor(value);
    }

    public static UUID randomUuid(Random random) {
        long l = random.nextLong() & UUID_VERSION_CLEAR_MASK | HALF_PI_RADIANS_SINE_TABLE_INDEX;
        long m = random.nextLong() & UUID_VARIANT_CLEAR_MASK | Long.MIN_VALUE;
        return new UUID(l, m);
    }

    public static int lerp(float delta, int start, int end) {
        return start + floor(delta * (end - start));
    }

    /**
     * {@return linear interpolation of {@code delta} between {@code start} and {@code end}, except that for any
     * positive {@code delta} the value is positive}
     *
     * <p>Like {@link #lerp(float, int, int)}, {@code lerpPositive(0, 0, 10)} returns {@code 0}.
     * However, if the delta is {@code 0.01f}, the {@code lerp} method would return {@code 0} since {@code 0.01 * 10}
     * floored is {@code 0}. This method returns {@code 1} in this situation.
     *
     * @apiNote This is used to calculate redstone comparator output and boss bar percentage.
     * @see #lerp(float, int, int)
     */
    public static int lerpPositive(float delta, int start, int end) {
        int i = end - start;
        return start + floor(delta * (i - 1)) + (delta > 0.0F ? 1 : 0);
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static int sign(double value) {
        if (value == 0.0) {
            return 0;
        } else {
            return value > 0.0 ? 1 : -1;
        }
    }

    public static float lerpAngleDegrees(float delta, float start, float end) {
        return start + delta * wrapDegrees(end - start);
    }

    public static double lerpAngleDegrees(double delta, double start, double end) {
        return start + delta * wrapDegrees(end - start);
    }

    public static float square(float n) {
        return n * n;
    }

    public static double square(double n) {
        return n * n;
    }

    public static int square(int n) {
        return n * n;
    }

    public static long square(long n) {
        return n * n;
    }

    public static int ceilDiv(int a, int b) {
        return -Math.floorDiv(-a, b);
    }

    /**
     * {@return a random, uniformly distributed integer value in {@code [min, max]}}
     *
     * @param max the maximum value, inclusive
     * @param min the minimum value, inclusive
     * @throws IllegalArgumentException if the range is empty (i.e. {@code max < min})
     */
    public static int nextBetween(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static float nextBetween(Random random, float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public static double squaredHypot(double a, double b) {
        return a * a + b * b;
    }

    public static double squaredMagnitude(double a, double b, double c) {
        return a * a + b * b + c * c;
    }

    public static double magnitude(double a, double b, double c) {
        return Math.sqrt(squaredMagnitude(a, b, c));
    }

    public static float squaredMagnitude(float a, float b, float c) {
        return a * a + b * b + c * c;
    }
}