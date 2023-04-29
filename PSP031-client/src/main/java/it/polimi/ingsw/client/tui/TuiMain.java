package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.NickNotValidException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.LobbyView;
import org.fusesource.jansi.AnsiConsole;

import java.net.InetSocketAddress;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TuiMain {

    public static void main(String[] args) {
        //If the client has multiple network adapters (e.g. virtualbox adapter), rmi may export objects to the wrong interface.
        //@see https://bugs.openjdk.org/browse/JDK-8042232
        // To work around this, run JVM with the parameter -Djava.rmi.server.hostname=<client address> or uncomment the following line.
        //System.setProperty("java.rmi.server.hostname", "<client address>");
        AnsiConsole.systemInstall();
        new TuiRenderer(
                AnsiConsole.out(),
                System.in,
                new ChoicePrompt(
                        "Which network protocol do you want to use?",
                        new ChoicePrompt.Choice("Socket",
                                (renderer, ctx) -> ctx.prompt(promptSocketAddress(ctx.subPrompt()))),
                        new ChoicePrompt.Choice("RMI",
                                (renderer, ctx) -> ctx.prompt(promptRmiAddress(ctx.subPrompt())))),
                TuiMain::printLogo);
    }

    private static void printLogo(TuiPrintStream out) {
        String f = ConsoleColors.YELLOW_BRIGHT;
        String b = ConsoleColors.YELLOW;
        out.println("" +
        //@formatter:off
        b + "───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n" +
        b + "─██████──────────██████─████████──████████────██████████████─██████──██████─██████████████─██████─────────██████████████─██████████─██████████████─\n" +
        b + "─██" + f + "░░" + b + "██████" +     "██" +     "██████" + f + "░░" + b + "██─██" + f + "░░" +     "░░" + b + "██" +     "──" +     "██" + f + "░░" +     "░░" + b + "██────██" + f + "░░" +     "░░░░░░" +     "░░" + b + "██─██" + f + "░░" + b + "██──██" + f + "░░" + b + "██─██" + f + "░░" +     "░░░░░░░░" + b + "██─██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" +     "░░░░░░░░" + b + "██─██" + f + "░░" +     "░░" +     "░░" + b + "██─██" + f + "░░" +     "░░░░░░░░" + b + "██─\n" +
        b + "─██" + f + "░░" +     "░░░░░░" +     "░░" +     "░░░░░░" +     "░░" + b + "██─██" +     "██" + f + "░░" + b + "██" +     "──" +     "██" + f + "░░" + b + "██" +     "██────██" + f + "░░" + b + "██████" +     "██" +     "██─██" + f + "░░" + b + "██──██" + f + "░░" + b + "██─██" + f + "░░" + b + "████████" +     "██─██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" + b + "████████" +     "██─██" +     "██" + f + "░░" + b + "██" +     "██─██" + f + "░░" + b + "████████" +     "██─\n" +
        b + "─██" + f + "░░" + b + "██████" + f + "░░" + b + "██████" + f + "░░" + b + "██───" +     "██" + f + "░░" +     "░░" + b + "██" + f + "░░" +     "░░" + b + "██" +     "──────██" + f + "░░" + b + "██────" +     "──" +     "───██" + f + "░░" + b + "██──██" + f + "░░" + b + "██─██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" + b + "██──────" +     "─────" +     "██" + f + "░░" + b + "██" +     "───██" + f + "░░" + b + "██──────" +     "───\n" +
        b + "─██" + f + "░░" + b + "██──██" + f + "░░" + b + "██──██" + f + "░░" + b + "██───" +     "██" +     "██" + f + "░░" +     "░░" +     "░░" + b + "██" +     "██" +     "──────██" + f + "░░" + b + "██████" +     "██" +     "██─██" + f + "░░" + b + "██████" + f + "░░" + b + "██─██" + f + "░░" + b + "████████" +     "██─██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" + b + "████████" +     "██───" +     "██" + f + "░░" + b + "██" +     "───██" + f + "░░" + b + "████████" +     "██─\n" +
        b + "─██" + f + "░░" + b + "██──██" + f + "░░" + b + "██──██" + f + "░░" + b + "██───" +     "──" +     "██" +     "██" + f + "░░" + b + "██" +     "██" +     "──" +     "──────██" + f + "░░" +     "░░░░░░" +     "░░" + b + "██─██" + f + "░░" +     "░░░░░░" +     "░░" + b + "██─██" + f + "░░" +     "░░░░░░░░" + b + "██─██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" +     "░░░░░░░░" + b + "██───" +     "██" + f + "░░" + b + "██" +     "───██" + f + "░░" +     "░░░░░░░░" + b + "██─\n" +
        b + "─██" + f + "░░" + b + "██──██" +     "██" +     "██──██" + f + "░░" + b + "██───" +     "──" +     "──" +     "██" + f + "░░" + b + "██" +     "──" +     "──" +     "──────██" +     "██" +     "██████" + f + "░░" + b + "██─██" + f + "░░" + b + "██████" + f + "░░" + b + "██─██" + f + "░░" + b + "████████" +     "██─██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" + b + "████████" +     "██───" +     "██" + f + "░░" + b + "██" +     "───██" + f + "░░" + b + "████████" +     "██─\n" +
        b + "─██" + f + "░░" + b + "██────" +     "──" +     "────██" + f + "░░" + b + "██───" +     "──" +     "──" +     "██" + f + "░░" + b + "██" +     "──" +     "──" +     "────────" +     "──" +     "────██" + f + "░░" + b + "██─██" + f + "░░" + b + "██──██" + f + "░░" + b + "██─██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" + b + "██──────" +     "─────" +     "██" + f + "░░" + b + "██" +     "───██" + f + "░░" + b + "██──────" +     "───\n" +
        b + "─██" + f + "░░" + b + "██────" +     "──" +     "────██" + f + "░░" + b + "██───" +     "──" +     "──" +     "██" + f + "░░" + b + "██" +     "──" +     "──" +     "──────██" +     "██" +     "██████" + f + "░░" + b + "██─██" + f + "░░" + b + "██──██" + f + "░░" + b + "██─██" + f + "░░" + b + "████████" +     "██─██" + f + "░░" + b + "████████" +     "██─██" + f + "░░" + b + "██──────" +     "───██" +     "██" + f + "░░" + b + "██" +     "██─██" + f + "░░" + b + "████████" +     "██─\n" +
        b + "─██" + f + "░░" + b + "██────" +     "──" +     "────██" + f + "░░" + b + "██───" +     "──" +     "──" +     "██" + f + "░░" + b + "██" +     "──" +     "──" +     "──────██" + f + "░░" +     "░░░░░░" +     "░░" + b + "██─██" + f + "░░" + b + "██──██" + f + "░░" + b + "██─██" + f + "░░" +     "░░░░░░░░" + b + "██─██" + f + "░░" +     "░░░░░░░░" + b + "██─██" + f + "░░" + b + "██──────" +     "───██" + f + "░░" +     "░░" +     "░░" + b + "██─██" + f + "░░" +     "░░░░░░░░" + b + "██─\n" +
        b + "─██████──────────██████───────██████──────────██████████████─██████──██████─██████████████─██████████████─██████─────────██████████─██████████████─\n" +
        b + "───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n" +
        ConsoleColors.RESET
        //@formatter:on
        );
    }

    private static Prompt promptSocketAddress(Prompt.Factory promptFactory) {
        return promptAddress(promptFactory, 1234,
                (host, port) -> new SocketClientNetManager(new InetSocketAddress(host, port)));
    }

    private static Prompt promptRmiAddress(Prompt.Factory promptFactory) {
        return promptAddress(promptFactory, Registry.REGISTRY_PORT, RmiClientNetManager::new);
    }

    private static Prompt promptAddress(Prompt.Factory promptFactory,
                                        int defaultPort,
                                        BiFunction<String, Integer, ClientNetManager> netManagerFactory) {
        final String defaultHost = "localhost";
        return promptFactory.input(
                String.format("Enter server address in <ip:port> form (defaults to %s:%d):", defaultHost, defaultPort),
                (renderer, ctx, input) -> {
                    final String host;
                    final int port;
                    if (input.trim().isEmpty()) {
                        host = defaultHost;
                        port = defaultPort;
                    } else {
                        String[] split = input.split(":", -1);
                        if (split.length != 2)
                            return ctx.invalid("Missing semicolon separator");

                        host = split[0];
                        try {
                            port = Integer.parseInt(split[1]);
                        } catch (NumberFormatException ex) {
                            return ctx.invalid("'" + split[1] + "' is not a valid port number");
                        }
                    }

                    return ctx.prompt(promptNick(renderer, ctx.subPrompt(), netManagerFactory.apply(host, port)));
                });
    }

    private static Prompt promptNick(TuiRenderer renderer, Prompt.Factory promptFactory, ClientNetManager netManager) {
        renderer.setScene(out -> {
            printLogo(out);
            out.println();
            out.println("Server: " + netManager.getHost() + ":" + netManager.getPort());
            out.println();
        });
        return promptFactory.input(
                "Choose a nickname:",
                (renderer0, ctx, nick) -> {
                    try {
                        var lobbyAndController = netManager.joinGame(nick);
                        return ctx.prompt(
                                promptLobby(renderer, netManager, lobbyAndController.lobby(), lobbyAndController.controller()));
                    } catch (NickNotValidException e) {
                        return ctx.invalid(Objects.requireNonNull(e.getMessage()));
                    } catch (Exception ex) {
                        // TODO: logging
                        ex.printStackTrace();
                        return ctx.invalid("Failed to connect to the server");
                    }
                });
    }

    private static Prompt promptLobby(TuiRenderer renderer,
                                      ClientNetManager netManager,
                                      LobbyView lobby,
                                      LobbyController controller) {
        final Consumer<Boolean> readyObserver = b -> renderer.rerender();
        lobby.joinedPlayers().registerObserver(players -> {
            renderer.rerender();
            // By using always the same readyObserver, we avoid registering dupes, as it's guaranteed by registerObserver
            players.forEach(player -> player.ready().registerObserver(readyObserver));
        });
        lobby.joinedPlayers().get().forEach(player -> player.ready().registerObserver(readyObserver));
        lobby.game().registerObserver(g -> renderer.setPrompt(g != null
                ? promptGame(renderer, netManager, g.game(), g.controller())
                // Game is over, we go back to the lobby
                : doPromptLobby(renderer, netManager, lobby, controller)));

        var currGame = lobby.game().get();
        return currGame != null
                ? promptGame(renderer, netManager, currGame.game(), currGame.controller())
                : doPromptLobby(renderer, netManager, lobby, controller);
    }

    private static Prompt doPromptLobby(TuiRenderer renderer,
                                        ClientNetManager netManager,
                                        LobbyView lobby,
                                        LobbyController controller) {
        renderer.setScene(out -> {
            printLogo(out);
            out.println();
            out.println("Server: " + netManager.getHost() + ":" + netManager.getPort());
            out.println();

            var players = lobby.joinedPlayers().get();
            out.printf("Players (%d/%d):%n", players.size(), Math.max(players.size(), lobby.getRequiredPlayers()));
            out.println();

            int i;
            for (i = 0; i < players.size(); i++) {
                var player = players.get(i);
                out.print(player.ready().get()
                        ? (ConsoleColors.GREEN + "R " + ConsoleColors.RESET)
                        : (ConsoleColors.RED + "N " + ConsoleColors.RESET));
                out.println(player.getNick());
            }

            for (; i < 4; i++)
                out.println();
        });

        return new ChoicePrompt(
                "Select an action:",
                new ChoicePrompt.Choice(
                        "Ready",
                        (renderer0, ctx) -> {
                            try {
                                controller.ready(true);
                                return ctx.done();
                            } catch (DisconnectedException e) {
                                return ctx.prompt("Disconnected from the server",
                                        promptNick(renderer0, ctx.rootPrompt(), netManager));
                            }
                        }),
                new ChoicePrompt.Choice(
                        "Not ready",
                        (renderer0, ctx) -> {
                            try {
                                controller.ready(false);
                                return ctx.done();
                            } catch (DisconnectedException e) {
                                return ctx.prompt("Disconnected from the server",
                                        promptNick(renderer0, ctx.rootPrompt(), netManager));
                            }
                        }),
                new ChoicePrompt.Choice(
                        "Quit",
                        (renderer0, ctx) -> {
                            // TODO: should quit more gracefully
                            System.exit(-1);
                            return ctx.done();
                        }));
    }

    private static Prompt promptGame(TuiRenderer renderer,
                                     ClientNetManager netManager,
                                     GameView game,
                                     GameController controller) {
        game.getBoard().tiles().forEach(t -> t.tile().registerObserver(c -> renderer.rerender()));
        game.currentTurn().registerObserver(c -> renderer.rerender());
        game.firstFinisher().registerObserver(c -> renderer.rerender());
        game.getCommonGoals().forEach(g -> g.achieved().registerObserver(c -> renderer.rerender()));
        game.getPlayers().forEach(p -> {
            p.connected().registerObserver(c -> renderer.rerender());
            p.score().registerObserver(c -> renderer.rerender());
            p.getShelfie().tiles().forEach(t -> t.tile().registerObserver(c -> renderer.rerender()));
        });

        renderer.setScene(new TuiGameScene(game));

        return new ChoicePrompt(
                "Select an action:",
                new ChoicePrompt.Choice(
                        "Make move",
                        (renderer0, ctx) -> {
                            if (!game.currentTurn().get().equals(game.thePlayer()))
                                return ctx.invalid("It's not your turn");
                            return ctx.prompt(promptBoard(ctx.subPrompt(), netManager, game, controller));
                        }),
                new ChoicePrompt.Choice(
                        "Quit",
                        (renderer0, ctx) -> {
                            // TODO: should quit more gracefully
                            System.exit(-1);
                            return ctx.done();
                        }));
    }

    private static Prompt promptBoard(Prompt.Factory promptFactory,
                                      ClientNetManager netManager,
                                      GameView game,
                                      GameController controller) {
        return promptFactory.input(
                "Coords of tiles in the board (in the desired order):\nRxC;RxC;RxC",
                (renderer0, ctx, input) -> {
                    List<BoardCoord> coords = new ArrayList<>();
                    String[] v = input.split(";", -1);
                    for (String currCoords : v) {
                        String[] coordsSplit = currCoords.split("x", -1);
                        if (coordsSplit.length != 2)
                            return ctx.invalid("Invalid coords string " + currCoords);

                        int r;
                        try {
                            r = Integer.parseInt(coordsSplit[0]) - 1;
                        } catch (NumberFormatException ex) {
                            return ctx.invalid("Invalid row " + coordsSplit[0] + " in coords " + currCoords);
                        }

                        int c;
                        try {
                            c = Integer.parseInt(coordsSplit[1]) - 1;
                        } catch (NumberFormatException ex) {
                            return ctx.invalid("Invalid col " + coordsSplit[1] + " in coords " + currCoords);
                        }

                        coords.add(new BoardCoord(r, c));
                    }
                    if (!game.getBoard().checkBoardCoord(coords))
                        return ctx.invalid("Invalid selection");
                    return ctx.prompt(promptCol(ctx.subPrompt(), netManager, game, controller, coords));
                });
    }

    private static Prompt promptCol(Prompt.Factory promptFactory,
                                    ClientNetManager netManager,
                                    GameView game,
                                    GameController controller,
                                    List<BoardCoord> coords) {
        return promptFactory.input(
                "Select the column: ",
                (renderer0, ctx, input) -> {
                    try {
                        int col = Integer.parseInt(input) - 1;
                        if (!game.thePlayer().getShelfie().checkColumnSpace(col, coords.size()))
                            return ctx.invalid("Invalid col " + (col + 1));
                        controller.makeMove(coords, col);
                    } catch (NumberFormatException e) {
                        return ctx.invalid("You have to select a column");
                    } catch (DisconnectedException e) {
                        return ctx.prompt("Disconnected from the server",
                                promptNick(renderer0, ctx.rootPrompt(), netManager));
                    }
                    return ctx.done();
                });
    }
}
