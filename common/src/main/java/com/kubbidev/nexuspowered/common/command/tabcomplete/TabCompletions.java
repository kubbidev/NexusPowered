package com.kubbidev.nexuspowered.common.command.tabcomplete;

import com.kubbidev.nexuspowered.common.NexusPlugin;

/**
 * Common completion suppliers used by plugins
 */
public final class TabCompletions {

    private static final CompletionSupplier BOOLEAN = CompletionSupplier.startsWith("true", "false");

    private final CompletionSupplier players;

    public TabCompletions(NexusPlugin<?> plugin) {
        this.players = CompletionSupplier.startsWith(() -> plugin.getPlayerList().stream());
    }

    // bit of a weird pattern, but meh it kinda works, reduces the boilerplate
    // of calling the commandmanager + tabcompletions getters every time

    public static CompletionSupplier booleans() {
        return BOOLEAN;
    }

    public static CompletionSupplier players(NexusPlugin<?> plugin) {
        return plugin.getCommandManager().getTabCompletions().players;
    }
}