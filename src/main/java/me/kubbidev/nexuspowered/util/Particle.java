package me.kubbidev.nexuspowered.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Particle {
    private final EnumWrappers.Particle particle;
    private int count = 1;
    private float x = 0;
    private float y = 0;
    private float z = 0;
    private float velocityX = 0;
    private float velocityY = 0;
    private float velocityZ = 0;
    private float speed = 0;
    private boolean far = false;
    private int[] data = new int[0];

    private final Set<Player> viewers = ConcurrentHashMap.newKeySet();

    @Contract("_ -> new")
    public static @NotNull Particle builder(@NotNull EnumWrappers.Particle particle) {
        return new Particle(particle);
    }

    private Particle(@NotNull EnumWrappers.Particle particle) {
        this.particle = Objects.requireNonNull(particle, "particle");
    }

    @Contract("_ -> this")
    public @NotNull Particle count(int count) {
        this.count = count;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle x(float x) {
        this.x = x;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle y(float y) {
        this.y = y;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle z(float z) {
        this.z = z;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle location(@NotNull Location location) {
        Objects.requireNonNull(location, "location");
        x((float) location.getX());
        y((float) location.getY());
        z((float) location.getZ());
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle velocityX(float velocityX) {
        this.velocityX = velocityX;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle velocityY(float velocityY) {
        this.velocityY = velocityY;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle velocityZ(float velocityZ) {
        this.velocityZ = velocityZ;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle velocity(@NotNull Vector velocity) {
        Objects.requireNonNull(velocity, "offset");
        velocityX((float) velocity.getX());
        velocityY((float) velocity.getY());
        velocityZ((float) velocity.getZ());
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle speed(float speed) {
        this.speed = speed;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle far(boolean far) {
        this.far = far;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle data(int[] data) {
        this.data = data;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle viewer(@NotNull Player viewer) {
        this.viewers.add(Objects.requireNonNull(viewer, "viewer"));
        return this;
    }

    @Contract("_ -> this")
    public @NotNull Particle viewers(@NotNull Collection<? extends Player> viewers) {
        this.viewers.addAll(Objects.requireNonNull(viewers, "viewers"));
        return this;
    }

    @Contract("-> this")
    public @NotNull Particle viewersAll() {
        this.viewers.clear();
        return this;
    }

    public void send() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.WORLD_PARTICLES);
        packet.getParticles().write(0, this.particle);
        packet.getFloat()
                .write(0, this.x)
                .write(1, this.y)
                .write(2, this.z)
                .write(3, this.velocityX)
                .write(4, this.velocityY)
                .write(5, this.velocityZ)
                .write(6, this.speed);

        packet.getIntegers().write(0, this.count);
        packet.getBooleans().write(0, this.far);
        packet.getIntegerArrays().write(0, this.data);

        if (this.viewers.isEmpty()) {
            ProtocolLibrary.getProtocolManager()
                    .broadcastServerPacket(packet);
        } else {
            for (Player viewer : this.viewers) {
                ProtocolLibrary.getProtocolManager()
                        .sendServerPacket(viewer, packet);
            }
        }

    }
}
