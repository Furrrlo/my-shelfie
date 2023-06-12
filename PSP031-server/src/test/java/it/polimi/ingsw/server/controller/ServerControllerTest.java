package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.controller.NickNotValidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ServerControllerTest {

    private volatile ServerController serverController;

    @BeforeEach
    void setUp() {
        serverController = new ServerController(5, TimeUnit.SECONDS) {
            @Override
            protected void detectDisconnectedPlayers() {
            }
        };
    }

    @Test
    void testGetOrCreateLobby_newLobby() {
        var lobby = serverController.getOrCreateLobby("p1").lobby().getUnsafe();
        var players = lobby.joinedPlayers().get();
        assertSame(0, players.size());
        assertFalse(lobby.isOpen());
    }

    @Test
    void testConnectPlayer() {
        assertDoesNotThrow(() -> serverController.connectPlayer("p1", serverTime -> {
        }));
    }

    @Test
    void testConnectPlayer_emptyNick() {
        assertThrows(NickNotValidException.class, () -> serverController.connectPlayer("", serverTime -> {
        }));
    }

    @Test
    void testConnectPlayer_duplicatedNick() {
        assertDoesNotThrow(() -> serverController.connectPlayer("p1", serverTime -> {
        }));
        assertThrows(NickNotValidException.class, () -> serverController.connectPlayer("p1", serverTime -> {
        }));
    }

}