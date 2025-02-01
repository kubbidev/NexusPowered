package me.kubbidev.nexuspowered.serialize;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.Nexus;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import me.kubbidev.nexuspowered.util.math.Mth;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * An immutable and serializable location object
 */
@Unmodifiable
public final class Position implements GsonSerializable {

    public static Position deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("x"));
        Preconditions.checkArgument(object.has("y"));
        Preconditions.checkArgument(object.has("z"));
        Preconditions.checkArgument(object.has("world"));

        double x = object.get("x").getAsDouble();
        double y = object.get("y").getAsDouble();
        double z = object.get("z").getAsDouble();
        String world = object.get("world").getAsString();

        return of(x, y, z, world);
    }

    public static Position of(double x, double y, double z, String world) {
        Objects.requireNonNull(world, "world");
        return new Position(x, y, z, world);
    }

    public static Position of(double x, double y, double z, World world) {
        Objects.requireNonNull(world, "world");
        return of(x, y, z, world.getName());
    }

    public static Position of(Vector vector, String world) {
        Objects.requireNonNull(vector, "vector");
        Objects.requireNonNull(world, "world");
        return of(
                vector.getX(),
                vector.getY(),
                vector.getZ(), world);
    }

    public static Position of(Location location) {
        Objects.requireNonNull(location, "location");
        return of(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getWorld().getName());
    }

    public static Position of(Block block) {
        Objects.requireNonNull(block, "block");
        return of(block.getLocation());
    }

    /**
     * Copies the given position.
     */
    public static Position of(Position position) {
        Objects.requireNonNull(position, "position");
        return of(
                position.getX(),
                position.getY(),
                position.getZ(),
                position.getWorld());
    }

    public static Position of(BlockPosition blockPos) {
        Objects.requireNonNull(blockPos, "blockPos");
        return of(
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ(),
                blockPos.getWorld());
    }

    /**
     * {@return a new position from {@code vec} with {@code deltaX}, {@code deltaY}, and
     * {@code deltaZ} added to X, Y, Z values, respectively}
     */
    public static Position add(BlockPosition blockPos, double deltaX, double deltaY, double deltaZ) {
        Objects.requireNonNull(blockPos, "blockPos");
        return of(
                blockPos.getX() + deltaX,
                blockPos.getY() + deltaY,
                blockPos.getZ() + deltaZ, blockPos.getWorld());
    }

    /**
     * Creates a position representing the center of the given block position.
     */
    public static Position ofCenter(BlockPosition blockPos) {
        Objects.requireNonNull(blockPos, "blockPos");
        return add(blockPos, 0.5, 0.5, 0.5);
    }

    /**
     * Creates a position representing the bottom center of the given block
     * position.
     *
     * <p>The bottom center of a block position {@code pos} is
     * {@code (pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)}.
     *
     * @see #ofCenter(BlockPosition)
     */
    public static Position ofBottomCenter(BlockPosition blockPos) {
        Objects.requireNonNull(blockPos, "blockPos");
        return add(blockPos, 0.5, 0.0, 0.5);
    }

    /**
     * Creates a position representing the center of the given block position but
     * with the given offset for the Y coordinate.
     *
     * @return a position of {@code (vec.getX() + 0.5, vec.getY() + deltaY,
     * vec.getZ() + 0.5)}
     */
    public static Position ofCenter(BlockPosition blockPos, double deltaY) {
        Objects.requireNonNull(blockPos, "blockPos");
        return add(blockPos, 0.5, deltaY, 0.5);
    }

    /**
     * The zero position (0, 0, 0).
     */
    public static final Function<String, Position> ZERO = world -> Position.of(0, 0, 0, world);

    private final double x;
    private final double y;
    private final double z;
    private final String world;

    @Nullable
    private Location bukkitLocation = null;

    /**
     * Creates a position of the given coordinates.
     */
    private Position(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public String getWorld() {
        return this.world;
    }

    public synchronized Location toLocation() {
        if (this.bukkitLocation == null) {
            this.bukkitLocation = new Location(Nexus.worldNullable(this.world), this.x, this.y, this.z);
        }

        return this.bukkitLocation.clone();
    }

    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    public Block toBlock() {
        return this.toLocation().getBlock();
    }

    public BlockPosition floor() {
        return BlockPosition.of(
                Mth.floor(this.x),
                Mth.floor(this.y),
                Mth.floor(this.z), this.world);
    }

    /**
     * Subtracts this position from the given position.
     *
     * @see #subtract(Position)
     * @return the difference between the given position and this position
     */
    public Position relativize(Position pos) {
        return Position.of(pos.x - this.x, pos.y - this.y, pos.z - this.z, pos.world);
    }

    /**
     * Normalizes this position.
     *
     * <p>Normalized position is a position with the same direction but with
     * length 1. Each coordinate of normalized position has value between 0
     * and 1.
     *
     * @return the normalized position of this position
     */
    public Position normalize() {
        double d = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return d < 1.0E-5F ? ZERO.apply(this.world) : Position.of(this.x / d, this.y / d, this.z / d, this.world);
    }

    /**
     * Returns the dot product of this position and the given position.
     */
    public double dotProduct(Position pos) {
        return this.x * pos.x + this.y * pos.y + this.z * pos.z;
    }

    /**
     * Returns the cross product of this position and the given position.
     */
    public Position crossProduct(Position pos) {
        return Position.of(
                this.y * pos.z - this.z * pos.y,
                this.z * pos.x - this.x * pos.z,
                this.x * pos.y - this.y * pos.x, this.world);
    }

    public Position getRelative(BlockFace face) {
        Objects.requireNonNull(face, "face");
        return Position.of(
                this.x + face.getModX(),
                this.y + face.getModY(),
                this.z + face.getModZ(), this.world);
    }

    public Position getRelative(BlockFace face, double distance) {
        Objects.requireNonNull(face, "face");
        return Position.of(
                this.x + (face.getModX() * distance),
                this.y + (face.getModY() * distance),
                this.z + (face.getModZ() * distance), this.world);
    }

    public Position add(Vector vector) {
        return this.add(vector.getX(), vector.getY(), vector.getZ());
    }

    public Position add(Location location) {
        return this.add(location.getX(), location.getY(), location.getZ());
    }

    public Position add(Position position) {
        return this.add(position.getX(), position.getY(), position.getZ());
    }

    public Position add(BlockPosition position) {
        return this.add(position.getX(), position.getY(), position.getZ());
    }

    public Position add(double value) {
        return this.add(value, value, value);
    }

    /**
     * Returns the sum of this position and the given position.
     *
     * @see #add(Position)
     */
    public Position add(double x, double y, double z) {
        return Position.of(this.x + x, this.y + y, this.z + z, this.world);
    }

    public Position subtract(Vector vector) {
        return this.subtract(vector.getX(), vector.getY(), vector.getZ());
    }

    public Position subtract(Location location) {
        return this.subtract(location.getX(), location.getY(), location.getZ());
    }

    public Position subtract(Position position) {
        return this.subtract(position.getX(), position.getY(), position.getZ());
    }

    public Position subtract(BlockPosition position) {
        return this.subtract(position.getX(), position.getY(), position.getZ());
    }

    public Position subtract(double value) {
        return this.subtract(value, value, value);
    }

    /**
     * Subtracts the given position from this position.
     *
     * @see #relativize(Position)
     * @return the difference between this position and the given position
     */
    public Position subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    /**
     * Checks if the distance between this vector and the given position is
     * less than {@code radius}.
     */
    public boolean isInRange(Position pos, double radius) {
        return this.squaredDistanceTo(pos.x, pos.y, pos.z) < Mth.square(radius);
    }

    /**
     * Returns the distance between this vector and the given vector.
     *
     * @see #squaredDistanceTo(Position)
     */
    public double distanceTo(Position pos) {
        double d = pos.x - this.x;
        double e = pos.y - this.y;
        double f = pos.z - this.z;
        return Math.sqrt(d * d + e * e + f * f);
    }

    /**
     * Returns the squared distance between this vector and the given vector.
     *
     * <p>Can be used for fast comparison between distances.
     *
     * @see #squaredDistanceTo(double, double, double)
     * @see #distanceTo(Position)
     */
    public double squaredDistanceTo(Position pos) {
        double d = pos.x - this.x;
        double e = pos.y - this.y;
        double f = pos.z - this.z;
        return d * d + e * e + f * f;
    }

    /**
     * Returns the squared distance between this position and the given position.
     *
     * <p>Can be used for fast comparison between distances.
     *
     * @see #squaredDistanceTo(Position)
     * @see #distanceTo(Position)
     */
    public double squaredDistanceTo(double x, double y, double z) {
        double d = x - this.x;
        double e = y - this.y;
        double f = z - this.z;
        return d * d + e * e + f * f;
    }

    public boolean isWithinRangeOf(Position pos, double horizontalRange, double verticalRange) {
        double d = pos.x - this.x;
        double e = pos.y - this.y;
        double f = pos.z - this.z;
        return Mth.squaredHypot(d, f) < Mth.square(horizontalRange) && Math.abs(e) < verticalRange;
    }

    /**
     * Creates a position with the same length but with the opposite direction.
     */
    public Position negate() {
        return this.multiply(-1.0);
    }

    public Position multiply(Vector vector) {
        return this.multiply(vector.getX(), vector.getY(), vector.getZ());
    }

    public Position multiply(Location location) {
        return this.multiply(location.getX(), location.getY(), location.getZ());
    }

    public Position multiply(Position position) {
        return this.multiply(position.getX(), position.getY(), position.getZ());
    }

    public Position multiply(BlockPosition position) {
        return this.multiply(position.getX(), position.getY(), position.getZ());
    }

    public Position multiply(double value) {
        return this.multiply(value, value, value);
    }

    /**
     * Returns a position whose coordinates are the product of each pair of
     * coordinates in this position and the given position.
     *
     * @see #multiply(double)
     */
    public Position multiply(double x, double y, double z) {
        return Position.of(this.x * x, this.y * y, this.z * z, this.world);
    }

    /**
     * {@return a position with each value added by {@code random.nextFloat() - 0.5f) * multiplier}}
     */
    public Position addRandom(Random random, double multiplier) {
        return this.add(
                (random.nextFloat() - 0.5) * multiplier,
                (random.nextFloat() - 0.5) * multiplier,
                (random.nextFloat() - 0.5) * multiplier
        );
    }

    /**
     * {@return the length of this position}
     *
     * <p>The length of a position is equivalent to the distance between that
     * position and the {@linkplain #ZERO} position.
     *
     * @see #lengthSquared()
     */
    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    /**
     * {@return the squared length of this position}
     *
     * <p>Can be used for fast comparison between lengths.
     *
     * @see #length()
     */
    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    /**
     * {@return the horizontal length of this position}
     *
     * <p>This length is same as the length of a 2-position with the {@link #x} and
     * {@link #z} components of this position, or the euclidean distance between
     * {@code (x, z)} and the origin.
     *
     * @see #horizontalLengthSquared()
     */
    public double horizontalLength() {
        return Math.sqrt(this.horizontalLengthSquared());
    }

    /**
     * {@return the squared horizontal length of this position}
     *
     * <p>Can be used for fast comparison between horizontal lengths.
     *
     * @see #horizontalLength()
     */
    public double horizontalLengthSquared() {
        return this.x * this.x + this.z * this.z;
    }

    /**
     * Performs linear interpolation from this position to the given position.
     *
     * @param delta the interpolation coefficient in the range between 0 and 1
     * @param to the position to interpolate to
     */
    public Position lerp(Position to, double delta) {
        return Position.of(
                Mth.lerp(delta, this.x, to.x),
                Mth.lerp(delta, this.y, to.y),
                Mth.lerp(delta, this.z, to.z), this.world);
    }

    /**
     * Rotates this position by the given angle counterclockwise around the X axis.
     *
     * @param angle the angle in radians
     */
    public Position rotateX(float angle) {
        double f = Mth.cos(angle);
        double g = Mth.sin(angle);
        double e = this.y * f + this.z * g;
        double h = this.z * f - this.y * g;
        return Position.of(this.x, e, h, this.world);
    }

    /**
     * Rotates this position by the given angle counterclockwise around the Y axis.
     *
     * @param angle the angle in radians
     */
    public Position rotateY(float angle) {
        double f = Mth.cos(angle);
        double g = Mth.sin(angle);
        double d = this.x * f + this.z * g;
        double h = this.z * f - this.x * g;
        return Position.of(d, this.y, h, this.world);
    }

    /**
     * Rotates this position by the given angle counterclockwise around the Z axis.
     *
     * @param angle the angle in radians
     */
    public Position rotateZ(float angle) {
        double f = Mth.cos(angle);
        double g = Mth.sin(angle);
        double d = this.x * f + this.y * g;
        double e = this.y * f - this.x * g;
        return Position.of(d, e, this.z, this.world);
    }

    public Region regionWith(Position other) {
        Objects.requireNonNull(other, "other");
        return Region.of(this, other);
    }

    public Point withDirection(Direction direction) {
        return Point.of(this, direction);
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("x", this.x)
                .add("y", this.y)
                .add("z", this.z)
                .add("world", this.world)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position other = (Position) o;
        return this.getWorld().equals(other.getWorld())
                && Double.compare(this.getX(), other.getX()) == 0
                && Double.compare(this.getY(), other.getY()) == 0
                && Double.compare(this.getZ(), other.getZ()) == 0;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;

        long x = Double.doubleToLongBits(this.getX());
        long y = Double.doubleToLongBits(this.getY());
        long z = Double.doubleToLongBits(this.getZ());

        result = result * PRIME + Long.hashCode(x);
        result = result * PRIME + Long.hashCode(y);
        result = result * PRIME + Long.hashCode(z);
        result = result * PRIME + this.getWorld().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x", this.getX())
                .add("y", this.getY())
                .add("z", this.getZ())
                .add("world", this.getWorld())
                .toString();
    }

    /**
     * {@return the coordinates joined with a colon and a space}
     */
    public String toShortString() {
        return this.getX() + ", " + this.getY() + ", " + this.getZ();
    }
}