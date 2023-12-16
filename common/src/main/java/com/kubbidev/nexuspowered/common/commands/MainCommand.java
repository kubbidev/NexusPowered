package com.kubbidev.nexuspowered.common.commands;

import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.command.abstraction.GeneralCommand;
import com.kubbidev.nexuspowered.common.commands.spec.DefaultCommandSpec;

public class MainCommand<P extends NexusPlugin<P>> extends GeneralCommand<P> {

    public MainCommand(String label, String permission) {
        super(
                label,
                permission,
                false,
                DefaultCommandSpec.MAIN
        );
    }
}
