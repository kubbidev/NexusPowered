package me.kubbidev.nexuspowered.event.functional.single;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import me.kubbidev.nexuspowered.event.SingleSubscription;
import me.kubbidev.nexuspowered.internal.LoaderUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

class SingleHandlerListImpl<T extends Event> implements SingleHandlerList<T> {

    private final SingleSubscriptionBuilderImpl<T>                   builder;
    private final List<BiConsumer<SingleSubscription<T>, ? super T>> handlers = new ArrayList<>(1);

    SingleHandlerListImpl(@NotNull SingleSubscriptionBuilderImpl<T> builder) {
        this.builder = builder;
    }

    @Override
    public @NotNull SingleHandlerList<T> biConsumer(@NotNull BiConsumer<SingleSubscription<T>, ? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @Override
    public @NotNull SingleSubscription<T> register() {
        if (this.handlers.isEmpty()) {
            throw new IllegalStateException("No handlers have been registered");
        }

        NexusEventListener<T> listener = new NexusEventListener<>(this.builder, this.handlers);
        listener.register(LoaderUtils.getPlugin());
        return listener;
    }
}