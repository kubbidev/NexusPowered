package me.kubbidev.nexuspowered.event.functional.merged;

import me.kubbidev.nexuspowered.event.MergedSubscription;
import me.kubbidev.nexuspowered.event.functional.FunctionalHandlerList;
import me.kubbidev.nexuspowered.util.Delegates;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MergedHandlerList<T> extends FunctionalHandlerList<T, MergedSubscription<T>> {

    @NotNull
    @Override
    default MergedHandlerList<T> consumer(@NotNull Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @NotNull
    @Override
    MergedHandlerList<T> biConsumer(@NotNull BiConsumer<MergedSubscription<T>, ? super T> handler);
}