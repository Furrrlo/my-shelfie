package it.polimi.ingsw.server.model;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.server.controller.LobbyServerController;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.*;

class ServerGameTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    void bagViewIsUnmodifiable() {
        final var game = LobbyServerController.createGame(
                0,
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        assertThrows(
                UnsupportedOperationException.class,
                () -> game.getBagView().add(new Tile(Color.BLUE)),
                "Elements can be added to bag view");
        assertThrows(
                UnsupportedOperationException.class,
                () -> game.getBagView().remove(0),
                "Elements can be removed from bag view");
    }

    @Test
    void bagIsModifiable() {
        final var game = LobbyServerController.createGame(
                0,
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        assertDoesNotThrow(
                () -> game.getBag().add(new Tile(Color.BLUE)),
                "Can't add elements to bag");
        assertDoesNotThrow(
                () -> game.getBag().remove(0),
                "Can't remove elements from bag");
    }

    @Test
    void bagViewIsViewOfBag() {
        final var game = LobbyServerController.createGame(
                0,
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        assertEquals(game.getBag(), game.getBagView());
        game.getBag().add(new Tile(Color.BLUE));
        assertEquals(game.getBag(), game.getBagView());
    }

    @Test
    @SuppressWarnings("EqualsWithItself")
    void testEquals() {
        final var randomFactory = RandomGeneratorFactory.getDefault();
        final long seed1 = randomFactory.create().nextLong();

        final var game1 = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        final var game2 = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));

        assertEquals(game1, game1, "Same instance is not the same");
        assertNotEquals(game1, new Object(), "Different object should not be equals");
        assertEquals(game1, game2, "Instances with no differences should be equals");

        // TODO:
        // final var gameDiffRnd = LobbyServerController.createGame(
        //         0,
        //         new Random(seed1 + 10),
        //         List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        // assertNotEquals(game1, gameDiffRnd, "Instances with different randoms should not be equals");

        final var gameDiffId = LobbyServerController.createGame(
                1,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        assertNotEquals(game1, gameDiffId, "Instances with different ids should not be equals");

        final var gameDiffBoard = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        gameDiffBoard.getBoard().tiles().findFirst().orElseThrow().tile().update(t -> t != null && t.getColor() == Color.BLUE
                ? new Tile(Color.GREEN)
                : new Tile(Color.BLUE));
        assertNotEquals(game1, gameDiffBoard, "Instances with different boards should not be equals");

        final var gameDiffBag = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        gameDiffBag.getBag().add(new Tile(Color.BLUE));
        assertNotEquals(game1, gameDiffBag, "Instances with different bags should not be equals");

        final var gameDiffPlayers = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_2"), new LobbyPlayer("example_player_3")));
        assertNotEquals(game1, gameDiffPlayers, "Instances with different players should not be equals");

        final var gameDiffTurn = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        gameDiffTurn.currentTurn().update(curr -> curr.equals(gameDiffTurn.getPlayers().get(0))
                ? gameDiffTurn.getPlayers().get(1)
                : gameDiffTurn.getPlayers().get(0));
        assertNotEquals(game1, gameDiffTurn, "Instances with different current turn should not be equals");

        final var gameDiffFirstFinisher = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        gameDiffFirstFinisher.firstFinisher().update(curr -> gameDiffTurn.getPlayers().get(0));
        assertNotEquals(game1, gameDiffFirstFinisher, "Instances with different first finisher should not be equals");
    }

    @Test
    void testHashCode() {
        final var randomFactory = RandomGeneratorFactory.getDefault();
        final long seed1 = randomFactory.create().nextLong();

        final var game1 = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        final var game2 = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));

        assertEquals(game1.hashCode(), game1.hashCode(), "Same instance is not the same");
        assertEquals(game1.hashCode(), game2.hashCode(), "Instances with no differences should be equals");

        // TODO:
        // final var gameDiffRnd = LobbyServerController.createGame(
        //         0,
        //         new Random(seed1 + 10),
        //         List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        // assertNotEquals(game1.hashCode(), gameDiffRnd.hashCode(), "Instances with different randoms should not be equals");

        final var gameDiffId = LobbyServerController.createGame(
                1,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        assertNotEquals(game1.hashCode(), gameDiffId.hashCode(), "Instances with different ids should not be equals");

        final var gameDiffBoard = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        gameDiffBoard.getBoard().tiles().findFirst().orElseThrow().tile().update(t -> t != null && t.getColor() == Color.BLUE
                ? new Tile(Color.GREEN)
                : new Tile(Color.BLUE));
        assertNotEquals(game1.hashCode(), gameDiffBoard.hashCode(), "Instances with different boards should not be equals");

        final var gameDiffBag = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        gameDiffBag.getBag().add(new Tile(Color.BLUE));
        assertNotEquals(game1.hashCode(), gameDiffBag.hashCode(), "Instances with different bags should not be equals");

        final var gameDiffPlayers = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_2"), new LobbyPlayer("example_player_3")));
        assertNotEquals(game1.hashCode(), gameDiffPlayers.hashCode(), "Instances with different players should not be equals");

        final var gameDiffTurn = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        gameDiffTurn.currentTurn().update(curr -> curr.equals(gameDiffTurn.getPlayers().get(0))
                ? gameDiffTurn.getPlayers().get(1)
                : gameDiffTurn.getPlayers().get(0));
        assertNotEquals(game1.hashCode(), gameDiffTurn.hashCode(),
                "Instances with different current turn should not be equals");

        final var gameDiffFirstFinisher = LobbyServerController.createGame(
                0,
                randomFactory.create(seed1),
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        gameDiffFirstFinisher.firstFinisher().update(curr -> gameDiffTurn.getPlayers().get(0));
        assertNotEquals(game1.hashCode(), gameDiffFirstFinisher.hashCode(),
                "Instances with different first finisher should not be equals");
    }

    @Test
    void testToString() {
        final var game = LobbyServerController.createGame(
                0,
                List.of(new LobbyPlayer("example_player_1"), new LobbyPlayer("example_player_2")));
        assertDoesNotThrow(game::toString);
    }
}