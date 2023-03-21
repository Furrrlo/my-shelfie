package it.polimi.ingsw.model;

import java.util.*;
import java.util.function.Consumer;

public class PropertyImpl<T> implements Property<T> {

    private T val;

    private List<Consumer<? super T>> observers;

    @SuppressWarnings("NullAway") // NullAway sadly doesn't implement generics properly yet
    public static <T> Property<@Nullable T> nullableProperty(@Nullable T prop) {
        return new PropertyImpl<>(prop);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyImpl<?> that)) return false;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    @Override
    public String toString() {
        return "PropertyImpl{" +
                "value=" + val +
                ", observers=" + observers +
                '}';
    }
}