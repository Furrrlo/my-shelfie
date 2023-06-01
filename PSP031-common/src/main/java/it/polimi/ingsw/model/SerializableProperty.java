package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SerializableProperty<T> implements Property<T>, Serializable {

    private T val;

    private final transient Set<Observer<? super T>> observers = new ConcurrentSkipListSet<>();

    @SuppressWarnings("NullAway") // NullAway sadly doesn't implement generics properly yet
    public static <T> SerializableProperty<@Nullable T> nullableProperty(@Nullable T prop) {
        return new SerializableProperty<>(prop);
    }

    /**
     * Default constructor
     */
    public SerializableProperty(T val) {
        this.val = val;
    }

    /**
     * Custom serialization hook defined by Java spec.
     * <p>
     * We use it to create a PropertyImpl with a non-null observers list,
     * as by default serialization leaves final fields as nulls.
     */
    @Serial
    private Object readResolve() throws ObjectStreamException {
        return new SerializableProperty<>(val);
    }

    @Override
    public T get() {
        return val;
    }

    @Override
    public void set(T val) {
        this.val = val;

        for (var iter = observers.iterator(); iter.hasNext();) {
            var observer = iter.next();
            var body = observer.body().get();
            // Discard possibly null references caused by WeakReferences
            if (body == null) {
                iter.remove();
                continue;
            }

            body.accept(val);
        }
    }

    @VisibleForTesting
    Set<Consumer<? super T>> getObservers() {
        return observers.stream()
                .map(o -> o.body().get())
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void registerObserver(Consumer<? super T> o) {
        observers.add(new Observer<>(o, System.currentTimeMillis()));
    }

    @Override
    public void registerWeakObserver(Consumer<? super T> o) {
        observers.add(new Observer<>(new WeakReference<>(o), System.currentTimeMillis()));
    }

    @Override
    public void unregisterObserver(Consumer<? super T> o) {
        // We don't care about the millis, removal happens by equals and hashCode which ignore those
        var removed = observers.remove(new Observer<>(o, 0));
        if (!removed) {
            // Since we use 0 millis, the set implementation might not be able to remove it
            // by using the standard methods as using compareTo returns bad results
            // Fall back to iterating if the first failed
            // This is basically the bad unlucky O(n) path that is sure to work
            observers.removeIf(obs -> {
                var body = obs.body().get();
                // Since we are iterating everything anyway, also delete expired WeakReferences (body == null)
                return body == null || body.equals(o);
            });
        }
    }

    @Override
    public String toString() {
        return "PropertyImpl{" +
                "value=" + val +
                ", observers=" + observers +
                '}';
    }

    /**
     * Observer container class which makes them comparable by insertion time
     *
     * @param body actual observer body which will be called
     * @param insertionMillis time this observer was inserted
     * @param <T> type of the data this observer is observing
     */
    private record Observer<T>(Supplier<@Nullable Consumer<T>> body, long insertionMillis) implements Comparable<Observer<?>> {

        private Observer(WeakReference<Consumer<T>> body, long insertionMillis) {
            this(body::get, insertionMillis);
        }

        private Observer(Consumer<T> body, long insertionMillis) {
            this(() -> body, insertionMillis);
        }

        @Override
        public int compareTo(SerializableProperty.Observer<?> o) {
            // Ensure that comparing the same body returns 0, to respect equals and hashcode
            if (Objects.equals(body.get(), o.body.get()))
                return 0;
            // Sort by insertion time
            var discriminant = Long.compare(insertionMillis, o.insertionMillis);
            if (discriminant != 0)
                return discriminant;
            // We have the same discriminant but the two objects are different, so we can't return 0
            // Differentiate them by using their ref pointer value
            return Integer.compare(System.identityHashCode(this), System.identityHashCode(o));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Observer<?> observer))
                return false;
            return Objects.equals(body.get(), observer.body.get());
        }

        @Override
        public int hashCode() {
            return Objects.hash(body.get());
        }
    }
}