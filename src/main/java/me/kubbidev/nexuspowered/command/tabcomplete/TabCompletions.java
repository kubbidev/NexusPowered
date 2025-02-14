package me.kubbidev.nexuspowered.command.tabcomplete;

/**
 * Common completion suppliers used by plugins
 */
public final class TabCompletions {
    private static final CompletionSupplier BOOLEAN = CompletionSupplier.startsWith("true", "false");

    // bit of a weird pattern, but meh it kinda works, reduces the boilerplate

    public static CompletionSupplier booleans() {
        return BOOLEAN;
    }
}