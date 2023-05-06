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
import it.polimi.ingsw.model.TileAndCoords;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;

class TuiPrompts {

    private static final Logger LOGGER = LoggerFactory.getLogger(TuiPrompts.class);

    private static @Nullable String myNick;

    public static Prompt initialPrompt() {
        return promptNetworkProtocol();
    }

    public static Consumer<TuiPrintStream> initialScene() {
        return TuiPrompts::printLogo;
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

    private static Prompt promptNetworkProtocol() {
        return new ChoicePrompt(
                "Which network protocol do you want to use?",
                new ChoicePrompt.Choice("Socket",
                        (renderer, ctx) -> ctx.prompt(promptSocketAddress(ctx.subPrompt()))),
                new ChoicePrompt.Choice("RMI",
                        (renderer, ctx) -> ctx.prompt(promptRmiAddress(ctx.subPrompt()))));
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
                String.format("Enter server address in <ip[:port]> form (defaults to %s:%d):", defaultHost, defaultPort),
                (renderer, ctx, input) -> {
                    String host = defaultHost;
                    int port = defaultPort;
                    if (!input.isEmpty()) {
                        String[] split = input.split(":", 0);
                        if (split.length > 2)
                            return ctx.invalid("Wrong format");
                        host = split[0];
                        if (split.length == 2) {
                            try {
                                port = Integer.parseInt(split[1]);
                            } catch (NumberFormatException ex) {
                                return ctx.invalid("'" + split[1] + "' is not a valid port number");
                            }
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
                        if (nick.isEmpty())
                            return ctx.invalid("Nick can't be empty");

                        myNick = nick;
                        var lobbyAndController = netManager.joinGame(nick);
                        return ctx.prompt(
                                promptLobby(renderer, netManager, lobbyAndController.lobby(),
                                        lobbyAndController.controller()));
                    } catch (NickNotValidException e) {
                        return ctx.invalid(Objects.requireNonNull(e.getMessage()));
                    } catch (Exception ex) {
                        LOGGER.error("Failed to connect to the server", ex);
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
            if (players.stream().noneMatch(p -> p.getNick().equals(myNick))) {
                renderer.setPrompt(promptReconnect(renderer, netManager));
            } else {
                renderer.rerender();
                // By using always the same readyObserver, we avoid registering dupes, as it's guaranteed by registerObserver
                players.forEach(player -> player.ready().registerObserver(readyObserver));
            }
        });
        lobby.joinedPlayers().get().forEach(player -> player.ready().registerObserver(readyObserver));
        lobby.game().registerObserver(g -> renderer.setPrompt(g != null
                ? promptGame(renderer, netManager, g.game(), g.controller())
                // Game is over, we go back to the lobby
                : doPromptLobby(renderer, netManager, lobby, controller)));
        lobby.requiredPlayers().registerObserver(r -> renderer.rerender());

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
            out.printf("Players (%d/%d):%n", players.size(),
                    Math.max(players.size(), lobby.requiredPlayers().get() != null ? lobby.requiredPlayers().get() : 0));
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

        if (lobby.requiredPlayers().get() == null) {
            return promptRequiredPlayers(netManager, lobby, controller);
        }
        return promptReady(netManager, lobby, controller);
    }

    private static Prompt promptRequiredPlayers(ClientNetManager netManager,
                                                LobbyView lobby,
                                                LobbyController controller) {
        return new InputPrompt("Enter number of players\nIf left blank the game will start when all players are ready",
                (renderer0, ctx, input) -> {
                    int requiredPlayers = 0;
                    if (!input.isEmpty()) {
                        try {
                            requiredPlayers = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            return ctx.invalid("Number not valid");
                        }

                        if (requiredPlayers < 2 || requiredPlayers > 4)
                            return ctx.invalid("Number of players must be between 2 and 4");
                    }
                    try {
                        controller.setRequiredPlayers(requiredPlayers);
                        return ctx.prompt(promptReady(netManager, lobby, controller));
                    } catch (DisconnectedException e) {
                        return ctx.prompt("Disconnected from the server",
                                promptReconnect(renderer0, netManager));
                    }
                });
    }

    private static Prompt promptReady(ClientNetManager netManager,
                                      LobbyView lobby,
                                      LobbyController controller) {
        return new ChoicePrompt("Select an action:",
                new ChoicePrompt.Choice(
                        "Ready",
                        (renderer0, ctx) -> {
                            try {
                                controller.ready(true);
                                return ctx.done();
                            } catch (DisconnectedException e) {
                                return ctx.prompt("Disconnected from the server",
                                        promptReconnect(renderer0, netManager));
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
                                        promptReconnect(renderer0, netManager));
                            }
                        }),
                new ChoicePrompt.Choice(
                        "Modify required players",
                        (renderer0, ctx) -> ctx.prompt(promptRequiredPlayers(netManager, lobby, controller)),
                        () -> lobby.joinedPlayers().get().get(0).getNick().equals(myNick)),
                new ChoicePrompt.Choice(
                        "Start now",
                        (renderer0, ctx) -> {
                            try {
                                controller.setRequiredPlayers(0);
                                return ctx.done();
                            } catch (DisconnectedException e) {
                                return ctx.prompt("Disconnected from the server",
                                        promptReconnect(renderer0, netManager));
                            }
                        },
                        () -> lobby.joinedPlayers().get().get(0).getNick().equals(myNick)
                                && Objects.requireNonNull(lobby.requiredPlayers().get()) != 0
                                && lobby.joinedPlayers().get().stream().allMatch(p -> p.ready().get())),
                new ChoicePrompt.Choice(
                        "Quit",
                        (renderer0, ctx) -> {
                            // TODO: should quit more gracefully
                            System.exit(-1);
                            return ctx.done();
                        }));
    }

    private static Prompt promptReconnect(TuiRenderer renderer,
                                          ClientNetManager netManager) {
        renderer.setScene(out -> {
            printLogo(out);
            out.println();
            out.println(ConsoleColors.RED_BOLD_BRIGHT + "Disconnected from the server" + ConsoleColors.RESET);
            out.println();
            out.println("Server: " + netManager.getHost() + ":" + netManager.getPort());
            out.println();
        });
        return new InputPrompt(
                "Press enter to reconnect",
                (renderer0, ctx, ignored) -> {
                    try {
                        if (myNick == null || myNick.isEmpty())
                            return ctx.prompt("Disconnected from the server",
                                    promptNick(renderer0, ctx.rootPrompt(), netManager));

                        var lobbyAndController = netManager.joinGame(myNick);

                        return ctx.prompt(
                                promptLobby(renderer, netManager, lobbyAndController.lobby(),
                                        lobbyAndController.controller()));
                    } catch (NickNotValidException e) {
                        return ctx.invalid(Objects.requireNonNull(e.getMessage()));
                    } catch (Exception ex) {
                        LOGGER.error("Failed to connect to the server", ex);
                        return ctx.invalid("Failed to connect to the server");
                    }
                });
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
        game.thePlayer().connected().registerObserver(c -> {
            if (!c) {
                renderer.setPrompt(promptReconnect(renderer, netManager));
            }
        });

        renderer.setScene(new TuiGameScene(game));

        return new ChoicePrompt(
                "Select an action:",
                new ChoicePrompt.Choice(
                        "Make move",
                        (renderer0, ctx) -> {
                            if (!game.currentTurn().get().equals(game.thePlayer()))
                                return ctx.invalid("It's not your turn");
                            return ctx.prompt(promptBoard(ctx.subPrompt(), netManager, game, controller, List.of()));
                        }),
                new ChoicePrompt.Choice(
                        "Zoom shelfie and personal goal",
                        (renderer0, ctx) -> ctx.prompt(zoomShelfie(ctx.subPrompt(), renderer, game))),
                new ChoicePrompt.Choice(
                        "Zoom board",
                        (renderer0, ctx) -> ctx.prompt(zoomBoard(ctx.subPrompt(), renderer, game))),
                new ChoicePrompt.Choice(
                        "Quit",
                        (renderer0, ctx) -> {
                            // TODO: should quit more gracefully
                            System.exit(-1);
                            return ctx.done();
                        }));
    }

    private static Prompt zoomShelfie(Prompt.Factory promptFactory,
                                      TuiRenderer renderer,
                                      GameView game) {

        renderer.setScene(new TuiPrinter.TuiShelfiePrinter(game));

        return promptFactory.input(
                "",
                (renderer0, ctx, input) -> {
                    renderer.setScene(new TuiGameScene(game));
                    return ctx.done();
                });
    }

    private static Prompt zoomBoard(Prompt.Factory promptFactory,
                                    TuiRenderer renderer,
                                    GameView game) {
        renderer.setScene(new TuiPrinter.TuiBoardPrinter(game));

        return promptFactory.input(
                "",
                (renderer0, ctx, input) -> {
                    renderer.setScene(new TuiGameScene(game));
                    return ctx.done();
                });
    }

    private static Prompt promptBoard(Prompt.Factory promptFactory,
                                      ClientNetManager netManager,
                                      GameView game,
                                      GameController controller,
                                      @Unmodifiable List<BoardCoord> coords) {
        final Pattern coordPattern = Pattern.compile("(?<col>[a-z]|[A-Z])(?<row>\\d)");
        final var picked = "Picked: " + coordsToDisplayString(game, coords);
        return promptFactory.input(
                coords.isEmpty()
                        ? "Select first tile to pick (ex: E3): "
                        : coords.size() == 1
                                ? "Select second tile to pick (ex: B5) or 'Done':\n" + picked
                                : "Select last tile to pick (ex: F6) or 'Done':\n" + picked,
                (renderer0, ctx, input) -> {
                    if (!coords.isEmpty() && input.equalsIgnoreCase("done"))
                        return ctx.prompt(promptCol(ctx.subPrompt(), netManager, game, controller, coords));

                    var matcher = coordPattern.matcher(input);
                    if (!matcher.matches())
                        return ctx.invalid("Unrecognized coords format " + input);

                    var rowString = matcher.group("row");
                    var colString = matcher.group("col").toUpperCase(Locale.ROOT);

                    int r;
                    try {
                        r = Integer.parseInt(rowString) - 1;
                    } catch (NumberFormatException ex) {
                        return ctx.invalid("Invalid row " + rowString);
                    }

                    if (colString.length() != 1)
                        return ctx.invalid("Invalid col " + colString);

                    int c = colString.charAt(0) - 'A';
                    if (c > game.getBoard().getCols())
                        return ctx.invalid("Invalid col " + colString + ". " +
                                "Last col is " + (char) ('A' + game.getBoard().getCols() - 1));

                    if (!game.getBoard().isValidTile(r, c))
                        return ctx.invalid(input + " is not a tile");
                    if (!game.getBoard().hasFreeSide(r, c))
                        return ctx.invalid(input + " does not have a free side");

                    var coord = new BoardCoord(r, c);
                    if (coords.contains(coord))
                        return ctx.invalid(input + " was already picked");

                    var newCoords = Stream.concat(coords.stream(), Stream.of(coord)).toList();
                    if (!game.getBoard().checkBoardCoord(newCoords))
                        return ctx.invalid(newCoords.size() == 2
                                ? input + " has no common side with the other"
                                : input + " cannot be picked with the others");

                    return ctx.prompt(newCoords.size() < 3
                            ? promptBoard(ctx.subPrompt(), netManager, game, controller, newCoords)
                            : promptCol(ctx.subPrompt(), netManager, game, controller, newCoords));
                });
    }

    private static Prompt promptCol(Prompt.Factory promptFactory,
                                    ClientNetManager netManager,
                                    GameView game,
                                    GameController controller,
                                    @Unmodifiable List<BoardCoord> coords) {
        final var picked = "Picked: " + coordsToDisplayString(game, coords);
        return promptFactory.input(
                "Select the shelfie column:\n" + picked,
                (renderer0, ctx, input) -> {
                    int col;
                    try {
                        col = Integer.parseInt(input) - 1;
                    } catch (NumberFormatException e) {
                        return ctx.invalid("You have to select a column");
                    }

                    if (!game.thePlayer().getShelfie().checkColumnSpace(col, coords.size()))
                        return ctx.invalid("There's not enough space in column " + (col + 1));

                    return ctx.prompt(promptOrder(ctx.subPrompt(), netManager, game, controller, coords, col));
                });
    }

    private static Prompt promptOrder(Prompt.Factory promptFactory,
                                      ClientNetManager netManager,
                                      GameView game,
                                      GameController controller,
                                      @Unmodifiable List<BoardCoord> coords,
                                      int shelfCol) {
        final String delimiter = ",";

        final var exampleOrder = new ArrayList<>(coords);
        Collections.shuffle(exampleOrder);
        final var example = exampleOrder.stream()
                .map(TuiPrompts::coordToBoardDisplayString)
                .collect(Collectors.joining(delimiter));

        return promptFactory.input(
                "Change the tile order (ex: '" + example + "'):\n" +
                        "Insertion Order (FIFO): " + coordsToDisplayString(game, coords) + "\n" +
                        "Shelfie column: " + shelfCol + "\n" +
                        "Type 'Done' when satisfied",
                (renderer0, ctx, input) -> {
                    if (input.equalsIgnoreCase("done")) {
                        try {
                            controller.makeMove(coords, shelfCol);
                        } catch (DisconnectedException e) {
                            return ctx.prompt("Disconnected from the server",
                                    promptReconnect(renderer0, netManager));
                        }
                        return ctx.done();
                    }

                    final var inputParts = input.split(delimiter, -1);
                    if (inputParts.length != coords.size())
                        return ctx.invalid("Missing some coords, found only " + inputParts.length);

                    var newCoords = new ArrayList<BoardCoord>();
                    for (String inputPart : inputParts) {
                        final var maybeCoord = coords.stream()
                                .filter(c -> inputPart.equalsIgnoreCase(coordToBoardDisplayString(c)))
                                .findFirst();
                        if (maybeCoord.isEmpty())
                            return ctx.invalid("Invalid coord " + inputPart);
                        if (newCoords.contains(maybeCoord.get()))
                            return ctx.invalid(inputPart + " was put multiple times");

                        newCoords.add(maybeCoord.get());
                    }

                    return ctx.prompt(promptOrder(ctx.subPrompt(), netManager, game, controller,
                            Collections.unmodifiableList(newCoords), shelfCol));
                });
    }

    private static String coordsToDisplayString(GameView game, List<BoardCoord> coords) {
        return coords.stream()
                .map(c -> new TileAndCoords<>(game.getBoard().tile(c.row(), c.col()).get(), c.row(), c.col()))
                .filter(c -> c.tile() != null)
                .map(c -> coordToBoardDisplayString(new BoardCoord(c.row(), c.col())) + ": " +
                        TuiColorConverter.color(c.tile().getColor(), false) + pxl + ConsoleColors.RESET)
                .map(s -> '(' + s + ')')
                .collect(Collectors.joining(", "));
    }

    private static String coordToBoardDisplayString(BoardCoord c) {
        return String.valueOf((char) ('A' + c.col())) + (c.row() + 1);
    }
}
