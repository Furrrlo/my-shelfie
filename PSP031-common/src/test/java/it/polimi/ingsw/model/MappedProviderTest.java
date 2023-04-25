package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class MappedProviderTest {

    @Test
    void testMappedProvider() throws ExecutionException, InterruptedException, TimeoutException {
        var prop = new SerializableProperty<>(5);
        var mapped1 = prop.map(num -> num > 10);
        var mapped2 = mapped1.map(bool -> bool ? 50L : 25L);

        assertFalse(mapped1.get(), "Mapped value is wrong");
        assertEquals(25L, mapped2.get(), "Value mapped twice is wrong");

        final CompletableFuture<Boolean> change1 = new CompletableFuture<>();
        mapped1.registerObserver(change1::complete);

        final CompletableFuture<Long> change2 = new CompletableFuture<>();
        mapped2.registerObserver(change2::complete);

        prop.set(20);
        assertTrue(change1.get(100, TimeUnit.MILLISECONDS), "Mapped value is wrong after change");
        assertEquals(50L, change2.get(100, TimeUnit.MILLISECONDS), "Value mapped twice is wrong after change");

        assertDoesNotThrow(() -> mapped1.unregisterObserver(v -> {
        }), "Mapped value should not throw on non-registered observer unregistering");
        assertDoesNotThrow(() -> mapped2.unregisterObserver(v -> {
        }), "Value mapped twice should not throw on non-registered observer unregistering");
    }
}