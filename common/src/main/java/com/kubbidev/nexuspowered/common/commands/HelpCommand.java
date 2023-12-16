package com.kubbidev.nexuspowered.common.commands;

import com.kubbidev.java.util.Predicates;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.command.abstraction.Command;
import com.kubbidev.nexuspowered.common.command.abstraction.CommandException;
import com.kubbidev.nexuspowered.common.command.abstraction.SingleCommand;
import com.kubbidev.nexuspowered.common.command.util.ArgumentList;
import com.kubbidev.nexuspowered.common.commands.spec.DefaultCommandSpec;
import com.kubbidev.nexuspowered.common.locale.LocaleMessage;
import com.kubbidev.nexuspowered.common.sender.Sender;

import java.util.HashSet;
import java.util.Set;

public class HelpCommand<P extends NexusPlugin<P>> extends SingleCommand<P> {

    public HelpCommand(String permission) {
        super(
                "Help",
                permission,
                false,
                DefaultCommandSpec.HELP,
                Predicates.alwaysFalse()
        );
    }

    @Override
    public void execute(P plugin, Sender sender, ArgumentList args, String label) throws CommandException {

        Set<Command<P, ?>> commands = new HashSet<>(plugin.getCommandManager().getCommands().values());
        for (Command<P, ?> command : commands) {
            if (!command.isAuthorized(sender) || command instanceof MainCommand)
                continue;

            String mainLabel = "";
            String subLabel = "";

//            if (command instanceof CommandTree) {
//                mainLabel = command.getName();
//                subLabel = "";
//            } else if (command instanceof CommandChild) {
//                mainLabel = command.getName();
//                subLabel = command.getName();
//            } else return;

            LocaleMessage.HELP.send(sender, mainLabel, subLabel, command.getDescription());
        }
    }
}
