package me.kubbidev.nexuspowered.event.functional.single;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import me.kubbidev.nexuspowered.event.SingleSubscription;
import me.kubbidev.nexuspowered.event.functional.FunctionalHandlerList;
import me.kubbidev.nexuspowered.util.Delegates;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public interface SingleHandlerList<T extends Event> extends FunctionalHandlerList<T, SingleSubscription<T>> {

    @Override
    default @NotNull SingleHandlerList<T> consumer(@NotNull Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return this.biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @Override
    @NotNull SingleHandlerList<T> biConsumer(@NotNull BiConsumer<SingleSubscription<T>, ? super T> handler);
}