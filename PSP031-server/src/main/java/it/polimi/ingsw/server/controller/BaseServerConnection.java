package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.model.Provider;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * Object which represents and keeps track of the connection of a specific player.
 * <p>
 * This is used to keep track of observers and any network-related objects and can be
 * used to close everything once the player disconnects.
 */
public abstract class BaseServerConnection implements PlayerObservableTracker, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServerConnection.class);

    protected final ServerController controller;
    private final ConcurrentMap<Provider<?>, Set<Consumer<?>>> observablesToObservers = new ConcurrentHashMap<>();

    protected final String nick;
    protected volatile boolean nickVerified;

    public BaseServerConnection(ServerController controller, String nick) {
        this.controller = controller;
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    protected void verifyNick() {
        nickVerified = true;
    }

    public void disconnectPlayer(Throwable cause) {
        try {
            close();
        } catch (IOException e) {
            cause.addSuppressed(new IOException("Failed to disconnect", e));
        }

        LOGGER.error("Disconnected {}", nick, cause);
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() throws IOException {
        try {
            unregisterObservers();
            doClose();
        } finally {
            // Make sure we only grab locks after we closed the connection, as if we do
            // it before it may lead to deadlocks
            if (nickVerified)
                callDisconnectPlayerHook();
        }
    }

    protected abstract void doClose() throws IOException;

    @MustBeInvokedByOverriders
    protected void callDisconnectPlayerHook() {
        controller.onDisconnectPlayer(nick);
    }

    public void onGameOver() {
        unregisterObservers();

        try {
            doClosePlayerGame();
        } catch (IOException e) {
            LOGGER.error("Failed to disconnect player {}", nick, e);
        }
    }

    protected abstract void doClosePlayerGame() throws IOException;

    @Override
    public <T> Consumer<T> registerObserver(Provider<T> toObserve, ThrowingConsumer<T> observer) {
        final var observers = observablesToObservers.computeIfAbsent(toObserve, v -> ConcurrentHashMap.newKeySet());
        Consumer<T> throwingObserver = t -> {
            try {
                observer.accept(t);
            } catch (DisconnectedException e) {
                disconnectPlayer(e);
            }
        };
        observers.add(throwingObserver);
        toObserve.registerObserver(throwingObserver);
        return throwingObserver;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void unregisterObservers() {
        var toUnregister = new HashMap<>(observablesToObservers);
        observablesToObservers.keySet().removeAll(toUnregister.keySet());
        toUnregister.forEach((provider, observers) -> observers.forEach(o -> ((Provider) provider).unregisterObserver(o)));
    }
}
