package me.kubbidev.nexuspowered.hologram.individual;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import me.kubbidev.nexuspowered.Events;
import me.kubbidev.nexuspowered.protocol.Protocol;
import me.kubbidev.nexuspowered.serialize.Position;
import me.kubbidev.nexuspowered.terminable.composite.CompositeTerminable;
import me.kubbidev.nexuspowered.util.Text;
import me.kubbidev.nexuspowered.util.entityspawner.EntitySpawner;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PacketIndividualHologramFactory implements IndividualHologramFactory {

    @Override
    public @NotNull IndividualHologram newHologram(@NotNull Position position, @NotNull List<HologramLine> lines) {
        return new PacketHologram(position, lines);
    }

    private static final class HologramEntity {
        private ArmorStand armorStand;
        private HologramLine line;
        private int entityId;

        private final Map<Integer, WrappedWatchableObject> cachedMetadata = new HashMap<>();

        private HologramEntity(HologramLine line) {
            this.line = line;
        }

        public ArmorStand getArmorStand() {
            return this.armorStand;
        }

        public void setArmorStand(ArmorStand armorStand) {
            this.armorStand = armorStand;
        }

        public HologramLine getLine() {
            return this.line;
        }

        public void setLine(HologramLine line) {
            this.line = line;
        }

        public int getId() {
            return this.entityId;
        }

        public void setId(int entityId) {
            this.entityId = entityId;
        }

        public Map<Integer, WrappedWatchableObject> getCachedMetadata() {
            return this.cachedMetadata;
        }
    }

    private static final class PacketHologram implements IndividualHologram {

        private Position position;
        private final List<HologramLine> lines = new ArrayList<>();
        private final List<HologramEntity> spawnedEntities = new ArrayList<>();
        private final Set<Player> viewers = Collections.synchronizedSet(new HashSet<>());
        private boolean spawned = false;

        private CompositeTerminable listeners = null;
        private Consumer<Player> clickCallback = null;

        PacketHologram(Position position, List<HologramLine> lines) {
            this.position = Objects.requireNonNull(position, "position");
            updateLines(lines);
        }

        private Position getNewLinePosition() {
            if (this.spawnedEntities.isEmpty()) {
                return this.position;
            }
            // get the last entry
            ArmorStand last = this.spawnedEntities.getLast().getArmorStand();
            return Position.of(last.getLocation()).subtract(0.0, 0.25, 0.0);
        }

        @Override
        public void updateLines(@NotNull List<HologramLine> lines) {
            Objects.requireNonNull(lines, "lines");
            Preconditions.checkArgument(!lines.isEmpty(), "lines cannot be empty");
            for (HologramLine line : lines) {
                Preconditions.checkArgument(line != null, "null line");
            }

            if (this.lines.equals(lines)) {
                return;
            }

            this.lines.clear();
            this.lines.addAll(lines);
        }

        @Override
        public @NotNull Set<Player> getViewers() {
            return ImmutableSet.copyOf(this.viewers);
        }

        @Override
        public void addViewer(@NotNull Player player) {
            if (!this.viewers.add(player)) {
                return;
            }

            // handle resending
            for (HologramEntity entity : this.spawnedEntities) {
                PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
                spawnPacket.getModifier().writeDefaults();

                // write entity id
                spawnPacket.getIntegers().write(0, entity.getId());

                // write unique id
                spawnPacket.getUUIDs().write(0, entity.getArmorStand().getUniqueId());

                // write type
                spawnPacket.getIntegers().write(1, 3);

                // write coordinates
                Location loc = entity.getArmorStand().getLocation();
                spawnPacket.getDoubles().write(0, loc.getX());
                spawnPacket.getDoubles().write(1, loc.getY());
                spawnPacket.getDoubles().write(2, loc.getZ());

                spawnPacket.getIntegers().write(2, (int) ((loc.getPitch()) * 256.0F / 360.0F));
                spawnPacket.getIntegers().write(3, (int) ((loc.getYaw()) * 256.0F / 360.0F));

                // write object data
                spawnPacket.getIntegers().write(5, 0);

                // send it
                Protocol.sendPacket(player, spawnPacket);

                // send missed metadata
                PacketContainer metadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

                // write entity id
                metadataPacket.getIntegers().write(0, entity.getId());

                // write metadata
                List<WrappedWatchableObject> watchableObjects = new ArrayList<>();

                // re-add all other cached metadata
                for (Map.Entry<Integer, WrappedWatchableObject> ent : entity.getCachedMetadata().entrySet()) {
                    watchableObjects.add(ent.getValue());
                }

                metadataPacket.getWatchableCollectionModifier().write(0, watchableObjects);

                // send it
                Protocol.sendPacket(player, metadataPacket);
            }
        }

        @Override
        public void removeViewer(@NotNull Player player) {
            if (!this.viewers.remove(player)) {
                return;
            }

            // handle removing the existing entity?
            PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

            // set ids
            int[] ids = this.spawnedEntities.stream().mapToInt(HologramEntity::getId).toArray();
            destroyPacket.getIntegerArrays().write(0, ids);

            Protocol.sendPacket(player, destroyPacket);
        }

        @Override
        public void spawn() {
            // ensure listening
            if (this.listeners == null) {
                setupPacketListeners();
            }

            // resize to fit any new lines
            int linesSize = this.lines.size();
            int spawnedSize = this.spawnedEntities.size();

            // remove excess lines
            if (linesSize < spawnedSize) {
                int diff = spawnedSize - linesSize;
                for (int i = 0; i < diff; i++) {

                    // get and remove the last entry
                    int index = this.spawnedEntities.size() - 1;

                    // remove the armor stand first
                    ArmorStand as = this.spawnedEntities.get(index).getArmorStand();
                    as.remove();

                    // then remove from the list
                    this.spawnedEntities.remove(index);
                }
            }

            // now enough armor stands are spawned, we can now update the text
            for (int i = 0; i < this.lines.size(); i++) {
                HologramLine line = this.lines.get(i);

                Component generatedName = Component.text("hologramline-" + ThreadLocalRandom.current().nextInt(100000000));

                if (i >= this.spawnedEntities.size()) {
                    Location loc = getNewLinePosition().toLocation();

                    // init the holo entity before actually spawning (so the listeners can catch it)
                    HologramEntity holoEntity = new HologramEntity(line);
                    this.spawnedEntities.add(holoEntity);

                    // ensure the hologram's chunk is loaded.
                    Chunk chunk = loc.getChunk();
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }

                    // spawn the armor stand
                    EntitySpawner.INSTANCE.spawn(loc, ArmorStand.class, as -> {
                        holoEntity.setId(as.getEntityId());
                        holoEntity.setArmorStand(as);

                        as.setSmall(true);
                        as.setMarker(true);
                        as.setArms(false);
                        as.setBasePlate(false);
                        as.setGravity(false);
                        as.setVisible(false);
                        as.customName(generatedName);
                        as.setCustomNameVisible(true);
                        as.setAI(false);
                        as.setCollidable(false);
                        as.setInvulnerable(true);
                    });

                } else {
                    // update existing line if necessary
                    HologramEntity as = this.spawnedEntities.get(i);

                    if (!as.getLine().equals(line)) {
                        as.setLine(line);
                        as.getArmorStand().customName(generatedName);
                    }
                }
            }

            this.spawned = true;
        }

        @Override
        public void despawn() {
            this.spawnedEntities.forEach(e -> e.getArmorStand().remove());
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

            for (HologramEntity stand : this.spawnedEntities) {
                if (!stand.getArmorStand().isValid()) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public @NotNull Collection<ArmorStand> getArmorStands() {
            return this.spawnedEntities.stream().map(HologramEntity::getArmorStand).collect(Collectors.toSet());
        }

        @Override
        public @Nullable ArmorStand getArmorStand(int line) {
            if (line >= this.spawnedEntities.size()) {
                return null;
            }
            return this.spawnedEntities.get(line).armorStand;
        }

        @Override
        public void updatePosition(@NotNull Position position) {
            Objects.requireNonNull(position, "position");
            if (this.position.equals(position)) {
                return;
            }

            this.position = position;
            despawn();
            spawn();
        }

        @Override
        public void setClickCallback(@Nullable Consumer<Player> clickCallback) {
            this.clickCallback = clickCallback;
        }

        @Override
        public void close() {
            despawn();
        }

        @Override
        public boolean isClosed() {
            return !this.spawned;
        }

        private HologramEntity getHologramEntity(int entityId) {
            for (HologramEntity entity : PacketHologram.this.spawnedEntities) {
                if (entity.getId() == entityId) {
                    return entity;
                }
            }
            return null;
        }

        private void setupPacketListeners() {
            this.listeners = CompositeTerminable.create();

            // remove players when they quit
            Events.subscribe(PlayerQuitEvent.class)
                    .handler(e -> this.viewers.remove(e.getPlayer()))
                    .bindWith(this.listeners);

            Protocol.subscribe(ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_METADATA)
                    .handler(e -> {
                        PacketContainer packet = e.getPacket();
                        Player player = e.getPlayer();

                        // get entity id
                        int entityId = packet.getIntegers().read(0);

                        // find a matching hologram line
                        HologramEntity hologram = getHologramEntity(entityId);
                        if (hologram == null) {
                            return;
                        }

                        // get metadata
                        List<WrappedWatchableObject> metadata = new ArrayList<>(packet.getWatchableCollectionModifier().read(0));

                        if (!this.viewers.contains(player)) {
                            // attempt to cache metadata anyway
                            for (WrappedWatchableObject value : metadata) {
                                hologram.getCachedMetadata().put(value.getIndex(), value);
                            }

                            e.setCancelled(true);
                            return;
                        }

                        // process metadata
                        for (WrappedWatchableObject value : metadata) {
                            // cache the metadata
                            hologram.getCachedMetadata().put(value.getIndex(), value);

                            if (value.getIndex() == 2) {
                                Component line = hologram.getLine().resolve(player);

                                value.setValue(convertNameMeta(value.getValue().getClass(), line));
                            }
                        }

                        packet = packet.deepClone();
                        packet.getWatchableCollectionModifier().write(0, metadata);
                        e.setPacket(packet);
                    })
                    .bindWith(this.listeners);

            Protocol.subscribe(ListenerPriority.HIGH, PacketType.Play.Server.SPAWN_ENTITY)
                    .handler(e -> {
                        PacketContainer packet = e.getPacket();
                        Player player = e.getPlayer();

                        // get entity id
                        int entityId = packet.getIntegers().read(0);

                        // find a matching hologram
                        HologramEntity hologram = getHologramEntity(entityId);
                        if (hologram == null) {
                            return;
                        }

                        if (!this.viewers.contains(player)) {
                            e.setCancelled(true);
                        }
                    })
                    .bindWith(this.listeners);

            Protocol.subscribe(ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY)
                    .handler(e -> {
                        PacketContainer packet = e.getPacket();
                        Player player = e.getPlayer();

                        // get entity id
                        int entityId = packet.getIntegers().read(0);

                        // find a matching hologram
                        HologramEntity hologram = getHologramEntity(entityId);
                        if (hologram == null) {
                            return;
                        }

                        // always cancel interacts involving hologram objects
                        e.setCancelled(true);

                        if (this.clickCallback == null) {
                            return;
                        }

                        // if the player isn't a viewer, don't process the click
                        if (!this.viewers.contains(player)) {
                            return;
                        }

                        Location location = hologram.getArmorStand().getLocation();
                        if (player.getLocation().distanceSquared(location) > (5 * 5)) {
                            return;
                        }

                        this.clickCallback.accept(player);
                    })
                    .bindWith(this.listeners);
        }

        private Object convertNameMeta(Class<?> metaClass, Component value) {
            if (metaClass == Optional.class) {
                return Optional.of(WrappedChatComponent.fromJson(Text.toGson(value)).getHandle());
            }

            throw new UnsupportedOperationException("Unsupported name meta type: " + metaClass.getName());
        }
    }
}