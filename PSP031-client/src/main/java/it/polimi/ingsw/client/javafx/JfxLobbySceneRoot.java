package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.model.LobbyPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Root node for the lobby scene
 * <p>
 * Allows the player to set the ready state
 */
class JfxLobbySceneRoot extends AnchorPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(JfxLobbySceneRoot.class);

    /**
     * Returns the appropriate scene root for the given lobby.
     * <p>
     * Depending on the state of the given lobby, this may return a {@link JfxLobbySceneRoot} or a {@link JfxGameSceneRoot}
     */
    public static Parent getSceneRootFor(FxResourcesLoader resources,
                                         ExecutorService threadPool,
                                         Stage stage,
                                         LobbyAndController<?> lobbyAndController,
                                         ClientNetManager netManager) {
        var currGame = lobbyAndController.lobby().game();

        Consumer<? super GameAndController<?>> observer;
        currGame.registerWeakObserver(observer = gameAndController -> {
            if (gameAndController != null) {
                Platform.runLater(() -> stage.getScene().setRoot(new JfxGameSceneRoot(resources, threadPool, stage,
                        gameAndController.game(), gameAndController.controller(),
                        netManager)));
            }
        });

        var game = currGame.get();
        return game == null
                ? new JfxLobbySceneRoot(resources, threadPool, stage, lobbyAndController, observer, netManager)
                : new JfxGameSceneRoot(resources, threadPool, stage, game.game(), game.controller(), netManager);
    }

    private final DisconnectedDialog disconnectedMessage;

    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) // Registered weakly to the game observable, we need to keep a strong ref
    private final Consumer<? super GameAndController<?>> gameObserver;
    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) // Registered weakly to the observable, we need to keep a strong ref
    private final Consumer<? super Boolean> disconnectObserver;

    private JfxLobbySceneRoot(FxResourcesLoader resources,
                              ExecutorService threadPool,
                              Stage stage,
                              LobbyAndController<?> lobbyAndController,
                              Consumer<? super GameAndController<?>> gameObserver,
                              ClientNetManager netManager) {
        var dialogs = Dialogs.setupDialogSupport(this);
        this.disconnectedMessage = new DisconnectedDialog(resources, threadPool, stage, netManager);
        dialogs.register(disconnectedMessage, true);
        disconnectedMessage.setManaged(false); // Do not make the layout manager manage this
        getChildren().add(disconnectedMessage);

        // Create a disconnection handler that shows the appropriate dialog and logs the exception
        final Consumer<Throwable> onDisconnect = cause -> threadPool.execute(() -> {
            try {
                netManager.close();
            } catch (Throwable t) {
                cause.addSuppressed(new IOException("Failed to close ClientNetManager", t));
            }

            LOGGER.error("Connection with the server was lost", cause);
            Platform.runLater(() -> dialogs.setVisible(disconnectedMessage));
        });

        this.gameObserver = gameObserver;
        lobbyAndController.lobby().thePlayerConnected()
                .registerWeakObserver(disconnectObserver = newValue -> Platform.runLater(() -> {
                    if (!newValue)
                        onDisconnect.accept(new Exception("thePlayer connected state is false"));
                }));

        //TODO:
        //X-change scene only when everyone is ready
        //-back to lobby button
        //X-threads
        //change ready button to not ready when player is ready and make player not ready when pressed
        //-if player is first to connect -> set number of players
        //      -change number of players when already in the lobby
        //when player disconnects from game and later reconnects he shouldn't go trough lobby

        AtomicBoolean isReady = new AtomicBoolean(false);
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
            threadPool.execute(() -> {
                try {
                    if (!isReady.get()) {
                        lobbyAndController.controller().ready(true);
                        isReady.set(true);
                    } else if (isReady.get()) {
                        lobbyAndController.controller().ready(false);
                        isReady.set(false);
                    }

                } catch (DisconnectedException ex) {
                    onDisconnect.accept(ex);
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

        AnchorPane.setTopAnchor(vbox, 1d);
        AnchorPane.setBottomAnchor(vbox, 10d);
        AnchorPane.setLeftAnchor(vbox, 10d);
        AnchorPane.setRightAnchor(vbox, 10d);
        getChildren().add(vbox);
        //prefWidthProperty().bind(scene.widthProperty());
        //prefHeightProperty().bind(scene.heightProperty());

        setStyle(getStyle() + "-fx-font-family: \"Inter\";");
        setBackground(new Background(new BackgroundImage(
                resources.loadImage("assets/misc/sfondo parquet.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        // Layout unmanaged children
        final double scale = Math.min(getWidth() / 1040d, getHeight() / 585d);
        this.disconnectedMessage.resizeRelocate((getWidth() - 230 * scale) / 2, (getHeight() - 230 * scale) / 2,
                230 * scale, 230.0 * scale);
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
