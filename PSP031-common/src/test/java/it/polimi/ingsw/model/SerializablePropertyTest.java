package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    @SuppressWarnings("NullAway") // NullAway doesn't support nullable in generics
    void testNullableSerializableAfterSet() throws IOException, ClassNotFoundException {
        final var property = SerializableProperty.nullableProperty("serialize_me");
        property.set(null);
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

        assertEquals(property, deserialized);
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
}