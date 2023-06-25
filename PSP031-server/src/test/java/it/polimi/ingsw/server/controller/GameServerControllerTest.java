package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.*;

class GameServerControllerTest {

    private volatile ServerGame game;
    private volatile GameServerController controller;
    private volatile ServerPlayer startingPlayer, otherPlayer;

    @BeforeEach
    void setUp() {
        final var randomFactory = RandomGeneratorFactory.getDefault();
        game = LobbyServerController.createGame(0, randomFactory.create(0),
                List.of(new LobbyPlayer("p1"),
                        new LobbyPlayer("p2")));
        controller = new GameServerController(new LockProtected<>(game));
        startingPlayer = game.getStartingPlayer();
        otherPlayer = game.getPlayers().stream()
                .filter(player -> player != startingPlayer)
                .findFirst()
                .orElseThrow();
    }

    @Test
    void testOnDisconnectPlayer() {
        controller.onDisconnectPlayer("p1");
        assertFalse(startingPlayer.connected().get());
        assertTrue(game.suspended().get());
        assertNotSame(startingPlayer, game.currentTurn().get());
        //endGame will be set to true in 30 sec
        // too long to wait (?) :(
    }

    @Test
    void testOnReconnectPlayer() {
        controller.onDisconnectPlayer("p1");
        controller.onReconnectedPlayer("p1");
        assertTrue(startingPlayer.connected().get());
        assertFalse(game.suspended().get());
    }

    @Test
    void testOnReconnectPlayer_2disconnection() {
        controller.onDisconnectPlayer("p1");
        controller.onDisconnectPlayer("p2");
        controller.onReconnectedPlayer("p1");
        assertTrue(startingPlayer.connected().get());
        assertTrue(game.suspended().get());
        assertSame(startingPlayer, game.currentTurn().get());
    }

    @Test
    void testMakeMove_endedGame() {
        game.endGame().set(true);
        assertThrows(IllegalStateException.class, () -> controller.makeMove(startingPlayer, List.of(), 0));
    }

    @Test
    void testMakeMove_suspendedGame() {
        game.suspended().set(true);
        assertThrows(IllegalStateException.class, () -> controller.makeMove(startingPlayer, List.of(), 0));
    }

    @Test
    void testMakeMove_firstFinisher() {
        assertNull(game.firstFinisher().get());
        fillShelfie(startingPlayer.getShelfie(), (r, c) -> r != 0 || c != 0);
        controller.makeMove(startingPlayer, List.of(new BoardCoord(1, 3)), 0);
        assertSame(startingPlayer, game.firstFinisher().get());
    }

    @Test
    void testMakeMove_firstFinisherAlreadySet() {
        assertNull(game.firstFinisher().get());
        fillShelfie(startingPlayer.getShelfie(), (r, c) -> r != 0 || c != 0);
        controller.makeMove(startingPlayer, List.of(new BoardCoord(1, 3)), 0);
        fillShelfie(otherPlayer.getShelfie(), (r, c) -> r != 0 || c != 0);
        controller.makeMove(otherPlayer, List.of(new BoardCoord(1, 4)), 0);
        assertSame(startingPlayer, game.firstFinisher().get());
    }

    @Test
    void testMakeMove_fullShelfie() {
        assertNull(game.firstFinisher().get());
        fillShelfie(startingPlayer.getShelfie(), (r, c) -> r != 0 || c != 0);
        assertThrows(IllegalArgumentException.class,
                () -> controller.makeMove(startingPlayer, List.of(new BoardCoord(1, 3)), 1));
        assertThrows(IllegalArgumentException.class,
                () -> controller.makeMove(startingPlayer, List.of(new BoardCoord(1, 3), new BoardCoord(1, 4)), 0));
    }

    @Test
    void testMakeMove_emptyBoardAndBag() {
        assertFalse(game.endGame().get());
        game.getBoard().tiles().forEach(t -> {
            if (t.row() != 1 || t.col() != 3)
                Property.setNullable(t.tile(), null);
        });
        game.getBag().clear();
        controller.makeMove(startingPlayer, List.of(new BoardCoord(1, 3)), 0);
        assertTrue(game.endGame().get());
    }

    @Test
    void testMakeMove_gameOver() {
        assertFalse(game.endGame().get());
        fillShelfie(startingPlayer.getShelfie(), (r, c) -> r != 0 || c != 0);
        controller.makeMove(startingPlayer, List.of(new BoardCoord(1, 3)), 0);
        assertFalse(game.endGame().get());
        controller.makeMove(otherPlayer, List.of(new BoardCoord(1, 4)), 0);
        assertTrue(game.endGame().get());
    }

