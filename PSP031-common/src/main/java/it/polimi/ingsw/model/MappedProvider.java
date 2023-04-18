package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

class MappedProvider<P, T> implements Provider<T>, Serializable {

    private final Provider<P> parent;
    private final SerializableFunction<P, T> mapper;

    private final Map<Consumer<? super T>, Consumer<? super P>> registeredObservers = new HashMap<>();

    public MappedProvider(Provider<P> parent, SerializableFunction<P, T> mapper) {
        this.parent = parent;
        this.mapper = mapper;
    }

    @Override
    public T get() {
        return mapper.apply(parent.get());
    }

    @Override
    public void registerObserver(Consumer<? super T> o) {
        parent.registerObserver(registeredObservers.computeIfAbsent(o, key -> v -> key.accept(mapper.apply(v))));
    }

    @Override
    public void unregisterObserver(Consumer<? super T> o) {
        var obs = registeredObservers.get(o);
        if (obs != null)
            parent.unregisterObserver(obs);
    }

    @Override
    public <T1> Provider<T1> map(SerializableFunction<T, T1> mapper) {
        return new MappedProvider<>(parent, concat(this.mapper, mapper));
    }

    private static <P, T, T1> SerializableFunction<P, T1> concat(Function<P, T> before, Function<T, T1> after) {
        return t -> after.apply(before.apply(t));
    }

    @Override
    public String toString() {
        return "MappedProvider{" +
                "mapper=" + mapper +
                ", registeredObservers=" + registeredObservers +
                '}';
    }
}
