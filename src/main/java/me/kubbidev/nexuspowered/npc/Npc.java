package me.kubbidev.nexuspowered.npc;

import com.google.gson.JsonElement;
import me.kubbidev.nexuspowered.Services;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.metadata.MetadataMap;
import me.kubbidev.nexuspowered.serialize.Position;
import me.kubbidev.nexuspowered.terminable.Terminable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents a NPC (non-player character)
 */
@ApiStatus.Experimental
public interface Npc extends Terminable, GsonSerializable {

    /**
     * Creates and returns a new npc.
     *
     * @param position      the position to spawn the npc at
     * @param nametag       the nametag to give the npc
     * @param skinTextures  the skin textures the NPC should have
     * @param skinSignature the signature of the provided textures
     * @return the new npc
     */
    @NotNull
    static Npc create(@NotNull Position position, @NotNull String nametag, @NotNull String skinTextures, @NotNull String skinSignature) {
        return Services.load(NpcFactory.class).newNpc(position, nametag, skinTextures, skinSignature);
    }

    static Npc deserialize(JsonElement element) {
        return Services.load(NpcFactory.class).deserialize(element);
    }

    /**
     * Spawns the npc
     */
    void spawn();

    /**
     * Despawns the npc
     */
    void despawn();

    /**
     * Check if the npc is currently spawned
     *
     * @return true if spawned and active, or false otherwise
     */
    boolean isSpawned();

    /**
     * Updates the position of the npc and respawns it
     *
     * @param position the new position
     */
    void updatePosition(@NotNull Position position);

    /**
     * Sets a click callback for this npc
     *
     * @param clickCallback the click callback, or null to unregister any existing callback
     */
    void setClickCallback(@Nullable Consumer<Player> clickCallback);

    /**
     * Returns a copy of the available viewers of the hologram.
     *
     * @return a {@link Set} of players.
     */
    @NotNull
    Set<Player> getViewers();

    /**
     * Adds a viewer to the hologram.
     *
     * @param player the player
     */
    void addViewer(@NotNull Player player);

    /**
     * Removes a viewer from the hologram.
     *
     * @param player the player
     */
    void removeViewer(@NotNull Player player);

    /**
     * Check if there are any viewers for the hologram.
     *
     * @return any viewers
     */
    default boolean hasViewers() {
        return !this.getViewers().isEmpty();
    }

    /**
     * Gets the NPCs attached metadata map.
     *
     * @return the metadata map
     */
    @NotNull
    MetadataMap getMeta();

    /**
     * Sets the NPCs skin to the given textures.
     *
     * @param textures  the textures
     * @param signature the signature of the textures
     */
    void setSkin(@NotNull String textures, @NotNull String signature);

    /**
     * Sets the name of this NPC.
     *
     * @param name the name
     */
    void setName(@NotNull String name);

    /**
     * Sets if this NPCs nametag should be shown.
     *
     * @param show is the nametag should be shown
     */
    void setShowNametag(boolean show);

    /**
     * Gets the location where this NPC was initially spawned at.
     *
     * @return the initial spawn location of the NPC
     */
    @NotNull
    Location getInitialSpawn();
}