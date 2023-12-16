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
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class CommandParent<P extends NexusPlugin<P>, T, I> extends Command<P, Void> {

    /** Child sub commands */
    private final List<Command<P, T>> children;

    /** The type of parent command */
    private final Type type;

    public CommandParent(String name, @Nullable String permission, boolean playersOnly, CommandSpec spec, Type type) {
        this(name, permission, playersOnly, spec, type, new ArrayList<>());
    }

    public CommandParent(String name, @Nullable String permission, boolean playersOnly, CommandSpec spec, Type type, List<Command<P, T>> children) {
        super(name, permission, playersOnly, spec, Predicates.alwaysFalse());
        this.children = children;
        this.type = type;
    }

    public @NotNull List<Command<P, T>> getChildren() {
        return this.children;
    }

    @Override
    public void execute(P plugin, Sender sender, Void ignored, ArgumentList args, String label) throws CommandException {
        // check if required argument and/or subcommand is missing
        if (args.size() < this.type.minArgs) {
            sendUsage(sender, label);
            return;
        }

        Command<P, T> sub = getChildren().stream()
                .filter(s -> s.getName().equalsIgnoreCase(args.get(this.type.cmdIndex)))
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

        if (sub.getArgumentCheck().test(args.size() - this.type.minArgs)) {
            sub.sendDetailedUsage(sender, label);
            return;
        }

        String targetArgument = args.get(0);
        I targetId = null;
        if (this.type == Type.TAKES_ARGUMENT_FOR_TARGET) {
            targetId = parseTarget(targetArgument, plugin, sender);
            if (targetId == null) {
                return;
            }
        }

        ReentrantLock lock = getLockForTarget(targetId);
        lock.lock();
        try {
            T target = getTarget(targetId, plugin, sender);
            if (target == null) {
                return;
            }

            try {
                sub.execute(plugin, sender, target, args.subList(this.type.minArgs, args.size()), label);
            } catch (CommandException e) {
                e.handle(sender, label, sub);
            }

            cleanup(target, plugin);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<String> tabComplete(P plugin, Sender sender, ArgumentList args) {
        return switch (this.type) {
            case TAKES_ARGUMENT_FOR_TARGET -> TabCompleter.create()
                    .at(0, CompletionSupplier.startsWith(() -> getTargets(plugin).stream()))
                    .at(1, CompletionSupplier.startsWith(() -> getChildren().stream()
                            .filter(s -> s.isAuthorized(sender))
                            .map(s -> s.getName().toLowerCase(Locale.ROOT))
                    ))
                    .from(2, partial -> getChildren().stream()
                            .filter(s -> s.isAuthorized(sender))
                            .filter(s -> s.getName().equalsIgnoreCase(args.get(1)))
                            .findFirst()
                            .map(cmd -> cmd.tabComplete(plugin, sender, args.subList(2, args.size())))
                            .orElse(Collections.emptyList())
                    )
                    .complete(args);
            case NO_TARGET_ARGUMENT -> TabCompleter.create()
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
        };
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

    protected abstract List<String> getTargets(P plugin);

    protected abstract I parseTarget(String target, P plugin, Sender sender);

    protected abstract ReentrantLock getLockForTarget(I target);

    protected abstract T getTarget(I target, P plugin, Sender sender);

    protected abstract void cleanup(T t, P plugin);

    public enum Type {
        // e.g. /nexus log sub-command....
        NO_TARGET_ARGUMENT(0),
        // e.g. /nexus user <USER> sub-command....
        TAKES_ARGUMENT_FOR_TARGET(1);

        private final int cmdIndex;
        private final int minArgs;

        Type(int cmdIndex) {
            this.cmdIndex = cmdIndex;
            this.minArgs = cmdIndex + 1;
        }
    }
}
