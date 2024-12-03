package me.kubbidev.nexuspowered.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.Nexus;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An immutable and serializable position + direction object
 */
public final class Point implements GsonSerializable {
    public static Point deserialize(JsonElement element) {
        Position position = Position.deserialize(element);
        Direction direction = Direction.deserialize(element);

        return of(position, direction);
    }

    public static Point of(Position position, Direction direction) {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(direction, "direction");
        return new Point(position, direction);
    }

    public static Point of(Location location) {
        Objects.requireNonNull(location, "location");
        return of(Position.of(location), Direction.from(location));
    }

    private final Position position;
    private final Direction direction;

    @Nullable
    private Location bukkitLocation = null;

    private Point(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public Position getPosition() {
        return this.position;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public synchronized Location toLocation() {
        if (this.bukkitLocation == null) {
            this.bukkitLocation = new Location(Nexus.worldNullable(this.position.getWorld()),
                    this.position.getX(),
                    this.position.getY(),
                    this.position.getZ(),
                    this.direction.getYaw(),
                    this.direction.getPitch());
        }

        return this.bukkitLocation.clone();
    }

    public Point add(double x, double y, double z) {
        return this.position.add(x, y, z).withDirection(this.direction);
    }

    public Point subtract(double x, double y, double z) {
        return this.position.subtract(x, y, z).withDirection(this.direction);
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        return JsonBuilder.object()
                .addAll(this.position.serialize())
                .addAll(this.direction.serialize())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Point)) {
            return false;
        }
        Point other = (Point) o;
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
        return "Point(position=" + this.getPosition() + ", direction=" + this.getDirection() + ")";
    }
}