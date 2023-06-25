package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.model.LobbyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Root node for the lobby scene
 * <p>
 * Allows the player to set the ready state
 */
class JfxLobbySceneRoot extends Pane {

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
    private final ImageView gameLogo;
    private final Node lobbyPane;
    private final Label currentMessage;
    private final BorderPane randomImagePane;
    private final InGameButton readyButton;

    @SuppressWarnings("FieldCanBeLocal")
    private final BooleanProperty thePlayerReady = new SimpleBooleanProperty(this, "thePlayerReady");
    private final BooleanProperty thePlayerLobbyCreator = new SimpleBooleanProperty(this, "thePlayerLobbyCreator");
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
        setStyle(getStyle() + "-fx-font-family: \"Inter\";");
        setBackground(new Background(new BackgroundImage(
                resources.loadImage("assets/misc/sfondo parquet.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));

        var dialogs = Dialogs.setupDialogSupport(this);
        this.disconnectedMessage = new DisconnectedDialog(resources, threadPool, stage, netManager);
        dialogs.register(disconnectedMessage, true);
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

        thePlayerLobbyCreator.bind(FxProperties
                .toFxProperty("lobbyCreator", this, lobbyAndController.lobby().joinedPlayers())
                .map(i -> lobbyAndController.lobby().isLobbyCreator(netManager.getNick())));
        thePlayerReady.bind(FxProperties
                .toFxProperty("joinedPlayersForThePlayerReady", this, lobbyAndController.lobby().joinedPlayers())
                .flatMap(players -> players.stream()
                        .filter(p -> p.getNick().equals(netManager.getNick()))
                        .findFirst()
                        .map(p -> FxProperties.toFxProperty("foundThePlayerReady", this, p.ready()))
                        .orElse(null))
                .orElse(false));
        var needsToSetRequiredPlayers = thePlayerLobbyCreator.and(BooleanExpression.booleanExpression(FxProperties
                .toFxProperty("requiredPlayersNum", this, lobbyAndController.lobby().requiredPlayers())
                .orElse(-1)
                .map(num -> num == null || num == -1)));

        gameLogo = new ImageView(resources.loadCroppedImage(
                "assets/Publisher material/Title 2000x618px.png",
                167, 77, 1667, 444));
        gameLogo.setPreserveRatio(true);
        getChildren().add(gameLogo);

        lobbyPane = new LobbyVbox(threadPool, netManager, lobbyAndController.lobby(),
                lobbyAndController.controller(), onDisconnect);
        getChildren().add(lobbyPane);

        currentMessage = new Label();
        currentMessage.setAlignment(Pos.CENTER);
        currentMessage.textProperty().bind(needsToSetRequiredPlayers.map(bool -> bool
                ? "Set the required number of players"
                : "Waiting for players..."));
        getChildren().add(currentMessage);

        var randomImages = List.of(
                resources.loadImage("assets/Publisher material/Display_2.jpg"),
                resources.loadImage("assets/Publisher material/Display_3.jpg"),
                resources.loadImage("assets/Publisher material/Display_4.jpg"),
                resources.loadImage("assets/Publisher material/Display_1.jpg"),
                resources.loadImage("assets/Publisher material/Display_5.jpg"));
        randomImagePane = new BorderPane();

        var randomImage = new ImageView(randomImages.get(0));
        randomImage.setPreserveRatio(true);
        randomImage.fitWidthProperty().bind(randomImagePane.widthProperty());
        randomImage.fitHeightProperty().bind(randomImagePane.heightProperty());

        var carouselFadeOut = new FadeTransition(Duration.millis(500), randomImage);
        carouselFadeOut.setFromValue(1);
        carouselFadeOut.setToValue(0);
        carouselFadeOut.setInterpolator(Interpolator.EASE_OUT);
        carouselFadeOut.setCycleCount(1);
        var carouselFadeIn = new FadeTransition(Duration.millis(500), randomImage);
        carouselFadeIn.setFromValue(0);
        carouselFadeIn.setToValue(1);
        carouselFadeIn.setInterpolator(Interpolator.EASE_IN);
        carouselFadeIn.setCycleCount(1);

        final var currentImageIdx = new AtomicInteger(0);
        Timeline carouselChangeImgTimeLine = new Timeline(new KeyFrame(Duration.seconds(10), evt -> {
            // Keep playing until the scene changes
            if (getScene() != null)
                carouselFadeOut.playFromStart();
        }));
        carouselChangeImgTimeLine.setCycleCount(1);
        carouselFadeOut.setOnFinished(evt -> {
            randomImage.setImage(randomImages.get(currentImageIdx.updateAndGet(v -> v + 1 >= randomImages.size() ? 0 : v + 1)));
            carouselFadeIn.playFromStart();
        });
        carouselFadeIn.setOnFinished(evt -> carouselChangeImgTimeLine.playFromStart());
        carouselChangeImgTimeLine.playFromStart();

        randomImagePane.setCenter(randomImage);
        getChildren().add(randomImagePane);

        readyButton = new InGameButton();
        readyButton.backgroundInsetsProperty().set(new Insets(0));
        readyButton.backgroundRadiusProperty().bind(readyButton.widthProperty()
                .map(w -> new CornerRadii(Math.min(20, 20 * (w.doubleValue() / 210d)))));
        readyButton.backgroundColorProperty().bind(thePlayerReady.map(ready -> ready ? Color.INDIANRED : Color.LIGHTSEAGREEN));
        readyButton.textProperty().bind(thePlayerReady.map(ready -> ready ? "Not ready" : "Ready!"));
        Fonts.changeWeight(readyButton.fontProperty(), FontWeight.EXTRA_BOLD);
        readyButton.visibleProperty().bind(needsToSetRequiredPlayers.not());

        AtomicBoolean isReady = new AtomicBoolean(false);
        EventHandler<ActionEvent> eventIpCHeck = e -> threadPool.execute(() -> {
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
        readyButton.setOnAction(eventIpCHeck);
        getChildren().add(readyButton);

        // Check if we already disconnected
        disconnectObserver.accept(lobbyAndController.lobby().thePlayerConnected().get());
    }

    @Override
    protected void layoutChildren() {
        final double scale = Math.min(getWidth() / 1040d, getHeight() / 585d);
        final double border = 20 * scale;

        this.disconnectedMessage.resizeRelocate((getWidth() - 230 * scale) / 2, (getHeight() - 230 * scale) / 2,
                230 * scale, 230.0 * scale);

        this.gameLogo.resizeRelocate(border, border, 350 * scale, 93.22 * scale);
        this.gameLogo.setFitWidth(350 * scale);
        this.gameLogo.setFitHeight(93.22 * scale);

        this.lobbyPane.resizeRelocate(
                border,
                2 * border + 93.22 * scale,
                350 * scale,
                getHeight() - border * 3 - 93.22 * scale);

        this.currentMessage.resizeRelocate(
                2 * border + 350 * scale,
                border,
                getWidth() - 3 * border - 350 * scale,
                20 * scale);
        Fonts.changeSize(this.currentMessage.fontProperty(), 16 * scale);

        this.randomImagePane.resizeRelocate(
                2 * border + 350 * scale,
                2 * border + 20 * scale,
                getWidth() - 3 * border - 350 * scale,
                getHeight() - 3 * border - 20 * scale - 40 * scale);

        this.readyButton.resizeRelocate(
                getWidth() - border - 250 * scale,
                getHeight() - border - 80 * scale,
                250 * scale,
                80 * scale);
        Fonts.changeSize(this.readyButton.fontProperty(), 30 * scale);
    }

    private class LobbyVbox extends VBox {

        public LobbyVbox(ExecutorService threadPool,
                         ClientNetManager netManager,
                         LobbyView lobby,
                         LobbyController controller,
                         Consumer<Throwable> onDisconnect) {

            setSpacing(15);
            setPadding(new Insets(10));
            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTGRAY,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    new Insets(0)))));

            Label title = new Label("Lobby");
            Fonts.changeWeight(title.fontProperty(), FontWeight.EXTRA_BOLD);
            title.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTSEAGREEN,
                    new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                    new Insets(-5)))));
            title.setAlignment(Pos.CENTER);
            title.prefWidthProperty().bind(this.widthProperty());
            this.getChildren().add(title);

            Label server = new Label("Server: " + netManager.getHost() + ":" + netManager.getPort());
            getChildren().add(server);

            VBox requiredPlayersContainer = new VBox();
            requiredPlayersContainer.visibleProperty().bind(thePlayerLobbyCreator);
            requiredPlayersContainer.setPadding(new Insets(0));
            requiredPlayersContainer.setSpacing(1);

            Label playerNumberLabel = new Label("Required players");
            InGameChoiceBox<String> playerNumberChoice = new InGameChoiceBox<>(Color.LIGHTSEAGREEN);
            playerNumberChoice.backgroundRadiusProperty().bind(widthProperty()
                    .map(w -> new CornerRadii(Math.min(5, 5 * (w.doubleValue() / 210d)))));
            playerNumberChoice.setBackgroundInsets(new Insets(0));
            playerNumberChoice.prefWidthProperty().bind(widthProperty());
            playerNumberChoice.getItems().addAll("", "None", "2", "3", "4");
            playerNumberChoice.setValue("");
            playerNumberChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(""))
                    return;

                // Remove the empty value as it's only used at the start
                playerNumberChoice.getItems().remove("");

                var requiredPlayers = switch (newValue) {
                    case "None" -> 0;
                    case "2" -> 2;
                    case "3" -> 3;
                    case "4" -> 4;
                    default -> throw new IllegalStateException("Unexpected value: " + newValue);
                };

                threadPool.execute(() -> {
                    try {
                        controller.setRequiredPlayers(requiredPlayers);
                    } catch (DisconnectedException e) {
                        onDisconnect.accept(e);
                    }
                });
            });

            // Only add children when it's visible, so that it doesn't take up space
            requiredPlayersContainer.visibleProperty().addListener((obs, oldV, newV) -> {
                if (newV) {
                    // Set the value to the current one when it becomes visible
                    var requiredPlayers = lobby.requiredPlayers().get();
                    if (requiredPlayers == null) {
                        if (!playerNumberChoice.getItems().contains(""))
                            playerNumberChoice.getItems().add(0, "");
                        playerNumberChoice.setValue("");
                    } else {
                        playerNumberChoice.getItems().remove("");
                        playerNumberChoice.setValue(switch (requiredPlayers) {
                            case 0 -> "None";
                            case 2, 3, 4 -> String.valueOf(requiredPlayers);
                            default -> throw new IllegalStateException("Unexpected value: " + requiredPlayers);
                        });
                    }

                    requiredPlayersContainer.getChildren().setAll(playerNumberLabel, playerNumberChoice);
                } else {
                    requiredPlayersContainer.getChildren().clear();
                }
            });
            if (requiredPlayersContainer.isVisible())
                requiredPlayersContainer.getChildren().setAll(playerNumberLabel, playerNumberChoice);
            getChildren().add(requiredPlayersContainer);

            VBox playersVbox = new VBox();
            playersVbox.setPadding(new Insets(0));
            playersVbox.setSpacing(1);
            Label numPlayers = new Label();
            numPlayers.textProperty().bind(FxProperties.compositeObservableValue(
                    FxProperties.toFxProperty("requiredPlayersNum", this, lobby.joinedPlayers()),
                    FxProperties.toFxProperty("requiredPlayersNum", this, lobby.requiredPlayers()))
                    .map(i -> {
                        var requiredPlayers = lobby.requiredPlayers().get();
                        var joinedPlayers = lobby.joinedPlayers().get().size();
                        return requiredPlayers == null || requiredPlayers == 0
                                ? "Players: " + joinedPlayers + "/" + Math.max(LobbyView.MIN_PLAYERS, joinedPlayers)
                                : "Players: " + joinedPlayers + "/" + Math.max(LobbyView.MIN_PLAYERS, requiredPlayers);
                    }));
            playersVbox.getChildren().addAll(numPlayers, new LobbyPlayersVbox(lobby));
            this.getChildren().add(playersVbox);
        }
    }

    private static class LobbyPlayersVbox extends VBox {

        @SuppressWarnings("FieldCanBeLocal") // Need to keep a strong ref
        private final ObjectProperty<List<? extends LobbyPlayer>> lobbyPlayers = new SimpleObjectProperty<>(this,
                "lobbyPlayers");

        public LobbyPlayersVbox(LobbyView lobby) {
            setPadding(new Insets(0));
            setSpacing(5);
            lobbyPlayers.addListener((obs, old, newList) -> {
                var newComponents = new ArrayList<Node>();
                for (LobbyPlayer p : newList)
                    newComponents.add(new LobbyPlayerComponent(p));
                getChildren().setAll(newComponents);
            });
            lobbyPlayers.bind(FxProperties.toFxProperty("joinedPlayers", this, lobby.joinedPlayers()));
        }
    }

    private static class LobbyPlayerComponent extends HBox {

        @SuppressWarnings("FieldCanBeLocal") // Need to keep a strong ref
        private final ObjectProperty<Boolean> ready = new SimpleObjectProperty<>(this, "ready");

        public LobbyPlayerComponent(LobbyPlayer p) {
            setSpacing(5);
            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.LIGHTSEAGREEN,
                    new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                    new Insets(0)))));
            setPadding(new Insets(5));

            var vbox = new VBox();
            vbox.setSpacing(1);

            Label playerLabel = new Label();
            playerLabel.setText(p.getNick());

            Label readyLabel = new Label();
            ready.bind(FxProperties.toFxProperty("ready", this, p.ready()));
            readyLabel.textProperty().bind(ready.map(ready -> ready ? "Ready" : "Not ready"));
            readyLabel.textFillProperty().bind(ready.map(ready -> ready ? Color.GREEN : Color.RED));

            vbox.getChildren().addAll(playerLabel, readyLabel);
            HBox.setHgrow(vbox, Priority.SOMETIMES);
            getChildren().add(vbox);

            var spinner = new ProgressIndicator();
            spinner.setPrefHeight(1); // Set v small, so it should take the size from the other components
            spinner.visibleProperty().bind(ready.map(ready -> !ready));
            getChildren().add(spinner);
        }
    }
}
