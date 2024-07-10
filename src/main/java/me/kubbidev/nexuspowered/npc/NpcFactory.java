package me.kubbidev.nexuspowered.npc;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kubbidev.nexuspowered.gson.GsonSerializable;
import me.kubbidev.nexuspowered.serialize.Position;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object which can create {@link Npc}s.
 */
public interface NpcFactory {

    /**
     * Spawns a NPC at the given location.
     *
     * @param position      the position to spawn the npc at
     * @param nametag       the nametag to give the npc
     * @param skinTextures  the skin textures the NPC should have
     * @param skinSignature the signature of the provided textures
     * @return the created npc
     */
    @NotNull
    Npc newNpc(@NotNull Position position, @NotNull String nametag, @NotNull String skinTextures, @NotNull String skinSignature);

    /**
     * Deserializes a npc instance from its {@link GsonSerializable serialized} form.
     *
     * @param element the data
     * @return the npc
     */
    @NotNull
    default Npc deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("position"));
        Preconditions.checkArgument(object.has("nametag"));
        Preconditions.checkArgument(object.has("skinTextures"));
        Preconditions.checkArgument(object.has("skinSignature"));

        Position position = Position.deserialize(object.get("position"));
        String nametag = object.get("nametag").getAsString();
        String skinTextures = object.get("skinTextures").getAsString();
        String skinSignature = object.get("skinSignature").getAsString();

        return newNpc(position, nametag, skinTextures, skinSignature);
    }
}