package me.kubbidev.nexuspowered.event.functional.merged;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import me.kubbidev.nexuspowered.event.MergedSubscription;
import me.kubbidev.nexuspowered.event.functional.FunctionalHandlerList;
import me.kubbidev.nexuspowered.util.Delegates;
import org.jetbrains.annotations.NotNull;

public interface MergedHandlerList<T> extends FunctionalHandlerList<T, MergedSubscription<T>> {

    @Override
    default @NotNull MergedHandlerList<T> consumer(@NotNull Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return this.biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @Override
    @NotNull MergedHandlerList<T> biConsumer(@NotNull BiConsumer<MergedSubscription<T>, ? super T> handler);
}