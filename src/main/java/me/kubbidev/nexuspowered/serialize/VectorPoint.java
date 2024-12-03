package me.kubbidev.nexuspowered.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An immutable and serializable vector + direction object
 */
public final class VectorPoint implements GsonSerializable {
    public static VectorPoint deserialize(JsonElement element) {
        Vector position = VectorSerializers.deserialize(element);
        Direction direction = Direction.deserialize(element);

        return of(position, direction);
    }

    public static VectorPoint of(Vector position, Direction direction) {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(direction, "direction");
        return new VectorPoint(position, direction);
    }

    public static VectorPoint of(Location location) {
        Objects.requireNonNull(location, "location");
        return of(Position.of(location).toVector(), Direction.from(location));
    }

    public static VectorPoint of(Point point) {
        Objects.requireNonNull(point, "point");
        return of(point.getPosition().toVector(), point.getDirection());
    }

    private final Vector position;
    private final Direction direction;

    @Nullable
    private Location bukkitLocation = null;

    private VectorPoint(Vector position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public Vector getPosition() {
        return this.position;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public synchronized Location toLocation(World world) {
        if (this.bukkitLocation == null || !this.bukkitLocation.getWorld().equals(world)) {
            this.bukkitLocation = new Location(world,
                    this.position.getX(),
                    this.position.getY(),
                    this.position.getZ(),
                    this.direction.getYaw(),
                    this.direction.getPitch());
        }

        return this.bukkitLocation.clone();
    }

    public VectorPoint add(double x, double y, double z) {
        return of(this.position.add(new Vector(x, y, z)), this.direction);
    }

    public VectorPoint subtract(double x, double y, double z) {
        return of(this.position.subtract(new Vector(x, y, z)), this.direction);
    }

    public Point withWorld(String world) {
        Objects.requireNonNull(world, "world");
        return Point.of(Position.of(this.position, world), this.direction);
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .addAll(VectorSerializers.serialize(this.position))
                .addAll(this.direction.serialize())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof VectorPoint)) {
            return false;
        }
        VectorPoint other = (VectorPoint) o;
        return this.getPosition().equals(other.getPosition())
                && this.getDirection().equals(other.getDirection());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getPosition().hashCode();
        result = result * PRIME + this.getDirection().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "VectorPoint(position=" + this.getPosition() + ", direction=" + this.getDirection() + ")";
    }
}