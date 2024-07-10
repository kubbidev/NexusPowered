package me.kubbidev.nexuspowered.interfaces;

/**
 * Represents a class which delegates calls to a different object.
 *
 * @param <T> the delegate type
 */
public interface Delegate<T> {

    static Object resolve(Object o) {
        while (o instanceof Delegate<?>) {
            o = ((Delegate<?>) o).delegate();
        }
        return o;
    }

    /**
     * Gets the delegate object
     *
     * @return the delegate object
     */
    T delegate();

}