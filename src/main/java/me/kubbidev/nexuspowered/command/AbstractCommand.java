package me.kubbidev.nexuspowered.command;

import java.util.List;
import me.kubbidev.nexuspowered.command.context.CommandContext;
import me.kubbidev.nexuspowered.command.context.ImmutableCommandContext;
import me.kubbidev.nexuspowered.internal.LoaderUtils;
import me.kubbidev.nexuspowered.util.CommandMapUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract implementation of {@link Command} and {@link CommandExecutor}.
 */
@NotNullByDefault
public abstract class AbstractCommand implements Command, CommandExecutor, TabCompleter {

    @Nullable
    protected String permission;
    @Nullable
    protected String description;

    @Override
    public void register(@NotNull String... aliases) {
        LoaderUtils.getPlugin().registerCommand(this, this.permission, this.description, aliases);
    }

    @Override
    public void close() {
        CommandMapUtil.unregisterCommand(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args
    ) {
        CommandContext<CommandSender> context = new ImmutableCommandContext<>(sender, label, args,
            command.getAliases());
        try {
            this.call(context);
        } catch (CommandInterruptException e) {
            e.getAction().accept(context.sender());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                org.bukkit.command.@NotNull Command command, @NotNull String label,
                                                @NotNull String[] args
    ) {
        CommandContext<CommandSender> context = new ImmutableCommandContext<>(sender, label, args,
            command.getAliases());
        try {
            return this.callTabCompleter(context);
        } catch (CommandInterruptException e) {
            e.getAction().accept(context.sender());
        }
        return null;
    }
}