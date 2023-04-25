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
                null);
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
                null);

        assertEquals(game1, game1, "Same instance is not the same");
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
                null);
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
                null);
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
                null);
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
                null);
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
                null);
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
                null);
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
                null);
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
                null);
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
                new PersonalGoal(0),
                null);
        assertNotEquals(game1, gameDiffFirstFinisher, "Instances with different first finisher should not be equals");
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
                null);
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
                null);

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
                null);
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
                null);
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
                null);
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
                null);
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
                null);
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
                null);
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
                null);
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
                null);
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
                new PersonalGoal(0),
                null);
        assertNotEquals(game1.hashCode(), gameDiffFirstFinisher.hashCode(),
                "Instances with different first finisher should not be equals");
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
                null);
        assertDoesNotThrow(game::toString);
    }

}