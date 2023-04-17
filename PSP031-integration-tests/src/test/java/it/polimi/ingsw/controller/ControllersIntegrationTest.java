package it.polimi.ingsw.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.controller.LobbyServerController;
import it.polimi.ingsw.server.controller.LockProtected;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.model.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.provider.Arguments;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ControllersIntegrationTest {

    public static void doTestControllers(Function<ServerController, Closeable> bindServerController,
                                         Supplier<ClientNetManager> clientNetManagerFactory)
            throws Throwable {
        final var nick = "test_nickname";

        var readyPromise = new CompletableFuture<Arguments>();

        final var serverLobbyPromise = new CompletableFuture<LockProtected<ServerLobby>>();
        try (var serverController = new ServerController(5, TimeUnit.SECONDS) {

            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lobbyAndController = super.getOrCreateLobby(nick);
                serverLobbyPromise.complete(lobbyAndController.lobby());
                return new ServerLobbyAndController<>(
                        lobbyAndController.lobby(),
                        new LobbyServerController(lobbyAndController.lobby()) {
                            @Override
                            public void ready(String nick, boolean ready) {
                                readyPromise.complete(Arguments.of(nick, ready));
                            }
                        });
            }
        }; Closeable ignored = bindServerController.apply(serverController)) {

            var lobbyAndController = clientNetManagerFactory.get().joinGame(nick);
            LobbyController lobbyController = lobbyAndController.controller();

            final var gameAndControllerPromise = new CompletableFuture<GameAndController<?>>();
            lobbyAndController.lobby().game().registerObserver(gameAndControllerPromise::complete);
            assertControllerMethodCalled(
                    "ready",
                    () -> lobbyController.ready(true),
                    Arguments.of(nick, true),
                    readyPromise);

            final var makeMovePromise = new CompletableFuture<Arguments>();

            final ServerGame serverGame;
            final var lockedServerLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
            try (var lobbyCloseable = lockedServerLobby.use()) {
                var serverLobby = lobbyCloseable.obj();
                serverLobby.game().set(new ServerGameAndController<>(
                        serverGame = LobbyServerController.createGame(0, serverLobby.joinedPlayers().get()),
                        new GameServerController(new LockProtected<>(serverGame, lockedServerLobby.getLock())) {
                            @Override
                            public void makeMove(ServerPlayer player, List<BoardCoord> selected, int shelfCol) {
                                makeMovePromise.complete(Arguments.of(player, selected, shelfCol));
                            }
                        }));
            }
            final var thePlayer = serverGame.getPlayers().stream()
                    .filter(p -> p.getNick().equals(nick))
                    .findFirst()
                    .orElseThrow();

            var gameAndController = gameAndControllerPromise.get(500, TimeUnit.MILLISECONDS);
            var gameController = gameAndController.controller();
            assertControllerMethodCalled(
                    "makeMove",
                    () -> gameController.makeMove(List.of(new BoardCoord(0, 0), new BoardCoord(5, 5)), 0),
                    Arguments.of(thePlayer, List.of(new BoardCoord(0, 0), new BoardCoord(5, 5)), 0),
                    makeMovePromise);
        }
    }

    private static void assertControllerMethodCalled(String name,
                                                     Executable executable,
                                                     Arguments expectedArguments,
                                                     CompletableFuture<Arguments> actualArguments)
            throws Throwable {
        executable.execute();
        try {
            assertArrayEquals(
                    expectedArguments.get(),
                    actualArguments.get(500, TimeUnit.MILLISECONDS).get(),
                    "Received wrong parameters for controller method " + name);
        } catch (TimeoutException ex) {
            throw new AssertionError("Failed to wait for controller method call " + name, ex);
        }
    }
}
