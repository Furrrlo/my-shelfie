package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.LobbyPlayer;
import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class JfxLobbyScene extends Scene {

    public JfxLobbyScene(Stage stage, LobbyAndController lobbyAndController, ClientNetManager netManager) {

        super(createRootNode(stage, lobbyAndController, netManager));

    }

    private static final ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private final AtomicInteger n = new AtomicInteger();

        @Override
        public Thread newThread(@NotNull Runnable r) {
            var th = new Thread(r);
            th.setName("jfx-lobby-ready-" + n.getAndIncrement());
            th.setDaemon(false);
            return th;
        }
    });

    private static Parent createRootNode(Stage stage, LobbyAndController lobbyAndController, ClientNetManager netManager) {

        //var mainPane = new CenteringFitPane();
        //mainPane.getChildren().add(new MainMenuPane());
        //Pattern ipPattern = Pattern
        //                .compile(" (\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3} ");

        //TODO:
        //-change scene only when everyone is ready
        //-back to lobby button
        //X-threads
        //change ready button to not ready when player is ready and make player not ready when pressed
        //-if player is first to connect -> set number of players
        //      -change number of players when already in the lobby
        //when player disconnects from game and later reconnects he shouldn't go trough lobby

        // Create labels
        Label connectionTypeLabel = new Label("Lobby");

        // Create grid pane for layout
        GridPane mainPane = new GridPane();
        mainPane.setHgap(10);
        mainPane.setVgap(10);
        mainPane.setPadding(new Insets(10));

        // Add components to grid pane
        mainPane.add(connectionTypeLabel, 0, 0);
        mainPane.setAlignment(Pos.CENTER);

        EventHandler<ActionEvent> eventIpCHeck = e -> {
            threadPool.submit(() -> {
                try {

                    lobbyAndController.controller().ready(true);

                    GameAndController<?> gameAndController;
                    if ((gameAndController = lobbyAndController.lobby().game().get()) == null) {
                        final CompletableFuture<GameAndController<?>> gameAndControllerFuture = new CompletableFuture<>();
                        lobbyAndController.lobby().game().registerObserver(gameAndControllerFuture::complete);
                        gameAndController = gameAndControllerFuture.get();
                    }

                    /*
                     * if(() -> lobbyAndController.lobby().joinedPlayers().get().size() >= 2
                     * && lobbyAndController.lobby().joinedPlayers().get().get(0).getNick().equals(netManager.getNick())
                     * && Objects.requireNonNull(lobbyAndController.lobby().requiredPlayers().get()) != 0
                     * && lobbyAndController.lobby().joinedPlayers().get().stream().allMatch(p -> p.ready().get())){ }
                     */
                    GameAndController<?> finalGameAndController = gameAndController;
                    Platform.runLater(() -> {
                        Scene scene = new JfxGameScene(finalGameAndController.game(), finalGameAndController.controller(),
                                netManager);

                        stage.setTitle("My Shelfie");

                        // Let jfx pick the best fit
                        stage.getIcons()
                                .add(new Image(FxResources.getResourceAsStream("assets/Publisher material/Icon 50x50px.png")));
                        stage.getIcons()
                                .add(new Image(FxResources.getResourceAsStream("assets/Publisher material/Box 280x280px.png")));

                        stage.setScene(scene);
                        stage.setMinWidth(800);
                        stage.setWidth(1080);
                        stage.setMinHeight(500);
                        stage.setHeight(720);
                        stage.show();

                    });

                } catch (Exception ex) {

                }

            });

        };

        final LobbyPlayersVbox lobbyPlayersVbox = new LobbyPlayersVbox();
        lobbyPlayersVbox.lobbyPlayersProperty().bind(FxProperties
                .toFxProperty("messages", lobbyPlayersVbox, lobbyAndController.lobby().joinedPlayers()));
        lobbyPlayersVbox.setAlignment(Pos.CENTER);

        // Create start button
        Button readyButton = new Button("Ready");
        readyButton.setOnAction(eventIpCHeck);

        //vbox
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(10);
        vbox.getChildren().addAll(mainPane, readyButton, lobbyPlayersVbox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10d);

        AnchorPane anchorPane = new AnchorPane();

        AnchorPane.setTopAnchor(vbox, 1d);
        AnchorPane.setBottomAnchor(vbox, 10d);
        AnchorPane.setLeftAnchor(vbox, 10d);
        AnchorPane.setRightAnchor(vbox, 10d);
        anchorPane.getChildren().add(vbox);
        //anchorPane.prefWidthProperty().bind(scene.widthProperty());
        //anchorPane.prefHeightProperty().bind(scene.heightProperty());

        anchorPane.setBackground(new Background(new BackgroundImage(
                new Image(FxResources.getResourceAsStream("assets/misc/sfondo parquet.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));

        return anchorPane;

    }

    private static class LobbyPlayersVbox extends VBox {

        private final ObjectProperty<List<? extends LobbyPlayer>> lobbyPlayers = new SimpleObjectProperty<>(this,
                "lobbyPlayers");

        public LobbyPlayersVbox() {
            lobbyPlayers.addListener((obs, old, newList) -> {
                var newComponents = new ArrayList<Node>();
                for (LobbyPlayer p : newList) {
                    Label readyLabel = new Label();
                    if (p.ready().get())
                        readyLabel.setText("ready");
                    else
                        readyLabel.setText("not ready");
                    var ready = new SimpleObjectProperty<>(p.ready(), "ready");
                    ready.bind(FxProperties.toFxProperty("ready", this, p.ready()));
                    ready.addListener(((observable, oldValue, newValue) -> {
                        if (p.ready().get())
                            readyLabel.setText("ready");
                        else
                            readyLabel.setText("not ready");
                    }));
                    Label playerLabel = new Label();
                    playerLabel.setText(p.getNick());
                    HBox hBox = new HBox();
                    hBox.getChildren().add(playerLabel);
                    hBox.getChildren().add(readyLabel);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setSpacing(5);
                    newComponents.add(hBox);
                }
                getChildren().setAll(newComponents);
            });
        }

        public List<? extends LobbyPlayer> getLobbyPlayers() {
            return lobbyPlayers.get();
        }

        public ObjectProperty<List<? extends LobbyPlayer>> lobbyPlayersProperty() {
            return lobbyPlayers;
        }

        public void setLobbyPlayers(List<? extends LobbyPlayer> lobbyPlayers) {
            this.lobbyPlayers.set(lobbyPlayers);
        }

    }
}