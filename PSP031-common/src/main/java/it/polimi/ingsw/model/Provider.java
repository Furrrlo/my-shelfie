package it.polimi.ingsw.model;

import java.util.function.Consumer;

public interface Provider<T> {

    T get();

    /**
     * Registers the given observer if not already registered, otherwise does nothing.
     *
     * @param o observer to register
     */
    void registerObserver(Consumer<? super T> o);

    void unregisterObserver(Consumer<? super T> o);
}