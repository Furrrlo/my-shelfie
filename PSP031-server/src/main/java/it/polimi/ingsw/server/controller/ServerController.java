package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.server.model.ServerGame;
import it.polimi.ingsw.server.model.ServerLobby;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdaterFactory;
import it.polimi.ingsw.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.ServerPlayer;
import it.polimi.ingsw.updater.LobbyUpdater;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServerController {

    @VisibleForTesting
    protected ServerLobby getOrCreateLobby(String nick) {
        // TODO: like pick game or create one if needed
        return new ServerLobby(4);
    }

    private Lock getLobbyLock(ServerLobby lobby) {
        // TODO: track locks for each lobby
        return new ReentrantLock();
    }

    private <T> Consumer<T> handleDisconnection(ServerPlayer player, ThrowingConsumer<T> c) {
        return handleDisconnection(player.getNick(), player, c);
    }

    private <T> Consumer<T> handleDisconnection(String nick, ThrowingConsumer<T> c) {
        // TODO: extract player instance if present somehow
        return handleDisconnection(nick, null, c);
    }

    private <T> Consumer<T> handleDisconnection(String nick,
                                                @Nullable ServerPlayer player,
                                                ThrowingConsumer<T> c) {
        // TODO: need to somehow unregister all the observers from game and lobby
        return t -> {
            try {
                c.accept(t);
            } catch (DisconnectedException e) {
                throw new RuntimeException("Player " + nick + " disconnected", e); // TODO: wat
            }
        };
    }

    public LobbyView joinGame(String nick,
                              LobbyUpdaterFactory lobbyUpdaterFactory,
                              Supplier<GameController> gameControllerFactory) {
        do {
            final ServerLobby serverLobby = getOrCreateLobby(nick);
            final Lock lock = getLobbyLock(serverLobby);

            lock.lock();
            try {
                // This is basically double-checked locking, getOrCreateGameLobbySomehow() checks with no lock
                // so that it can discard options fast, then here we re-check while actually holding the lock
                // to guarantee concurrency
                final List<String> currentPlayers = serverLobby.joinedPlayers().get();
                if (!currentPlayers.contains(nick) && currentPlayers.size() >= serverLobby.getRequiredPlayers())
                    continue;

                if (!currentPlayers.contains(nick)) {
                    serverLobby.joinedPlayers().update(l -> {
                        final var newList = new ArrayList<>(l);
                        newList.add(nick);
                        return newList;
                    });
                }

                final Lobby lobby = new Lobby(serverLobby.getRequiredPlayers(), serverLobby.joinedPlayers().get());

                final LobbyUpdater lobbyUpdater;
                try {
                    lobbyUpdater = lobbyUpdaterFactory.create(lobby);
                } catch (DisconnectedException e) {
                    throw new RuntimeException(e); // TODO: wat
                }
                serverLobby.joinedPlayers().registerObserver(handleDisconnection(nick, lobbyUpdater::updateJoinedPlayers));
                serverLobby.game().registerObserver(handleDisconnection(nick, game -> {
                    if (game != null)
                        updateGameForPlayer(nick, game, lobbyUpdater, gameControllerFactory);
                }));

                return lobby;
            } finally {
                lock.unlock();
            }
        } while (true);
    }

    private void updateGameForPlayer(String nick,
                                     ServerGame game,
                                     LobbyUpdater lobbyUpdater,
                                     Supplier<GameController> gameControllerFactory)
            throws DisconnectedException {
        // TODO: concurrency: the game can't be edited while doing this

        final Map<String, Player> clientPlayers = game.getPlayers().stream()
                .collect(Collectors.toMap(
                        ServerPlayer::getNick,
                        p -> new Player(p.getNick(), p.getShelfie())));
        final ServerPlayer thePlayer = game.getPlayers().stream()
                .filter(p -> p.getNick().equals(nick))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("" +
                        "Missing player " + nick + " which is supposed to be ingame " +
                        "(found players: " + game.getPlayers() + ")"));

        final GameUpdater gameUpdater = lobbyUpdater.updateGame(new GameAndController<>(
                new Game(
                        game.getGameID(),
                        game.getBoard(),
                        // Use another stream 'cause we need to keep order
                        game.getPlayers().stream()
                                .map(p -> clientPlayers.get(p.getNick()))
                                .collect(Collectors.toList()),
                        game.getPlayers().indexOf(game.currentTurn().get()),
                        game.getCommonGoals().stream()
                                .map(goal -> new CommonGoal(
                                        goal.getType(),
                                        goal.achieved().get().stream()
                                                .map(p -> clientPlayers.get(p.getNick()))
                                                .collect(Collectors.toList())))
                                .collect(Collectors.toList()),
                        thePlayer.getPersonalGoal(),
                        game.firstFinisher().get() == null ? null : game.getPlayers().indexOf(game.firstFinisher().get())),
                gameControllerFactory.get()));
        // Register all listeners to the game model
        game.getBoard().tiles().forEach(tileAndCoords -> tileAndCoords.tile().registerObserver(handleDisconnection(thePlayer,
                tile -> gameUpdater.updateBoardTile(tileAndCoords.row(), tileAndCoords.col(), tile))));
        game.getPlayers()
                .forEach(p -> p.getShelfie().tiles()
                        .forEach(tileAndCoords -> tileAndCoords.tile()
                                .registerObserver(handleDisconnection(thePlayer, tile -> gameUpdater.updatePlayerShelfieTile(
                                        p.getNick(),
                                        tileAndCoords.row(),
                                        tileAndCoords.col(),
                                        tile)))));
        game.currentTurn().registerObserver(handleDisconnection(thePlayer, p -> gameUpdater.updateCurrentTurn(p.getNick())));
        game.firstFinisher()
                .registerObserver(handleDisconnection(thePlayer, p -> gameUpdater.updateFirstFinisher(p.getNick())));
        game.getCommonGoals()
                .forEach(goal -> goal.achieved()
                        .registerObserver(handleDisconnection(thePlayer,
                                players -> gameUpdater.updateAchievedCommonGoal(goal.getType(), players.stream()
                                        .map(ServerPlayer::getNick)
                                        .collect(Collectors.toList())))));
    }

    private interface ThrowingConsumer<T> {

        void accept(T t) throws DisconnectedException;
    }
}
