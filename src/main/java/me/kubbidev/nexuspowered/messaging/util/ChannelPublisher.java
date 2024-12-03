package me.kubbidev.nexuspowered.messaging.util;

import me.kubbidev.nexuspowered.Schedulers;
import me.kubbidev.nexuspowered.messaging.Channel;
import me.kubbidev.nexuspowered.promise.ThreadContext;
import me.kubbidev.nexuspowered.scheduler.Task;
import me.kubbidev.nexuspowered.terminable.Terminable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Periodically publishes a message to a channel.
 *
 * @param <T> the message type
 */
public final class ChannelPublisher<T> implements Terminable {

    /**
     * Creates a new channel publisher.
     *
     * @param channel       the channel
     * @param duration      the duration to wait between publishing
     * @param unit          the unit of duration
     * @param threadContext the context to call the supplier in
     * @param supplier      the message supplier
     * @param <T>           the type of the message
     * @return a channel publisher
     */
    @NotNull
    public static <T> ChannelPublisher<T> create(@NotNull Channel<T> channel, long duration, @NotNull TimeUnit unit, @NotNull ThreadContext threadContext, @NotNull Supplier<? extends T> supplier) {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(unit, "unit");
        Objects.requireNonNull(threadContext, "threadContext");
        Objects.requireNonNull(supplier, "supplier");

        return new ChannelPublisher<>(channel, supplier, duration, unit, threadContext);
    }

    /**
     * Creates a new channel publisher.
     *
     * @param channel  the channel
     * @param duration the duration to wait between publishing
     * @param unit     the unit of duration
     * @param supplier the message supplier
     * @param <T>      the type of the message
     * @return a channel publisher
     */
    @NotNull
    public static <T> ChannelPublisher<T> create(@NotNull Channel<T> channel, long duration, @NotNull TimeUnit unit, @NotNull Supplier<? extends T> supplier) {
        return create(channel, duration, unit, ThreadContext.ASYNC, supplier);
    }

    private final Channel<T> channel;
    private final Supplier<? extends T> supplier;
    private final Task task;

    private ChannelPublisher(Channel<T> channel, Supplier<? extends T> supplier, long duration, TimeUnit unit, ThreadContext threadContext) {
        this.channel = channel;
        this.supplier = supplier;
        this.task = Schedulers.builder().on(threadContext).afterAndEvery(duration, unit)
                .run(this::submit);
    }

    public Channel<T> getChannel() {
        return this.channel;
    }

    private void submit() {
        this.channel.sendMessage(this.supplier.get());
    }

    @Override
    public void close() {
        this.task.close();
    }
}