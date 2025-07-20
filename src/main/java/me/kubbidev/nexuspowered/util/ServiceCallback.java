package me.kubbidev.nexuspowered.util;

import java.util.Optional;
import me.kubbidev.nexuspowered.Events;
import me.kubbidev.nexuspowered.Nexus;
import me.kubbidev.nexuspowered.event.MergedSubscription;
import me.kubbidev.nexuspowered.terminable.Terminable;
import org.bukkit.event.server.ServiceEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper to always provide the latest instance of a service.
 *
 * @param <T> the service class type
 */
@NotNullByDefault
public final class ServiceCallback<T> implements Terminable {

    private final Class<T>                         serviceClass;
    private final MergedSubscription<ServiceEvent> listener;
    @Nullable
    private       T                                instance = null;

    private ServiceCallback(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        this.refresh();

        // listen for service updates
        this.listener = Events.merge(
                ServiceEvent.class,
                ServiceRegisterEvent.class,
                ServiceUnregisterEvent.class
            )
            .filter(e -> e.getProvider().getService().equals(serviceClass))
            .handler(e -> this.refresh());
    }

    /**
     * Create a new ServiceCallback for the given class.
     *
     * @param serviceClass the service class
     * @param <T>          the service class type
     * @return a new service callback
     */
    public static <T> ServiceCallback<T> of(Class<T> serviceClass) {
        return new ServiceCallback<>(serviceClass);
    }

    /**
     * Refreshes the backing instance of the service.
     */
    public void refresh() {
        this.instance = Nexus.serviceNullable(this.serviceClass);
    }

    /**
     * Gets the service provider, or null if it is not provided for.
     *
     * @return the service provider
     */
    @Nullable
    public T getNullable() {
        return this.instance;
    }

    /**
     * Gets the service provider.
     *
     * @return the service provider
     */
    public Optional<T> get() {
        return Optional.ofNullable(this.instance);
    }

    @Override
    public void close() {
        this.listener.close();
    }

    @Override
    public boolean isClosed() {
        return this.listener.isClosed();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServiceCallback<?> other)) {
            return false;
        }
        return this.serviceClass.equals(other.serviceClass);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = result * PRIME + this.serviceClass.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ServiceCallback(serviceClass=" + this.serviceClass + ")";
    }
}