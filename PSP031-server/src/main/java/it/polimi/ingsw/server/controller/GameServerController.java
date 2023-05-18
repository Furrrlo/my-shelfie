package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.model.Property;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.UserMessage;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controller that handle the game, allowing to make a move and send messages.
 */
@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class GameServerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerController.class);

    /**
     * Reference to the game, protected by a lock
     */
    private final LockProtected<ServerGame> game;

    /**
     * Executes the {@link #endGameFuture}
     */
    private final ScheduledExecutorService executor;

    /**
     * Future that ends the game if less than 2 players are connected
     */
    private volatile @Nullable ScheduledFuture<?> endGameFuture;

    public GameServerController(LockProtected<ServerGame> game) {
        this.game = game;
        this.executor = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform()
                .name("GameServerController-endGame-thread")
                .factory());
    }

    /**
     * Method called when a player disconnects.
     * Mark the player as disconnected and suspend the game if remain only 1 player.
     * 
     * @param nick nick of the disconnecting player
     * @param cause cause of disconnection
     */
    public void onDisconnectPlayer(String nick, Throwable cause) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            game.getPlayers().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .ifPresent(serverPlayer -> {
                        serverPlayer.connected().set(false);
                        //If there is only one player, suspend the game
                        if (!game.suspended().get()
                                && game.getPlayers().stream().filter(p -> p.connected().get()).count() <= 1) {
                            game.suspended().set(true);
                            endGameFuture = executor.schedule(() -> {
                                LOGGER.info("Game " + game.getGameID() + " is over because players have disconnected");
                                //TODO: The only connected player (if any) should win
                                game.endGame().set(true);
                            }, 30, TimeUnit.SECONDS);
                        }

                        // If the current player disconnects, skip his turn
                        if (game.currentTurn().get().equals(serverPlayer))
                            changeCurrentTurn(game);
                    });
        }
    }

    /**
     * Method called when disconnected a player reconnects.
     * Mark the player as connected and resume the game if it is suspended.
     * 
     * @param nick nick of the reconnecting player
     */
    public void onReconnectedPlayer(String nick) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            game.getPlayers().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .ifPresent(serverPlayer -> {
                        serverPlayer.connected().set(true);
                        // If the current player is disconnected, we now have a new connected player that can play
                        if (!game.currentTurn().get().connected().get())
                            changeCurrentTurn(game);

                        if (game.suspended().get() && game.getPlayers().stream().filter(p -> p.connected().get()).count() > 1) {
                            game.suspended().set(false);
                            Objects.requireNonNull(endGameFuture).cancel(true);
                        }
                    });
        }
    }

    /**
     * Removes tiles from board and inserts in tiles
     * 
     * @param player player who is making the move
     * @param selected list of selected tiles
     * @param shelfCol shelfie column where to put selected tiles
     * @throws IllegalArgumentException if the move is not valid
     * @throws IllegalStateException if you can't make move at this time
     */
    public void makeMove(ServerPlayer player, List<BoardCoord> selected, int shelfCol) throws IllegalArgumentException {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();

            if (game.endGame().get())
                throw new IllegalStateException("Game is finished");

            if (game.suspended().get())
                throw new IllegalStateException("Game is suspended");

            if (!game.currentTurn().get().equals(player))
                throw new IllegalArgumentException("It's not this player turn");

            List<Tile> selectedTiles = new ArrayList<>();
            if (!game.getBoard().checkBoardCoord(selected)
                    || !player.getShelfie().checkColumnSpace(shelfCol, selected.size()))
                throw new IllegalArgumentException("Invalid move");

            // remove tiles from board
            for (BoardCoord coord : selected) {
                Property<@Nullable Tile> tileProp = game.getBoard().tile(coord.row(), coord.col());
                selectedTiles.add(Objects.requireNonNull(tileProp.get(), "Checked tile was invalid"));
                Property.setNullable(tileProp, null);
            }

            // add tiles to shelfie
            player.getShelfie().placeTiles(selectedTiles, shelfCol);

            if (player.getShelfie().isFull() && game.firstFinisher().get() == null)
                game.firstFinisher().set(player);

            if (game.getBoard().needsRefill())
                game.refillBoard();

            // Board is empty, and we can't refill it, end the game
            if (game.getBoard().isEmpty() && game.getBag().isEmpty()) {
                game.endGame().set(true);
            } else {
                // Change current turn
                changeCurrentTurn(game);
                // If someone already finished, and we reached the starting player, the game is over
                if (game.firstFinisher().get() != null && game.getStartingPlayer().equals(player)) {
                    game.endGame().set(true);
                }
            }
        }
    }

    /**
     * Change the current turn to the next player,
     * skipping disconnected players.
     * If no player is connected, do nothing.
     * 
     * @param game game
     */
    private void changeCurrentTurn(ServerGame game) {
        int nextPlayerIdx = game.getPlayers().indexOf(game.currentTurn().get());
        // Try for all the remaining players
        for (int i = 0; i < game.getPlayers().size() - 1; i++) {
            nextPlayerIdx++;
            if (nextPlayerIdx >= game.getPlayers().size())
                nextPlayerIdx = 0;

            var nextPlayer = game.getPlayers().get(nextPlayerIdx);
            // Only pick a player if there's a currently connected one
            if (nextPlayer.connected().get()) {
                game.currentTurn().set(nextPlayer);
                return;
            }
        }
    }

    /**
     * Send a message to the given player
     * 
     * @param nickSendingPlayer nick of the sender
     * @param message text of the message
     * @param nickReceivingPlayer nick of the recipient
     * @throws IllegalArgumentException if message is empty or nicks are not valid
     */
    public void sendMessage(String nickSendingPlayer, String message, String nickReceivingPlayer) {
        try (var gameCloseable = game.use()) {
            var game = gameCloseable.obj();
            if (game.getPlayers().stream().noneMatch(p -> p.getNick().equals(nickSendingPlayer)))
                throw new IllegalArgumentException("Sending player: " + nickSendingPlayer + " is not valid player");

            final var isForEveryone = nickReceivingPlayer.equals(UserMessage.EVERYONE_RECIPIENT);
            if (!isForEveryone && game.getPlayers().stream().noneMatch(p -> p.getNick().equals(nickReceivingPlayer)))
                throw new IllegalArgumentException("Sending player: " + nickReceivingPlayer + " is not valid player");

            if (message.equals(""))
                throw new IllegalArgumentException("No text written for message to be sent");

            var ps = game.getPlayers();
            game.message().set(
                    isForEveryone
                            ? UserMessage.forEveryone(nickSendingPlayer, getPlayerColor(nickSendingPlayer, ps), message)
                            : new UserMessage(
                                    nickSendingPlayer, getPlayerColor(nickSendingPlayer, ps),
                                    message,
                                    nickReceivingPlayer, getPlayerColor(nickReceivingPlayer, ps)));
        }
    }

    private String getPlayerColor(String nick, @Unmodifiable List<ServerPlayer> players) {
        int index = 0;
        for (ServerPlayer player : players) {
            if (player.getNick().equals(nick)) {
                index = players.indexOf(player);
            }
        }
        return switch (index) {
            case 0 -> "\033[0;31m"; // RED
            case 1 -> "\033[0;32m"; // GREEN
            case 2 -> "\033[0;33m"; // YELLOW
            case 3 -> "\033[0;36m"; // CYAN
            default -> "";
        };
    }
}
