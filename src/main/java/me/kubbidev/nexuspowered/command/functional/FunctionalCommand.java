package me.kubbidev.nexuspowered.command.functional;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import me.kubbidev.nexuspowered.command.AbstractCommand;
import me.kubbidev.nexuspowered.command.CommandInterruptException;
import me.kubbidev.nexuspowered.command.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

@NotNullByDefault
class FunctionalCommand<T extends CommandSender> extends AbstractCommand {

    private final ImmutableList<Predicate<CommandContext<?>>> predicates;
    private final FunctionalCommandHandler<T>                 handler;
    @Nullable
    private final FunctionalTabHandler<T>                     tabHandler;

    FunctionalCommand(ImmutableList<Predicate<CommandContext<?>>> predicates, FunctionalCommandHandler<T> handler,
                      @Nullable FunctionalTabHandler<T> tabHandler,
                      @Nullable String permission,
                      @Nullable String description) {
        this.predicates = predicates;
        this.handler = handler;
        this.tabHandler = tabHandler;
        this.permission = permission;
        this.description = description;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void call(@NotNull CommandContext<?> context) throws CommandInterruptException {
        for (Predicate<CommandContext<?>> predicate : this.predicates) {
            if (!predicate.test(context)) {
                return;
            }
        }

        this.handler.handle((CommandContext<T>) context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable List<String> callTabCompleter(@NotNull CommandContext<?> context)
        throws CommandInterruptException {
        if (this.tabHandler == null) {
            return null;
        }
        for (Predicate<CommandContext<?>> predicate : this.predicates) {
            if (!predicate.test(context)) {
                return null;
            }
        }

        return this.tabHandler.handle((CommandContext<T>) context);
    }
}