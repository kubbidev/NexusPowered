package me.kubbidev.nexuspowered;

import com.google.common.reflect.TypeToken;
import me.kubbidev.nexuspowered.event.functional.merged.MergedSubscriptionBuilder;
import me.kubbidev.nexuspowered.event.functional.single.SingleSubscriptionBuilder;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/**
 * A functional event listening utility.
 */
public final class Events {

    private Events() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Makes a SingleSubscriptionBuilder for a given event
     *
     * @param eventClass the class of the event
     * @param <T>        the event type
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventClass is null
     */
    @NotNull
    public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(@NotNull Class<T> eventClass) {
        return SingleSubscriptionBuilder.newBuilder(eventClass);
    }

    /**
     * Makes a SingleSubscriptionBuilder for a given event
     *
     * @param eventClass the class of the event
     * @param priority   the priority to listen at
     * @param <T>        the event type
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventClass or priority is null
     */
    @NotNull
    public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(@NotNull Class<T> eventClass,
                                                                           @NotNull EventPriority priority) {
        return SingleSubscriptionBuilder.newBuilder(eventClass, priority);
    }

    /**
     * Makes a MergedSubscriptionBuilder for a given super type
     *
     * @param handledClass the super type of the event handler
     * @param <T>          the super type class
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NotNull
    public static <T> MergedSubscriptionBuilder<T> merge(@NotNull Class<T> handledClass) {
        return MergedSubscriptionBuilder.newBuilder(handledClass);
    }

    /**
     * Makes a MergedSubscriptionBuilder for a given super type
     *
     * @param type the super type of the event handler
     * @param <T>  the super type class
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NotNull
    public static <T> MergedSubscriptionBuilder<T> merge(@NotNull TypeToken<T> type) {
        return MergedSubscriptionBuilder.newBuilder(type);
    }

    /**
     * Makes a MergedSubscriptionBuilder for a super event class
     *
     * @param superClass   the abstract super event class
     * @param eventClasses the event classes to be bound to
     * @param <S>          the super class type
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NotNull
    @SafeVarargs
    public static <S extends Event> MergedSubscriptionBuilder<S> merge(@NotNull Class<S> superClass,
                                                                       @NotNull Class<? extends S>... eventClasses) {
        return MergedSubscriptionBuilder.newBuilder(superClass, eventClasses);
    }

    /**
     * Makes a MergedSubscriptionBuilder for a super event class
     *
     * @param superClass   the abstract super event class
     * @param priority     the priority to listen at
     * @param eventClasses the event classes to be bound to
     * @param <S>          the super class type
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NotNull
    @SafeVarargs
    public static <S extends Event> MergedSubscriptionBuilder<S> merge(@NotNull Class<S> superClass,
                                                                       @NotNull EventPriority priority,
                                                                       @NotNull Class<? extends S>... eventClasses) {
        return MergedSubscriptionBuilder.newBuilder(superClass, priority, eventClasses);
    }

    /**
     * Submit the event on the current thread
     *
     * @param event the event to call
     */
    public static void call(@NotNull Event event) {
        Nexus.plugins().callEvent(event);
    }

    /**
     * Submit the event on a new async thread.
     *
     * @param event the event to call
     */
    public static void callAsync(@NotNull Event event) {
        Schedulers.async().run(() -> call(event));
    }

    /**
     * Submit the event on the main server thread.
     *
     * @param event the event to call
     */
    public static void callSync(@NotNull Event event) {
        Schedulers.sync().run(() -> call(event));
    }

    /**
     * Submit the event on the current thread
     *
     * @param event the event to call
     */
    @NotNull
    public static <T extends Event> T callAndReturn(@NotNull T event) {
        Nexus.plugins().callEvent(event);
        return event;
    }

    /**
     * Submit the event on a new async thread.
     *
     * @param event the event to call
     */
    @NotNull
    public static <T extends Event> T callAsyncAndJoin(@NotNull T event) {
        return Schedulers.async().supply(() -> callAndReturn(event)).join();
    }

    /**
     * Submit the event on the main server thread.
     *
     * @param event the event to call
     */
    @NotNull
    public static <T extends Event> T callSyncAndJoin(@NotNull T event) {
        return Schedulers.sync().supply(() -> callAndReturn(event)).join();
    }
}