    @Test
    void makeMove() {
        /*
         * valid positions for 2 players board
         * ****0**1**2**3**4**5**6**7**8 * ->*****0**1**2**3**4**5**6**7**8
         * 0 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, -> 0 { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
         * 1 { 0, 0, 0, 1, 1, 0, 0, 0, 0 }, -> 1 { 0, 0, 0, E, E, 0, 0, 0, 0 },
         * 2 { 0, 0, 0, 1, 1, 1, 0, 0, 0 }, -> 2 { 0, 0, 0, E, E, E, 0, 0, 0 },
         * 3 { 0, 0, 1, 1, 1, 1, 1, 1, 0 }, -> 3 { 0, 0, E, 1, E, E, E, 1, 0 },
         * 4 { 0, 1, 1, 1, 1, 1, 1, 1, 0 }, -> 4 { 0, E, E, E, E, E, E, E, 0 },
         * 5 { 0, 1, 1, 1, 1, 1, 1, 0, 0 }, -> 5 { 0, E, E, E, E, E, E, 0, 0 },
         * 6 { 0, 0, 0, 1, 1, 1, 0, 0, 0 }, -> 6 { 0, 0, 0, E, E, E, 0, 0, 0 },
         * 7 { 0, 0, 0, 0, 1, 1, 0, 0, 0 }, -> 7 { 0, 0, 0, 0, E, E, 0, 0, 0 },
         * 8 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, -> 8 { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
         * 
         * selection list for board refill triggering :
         * (1,3),(1,4) -> (2,3),(2,4),(2,5) -> (4,1),(5,1) -> (3,2),(4,2),(5,2)
         * (7,4),(7,5)-> (6,3),(6,4),(6,5)->(4,3),(5,3)->(3,4),(3,5),(3,6)->(4,7)->(5,4),(5,5),(5,6)->(4,4),(4,5),(4,6)
         */

        final var selectedList = new ArrayList<List<BoardCoord>>();
        selectedList.add(List.of(new BoardCoord(2, 3), new BoardCoord(2, 4), new BoardCoord(2, 5)));
        selectedList.add(List.of(new BoardCoord(4, 1), new BoardCoord(5, 1)));
        selectedList.add(List.of(new BoardCoord(3, 2), new BoardCoord(4, 2), new BoardCoord(5, 2)));
        selectedList.add(List.of(new BoardCoord(7, 4), new BoardCoord(7, 5)));
        selectedList.add(List.of(new BoardCoord(6, 3), new BoardCoord(6, 4), new BoardCoord(6, 5)));
        selectedList.add(List.of(new BoardCoord(4, 3), new BoardCoord(5, 3)));
        selectedList.add(List.of(new BoardCoord(3, 4), new BoardCoord(3, 5), new BoardCoord(3, 6)));
        selectedList.add(List.of(new BoardCoord(4, 7)));
        selectedList.add(List.of(new BoardCoord(5, 4), new BoardCoord(5, 5), new BoardCoord(5, 6)));

        //expects throw of IllegalArgumentException("Invalid move") if selected tiles are invalid
        List<BoardCoord> selectedWrong = new ArrayList<>();
        selectedWrong.add(new BoardCoord(0, 0));
        selectedWrong.add(new BoardCoord(0, 1));

        assertThrows(IllegalArgumentException.class, () -> controller.makeMove(startingPlayer,
                selectedWrong, 0));

        List<BoardCoord> selected = new ArrayList<>();
        selected.add(new BoardCoord(1, 3));
        selected.add(new BoardCoord(1, 4));

        //temporary memo of extracted tiles from board
        Property<Tile> tileProp0 = game.getBoard().tile(selected.get(0).row(), selected.get(0).col());
        Property<Tile> tileProp1 = game.getBoard().tile(selected.get(1).row(), selected.get(1).col());

        controller.makeMove(startingPlayer, selected, 0);

        //expected tiles in position (1,3) and (1,4) to be removed from board and set to null
        assertNull(game.getBoard().tile(selected.get(0).row(), selected.get(0).col()).get());
        assertNull(game.getBoard().tile(selected.get(1).row(), selected.get(1).col()).get());

        //expected tiles in shelfie in column 0, to be != null and to be equal to the ones extracted from boards,
        //following extraction order
        assertEquals(tileProp0.get(), startingPlayer.getShelfie().tile(0, 0).get());
        assertEquals(tileProp1.get(), startingPlayer.getShelfie().tile(1, 0).get());

        //after make move expected IllegalArgumentException("It's not this player turn") if the player who made
        //the move attempts another move
        List<BoardCoord> selected1 = new ArrayList<>();
        selected1.add(new BoardCoord(2, 3));
        selected1.add(new BoardCoord(2, 4));
        assertThrows(IllegalArgumentException.class, () -> controller.makeMove(startingPlayer, selected1, 0),
                "It's not this player turn");

        //at this point the starting player only made his first move picking tiles (1,3),(1,4), then let's simulate
        //the game until board refill is triggered
        int c1 = 0; //representing col index for player 1
        int c2 = 0; //representing col index for player 2
        for (int i = 0; i < selectedList.size(); i++) {
            var curr = selectedList.get(i);
            if (i % 2 == 0) {
                //player2 is playing
                controller.makeMove(otherPlayer, curr, c2);
                c2++;
                if (c2 > ShelfieView.COLUMNS)
                    c2 = 0;
            } else {
                //player1 is playing
                controller.makeMove(startingPlayer, curr, c1);
                c1++;
                if (c1 > ShelfieView.COLUMNS)
                    c1 = 0;
            }
        }
        //at this point we expect that on the board are remaining only (4,4),(4,5),(4,6) that are going to be picked by the last player
        //and (3,3),(3,7) that are going to trigger the refill therefore finding board to be full
        final var remainingTiles = game.getBoard().tiles()
                .filter(t -> game.getBoard().isValidTile(t.row(), t.col()))
                .filter(t -> t.tile().get() != null)
                .map(BoardCoord::new)
                .toList();

        final var expectedRemainingTiles = new ArrayList<BoardCoord>();
        expectedRemainingTiles.add(new BoardCoord(3, 3));
        expectedRemainingTiles.add(new BoardCoord(3, 7));
        expectedRemainingTiles.add(new BoardCoord(4, 4));
        expectedRemainingTiles.add(new BoardCoord(4, 5));
        expectedRemainingTiles.add(new BoardCoord(4, 6));

        //we expect that board contains all the remaining tiles
        assertSame(expectedRemainingTiles.size(), remainingTiles.size());
        assertTrue(remainingTiles.containsAll(expectedRemainingTiles));

        Tile t1 = game.getBoard().tile(3, 3).get();
        Tile t2 = game.getBoard().tile(3, 7).get();

        //this last move should trigger board refill, and after we expect the board to be full 
        var lastMove = List.of(new BoardCoord(4, 4), new BoardCoord(4, 5), new BoardCoord(4, 6));

        //player 1 now makes the last move triggering board refill
        controller.makeMove(startingPlayer, lastMove, c1);

        //we expect at this point board to be full ( 29 tiles on the board )
        //and we expect tiles in position (3,3) and (3,7) to have not changed
        assertEquals(29, game.getBoard().tiles().count());

        assertSame(t1, game.getBoard().tile(3, 3).get());
        assertSame(t2, game.getBoard().tile(3, 7).get());
    }

