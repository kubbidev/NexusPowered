package me.kubbidev.nexuspowered.event.functional.merged;

import me.kubbidev.nexuspowered.event.MergedSubscription;
import me.kubbidev.nexuspowered.internal.LoaderUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

class MergedHandlerListImpl<T> implements MergedHandlerList<T> {
    private final MergedSubscriptionBuilderImpl<T> builder;
    private final List<BiConsumer<MergedSubscription<T>, ? super T>> handlers = new ArrayList<>(1);

    MergedHandlerListImpl(@NotNull MergedSubscriptionBuilderImpl<T> builder) {
        this.builder = builder;
    }

    @NotNull
    @Override
    public MergedHandlerList<T> biConsumer(@NotNull BiConsumer<MergedSubscription<T>, ? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @NotNull
    @Override
    public MergedSubscription<T> register() {
        if (this.handlers.isEmpty()) {
            throw new IllegalStateException("No handlers have been registered");
        }

        NexusMergedEventListener<T> listener = new NexusMergedEventListener<>(this.builder, this.handlers);
        listener.register(LoaderUtils.getPlugin());
        return listener;
    }
}