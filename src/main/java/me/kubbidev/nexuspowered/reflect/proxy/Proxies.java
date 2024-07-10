package me.kubbidev.nexuspowered.reflect.proxy;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * A collection of utilities for working with proxies.
 *
 * @see Proxy
 */
public final class Proxies {

    /**
     * Returns a proxy instance for the specified interfaces that dispatches method invocations to the specified invocation handler.
     *
     * @param interfaceType the interface for the proxy class to implement
     * @param handler       the invocation handler to dispatch method invocations to
     * @param <T>           the type
     * @return a proxy instance
     * @throws IllegalArgumentException if {@code interfaces} contains a class that is not an interface
     */
    @NotNull
    public static <T> T create(@NotNull Class<T> interfaceType, @NotNull InvocationHandler handler) {
        return create(interfaceType.getClassLoader(), interfaceType, handler);
    }

    /**
     * Returns a proxy instance for the specified interfaces that dispatches method invocations to the specified invocation handler.
     *
     * @param loader        the class loader to define the proxy class
     * @param interfaceType the interface for the proxy class to implement
     * @param handler       the invocation handler to dispatch method invocations to
     * @param <T>           the type
     * @return a proxy instance
     * @throws IllegalArgumentException if {@code interfaces} contains a class that is not an interface
     */
    @NotNull
    public static <T> T create(@NotNull ClassLoader loader, @NotNull Class<T> interfaceType, @NotNull InvocationHandler handler) {
        return interfaceType.cast(create(loader, new Class<?>[]{interfaceType}, handler));
    }

    /**
     * Returns a proxy instance for the specified interfaces that dispatches method invocations to the specified invocation handler.
     *
     * @param loader        the class loader to define the proxy class
     * @param interfaceType the interface for the proxy class to implement
     * @param interfaces    the list of interfaces for the proxy class to implement
     * @param handler       the invocation handler to dispatch method invocations to
     * @param <T>           the type
     * @return a proxy instance
     * @throws IllegalArgumentException if {@code interfaces} contains a class that is not an interface
     */
    @NotNull
    public static <T> T create(@NotNull ClassLoader loader, @NotNull Class<T> interfaceType, @NotNull List<Class<?>> interfaces, @NotNull InvocationHandler handler) {
        return create(loader, interfaceType, interfaces.toArray(new Class<?>[0]), handler);
    }

    /**
     * Returns a proxy instance for the specified interfaces that dispatches method invocations to the specified invocation handler.
     *
     * @param loader        the class loader to define the proxy class
     * @param interfaceType the interface for the proxy class to implement
     * @param interfaces    the list of interfaces for the proxy class to implement
     * @param handler       the invocation handler to dispatch method invocations to
     * @param <T>           the type
     * @return a proxy instance
     * @throws IllegalArgumentException if {@code interfaces} contains a class that is not an interface
     */
    @NotNull
    public static <T> T create(@NotNull ClassLoader loader, @NotNull Class<T> interfaceType, @NotNull Class<?>[] interfaces, @NotNull InvocationHandler handler) {
        return interfaceType.cast(create(loader, Lists.asList(interfaceType, interfaces), handler));
    }

    /**
     * Returns a proxy instance for the specified interfaces that dispatches method invocations to the specified invocation handler.
     *
     * @param loader     the class loader to define the proxy class
     * @param interfaces the list of interfaces for the proxy class to implement
     * @param handler    the invocation handler to dispatch method invocations to
     * @return a proxy instance
     * @throws IllegalArgumentException if {@code interfaces} contains a class that is not an interface
     */
    @NotNull
    public static Object create(@NotNull ClassLoader loader, @NotNull List<Class<?>> interfaces, @NotNull InvocationHandler handler) {
        return create(loader, interfaces.toArray(new Class<?>[0]), handler);
    }

    /**
     * Returns a proxy instance for the specified interfaces that dispatches method invocations to the specified invocation handler.
     *
     * @param loader     the class loader to define the proxy class
     * @param interfaces the array of interfaces for the proxy class to implement
     * @param handler    the invocation handler to dispatch method invocations to
     * @return a proxy instance
     * @throws IllegalArgumentException if {@code interfaces} contains a class that is not an interface
     * @see Proxy#newProxyInstance(ClassLoader, Class[], InvocationHandler)
     */
    @NotNull
    public static Object create(@NotNull ClassLoader loader, @NotNull Class<?>[] interfaces, @NotNull InvocationHandler handler) {
        return Proxy.newProxyInstance(loader, interfaces, handler);
    }

    private Proxies() {
    }
}
