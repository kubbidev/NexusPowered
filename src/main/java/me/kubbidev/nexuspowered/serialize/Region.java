package me.kubbidev.nexuspowered.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An immutable and serializable region object
 */
public final class Region implements GsonSerializable {
    public static Region deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("min"));
        Preconditions.checkArgument(object.has("max"));

        Position a = Position.deserialize(object.get("min"));
        Position b = Position.deserialize(object.get("max"));

        return of(a, b);
    }

    public static Region of(Position a, Position b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");

        if (!a.getWorld().equals(b.getWorld())) {
            throw new IllegalArgumentException("positions are in different worlds");
        }

        return new Region(a, b);
    }

    private final Position min;
    private final Position max;

    private final double width;
    private final double height;
    private final double length;

    private Region(Position a, Position b) {
        this.min = Position.of(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ()), a.getWorld());

        this.max = Position.of(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ()), a.getWorld());

        this.width = this.max.getX() - this.min.getX();
        this.height = this.max.getY() - this.min.getY();
        this.length = this.max.getZ() - this.min.getZ();
    }

    public boolean inRegion(Position pos) {
        Objects.requireNonNull(pos, "pos");
        return pos.getWorld().equals(this.min.getWorld())
                && inRegion(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean inRegion(Location loc) {
        Objects.requireNonNull(loc, "loc");
        return loc.getWorld().getName().equals(this.min.getWorld())
                && inRegion(loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean inRegion(double x, double y, double z) {
        double minX = this.min.getX();
        double minY = this.min.getY();
        double minZ = this.min.getZ();
        double maxX = this.max.getX();
        double maxY = this.max.getY();
        double maxZ = this.max.getZ();
        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    public Position getMin() {
        return this.min;
    }

    public Position getMax() {
        return this.max;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public double getLength() {
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
        if (!(o instanceof Region)) {
            return false;
        }
        Region other = (Region) o;
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
        return "Region(min=" + this.getMin() + ", max=" + this.getMax() + ")";
    }

}