    @Test
    void sendMessage() {
        //TODO : complete testing sendMessage

        //at the beginning of the game, message is set to null
        assertNull(game.message().get());

        //if specified player, sends message, game.message().get() should be equals to the message that was sent
        var message = new UserMessage(startingPlayer.getNick(), "", "example", otherPlayer.getNick(), "");
        controller.sendMessage(startingPlayer.getNick(), "example", otherPlayer.getNick());
        assertEquals(message, game.message().get());

        //if another player send another message, game.message() should change to the new sent message
        var message1 = new UserMessage(otherPlayer.getNick(), "", "example", UserMessage.EVERYONE_RECIPIENT, "");
        controller.sendMessage(otherPlayer.getNick(), "example", UserMessage.EVERYONE_RECIPIENT);
        assertEquals(message1, game.message().get());

        //if sending player is not present between playing players should rise IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                () -> controller.sendMessage("wrong_nick", "message", otherPlayer.getNick()));

        //if receiving player is not present between playing players should rise IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                () -> controller.sendMessage(startingPlayer.getNick(), "message", "wrong_nick"));

        //if no text has been written in the message field should rise IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                () -> controller.sendMessage(startingPlayer.getNick(), "", otherPlayer.getNick()));
    }

    private void fillShelfie(Shelfie shelfie, BiPredicate<Integer, Integer> toFill) {
        for (int r = 0; r < Shelfie.ROWS; r++)
            for (int c = 0; c < Shelfie.COLUMNS; c++)
                if (toFill.test(r, c))
                    shelfie.tile(r, c).set(new Tile(Arrays.stream(Color.values()).findAny().orElseThrow()));
    }

}