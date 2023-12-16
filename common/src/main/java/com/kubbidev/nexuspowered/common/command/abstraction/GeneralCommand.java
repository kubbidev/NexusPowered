package com.kubbidev.nexuspowered.common.command.abstraction;

import com.kubbidev.java.util.Predicates;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.command.spec.CommandSpec;
import com.kubbidev.nexuspowered.common.command.tabcomplete.CompletionSupplier;
import com.kubbidev.nexuspowered.common.command.tabcomplete.TabCompleter;
import com.kubbidev.nexuspowered.common.command.util.ArgumentList;
import com.kubbidev.nexuspowered.common.locale.LocaleMessage;
import com.kubbidev.nexuspowered.common.sender.Sender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class GeneralCommand<P extends NexusPlugin<P>> extends Command<P, Void> {

    /** Child sub commands */
    private final List<Command<P, Void>> children;

    public GeneralCommand(String name, @Nullable String permission, boolean playersOnly, CommandSpec spec) {
        this(name, permission, playersOnly, spec, new ArrayList<>());
    }

    public GeneralCommand(String name, @Nullable String permission, boolean playersOnly, CommandSpec spec, @NotNull List<Command<P, Void>> children) {
        super(name, permission, playersOnly, spec, Predicates.alwaysFalse());
        this.children = children;
    }

    public void addChildren(@NotNull Command<P, Void> command) {
        this.children.add(command);
    }

    public @NotNull List<Command<P, Void>> getChildren() {
        return this.children;
    }

    @Override
    public void execute(P plugin, Sender sender, Void ignored, ArgumentList args, String label) throws CommandException {
        // check if required argument and/or subcommand is missing
        if (args.isEmpty()) {
            sendUsage(sender, label);
            return;
        }

        Command<P, Void> sub = getChildren().stream()
                .filter(s -> s.getName().equalsIgnoreCase(args.get(0)))
                .findFirst()
                .orElse(null);

        if (sub == null) {
            LocaleMessage.COMMAND_NOT_RECOGNISED.send(sender);
            return;
        }

        if (sub.isPlayersOnly() && sender.isConsole()) {
            LocaleMessage.COMMAND_PLAYER_ONLY.send(sender);
            return;
        }

        if (!sub.isAuthorized(sender)) {
            LocaleMessage.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        if (sub.getArgumentCheck().test(args.size() - 1)) {
            sub.sendDetailedUsage(sender, label);
            return;
        }

        try {
            sub.execute(plugin, sender, null, args.subList(1, args.size()), label);
        } catch (CommandException e) {
            e.handle(sender, label, sub);
        }
    }

    @Override
    public List<String> tabComplete(P plugin, Sender sender, ArgumentList args) {
        return TabCompleter.create()
                .at(0, CompletionSupplier.startsWith(() -> getChildren().stream()
                        .filter(s -> s.isAuthorized(sender))
                        .map(s -> s.getName().toLowerCase(Locale.ROOT))
                ))
                .from(1, partial -> getChildren().stream()
                        .filter(s -> s.isAuthorized(sender))
                        .filter(s -> s.getName().equalsIgnoreCase(args.get(0)))
                        .findFirst()
                        .map(cmd -> cmd.tabComplete(plugin, sender, args.subList(1, args.size())))
                        .orElse(Collections.emptyList())
                )
                .complete(args);
    }

    @Override
    public void sendUsage(Sender sender, String label) {
        List<Command<P, ?>> subs = getChildren().stream()
                .filter(s -> s.isAuthorized(sender))
                .collect(Collectors.toList());

        if (!subs.isEmpty()) {
            LocaleMessage.MAIN_COMMAND_USAGE_HEADER.send(sender, getName(), String.format(getUsage(), label));
            for (Command<P, ?> s : subs) {
                s.sendUsage(sender, label);
            }
        } else {
            LocaleMessage.COMMAND_NO_PERMISSION.send(sender);
        }
    }

    @Override
    public void sendDetailedUsage(Sender sender, String label) {
        sendUsage(sender, label);
    }

    @Override
    public boolean isAuthorized(Sender sender) {
        return getChildren().stream().anyMatch(sc -> sc.isAuthorized(sender));
    }
}
