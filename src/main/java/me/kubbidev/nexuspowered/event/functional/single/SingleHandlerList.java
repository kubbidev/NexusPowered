package me.kubbidev.nexuspowered.event.functional.single;

import me.kubbidev.nexuspowered.event.SingleSubscription;
import me.kubbidev.nexuspowered.event.functional.FunctionalHandlerList;
import me.kubbidev.nexuspowered.util.Delegates;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SingleHandlerList<T extends Event> extends FunctionalHandlerList<T, SingleSubscription<T>> {

    @NotNull
    @Override
    default SingleHandlerList<T> consumer(@NotNull Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @NotNull
    @Override
    SingleHandlerList<T> biConsumer(@NotNull BiConsumer<SingleSubscription<T>, ? super T> handler);
}