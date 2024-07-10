package me.kubbidev.nexuspowered.event.functional.single;

import me.kubbidev.nexuspowered.event.SingleSubscription;
import me.kubbidev.nexuspowered.event.functional.ExpiryTestStage;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

class SingleSubscriptionBuilderImpl<T extends Event> implements SingleSubscriptionBuilder<T> {
    final Class<T> eventClass;
    final EventPriority priority;

    BiConsumer<? super T, Throwable> exceptionConsumer = DEFAULT_EXCEPTION_CONSUMER;
    boolean handleSubclasses = false;

    final List<Predicate<T>> filters = new ArrayList<>(3);
    final List<BiPredicate<SingleSubscription<T>, T>> preExpiryTests = new ArrayList<>(0);
    final List<BiPredicate<SingleSubscription<T>, T>> midExpiryTests = new ArrayList<>(0);
    final List<BiPredicate<SingleSubscription<T>, T>> postExpiryTests = new ArrayList<>(0);

    SingleSubscriptionBuilderImpl(Class<T> eventClass, EventPriority priority) {
        this.eventClass = eventClass;
        this.priority = priority;
    }

    @NotNull
    @Override
    public SingleSubscriptionBuilder<T> expireIf(@NotNull BiPredicate<SingleSubscription<T>, T> predicate, @NotNull ExpiryTestStage... testPoints) {
        Objects.requireNonNull(testPoints, "testPoints");
        Objects.requireNonNull(predicate, "predicate");
        for (ExpiryTestStage testPoint : testPoints) {
            switch (testPoint) {
                case PRE:
                    this.preExpiryTests.add(predicate);
                    break;
                case POST_FILTER:
                    this.midExpiryTests.add(predicate);
                    break;
                case POST_HANDLE:
                    this.postExpiryTests.add(predicate);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown ExpiryTestPoint: " + testPoint);
            }
        }
        return this;
    }

    @NotNull
    @Override
    public SingleSubscriptionBuilder<T> filter(@NotNull Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.filters.add(predicate);
        return this;
    }

    @NotNull
    @Override
    public SingleSubscriptionBuilder<T> exceptionConsumer(@NotNull BiConsumer<? super T, Throwable> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer, "exceptionConsumer");
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    @NotNull
    @Override
    public SingleSubscriptionBuilder<T> handleSubclasses() {
        this.handleSubclasses = true;
        return this;
    }

    @NotNull
    @Override
    public SingleHandlerList<T> handlers() {
        return new SingleHandlerListImpl<>(this);
    }

}