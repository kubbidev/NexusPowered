package me.kubbidev.nexuspowered.command.tabcomplete;

import me.kubbidev.nexuspowered.util.Players;
import org.bukkit.entity.HumanEntity;

/**
 * Common completion suppliers used by plugins
 */
public final class TabCompletions {

    private static final CompletionSupplier BOOLEAN = CompletionSupplier.startsWith("true", "false");
    private static final CompletionSupplier PLAYERS = CompletionSupplier.startsWith(
        () -> Players.all().stream().map(HumanEntity::getName)
    );

    // Bit of a weird pattern, but meh it kinda works, reduces the boilerplate

    public static CompletionSupplier booleans() {
        return BOOLEAN;
    }

    public static CompletionSupplier players() {
        return PLAYERS;
    }
}