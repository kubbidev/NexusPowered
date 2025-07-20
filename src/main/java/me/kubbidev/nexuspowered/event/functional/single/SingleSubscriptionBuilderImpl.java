package me.kubbidev.nexuspowered.event.functional.single;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import me.kubbidev.nexuspowered.event.SingleSubscription;
import me.kubbidev.nexuspowered.event.functional.ExpiryTestStage;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

class SingleSubscriptionBuilderImpl<T extends Event> implements SingleSubscriptionBuilder<T> {

    final Class<T>                                    eventClass;
    final EventPriority                               priority;
    final List<Predicate<T>>                          filters         = new ArrayList<>(3);
    final List<BiPredicate<SingleSubscription<T>, T>> preExpiryTests  = new ArrayList<>(0);
    final List<BiPredicate<SingleSubscription<T>, T>> midExpiryTests  = new ArrayList<>(0);
    final List<BiPredicate<SingleSubscription<T>, T>> postExpiryTests = new ArrayList<>(0);
    BiConsumer<? super T, Throwable> exceptionConsumer = DEFAULT_EXCEPTION_CONSUMER;
    boolean                          handleSubclasses  = false;

    SingleSubscriptionBuilderImpl(Class<T> eventClass, EventPriority priority) {
        this.eventClass = eventClass;
        this.priority = priority;
    }

    @Override
    public @NotNull SingleSubscriptionBuilder<T> expireIf(@NotNull BiPredicate<SingleSubscription<T>, T> predicate,
                                                          @NotNull ExpiryTestStage... testPoints) {
        Objects.requireNonNull(testPoints, "testPoints");
        Objects.requireNonNull(predicate, "predicate");
        for (ExpiryTestStage testPoint : testPoints) {
            switch (testPoint) {
                case PRE -> this.preExpiryTests.add(predicate);
                case POST_FILTER -> this.midExpiryTests.add(predicate);
                case POST_HANDLE -> this.postExpiryTests.add(predicate);
                default -> throw new IllegalArgumentException("Unknown ExpiryTestPoint: " + testPoint);
            }
        }
        return this;
    }

    @Override
    public @NotNull SingleSubscriptionBuilder<T> filter(@NotNull Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.filters.add(predicate);
        return this;
    }

    @Override
    public @NotNull SingleSubscriptionBuilder<T> exceptionConsumer(
        @NotNull BiConsumer<? super T, Throwable> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer, "exceptionConsumer");
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    @Override
    public @NotNull SingleSubscriptionBuilder<T> handleSubclasses() {
        this.handleSubclasses = true;
        return this;
    }

    @Override
    public @NotNull SingleHandlerList<T> handlers() {
        return new SingleHandlerListImpl<>(this);
    }
}