package me.kubbidev.nexuspowered.command.tabcomplete;

import com.google.common.base.Preconditions;
import me.kubbidev.nexuspowered.command.CommandInterruptException;
import me.kubbidev.nexuspowered.command.context.CommandContext;
import me.kubbidev.nexuspowered.command.functional.FunctionalTabHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility for computing tab completion results
 */
public class TabCompleter<T extends CommandSender> implements FunctionalTabHandler<T> {
    private TabCompleter() {}

    public static <T extends CommandSender> TabCompleter<T> create() {
        return new TabCompleter<>();
    }

    private final Map<Integer, CompletionSupplier> suppliers = new HashMap<>();
    private int from = Integer.MAX_VALUE;

    /**
     * Marks that the given completion supplier should be used to compute tab
     * completions at the given index.
     *
     * @param position the position
     * @param supplier the supplier
     * @return this
     */
    public TabCompleter<T> at(int position, CompletionSupplier supplier) {
        Preconditions.checkState(position < this.from);
        this.suppliers.put(position, supplier);
        return this;
    }

    /**
     * Marks that the given completion supplier should be used to compute tab
     * completions at the given index and at all subsequent indexes infinitely.
     *
     * @param position the position
     * @param supplier the supplier
     * @return this
     */
    public TabCompleter<T> from(int position, CompletionSupplier supplier) {
        Preconditions.checkState(this.from == Integer.MAX_VALUE);
        this.suppliers.put(position, supplier);
        this.from = position;
        return this;
    }

    @Override
    public @Nullable List<String> handle(CommandContext<T> context) throws CommandInterruptException {
        List<String> args = context.args();
        int lastIndex = 0;
        String partial;

        // nothing entered yet
        if (args.isEmpty() || (partial = args.get(lastIndex = args.size() - 1)).trim().isEmpty()) {
            return getCompletions(lastIndex, "");
        }

        // started typing something
        return getCompletions(lastIndex, partial);
    }

    private List<String> getCompletions(int position, String partial) throws CommandInterruptException {
        if (position >= this.from) {
            return this.suppliers.get(this.from).supplyCompletions(partial);
        }

        return this.suppliers.getOrDefault(position, CompletionSupplier.EMPTY).supplyCompletions(partial);
    }
}