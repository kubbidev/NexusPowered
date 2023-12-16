package com.kubbidev.nexuspowered.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.kubbidev.nexuspowered.common.command.AbstractCommandManager;
import com.kubbidev.nexuspowered.common.command.abstraction.Command;

public class VelocityCommandManager<P extends VelocityNexusPlugin<P>> extends AbstractCommandManager<P> {

    private final P plugin;

    public VelocityCommandManager(P plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void registerCommand(String label, Command<P, ?> command) {
        if (getCommands().put(label, command) == null) {

            VelocityCommandExecutor executor = new VelocityCommandExecutor(plugin, command);

            CommandManager manager = plugin.getProxy().getCommandManager();
            CommandMeta commandMeta = manager.metaBuilder(label)
                    .plugin(plugin)
                    .build();

            manager.register(commandMeta, executor);
        }
    }

    @Override
    public void unregisterCommand(String label) {
        if (getCommands().remove(label) != null) {

            CommandManager manager = plugin.getProxy().getCommandManager();
            CommandMeta commandMeta = manager.metaBuilder(label)
                    .plugin(plugin)
                    .build();

            manager.unregister(commandMeta);
        }
    }
}
