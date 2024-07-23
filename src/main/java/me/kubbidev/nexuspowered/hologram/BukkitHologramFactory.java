package me.kubbidev.nexuspowered.hologram;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import me.kubbidev.nexuspowered.Events;
import me.kubbidev.nexuspowered.Nexus;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import me.kubbidev.nexuspowered.reflect.MinecraftVersion;
import me.kubbidev.nexuspowered.reflect.MinecraftVersions;
import me.kubbidev.nexuspowered.serialize.Position;
import me.kubbidev.nexuspowered.terminable.composite.CompositeTerminable;
import me.kubbidev.nexuspowered.util.Text;
import me.kubbidev.nexuspowered.util.entityspawner.EntitySpawner;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class BukkitHologramFactory implements HologramFactory {

    @Override
    public @NotNull Hologram newHologram(@NotNull Position position, @NotNull List<Component> lines) {
        return new BukkitHologram(position, lines);
    }

    private static final class BukkitHologram implements Hologram {
        private Position position;
        private final List<Component> lines = new ArrayList<>();
        private final List<ArmorStand> spawnedEntities = new ArrayList<>();
        private boolean spawned = false;

        private CompositeTerminable listeners = null;
        private Consumer<Player> clickCallback = null;
        private final List<Pig> spawnedPassengers = new ArrayList<>();

        BukkitHologram(Position position, List<Component> lines) {
            this.position = Objects.requireNonNull(position, "position");
            updateLines(lines);
        }

        private Position getNewLinePosition() {
            if (this.spawnedEntities.isEmpty()) {
                return this.position;
            } else {
                // get the last entry
                ArmorStand last = this.spawnedEntities.getLast();
                return Position.of(last.getLocation()).subtract(0.0, 0.25, 0.0);
            }
        }

        @Override
        public void updateLines(@NotNull List<Component> lines) {
            Objects.requireNonNull(lines, "lines");
            Preconditions.checkArgument(!lines.isEmpty(), "lines cannot be empty");
            for (Component line : lines) {
                Preconditions.checkArgument(line != null, "null line");
            }

            if (this.lines.equals(lines)) {
                return;
            }

            this.lines.clear();
            this.lines.addAll(lines);
        }

        @Override
        public @NotNull JsonElement serialize() {
            return JsonBuilder.object()
                    .add("position", this.position)
                    .add("lines", JsonBuilder.array().addStrings(Text.toGson(this.lines)).build())
                    .build();
        }

        @Override
        public void spawn() {
            // resize to fit any new lines
            int linesSize = this.lines.size();
            int spawnedSize = this.spawnedEntities.size();

            // remove excess lines
            if (linesSize < spawnedSize) {
                int diff = spawnedSize - linesSize;
                for (int i = 0; i < diff; i++) {

                    // get and remove the last entry
                    ArmorStand as = this.spawnedEntities.removeLast();
                    as.remove();

                    if (this.listeners != null) {
                        Pig pig = this.spawnedPassengers.removeLast();
                        pig.remove();
                    }
                }
            }

            // now enough armor stands are spawned, we can now update the text
            for (int i = 0; i < this.lines.size(); i++) {
                Component line = this.lines.get(i);

                if (i >= this.spawnedEntities.size()) {
                    Location location = getNewLinePosition().toLocation();

                    // ensure the hologram's chunk is loaded.
                    Chunk chunk = location.getChunk();
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }

                    // remove any armor stands already at this location. (leftover from a server restart)
                    location.getWorld().getNearbyEntities(location, 1.0, 1.0, 1.0).forEach(e -> {
                        if (e.getType() == EntityType.ARMOR_STAND && locationsEqual(e.getLocation(), location)) {
                            e.remove();
                        }
                    });

                    EntitySpawner.INSTANCE.spawn(location, ArmorStand.class, as -> {
                        as.setSmall(true);
                        as.setMarker(true);
                        as.setArms(false);
                        as.setBasePlate(false);
                        as.setGravity(false);
                        as.setVisible(false);
                        as.customName(line);
                        as.setCustomNameVisible(true);

                        if (MinecraftVersion.getRuntimeVersion().isAfterOrEq(MinecraftVersions.v1_9)) {
                            as.setAI(false);
                            as.setCollidable(false);
                            as.setInvulnerable(true);
                        }

                        as.setCanTick(false);

                        if (this.listeners != null) {
                            Pig pig = (Pig) as.getWorld().spawnEntity(as.getLocation(), EntityType.PIG);
                            pig.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                            pig.setCustomNameVisible(false);
                            pig.setSilent(true);
                            pig.setGravity(false);

                            pig.setMetadata("nodespawn", new FixedMetadataValue(Nexus.hostPlugin(), true));

                            if (MinecraftVersion.getRuntimeVersion().isAfterOrEq(MinecraftVersions.v1_9)) {
                                pig.setAI(false);
                                pig.setCollidable(false);
                                pig.setInvulnerable(true);
                            }

                            as.addPassenger(pig);

                            this.spawnedPassengers.add(pig);
                        }

                        this.spawnedEntities.add(as);
                    });
                } else {
                    // update existing line if necessary
                    ArmorStand as = this.spawnedEntities.get(i);

                    if (Objects.equals(as.customName(), line)) {
                        continue;
                    }
                    as.customName(line);
                }
            }

            if (this.listeners == null && this.clickCallback != null) {
                setClickCallback(this.clickCallback);
            }
            this.spawned = true;
        }

        @Override
        public void despawn() {
            this.spawnedEntities.forEach(Entity::remove);
            this.spawnedEntities.clear();
            this.spawned = false;

            if (this.listeners != null) {
                this.listeners.closeAndReportException();
            }
            this.listeners = null;
        }

        @Override
        public boolean isSpawned() {
            if (!this.spawned) {
                return false;
            }

            for (ArmorStand stand : this.spawnedEntities) {
                if (!stand.isValid()) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public @NotNull Collection<ArmorStand> getArmorStands() {
            return this.spawnedEntities;
        }

        @Override
        public @Nullable ArmorStand getArmorStand(int line) {
            if (line >= this.spawnedEntities.size()) {
                return null;
            }
            return this.spawnedEntities.get(line);
        }

        @Override
        public void updatePosition(@NotNull Position position) {
            Objects.requireNonNull(position, "position");

            this.position = position;
            if (!isSpawned()) {
                spawn();
            } else {
                double offset = 0.0;
                for (int i = 0; i < this.spawnedEntities.size(); i++) {
                    ArmorStand as = this.spawnedEntities.get(i);
                    Location location = position.toLocation().clone().add(0, offset, 0);

                    if (i < this.spawnedPassengers.size()) {
                        Pig pig = this.spawnedPassengers.get(i);
                        if (pig != null) {
                            pig.teleport(location);
                        }
                    }

                    as.teleport(location);
                    offset += 0.25;
                }
            }
        }

        @Override
        public void setClickCallback(@Nullable Consumer<Player> clickCallback) {
            // unregister any existing listeners
            if (clickCallback == null) {
                if (this.listeners != null) {
                    this.listeners.closeAndReportException();
                }
                this.clickCallback = null;
                this.listeners = null;
                return;
            }

            this.clickCallback = clickCallback;

            if (this.listeners == null) {
                this.listeners = CompositeTerminable.create();

                this.listeners.bind(() -> {
                    this.spawnedPassengers.forEach(Entity::remove);
                    this.spawnedPassengers.clear();
                });

                this.spawnedPassengers.forEach(Entity::remove);
                this.spawnedPassengers.clear();

                for (ArmorStand as : this.spawnedEntities) {
                    Pig pig = (Pig) as.getWorld().spawnEntity(as.getLocation(), EntityType.PIG);
                    pig.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                    pig.setCustomNameVisible(false);
                    pig.setSilent(true);
                    pig.setGravity(false);

                    pig.setMetadata("nodespawn", new FixedMetadataValue(Nexus.hostPlugin(), true));

                    if (MinecraftVersion.getRuntimeVersion().isAfterOrEq(MinecraftVersions.v1_9)) {
                        pig.setAI(false);
                        pig.setCollidable(false);
                        pig.setInvulnerable(true);
                    }

                    as.addPassenger(pig);
                }

                Events.subscribe(PigZapEvent.class)
                        .handler(e -> {
                            for (Pig spawned : this.spawnedPassengers) {
                                if (spawned.equals(e.getEntity())) {
                                    e.setCancelled(true);
                                    return;
                                }
                            }
                        }).bindWith(this.listeners);

                Events.subscribe(PlayerInteractEntityEvent.class)
                        .filter(e -> e.getRightClicked() instanceof Pig)
                        .handler(e -> {
                            Player p = e.getPlayer();
                            Pig pig = (Pig) e.getRightClicked();

                            for (Pig spawned : this.spawnedPassengers) {
                                if (spawned.equals(pig)) {
                                    e.setCancelled(true);
                                    this.clickCallback.accept(p);
                                    return;
                                }
                            }
                        })
                        .bindWith(this.listeners);

                Events.subscribe(EntityDamageByEntityEvent.class)
                        .filter(e -> e.getEntity() instanceof Pig)
                        .filter(e -> e.getDamager() instanceof Player)
                        .handler(e -> {
                            Player p = (Player) e.getDamager();
                            Pig pig = (Pig) e.getEntity();

                            for (Pig spawned : this.spawnedPassengers) {
                                if (spawned.equals(pig)) {
                                    e.setCancelled(true);
                                    this.clickCallback.accept(p);
                                    return;
                                }
                            }
                        })
                        .bindWith(this.listeners);
            }
        }

        @Override
        public void close() {
            despawn();
        }

        @Override
        public boolean isClosed() {
            return !this.spawned;
        }

        private static boolean locationsEqual(Location l1, Location l2) {
            return Double.doubleToLongBits(l1.getX()) == Double.doubleToLongBits(l2.getX()) &&
                    Double.doubleToLongBits(l1.getY()) == Double.doubleToLongBits(l2.getY()) &&
                    Double.doubleToLongBits(l1.getZ()) == Double.doubleToLongBits(l2.getZ());
        }
    }
}