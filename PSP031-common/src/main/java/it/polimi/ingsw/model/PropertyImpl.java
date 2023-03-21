package it.polimi.ingsw.model;

import java.util.*;
import java.util.function.Consumer;

/**
 * 
 */
public class PropertyImpl<T> implements Property<T> {
    private T val;

    private List<Consumer<T>> observers;



    /**
     * Default constructor
     */
    public PropertyImpl(T val) {
        this.val = val;
        observers = new ArrayList<Consumer<T>>();
    }

    /**
     * 
     */
    public T get() {
        return val;
    }

    /**
     * @param val 
     * @return
     */
    public void set(T val) {
        this.val = val;
    }

    /**
     * @param o 
     * @return
     */
    public void registerObserver(Consumer<T> o) {
        observers.add(o);
    }

    /**
     * @param o 
     * @return
     */
    public void unregisterObserver(Consumer<T> o) {
        observers.remove(o);
    }

}