package com.kubbidev.nexuspowered.paper;

import com.kubbidev.nexuspowered.common.command.util.ArgumentTokenizer;
import com.kubbidev.nexuspowered.common.sender.Sender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaperCommandExecutor extends Command {

    private final PaperNexusPlugin<?> plugin;

    public PaperCommandExecutor(@NotNull String name, PaperNexusPlugin<?> plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Sender wrapped = this.plugin.getSenderFactory().wrap(sender);
        List<String> arguments = ArgumentTokenizer.EXECUTE.tokenizeInput(args);

        this.plugin.getCommandManager().executeCommand(wrapped, label, arguments);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) throws IllegalArgumentException {
        Sender wrapped = this.plugin.getSenderFactory().wrap(sender);
        List<String> arguments = ArgumentTokenizer.TAB_COMPLETE.tokenizeInput(args);

        return this.plugin.getCommandManager().tabCompleteCommand(wrapped, label, arguments);
    }
}
