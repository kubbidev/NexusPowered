package com.kubbidev.nexuspowered.paper;

import com.kubbidev.java.util.TriState;
import com.kubbidev.nexuspowered.common.locale.TranslationManager;
import com.kubbidev.nexuspowered.common.sender.Sender;
import com.kubbidev.nexuspowered.common.sender.SenderFactory;
import com.kubbidev.nexuspowered.common.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

public class PaperSenderFactory<P extends PaperNexusPlugin<P>> extends SenderFactory<P, CommandSender> {
    public PaperSenderFactory(P plugin) {
        super(plugin);
    }

    @Override
    protected String getName(CommandSender sender) {
        if (sender instanceof Player) {
            return sender.getName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected UUID getUniqueId(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getUniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ComponentUtils.fromMiniMessage(message));
    }

    @Override
    protected TriState getPermissionValue(CommandSender sender, String node) {
        if (sender.hasPermission(node)) {
            return TriState.TRUE;
        } else if (sender.isPermissionSet(node)) {
            return TriState.FALSE;
        } else {
            return TriState.NOT_SET;
        }
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSender sender, String command) {
        getPlugin().getServer().dispatchCommand(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender;
    }

    @Override
    protected Locale getLocale(CommandSender sender) {

        Locale locale = Locale.getDefault();

        if (sender instanceof Player) {
            locale = ((Player) sender).locale();
        }
        if (locale == null) {
            locale = TranslationManager.DEFAULT_LOCALE;
        }
        return locale;
    }
}