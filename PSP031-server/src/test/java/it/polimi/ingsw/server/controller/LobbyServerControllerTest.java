package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerGameAndController;
import it.polimi.ingsw.server.model.ServerLobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServerControllerTest {

    private volatile ServerLobby lobby;
    private volatile LobbyServerController controller;

    @BeforeEach
    void setUp() {
        lobby = new ServerLobby();
        lobby.joinedPlayers().set(List.of(new LobbyPlayer("p1"), new LobbyPlayer("p2"), new LobbyPlayer("p3")));
        controller = new LobbyServerController(new LockProtected<>(lobby));
    }

    @Test
    void testSetRequiredPlayers_wrongPlayer() {
        assertThrows(IllegalArgumentException.class, () -> controller.setRequiredPlayers("p2", 2));
        assertThrows(IllegalArgumentException.class, () -> controller.setRequiredPlayers("p3", 2));
    }

    @Test
    void testSetRequiredPlayers_illegalNumber() {
        assertThrows(IllegalArgumentException.class, () -> controller.setRequiredPlayers("p1", 1));
        assertThrows(IllegalArgumentException.class, () -> controller.setRequiredPlayers("p1", -1));
        assertThrows(IllegalArgumentException.class, () -> controller.setRequiredPlayers("p1", 5));
    }

    @Test
    void testSetRequiredPlayers_correct() {
        assertNull(lobby.requiredPlayers().get());
        assertDoesNotThrow(() -> controller.setRequiredPlayers("p1", 2));
        assertSame(2, lobby.requiredPlayers().get());
        assertDoesNotThrow(() -> controller.setRequiredPlayers("p1", 3));
        assertSame(3, lobby.requiredPlayers().get());
        assertDoesNotThrow(() -> controller.setRequiredPlayers("p1", 4));
        assertSame(4, lobby.requiredPlayers().get());
        assertDoesNotThrow(() -> controller.setRequiredPlayers("p1", 0));
        assertSame(0, lobby.requiredPlayers().get());
    }

    @Test
    void testReady() {
        assertFalse(lobby.joinedPlayers().get().get(0).ready().get());
        assertDoesNotThrow(() -> controller.ready("p1", true));
        assertTrue(lobby.joinedPlayers().get().get(0).ready().get());
    }

    @Test
    void testReady_nonexistentPlayer() {
        assertThrows(IllegalStateException.class, () -> controller.ready("p5", true));
    }

    @Test
    void testReady_startedGame() {
        ServerGame game;
        lobby.game().set((new ServerGameAndController<>(
                game = LobbyServerController.createGame(0, lobby.joinedPlayers().get()),
                new GameServerController(new LockProtected<>(game)))));

        assertFalse(lobby.joinedPlayers().get().get(0).ready().get());
        assertDoesNotThrow(() -> controller.ready("p1", true));
        assertFalse(lobby.joinedPlayers().get().get(0).ready().get());
    }

    @Test
    void startGame_noRequiredPlayers() {
        assertNull(lobby.game().get());
        controller.setRequiredPlayers("p1", 0);
        assertNull(lobby.game().get());
        controller.ready("p1", true);
        assertNull(lobby.game().get());
        controller.ready("p2", true);
        assertNull(lobby.game().get());
        controller.ready("p3", true);
        assertNotNull(lobby.game().get());
    }

    @Test
    void startGame_requiredPlayers() {
        assertNull(lobby.game().get());
        controller.setRequiredPlayers("p1", 3);
        assertNull(lobby.game().get());
        controller.ready("p1", true);
        assertNull(lobby.game().get());
        controller.ready("p2", true);
        assertNull(lobby.game().get());
        controller.ready("p3", true);
        assertNotNull(lobby.game().get());
    }
}