package it.polimi.ingsw.model;

import java.util.*;
import java.util.function.Consumer;

public class PropertyImpl<T> implements Property<T> {

    private T val;

    private List<Consumer<? super T>> observers;

    /**
     * Default constructor
     */
    public PropertyImpl(T val) {
        this.val = val;
    }

    @Override
    public T get() {
        return val;
    }

    @Override
    public void set(T val) {
        this.val = val;
    }

    @Override
    public void registerObserver(Consumer<? super T> o) {
        observers.add(o);
    }

    @Override
    public void unregisterObserver(Consumer<? super T> o) {
        observers.remove(o);
    }
}