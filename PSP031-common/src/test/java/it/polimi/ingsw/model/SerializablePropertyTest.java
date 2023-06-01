package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SerializablePropertyTest {

    @Test
    void testSerializable() throws IOException, ClassNotFoundException {
        final var property = new SerializableProperty<>("serialize_me");
        doTestSerializable(property);
    }

    @Test
    void testSerializableAfterSet() throws IOException, ClassNotFoundException {
        final var property = new SerializableProperty<>("serialize_me");
        property.set("another_string_to_serialize");
        doTestSerializable(property);
    }

    @Test
    void testNullableSerializable() throws IOException, ClassNotFoundException {
        final var property = SerializableProperty.nullableProperty(null);
        doTestSerializable(property);
    }

    @Test
    void testNullableSerializableAfterSet() throws IOException, ClassNotFoundException {
        final var property = SerializableProperty.nullableProperty("serialize_me");
        Property.setNullable(property, null);
        doTestSerializable(property);
    }

    void doTestSerializable(SerializableProperty<?> property) throws IOException, ClassNotFoundException {
        property.registerObserver(v -> System.out.println("random observer"));

        final SerializableProperty<?> deserialized;
        try (var pipe = new PipedInputStream();
             var oos = new ObjectOutputStream(new PipedOutputStream(pipe));
             var ois = new ObjectInputStream(pipe)) {

            oos.writeObject(property);
            deserialized = (SerializableProperty<?>) ois.readObject();
        }

        assertEquals(property.get(), deserialized.get());
        assertNotNull(deserialized.getObservers());
        assertTrue(deserialized.getObservers().isEmpty());
        assertDoesNotThrow(() -> deserialized.registerObserver(v -> System.out.println("other random observer")));
    }

    @Test
    void testSetObservable() throws ExecutionException, InterruptedException, TimeoutException {
        final var property = new SerializableProperty<>("empty");
        final var observed = new CompletableFuture<String>();
        property.registerObserver(observed::complete);
        property.set("set");
        assertEquals("set", property.get());
        assertEquals(property.get(), observed.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void testUpdateObservable() throws ExecutionException, InterruptedException, TimeoutException {
        final var property = new SerializableProperty<>("empty");
        final var observed = new CompletableFuture<String>();
        property.registerObserver(observed::complete);
        property.update(v -> v + "_updated");
        assertEquals("empty_updated", property.get());
        assertEquals(property.get(), observed.get(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void testUnregistering() throws InterruptedException {
        final var property = new SerializableProperty<>("empty");
        final var observed = IntStream.range(0, 100)
                .mapToObj(i -> (Consumer<? super String>) str -> {
                })
                .collect(Collectors.toList());
        // Register observers with a few millis of time diff each
        for (Consumer<? super String> p : observed) {
            Thread.sleep(10);
            property.registerObserver(p);
        }
        assertTrue(property.getObservers().containsAll(observed));

        // Try to remove the one in the middle
        // This is cause the impl uses an ordering-based collection (at the time of writing)
        // so it might be able to remove the first one without issue but struggle with one in the middle
        var toRemove = observed.remove(50);
        property.unregisterObserver(toRemove);
        assertFalse(property.getObservers().contains(toRemove), "Property still contains " + toRemove);

        // Try to remove the others randomly
        var rnd = RandomGenerator.getDefault();
        while (!observed.isEmpty()) {
            toRemove = observed.remove(rnd.nextInt(observed.size()));
            property.unregisterObserver(toRemove);
            assertFalse(property.getObservers().contains(toRemove), "Property still contains " + toRemove);
        }
    }
}