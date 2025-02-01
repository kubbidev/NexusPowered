package me.kubbidev.nexuspowered.serialize;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
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
 * An immutable and serializable block location object
 */
@Unmodifiable
public final class BlockPosition implements GsonSerializable {
    public static final int SIZE_BITS_XZ = 1 + Mth.floorLog2(Mth.smallestEncompassingPowerOfTwo(30000000));
    public static final int SIZE_BITS_Y = 64 - 2 * SIZE_BITS_XZ;

    private static final long BITS_X = (1L << SIZE_BITS_XZ) - 1L;
    private static final long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
    private static final long BITS_Z = (1L << SIZE_BITS_XZ) - 1L;

    private static final int BIT_SHIFT_Z = SIZE_BITS_Y;
    private static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_XZ;

    public static BlockPosition deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("x"));
        Preconditions.checkArgument(object.has("y"));
        Preconditions.checkArgument(object.has("z"));
        Preconditions.checkArgument(object.has("world"));

        int x = object.get("x").getAsInt();
        int y = object.get("y").getAsInt();
        int z = object.get("z").getAsInt();
        String world = object.get("world").getAsString();

        return of(x, y, z, world);
    }

    public static BlockPosition ofFloored(double x, double y, double z, String world) {
        return of(Mth.floor(x), Mth.floor(y), Mth.floor(z), world);
    }

    public static BlockPosition ofFloored(Position position) {
        return Objects.requireNonNull(position, "position").floor();
    }

    public static BlockPosition of(int x, int y, int z, String world) {
        Objects.requireNonNull(world, "world");
        return new BlockPosition(x, y, z, world);
    }

    public static BlockPosition of(int x, int y, int z, World world) {
        Objects.requireNonNull(world, "world");
        return of(x, y, z, world.getName());
    }

    public static BlockPosition of(Vector vector, String world) {
        Objects.requireNonNull(world, "world");
        return ofFloored(
                vector.getX(),
                vector.getY(),
                vector.getZ(), world);
    }

    public static BlockPosition of(Location location) {
        Objects.requireNonNull(location, "location");
        return ofFloored(
                location.getX(),
                location.getY(),
                location.getZ(), location.getWorld().getName());
    }

    public static BlockPosition of(Block block) {
        Objects.requireNonNull(block, "block");
        return of(block.getLocation());
    }

    /**
     * Copies the given position.
     */
    public static BlockPosition of(BlockPosition blockPos) {
        Objects.requireNonNull(blockPos, "blockPos");
        return of(
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ(),
                blockPos.getWorld());
    }

    public static BlockPosition min(BlockPosition a, BlockPosition b) {
        return of(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ()), a.getWorld());
    }

    public static BlockPosition max(BlockPosition a, BlockPosition b) {
        return of(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ()), a.getWorld());
    }

    public static int unpackLongX(long packedPos) {
        return (int) (packedPos << 64 - BIT_SHIFT_X - SIZE_BITS_XZ >> 64 - SIZE_BITS_XZ);
    }

    public static int unpackLongY(long packedPos) {
        return (int) (packedPos << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
    }

    public static int unpackLongZ(long packedPos) {
        return (int) (packedPos << 64 - BIT_SHIFT_Z - SIZE_BITS_XZ >> 64 - SIZE_BITS_XZ);
    }

    public static BlockPosition fromLong(long packedPos, String world) {
        return of(
                unpackLongX(packedPos),
                unpackLongY(packedPos),
                unpackLongZ(packedPos), world
        );
    }

    public static long asLong(int x, int y, int z) {
        long l = 0L;
        l |= ((long) x & BITS_X) << BIT_SHIFT_X;
        l |= ((long) y & BITS_Y);
        return l | ((long) z & BITS_Z) << BIT_SHIFT_Z;
    }

    /**
     * The block position which x, y, and z values are all zero.
     */
    public static final Function<String, BlockPosition> ZERO = world -> new BlockPosition(0, 0, 0, world);

    private final int x;
    private final int y;
    private final int z;
    private final String world;

    @Nullable
    private Location bukkitLocation = null;

    /**
     * Creates a block position of the given coordinates.
     */
    private BlockPosition(int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
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

    public Position toPosition() {
        return Position.of(this);
    }

    public Position toPositionCenter() {
        return Position.ofCenter(this);
    }

    public Position toPositionBottomCenter() {
        return Position.ofBottomCenter(this);
    }

    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }

    public ChunkPosition toChunk() {
        return ChunkPosition.of(this.x >> 4, this.z >> 4, this.world);
    }

    /**
     * Subtracts this block position from the given block position.
     *
     * @see #subtract(BlockPosition)
     * @return the difference between the given block position and this block position
     */
    public BlockPosition relativize(BlockPosition pos) {
        return BlockPosition.of(pos.x - this.x, pos.y - this.y, pos.z - this.z, pos.world);
    }

    /**
     * Returns the dot product of this block position and the given block position.
     */
    public int dotProduct(BlockPosition pos) {
        return this.x * pos.x + this.y * pos.y + this.z * pos.z;
    }

    /**
     * Returns the cross product of this block position and the given block position.
     */
    public BlockPosition crossProduct(BlockPosition pos) {
        return BlockPosition.of(
                this.y * pos.z - this.z * pos.y,
                this.z * pos.x - this.x * pos.z,
                this.x * pos.y - this.y * pos.x, this.world);
    }

    public BlockPosition withY(int y) {
        return BlockPosition.of(this.x, y, this.y, this.world);
    }

    public boolean contains(Position position) {
        return this.equals(position.floor());
    }

    public BlockPosition getRelative(BlockFace face) {
        Objects.requireNonNull(face, "face");
        return BlockPosition.of(
                this.x + face.getModX(),
                this.y + face.getModY(),
                this.z + face.getModZ(), this.world);
    }

    public BlockPosition getRelative(BlockFace face, int distance) {
        Objects.requireNonNull(face, "face");
        return BlockPosition.of(
                this.x + (face.getModX() * distance),
                this.y + (face.getModY() * distance),
                this.z + (face.getModZ() * distance), this.world);
    }

    public BlockPosition add(Vector vector) {
        return this.add(
                Mth.floor(vector.getX()),
                Mth.floor(vector.getY()),
                Mth.floor(vector.getZ()));
    }

    public BlockPosition add(Location location) {
        return this.add(
                Mth.floor(location.getX()),
                Mth.floor(location.getY()),
                Mth.floor(location.getZ()));
    }

    public BlockPosition add(Position position) {
        return this.add(
                Mth.floor(position.getX()),
                Mth.floor(position.getY()),
                Mth.floor(position.getZ()));
    }

    public BlockPosition add(BlockPosition position) {
        return this.add(position.getX(), position.getY(), position.getZ());
    }

    public BlockPosition add(int value) {
        return this.add(value, value, value);
    }

    /**
     * Returns the sum of this block position and the given position.
     *
     * @see #add(Position)
     */
    public BlockPosition add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : BlockPosition.of(this.x + x, this.y + y, this.z + z, this.world);
    }

    public BlockPosition subtract(Vector vector) {
        return this.subtract(
                Mth.floor(vector.getX()),
                Mth.floor(vector.getY()),
                Mth.floor(vector.getZ()));
    }

    public BlockPosition subtract(Location location) {
        return this.subtract(
                Mth.floor(location.getX()),
                Mth.floor(location.getY()),
                Mth.floor(location.getZ()));
    }

    public BlockPosition subtract(Position position) {
        return this.subtract(
                Mth.floor(position.getX()),
                Mth.floor(position.getY()),
                Mth.floor(position.getZ()));
    }

    public BlockPosition subtract(BlockPosition position) {
        return this.subtract(position.getX(), position.getY(), position.getZ());
    }

    public BlockPosition subtract(int value) {
        return this.subtract(value, value, value);
    }

    /**
     * Subtracts the given position from this block position.
     *
     * @see #relativize(BlockPosition)
     * @return the difference between this block position and the given position
     */
    public BlockPosition subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }

    /**
     * Creates a block position with the same length but with the opposite direction.
     */
    public BlockPosition negate() {
        return this.multiply(-1);
    }

    public BlockPosition multiply(Vector vector) {
        return this.multiply(
                Mth.floor(vector.getX()),
                Mth.floor(vector.getY()),
                Mth.floor(vector.getZ()));
    }

    public BlockPosition multiply(Location location) {
        return this.multiply(
                Mth.floor(location.getX()),
                Mth.floor(location.getY()),
                Mth.floor(location.getZ()));
    }

    public BlockPosition multiply(Position position) {
        return this.multiply(
                Mth.floor(position.getX()),
                Mth.floor(position.getY()),
                Mth.floor(position.getZ()));
    }

    public BlockPosition multiply(BlockPosition position) {
        return this.multiply(position.getX(), position.getY(), position.getZ());
    }

    public BlockPosition multiply(int value) {
        return this.multiply(value, value, value);
    }

    /**
     * Returns a position whose coordinates are the product of each pair of
     * coordinates in this position and the given position.
     *
     * @see #multiply(int)
     */
    public BlockPosition multiply(int x, int y, int z) {
        return BlockPosition.of(this.x * x, this.y * y, this.z * z, this.world);
    }

    public BlockRegion regionWith(BlockPosition other) {
        Objects.requireNonNull(other, "other");
        return BlockRegion.of(this, other);
    }

    /**
     * Iterates through {@code count} random block positions in a given range around the given position.
     *
     * <p>The iterator yields positions in no specific order. The same position
     * may be returned multiple times by the iterator.
     *
     * @param range the maximum distance from the given pos in any axis
     * @param around the {@link BlockPosition} to iterate around
     * @param count the number of positions to iterate
     */
    public static Iterable<BlockPosition> iterateRandomly(Random random, int count, BlockPosition around, int range) {
        return iterateRandomly(
                random, count, around.getWorld(),
                around.getX() - range,
                around.getY() - range,
                around.getZ() - range,
                around.getX() + range,
                around.getY() + range,
                around.getZ() + range
        );
    }

    /**
     * Iterates through {@code count} random block positions in the given area.
     *
     * <p>The iterator yields positions in no specific order. The same position
     * may be returned multiple times by the iterator.
     *
     * @param count the number of positions to iterate
     * @param minX the minimum x value for returned positions
     * @param minY the minimum y value for returned positions
     * @param minZ the minimum z value for returned positions
     * @param maxX the maximum x value for returned positions
     * @param maxY the maximum y value for returned positions
     * @param maxZ the maximum z value for returned positions
     */
    public static Iterable<BlockPosition> iterateRandomly(Random random, int count, String world,
                                                          int minX, int minY, int minZ,
                                                          int maxX, int maxY, int maxZ) {
        int i = maxX - minX + 1;
        int j = maxY - minY + 1;
        int k = maxZ - minZ + 1;
        return () -> new AbstractIterator<BlockPosition>() {
            int remaining = count;

            @Override
            protected BlockPosition computeNext() {
                if (this.remaining <= 0) {
                    return this.endOfData();
                } else {
                    BlockPosition blockPos = BlockPosition.of(
                            minX + random.nextInt(i),
                            minY + random.nextInt(j),
                            minZ + random.nextInt(k), world
                    );
                    this.remaining--;
                    return blockPos;
                }
            }
        };
    }

    /**
     * Iterates block positions around the {@code center} in a square of
     * ({@code 2 * radius + 1}) by ({@code 2 * radius + 1}). The dataRegion
     * are iterated in a (square) spiral around the center.
     *
     * <p>The first block returned is the center, then the iterator moves
     * a block towards the first direction, followed by moving along
     * the second direction.
     *
     * @throws IllegalStateException when the 2 directions lie on the same axis
     *
     * @param firstFace the direction the iterator moves first
     * @param secondFace the direction the iterator moves after the first
     * @param center the center of iteration
     * @param radius the maximum chebychev distance
     */
    public static Iterable<BlockPosition> iterateInSquare(BlockPosition center, int radius, BlockFace firstFace, BlockFace secondFace) {
        assert firstFace != secondFace.getOppositeFace() && firstFace.getOppositeFace() != secondFace
                : "The two directions cannot be on the same axis";

        return () -> new AbstractIterator<BlockPosition>() {
            private final BlockFace[] directions = new BlockFace[]{
                    firstFace, secondFace, firstFace.getOppositeFace(), secondFace.getOppositeFace()};

            private BlockPosition pos = center.getRelative(secondFace);
            private final int maxDirectionChanges = 4 * radius;
            private int directionChangeCount = -1;
            private int maxSteps;
            private int steps;
            private int currentX = this.pos.getX();
            private int currentY = this.pos.getY();
            private int currentZ = this.pos.getZ();

            @Override
            protected BlockPosition computeNext() {
                BlockFace face = this.directions[(this.directionChangeCount + 4) % 4];
                this.pos = BlockPosition.of(
                        this.currentX + face.getModX(),
                        this.currentY + face.getModY(),
                        this.currentZ + face.getModZ(), center.getWorld());

                this.currentX = this.pos.getX();
                this.currentY = this.pos.getY();
                this.currentZ = this.pos.getZ();
                if (this.steps >= this.maxSteps) {
                    if (this.directionChangeCount >= this.maxDirectionChanges) {
                        return this.endOfData();
                    }

                    this.directionChangeCount++;
                    this.steps = 0;
                    this.maxSteps = this.directionChangeCount / 2 + 1;
                }

                this.steps++;
                return this.pos;
            }
        };
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
        if (!(o instanceof BlockPosition)) {
            return false;
        }
        BlockPosition other = (BlockPosition) o;
        return this.getWorld().equals(other.getWorld())
                && this.getX() == other.getX()
                && this.getY() == other.getY()
                && this.getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getX();
        result = result * PRIME + this.getY();
        result = result * PRIME + this.getZ();
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