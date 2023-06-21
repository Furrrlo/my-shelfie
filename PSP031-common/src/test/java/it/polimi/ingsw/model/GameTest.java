package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var game1 = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        final var game2 = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);

        assertEquals(game1, game1, "Same instance is not the same");
        assertEquals(game1.getGameID(), game2.getGameID(), "Same game should have same id");
        assertEquals(game1.getStartingPlayer(), game2.getStartingPlayer(), "Same game should have same starting player");
        assertNotEquals(game1, new Object(), "Different object should not be equals");
        assertEquals(game1, game2, "Instances with no differences should be equals");

        final var gameDiffId = new Game(
                1,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1, gameDiffId, "Instances with different ids should not be equals");

        final var gameDiffBoard = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        gameDiffBoard.getBoard().tiles().findFirst().orElseThrow().tile().set(new Tile(Color.BLUE));
        assertNotEquals(game1, gameDiffBoard, "Instances with different boards should not be equals");

        final var gameDiffPlayers = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                2,
                0,
                1,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1, gameDiffPlayers, "Instances with different players should not be equals");

        final var gameDiffThePlayer = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                0,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1, gameDiffThePlayer, "Instances with different thePlayer should not be equals");

        final var gameDiffTurn = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1, gameDiffTurn, "Instances with different current turn should not be equals");

        final var gameDiffStartingPlayer = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                0,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1, gameDiffStartingPlayer, "Instances with different starting player should not be equals");

        final var gameDiffCommonGoal = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.TRIANGLE, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1, gameDiffCommonGoal, "Instances with different common goals should not be equals");

        final var gameDiffPersonalGoal = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(0),
                null, false, false);
        assertNotEquals(game1, gameDiffPersonalGoal, "Instances with different personal goal should not be equals");

        final var gameDiffFirstFinisher = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                0, false, false);
        assertNotEquals(game1, gameDiffFirstFinisher, "Instances with different first finisher should not be equals");

        final var gameDiffEndGame = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, true, false);
        assertNotEquals(game1, gameDiffEndGame, "Instances with different end game should not be equals");

        final var gameDiffSuspended = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, true);
        assertNotEquals(game1, gameDiffSuspended, "Instances with different suspended should not be equals");

        final var gameDiffMessages = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        gameDiffMessages.messageList().set(List.of(UserMessage.forEveryone("", "", "")));
        assertNotEquals(game1, gameDiffMessages, "Instances with different messages should not be equals");

    }

    @Test
    void testHashCode() {
        final var game1 = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        final var game2 = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);

        assertEquals(game1.hashCode(), game1.hashCode(), "Same instance is not the same");
        assertEquals(game1.hashCode(), game2.hashCode(), "Instances with no differences should be equals");

        final var gameDiffId = new Game(
                1,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1.hashCode(), gameDiffId.hashCode(), "Instances with different ids should not be equals");

        final var gameDiffBoard = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        gameDiffBoard.getBoard().tiles().findFirst().orElseThrow().tile().set(new Tile(Color.BLUE));
        assertNotEquals(game1.hashCode(), gameDiffBoard.hashCode(), "Instances with different boards should not be equals");

        final var gameDiffPlayers = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                2,
                0,
                1,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1.hashCode(), gameDiffPlayers.hashCode(), "Instances with different players should not be equals");

        final var gameDiffThePlayer = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                0,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1.hashCode(), gameDiffThePlayer.hashCode(),
                "Instances with different thePlayer should not be equals");

        final var gameDiffTurn = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1.hashCode(), gameDiffTurn.hashCode(),
                "Instances with different current turn should not be equals");

        final var gameDiffStartingPlayer = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                0,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1.hashCode(), gameDiffStartingPlayer.hashCode(),
                "Instances with different starting player should not be equals");

        final var gameDiffCommonGoal = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.TRIANGLE, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertNotEquals(game1.hashCode(), gameDiffCommonGoal.hashCode(),
                "Instances with different common goals should not be equals");

        final var gameDiffPersonalGoal = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(0),
                null, false, false);
        assertNotEquals(game1.hashCode(), gameDiffPersonalGoal.hashCode(),
                "Instances with different personal goal should not be equals");

        final var gameDiffFirstFinisher = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                0, false, false);
        assertNotEquals(game1.hashCode(), gameDiffFirstFinisher.hashCode(),
                "Instances with different first finisher should not be equals");

        final var gameDiffEndGame = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, true, false);
        assertNotEquals(game1.hashCode(), gameDiffEndGame.hashCode(),
                "Instances with different end game should not be equals");

        final var gameDiffSuspended = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("thePlayer", new Shelfie(), sp, true, ct, ff, 0)),
                3,
                1,
                2,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, true);
        assertNotEquals(game1.hashCode(), gameDiffSuspended.hashCode(),
                "Instances with different suspended should not be equals");
    }

    @Test
    void personalGoal() {
        final var game = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0)),
                0,
                0,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertInstanceOf(PersonalGoal.class, game.getPersonalGoal());
    }

    @Test
    void testGetSortedPlayer() {
        final var game = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 1),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 2)),
                0,
                0,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertEquals(List.of("player2", "player1"), game.getSortedPlayers().stream().map(PlayerView::getNick).toList());

        final var game2 = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 2),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 1),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 3)),
                0,
                0,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertEquals(List.of("player3", "player1", "player2"),
                game2.getSortedPlayers().stream().map(PlayerView::getNick).toList());

        final var game3 = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, false, ct, ff, 2),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, false, ct, ff, 1),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, false, ct, ff, 3)),
                0,
                0,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertEquals(List.of("player3", "player1", "player2"),
                game3.getSortedPlayers().stream().map(PlayerView::getNick).toList());

        final var game4 = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, false, ct, ff, 2),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 1),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, true, ct, ff, 3)),
                0,
                0,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertEquals(List.of("player3", "player2", "player1"),
                game4.getSortedPlayers().stream().map(PlayerView::getNick).toList());

        final var game5 = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, false, ct, ff, 2),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 1),
                        (sp, ct, ff) -> new Player("player3", new Shelfie(), sp, false, ct, ff, 3),
                        (sp, ct, ff) -> new Player("player4", new Shelfie(), sp, true, ct, ff, 3)),
                0,
                0,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertEquals(List.of("player4", "player2", "player3", "player1"),
                game5.getSortedPlayers().stream().map(PlayerView::getNick).toList());
    }

    @Test
    void testToString() {
        final var game = new Game(
                0,
                new Board(2),
                List.of(
                        (sp, ct, ff) -> new Player("player1", new Shelfie(), sp, true, ct, ff, 0),
                        (sp, ct, ff) -> new Player("player2", new Shelfie(), sp, true, ct, ff, 0)),
                0,
                0,
                0,
                players -> List.of(new CommonGoal(Type.DIAGONAL, List.of()), new CommonGoal(Type.CROSS, List.of())),
                new PersonalGoal(1),
                null, false, false);
        assertDoesNotThrow(game::toString);
    }

}