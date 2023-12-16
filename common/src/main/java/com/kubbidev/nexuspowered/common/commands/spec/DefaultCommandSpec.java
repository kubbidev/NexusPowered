package com.kubbidev.nexuspowered.common.commands.spec;

import com.kubbidev.nexuspowered.common.command.spec.CommandSpec;
import net.kyori.adventure.key.KeyPattern;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandSpec extends CommandSpec {

    public static final CommandSpec MAIN = new DefaultCommandSpec("main", "/%s");
    public static final CommandSpec INFO = new DefaultCommandSpec("info", "/%s info");
    public static final CommandSpec HELP = new DefaultCommandSpec("help", "/%s help");
    public static final CommandSpec RELOAD = new DefaultCommandSpec("reload", "/%s reload");
    public static final CommandSpec PLUGINS = new DefaultCommandSpec("plugins", "/%s plugins");

    public DefaultCommandSpec(String name, String usage, PartialArgument... args) {
        super(name, usage, args);
    }

    public DefaultCommandSpec(String name, PartialArgument... args) {
        super(name, args);
    }

    @KeyPattern.Namespace
    @Override
    public @NotNull String namespace() {
        return "nexuspowered";
    }
}
