package me.kubbidev.nexuspowered.command.context;

import com.google.common.collect.ImmutableList;
import java.util.List;
import me.kubbidev.nexuspowered.command.argument.Argument;
import me.kubbidev.nexuspowered.command.argument.SimpleArgument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImmutableCommandContext<T extends CommandSender> implements CommandContext<T> {

    private final T                     sender;
    private final String                label;
    private final ImmutableList<String> args;
    private final ImmutableList<String> aliases;

    public ImmutableCommandContext(T sender, String label, String[] args, List<String> aliases) {
        this.sender = sender;
        this.label = label;
        this.args = ImmutableList.copyOf(args);
        this.aliases = ImmutableList.copyOf(aliases);
    }

    @Override
    public @NotNull T sender() {
        return this.sender;
    }

    @Override
    public @NotNull ImmutableList<String> args() {
        return this.args;
    }

    @Override
    public @NotNull Argument arg(int index) {
        return new SimpleArgument(index, rawArg(index));
    }

    @Override
    public @Nullable String rawArg(int index) {
        if (index < 0 || index >= this.args.size()) {
            return null;
        }
        return this.args.get(index);
    }

    @Override
    public @NotNull String label() {
        return this.label;
    }

    @Override
    public @NotNull ImmutableList<String> aliases() {
        return this.aliases;
    }
}