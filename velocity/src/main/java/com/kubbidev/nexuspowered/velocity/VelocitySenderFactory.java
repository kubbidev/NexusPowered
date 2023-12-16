package com.kubbidev.nexuspowered.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.kubbidev.java.util.TriState;
import com.kubbidev.nexuspowered.common.locale.TranslationManager;
import com.kubbidev.nexuspowered.common.sender.Sender;
import com.kubbidev.nexuspowered.common.sender.SenderFactory;
import com.kubbidev.nexuspowered.common.util.ComponentUtils;
import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.UUID;

public class VelocitySenderFactory<P extends VelocityNexusPlugin<P>> extends SenderFactory<P, CommandSource> {
    public VelocitySenderFactory(P plugin) {
        super(plugin);
    }

    @Override
    protected String getName(CommandSource source) {
        if (source instanceof Player) {
            return ((Player) source).getUsername();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected UUID getUniqueId(CommandSource source) {
        if (source instanceof Player) {
            return ((Player) source).getUniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected void sendMessage(CommandSource source, Component message) {
        source.sendMessage(message);
    }

    @Override
    protected void sendMessage(CommandSource source, String message) {
        source.sendMessage(ComponentUtils.fromMiniMessage(message));
    }

    @Override
    protected TriState getPermissionValue(CommandSource source, String node) {
        Tristate tristate = source.getPermissionValue(node);

        return switch (tristate) {
            case TRUE -> TriState.TRUE;
            case FALSE -> TriState.FALSE;
            default -> TriState.NOT_SET;
        };
    }

    @Override
    protected boolean hasPermission(CommandSource source, String node) {
        return source.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSource source, String command) {
        getPlugin().getProxy().getCommandManager().executeAsync(source, command).join();
    }

    @Override
    protected boolean isConsole(CommandSource sender) {
        return sender instanceof ConsoleCommandSource;
    }

    @Override
    protected Locale getLocale(CommandSource sender) {
        Locale locale = Locale.getDefault();

        if (sender instanceof Player) {
            locale = ((Player) sender).getEffectiveLocale();
        }
        if (locale == null) {
            locale = TranslationManager.DEFAULT_LOCALE;
        }
        return locale;
    }
}