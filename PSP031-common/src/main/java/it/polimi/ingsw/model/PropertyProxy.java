package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class PropertyProxy<T> implements Property<T>, Serializable {

    private @Nullable Property<T> proxied;

    public PropertyProxy() {
        this.proxied = null;
    }

    private Property<T> getProxied() {
        return Objects.requireNonNull(proxied, "Proxy hasn't been resolved yet");
    }

    public void setProxied(Property<T> proxied) {
        if (this.proxied != null)
            throw new IllegalStateException("Proxy has already been resolved");
        this.proxied = proxied;
    }

    @Override
    public void set(T val) {
        getProxied().set(val);
    }

    @Override
    public void update(Function<T, T> updater) {
        getProxied().update(updater);
    }

    @Override
    public T get() {
        return getProxied().get();
    }

    @Override
    public void registerObserver(Consumer<? super T> o) {
        getProxied().registerObserver(o);
    }

    @Override
    public void unregisterObserver(Consumer<? super T> o) {
        getProxied().unregisterObserver(o);
    }

    @Override
    public String toString() {
        return "PropertyProxy{" +
                "proxied=" + proxied +
                '}';
    }
}
