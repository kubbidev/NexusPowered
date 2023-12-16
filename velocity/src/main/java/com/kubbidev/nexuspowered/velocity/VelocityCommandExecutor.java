package com.kubbidev.nexuspowered.velocity;

import com.velocitypowered.api.command.RawCommand;
import com.kubbidev.nexuspowered.common.command.abstraction.Command;
import com.kubbidev.nexuspowered.common.command.util.ArgumentTokenizer;
import com.kubbidev.nexuspowered.common.sender.Sender;

import java.util.List;

public class VelocityCommandExecutor implements RawCommand {

    private final VelocityNexusPlugin<?> plugin;
    private final Command<?, ?> command;

    public VelocityCommandExecutor(VelocityNexusPlugin<?> plugin, Command<?, ?> command) {
        this.plugin = plugin;
        this.command = command;
    }

    @Override
    public void execute(Invocation invocation) {
        Sender wrapped = this.plugin.getSenderFactory().wrap(invocation.source());
        List<String> arguments = ArgumentTokenizer.EXECUTE.tokenizeInput(invocation.arguments());

        this.plugin.getCommandManager().executeCommand(wrapped, invocation.alias(), arguments);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        Sender wrapped = this.plugin.getSenderFactory().wrap(invocation.source());
        List<String> arguments = ArgumentTokenizer.TAB_COMPLETE.tokenizeInput(invocation.arguments());

        return this.plugin.getCommandManager().tabCompleteCommand(wrapped, invocation.alias(), arguments);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(command.getPermission());
    }
}
