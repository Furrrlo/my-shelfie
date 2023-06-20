package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.client.network.rmi.RmiClientNetManager;
import it.polimi.ingsw.client.network.socket.SocketClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.controller.NickNotValidException;
import it.polimi.ingsw.model.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.polimi.ingsw.client.tui.TuiPrintStream.pxl;

/**
 * Prompts for the TUI version of the game
 */
class TuiPrompts {

    private static final Logger LOGGER = LoggerFactory.getLogger(TuiPrompts.class);

    public static Prompt initialPrompt() {
        return promptNetworkProtocol();
    }

    public static TuiScene initialScene() {
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
        return promptAddress(promptFactory, SocketClientNetManager.DEFAULT_PORT,
                (host, port, nick) -> SocketClientNetManager.connect(new InetSocketAddress(host, port), nick));
    }

    private static Prompt promptRmiAddress(Prompt.Factory promptFactory) {
        return promptAddress(promptFactory, Registry.REGISTRY_PORT,
                (host, port, nick) -> RmiClientNetManager.connect(host, port, nick));
    }

    private static Prompt promptAddress(Prompt.Factory promptFactory,
                                        int defaultPort,
                                        ClientNetManager.Factory netManagerFactory) {
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
                    return ctx.prompt(promptNick(renderer, ctx.subPrompt(), netManagerFactory, host, port));
                });
    }

    private static Prompt promptNick(TuiRenderer renderer,
                                     Prompt.Factory promptFactory,
                                     ClientNetManager.Factory netManagerFactory,
                                     String host,
                                     int port) {
        renderer.setScene(out -> {
            printLogo(out);
            out.println();
            out.println("Server: " + host + ":" + port);
            out.println();
        });
        return promptFactory.input(
                "Choose a nickname:",
                (renderer0, ctx, nick) -> {
                    ClientNetManager netManager = null;
                    try {
                        if (nick.isEmpty())
                            return ctx.invalid("Nick can't be empty");

                        netManager = netManagerFactory.create(host, port, nick);
                        var lobbyAndController = netManager.joinGame();
                        return ctx.prompt(
                                promptLobby(renderer, netManager, lobbyAndController.lobby(),
                                        lobbyAndController.controller()));
                    } catch (NickNotValidException e) {
                        return ctx.invalid(Objects.requireNonNull(e.getMessage()));
                    } catch (Throwable ex) {
                        if (netManager != null) {
                            try {
                                netManager.close();
                            } catch (IOException ex0) {
                                ex.addSuppressed(ex0);
                            }
                        }

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
        lobby.thePlayerConnected().registerObserver(connected -> {
            if (!connected)
                renderer.setPrompt(promptReconnect(renderer, netManager, null));
        });
        lobby.joinedPlayers().registerObserver(players -> {
            renderer.rerender();
            // By using always the same readyObserver, we avoid registering dupes, as it's guaranteed by registerObserver
            players.forEach(player -> player.ready().registerObserver(readyObserver));
        });
        lobby.joinedPlayers().get().forEach(player -> player.ready().registerObserver(readyObserver));
        lobby.game().registerObserver(g -> {
            if (g != null) {
                registerGameObservers(renderer, netManager, g.game(), g.controller());
                if (g.game().endGame().get())
                    renderer.setPrompt(promptEndGame(renderer, netManager, g.game()));
                else if (g.game().suspended().get())
                    renderer.setPrompt(promptSuspended());
                else
                    renderer.setPrompt(promptGame(renderer, netManager, g.game(), g.controller()));
            } else {
                // Game is over, we go back to the lobby
                renderer.setPrompt(doPromptLobby(renderer, netManager, lobby, controller));
            }
        });
        lobby.requiredPlayers().registerObserver(r -> renderer.rerender());

        var currGame = lobby.game().get();

        if (currGame != null) {
            registerGameObservers(renderer, netManager, currGame.game(), currGame.controller());
            if (currGame.game().endGame().get())
                return promptEndGame(renderer, netManager, currGame.game());
            else if (currGame.game().suspended().get())
                return promptSuspended();
            else
                return promptGame(renderer, netManager, currGame.game(), currGame.controller());
        }
        return doPromptLobby(renderer, netManager, lobby, controller);
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
            var requiredPlayers = lobby.requiredPlayers().get();
            out.printf("Players (%d/%d):%n", players.size(),
                    Math.max(players.size(), requiredPlayers != null ? requiredPlayers : 0));
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

                        if (requiredPlayers < LobbyView.MIN_PLAYERS || requiredPlayers > LobbyView.MAX_PLAYERS)
                            return ctx.invalid("Number of players must be between 2 and 4");
                    }
                    try {
                        controller.setRequiredPlayers(requiredPlayers);
                        return ctx.prompt(promptReady(netManager, lobby, controller));
                    } catch (DisconnectedException e) {
                        return ctx.prompt("Disconnected from the server",
                                promptReconnect(renderer0, netManager, e));
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
                                        promptReconnect(renderer0, netManager, e));
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
                                        promptReconnect(renderer0, netManager, e));
                            }
                        }),
                new ChoicePrompt.Choice(
                        "Modify required players",
                        (renderer0, ctx) -> ctx.prompt(promptRequiredPlayers(netManager, lobby, controller)),
                        () -> lobby.joinedPlayers().get().size() > 0
                                && lobby.joinedPlayers().get().get(0).getNick().equals(netManager.getNick())),
                new ChoicePrompt.Choice(
                        "Start now",
                        (renderer0, ctx) -> {
                            try {
                                controller.setRequiredPlayers(0);
                                return ctx.done();
                            } catch (DisconnectedException e) {
                                return ctx.prompt("Disconnected from the server",
                                        promptReconnect(renderer0, netManager, e));
                            }
                        },
                        () -> lobby.joinedPlayers().get().size() >= 2
                                && lobby.isLobbyCreator(netManager.getNick())
                                && Optional.ofNullable(lobby.requiredPlayers().get())
                                        .map(req -> req != 0)
                                        .orElse(false)
                                && lobby.joinedPlayers().get().stream().allMatch(p -> p.ready().get())),
                new ChoicePrompt.Choice(
                        "Quit",
                        (renderer0, ctx) -> {
                            try {
                                netManager.close();
                                System.exit(0);
                            } catch (IOException ex) {
                                LOGGER.error("Failed to disconnect from the server while closing", ex);
                                System.exit(-1);
                            }

                            throw new AssertionError("Should never be reached");
                        }));
    }

    private static Prompt promptReconnect(TuiRenderer renderer, ClientNetManager oldNetManager, @Nullable Throwable cause) {
        if (cause != null)
            LOGGER.error("Unexpected disconnection from the server", cause);

        try {
            oldNetManager.close();
        } catch (IOException e) {
            LOGGER.error("Failed to disconnect from old ClientNetManager (which is supposed to be already disconnected)", e);
        }

        renderer.setScene(out -> {
            printLogo(out);
            out.println();
            out.println(ConsoleColors.RED_BOLD_BRIGHT + "Disconnected from the server" + ConsoleColors.RESET);
            out.println();
            out.println("Server: " + oldNetManager.getHost() + ":" + oldNetManager.getPort());
            out.println();
        });
        return new InputPrompt(
                "Press enter to reconnect",
                (renderer0, ctx, ignored) -> {
                    try {
                        var netManager = oldNetManager.recreateAndReconnect();
                        var lobbyAndController = netManager.joinGame();

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

    private static Prompt promptSuspended() {
        return new InputPrompt(
                """
                        The game is suspended because
                        all other player have disconnected.
                        If no one reconnects within 30 seconds, the game will end.""",
                (renderer0, ctx, ignored) -> ctx.invalid("Waiting for other players..."));
    }

    private static Prompt promptEndGame(TuiRenderer renderer,
                                        ClientNetManager netManager,
                                        GameView game) {
        renderer.setScene(new TuiGameScene(game));

        var scoreSortedPlayers = game.getSortedPlayers();
        var scoreboard = IntStream.range(0, scoreSortedPlayers.size())
                .mapToObj(i -> {
                    var p = scoreSortedPlayers.get(i);
                    return (i + 1) + ". " + (!p.connected().get() ? ConsoleColors.RED_BOLD_BRIGHT : "") + p.getNick() + ": "
                            + p.score().get() + "pt" + ConsoleColors.RESET;
                })
                .collect(Collectors.joining("\n"));
        return new ChoicePrompt(
                "Game is over, the final score is:\n"
                        + scoreboard + "\n\n"
                        + "What do you want to do now?",
                new ChoicePrompt.Choice("Connect to a new game", (renderer0, ctx) -> {
                    try {
                        var lobbyAndController = netManager.joinGame();

                        return ctx.prompt(
                                promptLobby(renderer, netManager, lobbyAndController.lobby(),
                                        lobbyAndController.controller()));
                    } catch (NickNotValidException e) {
                        return ctx.invalid(Objects.requireNonNull(e.getMessage()));
                    } catch (Exception ex) {
                        return ctx.prompt("Disconnected from the server",
                                promptReconnect(renderer0, netManager, ex));
                    }
                }),
                new ChoicePrompt.Choice("Quit", (renderer0, ctx) -> {
                    try {
                        netManager.close();
                        System.exit(0);
                    } catch (IOException ex) {
                        LOGGER.error("Failed to disconnect from the server while closing", ex);
                        System.exit(-1);
                    }

                    throw new AssertionError("Should never be reached");
                }));
    }

    private static void registerGameObservers(TuiRenderer renderer,
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
                renderer.setPrompt(promptReconnect(renderer, netManager, null));
            }
        });
        game.suspended().registerObserver(s -> {
            if (s)
                renderer.setPrompt(promptSuspended());
            else
                renderer.setPrompt(promptGame(renderer, netManager, game, controller));
        });
        game.endGame().registerObserver(endGame -> {
            if (endGame)
                renderer.setPrompt(promptEndGame(renderer, netManager, game));
            else
                renderer.setPrompt(promptGame(renderer, netManager, game, controller));
        });
        game.messageList().registerObserver(messageList -> {
            renderer.rerender();
        });
    }

    private static Prompt promptGame(TuiRenderer renderer,
                                     ClientNetManager netManager,
                                     GameView game,
                                     GameController controller) {
        renderer.setScene(new TuiGameScene(game));

        final List<ChoicePrompt.Choice> choices = new ArrayList<>();
        choices.add(new ChoicePrompt.Choice(
                "Make move",
                (renderer0, ctx) -> {
                    if (!game.currentTurn().get().equals(game.thePlayer()))
                        return ctx.invalid("It's not your turn");
                    return ctx.prompt(promptBoard(ctx.subPrompt(), netManager, game, controller, List.of()));
                }));
        if (TuiZoomedScene.isSupported()) {
            choices.add(new ChoicePrompt.Choice(
                    "Zoom board shelfie and personal goal",
                    (renderer0, ctx) -> ctx.prompt(zoomShelfie(ctx.subPrompt(), renderer, game))));
        }
        choices.add(new ChoicePrompt.Choice(
                "Send message",
                (renderer0, ctx) -> ctx.prompt(promptMessage(ctx.subPrompt(), netManager, game, controller))));
        choices.add(new ChoicePrompt.Choice(
                "Quit",
                (renderer0, ctx) -> {
                    try {
                        netManager.close();
                        System.exit(0);
                    } catch (IOException ex) {
                        LOGGER.error("Failed to disconnect from the server while closing", ex);
                        System.exit(-1);
                    }

                    throw new AssertionError("Should never be reached");
                }));
        return new ChoicePrompt("Select an action:", choices.toArray(new ChoicePrompt.Choice[0]));
    }

    private static Prompt zoomShelfie(Prompt.Factory promptFactory,
                                      TuiRenderer renderer,
                                      GameView game) {

        renderer.setScene(new TuiZoomedShelfiePrinter(game));

        return promptFactory.input(
                "",
                (renderer0, ctx, input) -> {
                    renderer.setScene(new TuiGameScene(game));
                    return ctx.done();
                });
    }

    private static Prompt promptMessage(Prompt.Factory promptFactory,
                                        ClientNetManager netManager,
                                        GameView game,
                                        GameController controller) {

        final List<ChoicePrompt.Choice> choices = new ArrayList<>();
        for (int i = 0; i < game.getPlayers().size(); i++)
            if (!game.thePlayer().equals(game.getPlayers().get(i))) {
                var receivingPlayer = game.getPlayers().get(i);
                choices.add(new ChoicePrompt.Choice(receivingPlayer.getNick(),
                        (renderer0, ctx) -> ctx
                                .prompt(promptWriteMessage(ctx.subPrompt(), netManager, controller,
                                        receivingPlayer.getNick()))));
            }
        choices.add(new ChoicePrompt.Choice("Send to everyone",
                (renderer0, ctx) -> ctx
                        .prompt(promptWriteMessage(ctx.subPrompt(), netManager, controller, UserMessage.EVERYONE_RECIPIENT))));
        return promptFactory.choice("Select player to send message:", choices.toArray(new ChoicePrompt.Choice[0]));
    }

    private static Prompt promptWriteMessage(Prompt.Factory promptFactory,
                                             ClientNetManager netManager,
                                             GameController controller,
                                             String nickReceivingPlayer) {

        return promptFactory.input("Write text message (ex. Hello): ",
                (renderer0, ctx, input) -> {
                    if (input.equals(""))
                        return ctx.invalid("You have to write something");
                    else {
                        try {
                            controller.sendMessage(input, nickReceivingPlayer);
                        } catch (DisconnectedException e) {
                            return ctx.prompt("Disconnected from the server",
                                    promptReconnect(renderer0, netManager, e));
                        }
                        return ctx.done();
                    }
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
                        "Shelfie column: " + (shelfCol + 1) + "\n" +
                        "Type 'Done' when satisfied",
                (renderer0, ctx, input) -> {
                    if (input.equalsIgnoreCase("done")) {
                        try {
                            controller.makeMove(coords, shelfCol);
                        } catch (DisconnectedException e) {
                            return ctx.prompt("Disconnected from the server",
                                    promptReconnect(renderer0, netManager, e));
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
