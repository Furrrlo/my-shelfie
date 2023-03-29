package it.polimi.ingsw.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.PersonalGoal;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.Type;
import it.polimi.ingsw.server.controller.GameServerController;
import it.polimi.ingsw.server.controller.LobbyServerController;
import it.polimi.ingsw.server.controller.LockProtected;
import it.polimi.ingsw.server.controller.ServerController;
import it.polimi.ingsw.server.model.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ControllersIntegrationTest {

    public static void doTestControllers(Consumer<ServerController> bindServerController,
                                         Supplier<ClientNetManager> clientNetManagerFactory)
            throws Throwable {
        final var nick = "test_nickname";

        var readyPromise = new CompletableFuture<Arguments>();

        final var serverLobbyPromise = new CompletableFuture<ServerLobby>();
        bindServerController.accept(new ServerController() {

            @Override
            protected ServerLobbyAndController<ServerLobby> getOrCreateLobby(String nick) {
                final var lobbyAndController = super.getOrCreateLobby(nick);
                serverLobbyPromise.complete(lobbyAndController.lobby().getUnsafe());
                return new ServerLobbyAndController<>(
                        lobbyAndController.lobby(),
                        new LobbyServerController(lobbyAndController.lobby()) {
                            @Override
                            public void ready(String nick, boolean ready) {
                                readyPromise.complete(Arguments.of(nick, ready));
                            }
                        });
            }
        });

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
        final List<ServerPlayer> players;
        final var serverLobby = serverLobbyPromise.get(500, TimeUnit.MILLISECONDS);
        serverLobby.game().set(new ServerGameAndController<>(new LockProtected<>(serverGame = new ServerGame(
                0,
                new Board(serverLobby.joinedPlayers().get().size()),
                List.of(),
                players = serverLobby.joinedPlayers().get().stream()
                        .map(n -> new ServerPlayer(n.getNick(), new PersonalGoal(new Tile[6][5])))
                        .collect(Collectors.toList()),
                players.size() - 1,
                List.of(new ServerCommonGoal(Type.CROSS), new ServerCommonGoal(Type.ALL_CORNERS)))),
                new GameServerController(serverGame) {
                    @Override
                    public void makeMove(ServerPlayer player, List<BoardCoord> selected, int shelfCol) {
                        makeMovePromise.complete(Arguments.of(player, selected, shelfCol));
                    }
                }));
        final var thePlayer = players.stream()
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