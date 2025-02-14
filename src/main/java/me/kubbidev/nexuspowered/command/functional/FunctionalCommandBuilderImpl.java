package me.kubbidev.nexuspowered.command.functional;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import me.kubbidev.nexuspowered.command.Command;
import me.kubbidev.nexuspowered.command.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@NotNullByDefault
class FunctionalCommandBuilderImpl<T extends CommandSender> implements FunctionalCommandBuilder<T> {
    private final ImmutableList.Builder<Predicate<CommandContext<?>>> predicates;
    private @Nullable FunctionalTabHandler<T> tabHandler;
    private @Nullable String permission;
    private @Nullable String description;
    private @Nullable String permissionMessage;

    private FunctionalCommandBuilderImpl(ImmutableList.Builder<Predicate<CommandContext<?>>> predicates,
                                         @Nullable FunctionalTabHandler<T> tabHandler,
                                         @Nullable String permission,
                                         @Nullable String description, @Nullable String permissionMessage) {
        this.predicates = predicates;
        this.tabHandler = tabHandler;
        this.permission = permission;
        this.description = description;
        this.permissionMessage = permissionMessage;
    }

    FunctionalCommandBuilderImpl() {
        this(ImmutableList.builder(), null, null, null, null);
    }

    @Override
    public FunctionalCommandBuilder<T> assertPermission(String permission, @Nullable String failureMessage) {
        Objects.requireNonNull(permission, "permission");
        this.permission = permission;
        this.permissionMessage = failureMessage;
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> description(String description) {
        Objects.requireNonNull(description, "description");
        this.description = description;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> test, @Nullable String failureMessage) {
        this.predicates.add(context -> {
            if (test.test((CommandContext<? extends T>) context)) {
                return true;
            }
            if (failureMessage != null) {
                context.reply(failureMessage);
            }
            return false;
        });
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertOp(String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender().isOp()) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FunctionalCommandBuilder<Player> assertPlayer(String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender() instanceof Player) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        // cast the generic type
        return (FunctionalCommandBuilder<@NotNull Player>) new FunctionalCommandBuilderImpl<>(
                this.predicates, this.tabHandler, this.permission, this.description, this.permissionMessage);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FunctionalCommandBuilder<ConsoleCommandSender> assertConsole(String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender() instanceof ConsoleCommandSender) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        // cast the generic type
        return (FunctionalCommandBuilder<@NotNull ConsoleCommandSender>) new FunctionalCommandBuilderImpl<>(
                this.predicates, this.tabHandler, this.permission, this.description, this.permissionMessage);
    }

    @Override
    public FunctionalCommandBuilder<T> assertUsage(String usage, String failureMessage) {
        Objects.requireNonNull(usage, "usage");
        Objects.requireNonNull(failureMessage, "failureMessage");

        List<String> usageParts = Splitter.on(" ").splitToList(usage);

        int requiredArgs = 0;
        for (String usagePart : usageParts) {
            if (!usagePart.startsWith("<") && !usagePart.endsWith(">")) {
                // assume it's a required argument
                requiredArgs++;
            }
        }

        int finalRequiredArgs = requiredArgs;
        this.predicates.add(context -> {
            if (context.args().size() >= finalRequiredArgs) {
                return true;
            }

            context.reply(failureMessage.replace("{usage}", "/" + context.label() + " " + usage));
            return false;
        });

        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertArgument(int index, Predicate<@Nullable String> test, String failureMessage) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            String arg = context.rawArg(index);
            if (test.test(arg)) {
                return true;
            }

            context.reply(failureMessage
                    .replace("{arg}", String.valueOf(arg))
                    .replace("{index}", Integer.toString(index))
            );
            return false;
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FunctionalCommandBuilder<T> assertSender(Predicate<T> test, String failureMessage) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            T sender = (T) context.sender();
            if (test.test(sender)) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> tabHandler(FunctionalTabHandler<T> tabHandler) {
        this.tabHandler = tabHandler;
        return this;
    }

    @Override
    public Command handler(FunctionalCommandHandler<T> handler) {
        Objects.requireNonNull(handler, "handler");
        return new FunctionalCommand<>(this.predicates.build(), handler, this.tabHandler, this.permission, this.description, this.permissionMessage);
    }
}