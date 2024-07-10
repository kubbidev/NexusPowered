package me.kubbidev.nexuspowered.signprompt;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.protocol.Protocol;
import me.kubbidev.nexuspowered.util.Players;
import me.kubbidev.nexuspowered.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link SignPromptFactory} using ProtocolLib.
 */
public class PacketSignPromptFactory implements SignPromptFactory {

    @Override
    public void openPrompt(@NotNull Player player, @NotNull List<Component> lines, @NotNull ResponseHandler responseHandler) {
        Location location = player.getLocation().clone();
        location.setY(255);
        Players.sendBlockChange(player, location, Material.OAK_WALL_SIGN);

        BlockPosition position = new BlockPosition(location.toVector());
        PacketContainer writeToSign = new PacketContainer(PacketType.Play.Server.TILE_ENTITY_DATA);
        writeToSign.getBlockPositionModifier().write(0, position);
        writeToSign.getIntegers().write(0, 9);
        NbtCompound compound = NbtFactory.ofCompound("");

        for (int i = 0; i < 4; i++) {
            compound.put("Text" + (i + 1), "{\"text\":\"" + (lines.size() > i ? Text.toLegacy(lines.get(i)) : "") + "\"}");
        }

        compound.put("id", "minecraft:sign");
        compound.put("x", position.getX());
        compound.put("y", position.getY());
        compound.put("z", position.getZ());

        writeToSign.getNbtModifier().write(0, compound);
        Protocol.sendPacket(player, writeToSign);

        PacketContainer openSign = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        openSign.getBlockPositionModifier().write(0, position);
        Protocol.sendPacket(player, openSign);

        // we need to ensure that the callback is only called once.
        AtomicBoolean active = new AtomicBoolean(true);

        Protocol.subscribe(PacketType.Play.Client.UPDATE_SIGN)
                .filter(e -> e.getPlayer().getUniqueId().equals(player.getUniqueId()))
                .biHandler((sub, event) -> {
                    if (!active.getAndSet(false)) {
                        return;
                    }

                    PacketContainer container = event.getPacket();

                    List<String> input = new ArrayList<>(Arrays.asList(container.getStringArrays().read(0)));
                    Response response = responseHandler.handleResponse(input);

                    if (response == Response.TRY_AGAIN) {
                        // didn't pass, re-send the sign and request another input
                        Schedulers.sync().runLater(() -> {
                            if (player.isOnline()) {
                                openPrompt(player, lines, responseHandler);
                            }
                        }, 1L);
                    }

                    // cleanup this instance
                    sub.close();
                    Players.sendBlockChange(player, location, Material.AIR);
                });
    }
}