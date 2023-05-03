package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.controller.LobbyServerController;
import it.polimi.ingsw.server.controller.LockProtected;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerLobbyTest {

    @Test
    void hasGameStartedRespectsGameAttribute() {
        final var lobby = new ServerLobby();
        lobby.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));
        assertFalse(lobby.hasGameStarted(), "There's no game yet");

        final ServerGame game;
        lobby.game().set(new ServerGameAndController<>(
                game = LobbyServerController.createGame(0, lobby.joinedPlayers().get()),
                new GameServerController(new LockProtected<>(game))));
        assertTrue(lobby.hasGameStarted(), "There a game");
    }

    @Test
    void playerCantJoinWhenGameHasStarted() {
        final var lobby = new ServerLobby();
        lobby.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));

        final ServerGame game;
        lobby.game().set(new ServerGameAndController<>(
                game = LobbyServerController.createGame(0, lobby.joinedPlayers().get()),
                new GameServerController(new LockProtected<>(game))));

        assertFalse(lobby.canOnePlayerJoin(), "Game is already in progress, player can't join");
    }

    @Test
    void playerCantJoinWhenTheresNoSpace() {
        final var lobby = new ServerLobby();
        lobby.joinedPlayers().update(l -> List.of(
                new LobbyPlayer("test_player_1"),
                new LobbyPlayer("test_player_2"),
                new LobbyPlayer("test_player_3"),
                new LobbyPlayer("test_player_4")));
        assertFalse(lobby.canOnePlayerJoin(), "There's no space, player can't join");
    }

    @Test
    void playerCanJoin() {
        final var lobby = new ServerLobby();
        lobby.joinedPlayers().update(l -> List.of(
                new LobbyPlayer("test_player_1"),
                new LobbyPlayer("test_player_2"),
                new LobbyPlayer("test_player_3")));
        assertTrue(lobby.canOnePlayerJoin());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var lobby1 = new ServerLobby();
        lobby1.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));

        final var lobby2 = new ServerLobby();
        lobby2.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));

        assertEquals(lobby1, lobby1, "Same instance is not the same");
        assertNotEquals(lobby1, new Object(), "Different object should not be equals");
        assertEquals(lobby1, lobby2, "Instances with no differences should be equals");

        final var lobbyDiffReqPlayers = new ServerLobby();
        lobbyDiffReqPlayers.requiredPlayers().set(2);
        lobbyDiffReqPlayers.joinedPlayers()
                .update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));
        assertNotEquals(lobby1, lobbyDiffReqPlayers, "Instances with different required players should not be equals");

        final var lobbyDiffJoinedPlayers = new ServerLobby();
        lobbyDiffReqPlayers.requiredPlayers().set(4);
        lobbyDiffJoinedPlayers.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_2")));
        assertNotEquals(lobby1, lobbyDiffJoinedPlayers, "Instances with different joined players should not be equals");

        final var lobbyDiffGame = new ServerLobby();
        lobbyDiffGame.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));
        final ServerGame game;
        lobbyDiffGame.game().set(new ServerGameAndController<>(
                game = LobbyServerController.createGame(0, lobbyDiffGame.joinedPlayers().get()),
                new GameServerController(new LockProtected<>(game))));
        assertNotEquals(lobby1, lobbyDiffGame, "Instances with different games should not be equals");
    }

    @Test
    void testHashCode() {
        final var lobby1 = new ServerLobby();
        lobby1.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));

        final var lobby2 = new ServerLobby();
        lobby2.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));

        assertEquals(lobby1.hashCode(), lobby1.hashCode(), "Same instance is not the same");
        assertEquals(lobby1.hashCode(), lobby2.hashCode(), "Instances with no differences should be equals");

        final var lobbyDiffReqPlayers = new ServerLobby();
        lobbyDiffReqPlayers.requiredPlayers().set(2);
        lobbyDiffReqPlayers.joinedPlayers()
                .update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));
        assertNotEquals(lobby1.hashCode(), lobbyDiffReqPlayers.hashCode(),
                "Instances with different required players should not be equals");

        final var lobbyDiffJoinedPlayers = new ServerLobby();
        lobbyDiffReqPlayers.requiredPlayers().set(4);
        lobbyDiffJoinedPlayers.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_2")));
        assertNotEquals(lobby1.hashCode(), lobbyDiffJoinedPlayers.hashCode(),
                "Instances with different joined players should not be equals");

        final var lobbyDiffGame = new ServerLobby();
        lobbyDiffGame.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")));
        final ServerGame game;
        lobbyDiffGame.game().set(new ServerGameAndController<>(
                game = LobbyServerController.createGame(0, lobbyDiffGame.joinedPlayers().get()),
                new GameServerController(new LockProtected<>(game))));
        assertNotEquals(lobby1.hashCode(), lobbyDiffGame.hashCode(),
                "Instances with different games should not be equals");
    }

    @Test
    void testToString() {
        final var lobby = new ServerLobby();
        lobby.joinedPlayers().update(l -> List.of(new LobbyPlayer("test_player")));
        assertDoesNotThrow(lobby::toString);
    }
}