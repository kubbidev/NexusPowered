package com.kubbidev.nexuspowered.common.commands;

import com.kubbidev.java.util.Predicates;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.command.abstraction.CommandException;
import com.kubbidev.nexuspowered.common.command.abstraction.SingleCommand;
import com.kubbidev.nexuspowered.common.command.util.ArgumentList;
import com.kubbidev.nexuspowered.common.commands.spec.DefaultCommandSpec;
import com.kubbidev.nexuspowered.common.locale.LocaleMessage;
import com.kubbidev.nexuspowered.common.sender.Sender;

public class ReloadCommand<P extends NexusPlugin<P>> extends SingleCommand<P> {

    public ReloadCommand(String permission) {
        super(
                "Reload",
                permission,
                false,
                DefaultCommandSpec.RELOAD,
                Predicates.alwaysFalse()
        );
    }

    @Override
    public void execute(P plugin, Sender sender, ArgumentList args, String label) throws CommandException {
        LocaleMessage.RELOAD.send(sender);
        // Reload config
        plugin.reload();
    }
}
