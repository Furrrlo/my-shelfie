package it.polimi.ingsw.model;

import java.util.function.Consumer;

public interface Provider<T> {

    T get();

    void registerObserver(Consumer<? super T> o);

    void unregisterObserver(Consumer<? super T> o);
}