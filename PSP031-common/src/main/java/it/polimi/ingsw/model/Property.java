package it.polimi.ingsw.model;

import java.util.function.Function;

public interface Property<T> extends Provider<T> {

    void set(T val);

    default void update(Function<T, T> updater) {
        set(updater.apply(get()));
    }
}