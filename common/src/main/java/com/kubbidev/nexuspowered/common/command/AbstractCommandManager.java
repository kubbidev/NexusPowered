package com.kubbidev.nexuspowered.common.command;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kubbidev.java.module.Loadable;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.command.abstraction.Command;
import com.kubbidev.nexuspowered.common.command.abstraction.CommandException;
import com.kubbidev.nexuspowered.common.command.tabcomplete.TabCompleter;
import com.kubbidev.nexuspowered.common.command.tabcomplete.TabCompletions;
import com.kubbidev.nexuspowered.common.command.util.ArgumentList;
import com.kubbidev.nexuspowered.common.commands.HelpCommand;
import com.kubbidev.nexuspowered.common.commands.InfoCommand;
import com.kubbidev.nexuspowered.common.commands.MainCommand;
import com.kubbidev.nexuspowered.common.commands.ReloadCommand;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerAdapter;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerTask;
import com.kubbidev.nexuspowered.common.locale.LocaleMessage;
import com.kubbidev.nexuspowered.common.sender.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class AbstractCommandManager<P extends NexusPlugin<P>> implements Loadable {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("nexuspowered-command-executor")
            .build());

    private final P plugin;
    private final AtomicBoolean executingCommand = new AtomicBoolean(false);
    private final TabCompletions tabCompletions;

    private final Map<String, Command<P, ?>> commands = new HashMap<>();

    public AbstractCommandManager(P plugin) {
        this.plugin = plugin;
        this.tabCompletions = new TabCompletions(plugin);
    }

    @Override
    public void setup() {
        // Create main plugin command and attach help sub-command as a default executor.
        String label = plugin.getConfiguration().get(plugin.provideDefaultLabel());
        String permission = plugin.getId();

        MainCommand<P> mainCommand = new MainCommand<>(label, permission);

        mainCommand.addChildren(new InfoCommand<>(permission + ".info"));
        mainCommand.addChildren(new HelpCommand<>(permission + ".help"));
        mainCommand.addChildren(new ReloadCommand<>(permission + ".reload"));

        this.plugin.registerCommands(this, mainCommand);

        registerCommand(label, mainCommand);
    }

    @Override
    public void shutdown() {

    }

    /**
     * This method is used by other platform to register command into their command system.
     *
     * @param label the label of the command to register.
     * @param command to register into the command system.
     */
    public abstract void registerCommand(String label, Command<P, ?> command);

    /**
     * This method is used by other platform to unregister command from their command system.
     *
     * @param label the command label to unregister.
     */
    public abstract void unregisterCommand(String label);

    public P getPlugin() {
        return this.plugin;
    }

    public TabCompletions getTabCompletions() {
        return this.tabCompletions;
    }

    public Map<String, Command<P, ?>> getCommands() {
        return this.commands;
    }

    public CompletableFuture<Void> executeCommand(Sender sender, String label, List<String> args) {

        SchedulerAdapter scheduler = this.plugin.getSchedulerAdapter();
        List<String> argsCopy = new ArrayList<>(args);

        // if the executingCommand flag is set, there is another command executing at the moment
        if (this.executingCommand.get()) {
            LocaleMessage.ALREADY_EXECUTING_COMMAND.send(sender);
        }

        // a reference to the thread being used to execute the command
        AtomicReference<Thread> executorThread = new AtomicReference<>();
        // a reference to the timeout task scheduled to catch if this command takes too long to execute
        AtomicReference<SchedulerTask> timeoutTask = new AtomicReference<>();

        // schedule the actual execution of the command using the command executor service
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // set flags
            executorThread.set(Thread.currentThread());
            this.executingCommand.set(true);

            // actually try to execute the command
            try {
                execute(sender, label, args);
            } catch (Throwable e) {
                // catch any exception
                this.plugin.getLoggerAdapter().severe("Exception whilst executing command: {}", args, e);
            } finally {
                // unset flags
                this.executingCommand.set(false);
                executorThread.set(null);

                // cancel the timeout task
                SchedulerTask timeout;
                if ((timeout = timeoutTask.get()) != null) {
                    timeout.cancel();
                }
            }
        }, EXECUTOR);

        // schedule another task to catch if the command doesn't complete after 10 seconds
        timeoutTask.set(scheduler.asyncLater(() -> {
            if (!future.isDone()) {
                handleCommandTimeout(executorThread, argsCopy);
            }
        }, 10, TimeUnit.SECONDS));

        return future;
    }

    private void handleCommandTimeout(AtomicReference<Thread> thread, List<String> args) {
        Thread executorThread = thread.get();
        if (executorThread == null) {
            this.plugin.getLoggerAdapter().warn("Command execution {} has not completed - is another command execution blocking it?", args);
        } else {
            String stackTrace = Arrays.stream(executorThread.getStackTrace())
                    .map(el -> "  " + el.toString())
                    .collect(Collectors.joining("\n"));
            this.plugin.getLoggerAdapter().warn("Command execution {} has not completed. Trace: \n{}", args, stackTrace);
        }
    }

    public boolean hasPermissionForAny(Sender sender) {
        return this.commands.values().stream().anyMatch(c -> c.shouldDisplay() && c.isAuthorized(sender));
    }

    private void execute(Sender sender, String label, List<String> arguments) {

        // Handle no arguments
        if (arguments.isEmpty() || arguments.size() == 1 && arguments.get(0).trim().isEmpty()) {
            sender.sendMessage(LocaleMessage.prefixed(Component.text()
                    .color(NamedTextColor.DARK_GREEN)
                    .append(Component.text("Running "))
                    .append(Component.text(NexusPlugin.getEngineName(), NamedTextColor.AQUA))
                    .append(Component.space())
                    .append(Component.text("v" + this.plugin.getPluginVersion(), NamedTextColor.AQUA))
                    .append(LocaleMessage.FULL_STOP)
            ));
            return;
        }

        // Look for the main command.
        Command<P, ?> command = this.commands.get(label.toLowerCase(Locale.ROOT));

        // Main command not found
        if (command == null) {
            sendCommandUsage(sender, label);
            return;
        }

        // Check the command allow console to execute it.
        if (command.isPlayersOnly() && sender.isConsole()) {
            LocaleMessage.COMMAND_PLAYER_ONLY.send(sender);
            return;
        }

        // Check the Sender has permission to use the main command.
        if (!command.isAuthorized(sender)) {
            sendCommandUsage(sender, label);
            return;
        }

        // Check the correct number of args were given for the main command
        if (command.getArgumentCheck().test(arguments.size())) {
            command.sendDetailedUsage(sender, label);
            return;
        }

        // Try to execute the command.
        try {
            command.execute(this.plugin, sender, null, new ArgumentList(arguments), label);
        } catch (CommandException e) {
            e.handle(sender, label, command);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public List<String> tabCompleteCommand(Sender sender, String label, List<String> arguments) {
        List<Command<P, ?>> mains = this.commands.values().stream()
                .filter(Command::shouldDisplay)
                .filter(m -> m.isAuthorized(sender))
                .toList();

        return TabCompleter.create()
                .from(0, partial -> mains.stream()
                        .filter(m -> m.getName().equalsIgnoreCase(label))
                        .findFirst()
                        .map(cmd -> cmd.tabComplete(this.plugin, sender, new ArgumentList(arguments)))
                        .orElse(Collections.emptyList())
                )
                .complete(arguments);
    }

    private void sendCommandUsage(Sender sender, String label) {
        sender.sendMessage(LocaleMessage.prefixed(Component.text()
                .color(NamedTextColor.DARK_GREEN)
                .append(Component.text("Running "))
                .append(Component.text(NexusPlugin.getEngineName(), NamedTextColor.AQUA))
                .append(Component.space())
                .append(Component.text("v" + this.plugin.getPluginVersion(), NamedTextColor.AQUA))
                .append(LocaleMessage.FULL_STOP)
        ));

        this.commands.values().stream()
                .filter(Command::shouldDisplay)
                .filter(c -> c.isAuthorized(sender))
                .forEach(c -> sender.sendMessage(Component.text()
                        .append(Component.text('>', NamedTextColor.DARK_AQUA))
                        .append(Component.space())
                        .append(Component.text(String.format(c.getUsage(), label), NamedTextColor.GREEN))
                        .clickEvent(ClickEvent.suggestCommand(String.format(c.getUsage(), label)))
                        .build()
                ));
    }
}
