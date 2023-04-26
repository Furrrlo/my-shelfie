package it.polimi.ingsw.model;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class PropertyProxyTest {

    @Test
    void testProxy() {
        var proxy = new PropertyProxy<@Nullable Object>();
        assertThrows(NullPointerException.class, proxy::get, "Proxy methods should not work without the proxied object");
        assertThrows(NullPointerException.class, () -> Property.setNullable(proxy, null),
                "Proxy methods should not work without the proxied object");
        assertThrows(NullPointerException.class, () -> proxy.update(v -> null),
                "Proxy methods should not work without the proxied object");
        assertThrows(NullPointerException.class, () -> proxy.registerObserver(c -> {
        }), "Proxy methods should not work without the proxied object");
        assertThrows(NullPointerException.class, () -> proxy.unregisterObserver(c -> {
        }), "Proxy methods should not work without the proxied object");
        var mapped = assertDoesNotThrow(
                () -> proxy.map(Objects::nonNull),
                "Map method should also work without the proxy object");

        var proxied = new SerializableProperty<@Nullable Object>(new Object());
        proxy.setProxied(proxied);
        assertThrows(IllegalStateException.class, () -> proxy.setProxied(proxied), "The proxied object can only be set once");

        assertEquals(proxied.get(), proxy.get());
        assertTrue(mapped.get());

        assertDoesNotThrow(() -> Property.setNullable(proxy, null));
        assertEquals(proxied.get(), proxy.get());
        assertFalse(mapped.get());

        assertDoesNotThrow(() -> proxy.update(v -> new Object()));
        assertEquals(proxied.get(), proxy.get());
        assertTrue(mapped.get());

        Consumer<? super Object> observer = c -> {
        };
        assertDoesNotThrow(() -> proxy.registerObserver(observer));
        assertTrue(proxied.getObservers().contains(observer));

        assertDoesNotThrow(() -> proxy.unregisterObserver(observer));
        assertFalse(proxied.getObservers().contains(observer));
    }
}