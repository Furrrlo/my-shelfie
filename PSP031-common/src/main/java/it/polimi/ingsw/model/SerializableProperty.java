package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerializableProperty<T> implements Property<T>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializableProperty.class);

    private T val;

    private final transient Map<ObserverBody<? super T>, Long> observersToInsertionTime = new ConcurrentHashMap<>();
    private final transient Set<ObserverInsertion<? super T>> sortedObservers = new ConcurrentSkipListSet<>();

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

        for (var iter = sortedObservers.iterator(); iter.hasNext();) {
            var observer = iter.next();
            var body = observer.body().supplier().get();
            // Discard possibly null references caused by WeakReferences
            if (body == null) {
                iter.remove();
                continue;
            }

            try {
                body.accept(val);
            } catch (Throwable t) {
                LOGGER.error("Exception during observer invocation {} with value {}", observer, val, t);
            }
        }
    }

    @VisibleForTesting
    Set<Consumer<? super T>> getObservers() {
        return Stream.concat(observersToInsertionTime.keySet().stream(), sortedObservers.stream().map(ObserverInsertion::body))
                .map(b -> b.supplier().get())
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void registerObserver(Consumer<? super T> o) {
        long insertionTime = System.currentTimeMillis();
        var body = new ObserverBody<>(o);
        if (observersToInsertionTime.putIfAbsent(body, insertionTime) == null)
            sortedObservers.add(new ObserverInsertion<>(body, insertionTime));
    }

    @Override
    public void registerWeakObserver(Consumer<? super T> o) {
        long insertionTime = System.currentTimeMillis();
        var body = new ObserverBody<>(new WeakReference<>(o));
        if (observersToInsertionTime.putIfAbsent(body, insertionTime) == null)
            sortedObservers.add(new ObserverInsertion<>(body, insertionTime));
    }

    @Override
    public void unregisterObserver(Consumer<? super T> o) {
        var body = new ObserverBody<>(o);
        Long insertionTime = observersToInsertionTime.remove(body);
        if (insertionTime != null)
            sortedObservers.remove(new ObserverInsertion<>(body, insertionTime));
    }

    @Override
    public String toString() {
        return "PropertyImpl{" +
                "value=" + val +
                ", observersToInsertionTime=" + observersToInsertionTime +
                ", sortedObservers=" + sortedObservers +
                '}';
    }

    /**
     * @param supplier supplier which returns the actual lambda which will be called
     * @param <T> type of the data this observer is observing
     */
    private record ObserverBody<T>(Supplier<@Nullable Consumer<T>> supplier) {

        private ObserverBody(WeakReference<Consumer<T>> body) {
            this(body::get);
        }

        private ObserverBody(Consumer<T> body) {
            this(() -> body);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof ObserverBody<?> observer))
                return false;
            return Objects.equals(supplier.get(), observer.supplier.get());
        }

        @Override
        public int hashCode() {
            return Objects.hash(supplier.get());
        }
    }

    /**
     * Observer container class which makes them sortable by insertion time
     *
     * @param body actual observer body which will be called
     * @param insertionMillis time this observer was inserted
     * @param <T> type of the data this observer is observing
     */
    private record ObserverInsertion<T>(ObserverBody<T> body,
            long insertionMillis) implements Comparable<ObserverInsertion<?>> {
        @Override
        public int compareTo(ObserverInsertion<?> o) {
            // Sort by insertion time
            var discriminant = Long.compare(insertionMillis, o.insertionMillis);
            if (discriminant != 0)
                return discriminant;
            // Ensure that comparing the same body returns 0, to respect equals and hashcode
            if (Objects.equals(body, o.body))
                return 0;
            // We have the same discriminant but the two objects are different, so we can't return 0
            // Differentiate them by using their ref pointer value
            return Integer.compare(System.identityHashCode(this), System.identityHashCode(o));
        }
    }
}