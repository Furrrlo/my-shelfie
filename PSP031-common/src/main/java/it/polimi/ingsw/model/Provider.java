package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Interface which contains data and allows to register/unregister observers
 * to be notified about related changes.
 * <p>
 * Implementations should be fully concurrent and observers should be able to mutate the data,
 * register or unregister additional observers or themselves when they are notififed of a value
 * change.
 *
 * @param <T> type o the contained data
 */
public interface Provider<T> {

    T get();

    /**
     * Registers the given observer if not already registered, otherwise does nothing.
     *
     * @param o observer to register
     */
    void registerObserver(Consumer<? super T> o);

    void registerWeakObserver(Consumer<? super T> o);

    void unregisterObserver(Consumer<? super T> o);

    default <T1> Provider<T1> map(SerializableFunction<T, T1> mapper) {
        return new MappedProvider<>(this, mapper);
    }

    interface SerializableFunction<T, R> extends Function<T, R>, Serializable {
    }
}