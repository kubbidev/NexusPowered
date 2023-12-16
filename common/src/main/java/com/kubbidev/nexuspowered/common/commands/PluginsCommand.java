package com.kubbidev.nexuspowered.common.commands;

import com.kubbidev.java.util.Predicates;
import com.kubbidev.nexuspowered.common.NexusEngine;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.command.abstraction.CommandException;
import com.kubbidev.nexuspowered.common.command.abstraction.SingleCommand;
import com.kubbidev.nexuspowered.common.command.util.ArgumentList;
import com.kubbidev.nexuspowered.common.commands.spec.DefaultCommandSpec;
import com.kubbidev.nexuspowered.common.locale.LocaleMessage;
import com.kubbidev.nexuspowered.common.sender.Sender;

import java.util.ArrayList;
import java.util.List;

public class PluginsCommand<P extends NexusEngine<P>> extends SingleCommand<P> {

    public PluginsCommand() {
        super(
                "Plugins",
                "nexuspowered.plugins",
                false,
                DefaultCommandSpec.PLUGINS,
                Predicates.alwaysFalse()
        );
    }

    @Override
    public void execute(P plugin, Sender sender, ArgumentList args, String label) throws CommandException {

        List<NexusPlugin<?>> plugins = new ArrayList<>(plugin.getChildPlugins());

        if (plugins.isEmpty()) {
            LocaleMessage.NO_PLUGIN_INSTALL.send(sender);
        } else LocaleMessage.PLUGIN.send(sender, plugins);
    }
}
