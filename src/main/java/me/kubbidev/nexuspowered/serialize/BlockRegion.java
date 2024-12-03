package me.kubbidev.nexuspowered.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An immutable and serializable block region object
 */
public final class BlockRegion implements GsonSerializable {
    public static BlockRegion deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("min"));
        Preconditions.checkArgument(object.has("max"));

        BlockPosition a = BlockPosition.deserialize(object.get("min"));
        BlockPosition b = BlockPosition.deserialize(object.get("max"));

        return of(a, b);
    }

    public static BlockRegion of(BlockPosition a, BlockPosition b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");

        if (!a.getWorld().equals(b.getWorld())) {
            throw new IllegalArgumentException("positions are in different worlds");
        }

        return new BlockRegion(a, b);
    }

    private final BlockPosition min;
    private final BlockPosition max;

    private final int width;
    private final int height;
    private final int length;

    private BlockRegion(BlockPosition a, BlockPosition b) {
        this.min = BlockPosition.of(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ()), a.getWorld());

        this.max = BlockPosition.of(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ()), a.getWorld());

        this.width = this.max.getX() - this.min.getX();
        this.height = this.max.getY() - this.min.getY();
        this.length = this.max.getZ() - this.min.getZ();
    }

    public boolean inRegion(BlockPosition pos) {
        Objects.requireNonNull(pos, "pos");
        return pos.getWorld().equals(this.min.getWorld())
                && inRegion(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean inRegion(Block block) {
        Objects.requireNonNull(block, "block");
        return block.getWorld().getName().equals(this.min.getWorld())
                && inRegion(block.getX(), block.getY(), block.getZ());
    }

    public boolean inRegion(int x, int y, int z) {
        int minX = this.min.getX();
        int minY = this.min.getY();
        int minZ = this.min.getZ();
        int maxX = this.max.getX();
        int maxY = this.max.getY();
        int maxZ = this.max.getZ();
        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    public BlockPosition getMin() {
        return this.min;
    }

    public BlockPosition getMax() {
        return this.max;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLength() {
        return this.length;
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("min", this.min)
                .add("max", this.max)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BlockRegion)) {
            return false;
        }
        BlockRegion other = (BlockRegion) o;
        return this.getMin().equals(other.getMin())
                && this.getMax().equals(other.getMax());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getMin().hashCode();
        result = result * PRIME + this.getMax().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BlockRegion(min=" + this.getMin() + ", max=" + this.getMax() + ")";
    }

}