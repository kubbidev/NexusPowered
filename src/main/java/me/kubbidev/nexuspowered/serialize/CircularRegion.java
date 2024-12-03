package me.kubbidev.nexuspowered.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import me.kubbidev.nexuspowered.util.math.Mx;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class CircularRegion implements GsonSerializable {
    public static CircularRegion deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("center"));
        Preconditions.checkArgument(object.has("radius"));

        Position center = Position.deserialize(object.get("center"));
        double radius = object.get("radius").getAsDouble();

        return of(center, radius);
    }

    public static CircularRegion of(Position center, double radius) {
        Objects.requireNonNull(center, "center");
        if (radius <= 0) {
            throw new IllegalArgumentException("radius cannot be negative");
        }
        return new CircularRegion(center, radius);
    }

    private final Position center;
    private final double radius;

    private CircularRegion(Position center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    /**
     * Determines if the specified {@link Position} is within the region
     * @param pos target position
     * @return true if the position is in the region
     */
    public boolean inRegion(Position pos) {
        Objects.requireNonNull(pos, "pos");
        return pos.toVector().distanceSquared(this.center.toVector()) < Mx.square(this.radius);
    }

    /**
     * Determines if the specified {@link Block} is within the region
     * @param block target block
     * @return true if the block is in the region
     */
    public boolean inRegion(Block block) {
        Objects.requireNonNull(block, "block");
        return block.getLocation().distanceSquared(this.center.toLocation()) < Mx.square(this.radius);
    }

    /**
     * The center of the region as a {@link Position}
     * @return the center
     */
    public Position getCenter() {
        return this.center;
    }

    /**
     * The radius of the region
     * @return the radius
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * The diameter of the region
     * @return the diameter
     */
    public double getDiameter() {
        return this.radius * 2;
    }

    /**
     * The circumference of the region
     * @return the circumference
     */
    public double getCircumference() {
        return 2 * Math.PI * this.radius;
    }

    /**
     * Get the circumference {@link BlockPosition} of the region
     * @return the {@link BlockPosition}s
     */
    @NotNull
    public Set<BlockPosition> getOuterBlockPositions() {
        Set<BlockPosition> positions = new HashSet<>((int) getCircumference());
        for (int degree = 0; degree < 360; degree++) {
            double radian = Math.toRadians(degree);

            double x = Mx.cos(radian) * this.radius;
            double z = Mx.sin(radian) * this.radius;

            positions.add(this.center.add((int) x, 0, (int) z).floor());
        }
        return Collections.unmodifiableSet(positions);
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .add("center", this.center)
                .add("radius", this.radius)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CircularRegion)) {
            return false;
        }
        CircularRegion other = (CircularRegion) o;
        return Double.compare(this.getRadius(), other.getRadius()) == 0
                && this.getCenter().equals(other.getCenter());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;

        long radius = Double.doubleToLongBits(this.getRadius());
        result = result * PRIME + Long.hashCode(radius);
        result = result * PRIME + this.getCenter().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CircularRegion(center=" + this.getCenter() + ", radius=" + this.getRadius() + ")";
    }
}