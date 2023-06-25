package it.polimi.ingsw.model;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.controller.GameController;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    @Test
    void testIsLobbyCreator() {
        final var lobby1 = new Lobby(4, List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")),
                "test_player_2");
        assertTrue(lobby1.isLobbyCreator("test_player_1"));

        final var lobby2 = new Lobby(4, List.of(new LobbyPlayer("test_player_1"), new LobbyPlayer("test_player_2")),
                "test_player_2");
        assertFalse(lobby2.isLobbyCreator("test_player_2"));

        final var empty = new Lobby(0, List.of(), "");
        assertFalse(empty.isLobbyCreator(""));
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var lobby1 = new Lobby(4, List.of(new LobbyPlayer("test_player_1")), "test_player_1");
        final var lobby2 = new Lobby(4, List.of(new LobbyPlayer("test_player_1")), "test_player_1");

        assertEquals(lobby1, lobby1, "Same instance is not the same");
        assertNotEquals(lobby1, new Object(), "Different object should not be equals");
        assertEquals(lobby1, lobby2, "Instances with no differences should be equals");

        final var lobbyDiffReqPlayers = new Lobby(2, List.of(new LobbyPlayer("test_player_1")), "test_player_1");
        assertNotEquals(lobby1, lobbyDiffReqPlayers, "Instances with different required players should not be equals");

        final var lobbyDiffJoinedPlayers = new Lobby(4, List.of(new LobbyPlayer("test_player_2")), "test_player_2");
        assertNotEquals(lobby1, lobbyDiffJoinedPlayers, "Instances with different joined players should not be equals");

        final var lobbyDiffGame = new Lobby(
                4,
                List.of(new LobbyPlayer("test_player_1")),
                "test_player_1",
                new GameAndController<>(
                        new Game(
                                0,
                                new Board(2),
                                List.of((sp, ct, ff) -> new Player("test_player_1", new Shelfie(), sp, true, ct, ff, 0)),
                                0,
                                0,
                                0,
                                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()),
                                        new CommonGoal(Type.CROSS, List.of())),
                                new PersonalGoal(1),
                                null, false, false),
                        new EmptyGameController()));
        assertNotEquals(lobby1, lobbyDiffGame, "Instances with different games should not be equals");
    }

    @Test
    void testHashCode() {
        final var lobby1 = new Lobby(4, List.of(new LobbyPlayer("test_player_1")), "test_player_1");
        final var lobby2 = new Lobby(4, List.of(new LobbyPlayer("test_player_1")), "test_player_1");

        assertEquals(lobby1.hashCode(), lobby1.hashCode(), "Same instance is not the same");
        assertEquals(lobby1.hashCode(), lobby2.hashCode(), "Instances with no differences should be equals");

        final var lobbyDiffReqPlayers = new Lobby(2, List.of(new LobbyPlayer("test_player_1")), "test_player_1");
        assertNotEquals(lobby1.hashCode(), lobbyDiffReqPlayers.hashCode(),
                "Instances with different required players should not be equals");

        final var lobbyDiffJoinedPlayers = new Lobby(4, List.of(new LobbyPlayer("test_player_2")), "test_player_2");
        assertNotEquals(lobby1.hashCode(), lobbyDiffJoinedPlayers.hashCode(),
                "Instances with different joined players should not be equals");

        final var lobbyDiffGame = new Lobby(
                4,
                List.of(new LobbyPlayer("test_player_1")),
                "test_player_1",
                new GameAndController<>(
                        new Game(
                                0,
                                new Board(2),
                                List.of((sp, ct, ff) -> new Player("test_player_1", new Shelfie(), sp, true, ct, ff, 0)),
                                0,
                                0,
                                0,
                                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()),
                                        new CommonGoal(Type.CROSS, List.of())),
                                new PersonalGoal(1),
                                null, false, false),
                        new EmptyGameController()));
        assertNotEquals(lobby1.hashCode(), lobbyDiffGame.hashCode(), "Instances with different games should not be equals");
    }

    @Test
    void testToString() {
        final var lobby = new Lobby(4, List.of(new LobbyPlayer("test_player")), "test_player");
        assertDoesNotThrow(lobby::toString);
    }

    static class EmptyGameController implements GameController {

        @Override
        public void makeMove(List<BoardCoord> selected, int shelfCol) {
        }

        @Override
        public void sendMessage(String message, String nickReceivingPlayer) {
        }
    }
}