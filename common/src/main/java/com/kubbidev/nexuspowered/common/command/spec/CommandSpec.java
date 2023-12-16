package com.kubbidev.nexuspowered.common.command.spec;

import com.kubbidev.java.util.ImmutableCollectors;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class CommandSpec implements Namespaced {

    private final String name;
    private final String usage;
    private final List<Argument> args;

    public CommandSpec(String name, String usage, PartialArgument... args) {
        this.name = name;
        this.usage = usage;
        this.args = args.length == 0 ? null : Arrays.stream(args)
                .map(builder -> {
                    String key = builder.id.replace(".", "").replace(' ', '-');
                    TranslatableComponent description = Component.translatable(namespace() + ".usage." + key() + ".argument." + key);
                    return new Argument(builder.name, builder.required, description);
                })
                .collect(ImmutableCollectors.toList());
    }

    public CommandSpec(String name, PartialArgument... args) {
        this(name, null, args);
    }

    public TranslatableComponent description() {
        return Component.translatable(namespace() + ".usage." + this.key() + ".description");
    }

    public String usage() {
        return this.usage;
    }

    public List<Argument> args() {
        return this.args;
    }

    public String key() {
        return this.name.toLowerCase(Locale.ROOT).replace('_', '-');
    }

    public static PartialArgument arg(String id, String name, boolean required) {
        return new PartialArgument(id, name, required);
    }

    public static PartialArgument arg(String name, boolean required) {
        return new PartialArgument(name, name, required);
    }

    public static final class PartialArgument {
        private final String id;
        private final String name;
        private final boolean required;

        private PartialArgument(String id, String name, boolean required) {
            this.id = id;
            this.name = name;
            this.required = required;
        }
    }
}
