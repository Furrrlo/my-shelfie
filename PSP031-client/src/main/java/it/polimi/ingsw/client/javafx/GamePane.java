package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.BoardCoord;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.PlayerView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class GamePane extends Pane {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePane.class);

    /**
     * set to appear when the required number of players for a game is achieved, and they are all ready.
     * It displays all the game's components needed for the player to play, including the other players'
     * shelfies and the chat
     */
    private final PlayerShelfieComponent thePlayerShelfie;
    private final Pane thePlayerPoints;
    private final CommonGoalsPane commonGoalCardsPane;
    private final PersonalGoalComponent personalGoalCard;
    private final PersonalGoalDescription personalGoalDescription;
    private final BoardComponent board;
    private final ImageButton adjacentItemTiles;

    private final FirstFinisherDescription firstFinisherDescription;
    private final AdjacentItemTilesDescription adjacentItemTilesDescription;
    private final Pane boardPane;
    private final PickedTilesPane pickedTilesPane;
    private final Pane player1Shelfie;
    private final Pane player2Shelfie;
    private final Pane player3Shelfie;
    private final QuitGameButton quitGameBtn;
    private final ChatButton newChatBtn;
    private final Label newMsg;
    private final ChatComponent chatPane;

    private final NotCurrentTurnDialog notCurrentTurnMessage;
    private final SuspendedGameDialog suspendedGameMessage;
    private final DisconnectedDialog disconnectedMessage;
    private final QuitGameDialog quitGameMessage;
    private @Nullable EndGamePane endGamePane;
    private final ScoringTokenComponent finishToken;

    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) // It's registered weakly to the provider, so we need to keep a strong ref
    private final Consumer<Boolean> suspendedObserver;
    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) // It's registered weakly to the provider, so we need to keep a strong ref
    private final Consumer<Boolean> disconnectedObserver;
    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) // It's registered weakly to the provider, so we need to keep a strong ref
    private final Consumer<Boolean> endGameObserver;
    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) // It's registered weakly to the provider, so we need to keep a strong ref
    private final Consumer<PlayerView> firstFinisherObserver;
    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) // It's registered weakly to the provider, so we need to keep a strong ref
    private final Consumer<List<? extends PlayerView>> achieved1Observer;
    @SuppressWarnings({ "FieldCanBeLocal", "unused" }) // It's registered weakly to the provider, so we need to keep a strong ref
    private final Consumer<List<? extends PlayerView>> achieved2Observer;

    public GamePane(FxResourcesLoader resources,
                    ExecutorService threadPool,
                    Stage stage,
                    GameView game,
                    GameController controller,
                    ClientNetManager netManager) {

        var dialogs = Dialogs.setupDialogSupport(this);
        //set new alerts for suspended game and disconnected player ( making them initially not visible,
        // will be later made visible when corresponding state is caught )
        this.notCurrentTurnMessage = new NotCurrentTurnDialog();
        dialogs.register(notCurrentTurnMessage, false);
        this.suspendedGameMessage = new SuspendedGameDialog();
        dialogs.register(suspendedGameMessage, true);
        this.disconnectedMessage = new DisconnectedDialog(resources, threadPool, stage, netManager);
        dialogs.register(disconnectedMessage, true);
        //set alert for quitGame message
        this.quitGameMessage = new QuitGameDialog();
        dialogs.register(quitGameMessage, true);

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

        //adding first finisher token on game pane otherwise it would have been disabled when is not
        //the player's turn
        this.finishToken = new ScoringTokenComponent(resources);
        finishToken.setRotate(9);
        finishToken.setScore(
                game.firstFinisher().get() != null && Objects.equals(game.firstFinisher().get(), game.thePlayer()) ? 1 : 0);

        //adding game components
        getChildren().add(this.thePlayerShelfie = new PlayerShelfieComponent(resources, game.thePlayer(),
                game.getPersonalGoal(), game.getCommonGoals().get(0), game.getCommonGoals().get(1)));
        getChildren().add(this.thePlayerPoints = new PlayerPointsComponent(game.thePlayer().score()));
        getChildren().add(
                this.commonGoalCardsPane = new CommonGoalsPane(resources,
                        game.getCommonGoals().get(0), game.getCommonGoals().get(1),
                        (!game.getCommonGoals().get(0).achieved().get().contains(game.thePlayer())) ? 0
                                : switch (game.getCommonGoals().get(0).achieved().get().indexOf(game.thePlayer())) {
                                    case 0 -> 8;
                                    case 1 -> 6;
                                    case 2 -> 4;
                                    case 3 -> 2;
                                    default -> throw new IllegalStateException("Unexpected value: ");
                                },
                        (!game.getCommonGoals().get(1).achieved().get().contains(game.thePlayer())) ? 0
                                : switch (game.getCommonGoals().get(1).achieved().get().indexOf(game.thePlayer())) {
                                    case 0 -> 8;
                                    case 1 -> 6;
                                    case 2 -> 4;
                                    case 3 -> 2;
                                    default -> throw new IllegalStateException("Unexpected value: ");
                                }));

        getChildren().add(this.personalGoalCard = new PersonalGoalComponent(resources, game.getPersonalGoal()));

        //creating description for personal goal
        this.getChildren().add(this.personalGoalDescription = new PersonalGoalDescription());
        personalGoalDescription.setVisible(false);

        //displaying common goals on shelfie
        this.commonGoalCardsPane.getChildren().get(commonGoalCardsPane.getCommonGoal1NodeIndex()).setOnMousePressed(event -> {
            thePlayerShelfie.setCommonGoal2Visible(false);
            thePlayerShelfie.setPersonalGoalVisible(false);
            personalGoalDescription.setVisible(false);

            if (!thePlayerShelfie.getCommonGoal1Visible()) {
                //bring down opacity of shelfie tiles
                thePlayerShelfie.setTilesLowOpacity();
            } else
                thePlayerShelfie.restoreTilesOpacity();

            thePlayerShelfie.setCommonGoal1Visible(!thePlayerShelfie.getCommonGoal1Visible());
            commonGoalCardsPane.setDescriptionVisible(!commonGoalCardsPane.getDescriptionVisible(), 1);
        });
        this.commonGoalCardsPane.getChildren().get(commonGoalCardsPane.getCommonGoal2NodeIndex()).setOnMousePressed(event -> {
            thePlayerShelfie.setCommonGoal1Visible(false);
            thePlayerShelfie.setPersonalGoalVisible(false);
            personalGoalDescription.setVisible(false);

            if (!thePlayerShelfie.getCommonGoal2Visible()) {
                //bring down opacity of shelfie tiles
                thePlayerShelfie.setTilesLowOpacity();
            } else
                thePlayerShelfie.restoreTilesOpacity();

            commonGoalCardsPane.setDescriptionVisible(!commonGoalCardsPane.getDescriptionVisible(), 2);
            thePlayerShelfie.setCommonGoal2Visible(!thePlayerShelfie.getCommonGoal2Visible());
        });
        //turning description of common goals and commonGoals pattern off when pressed on it
        this.commonGoalCardsPane.getChildren().get(commonGoalCardsPane.getDescriptionNodeIndex()).setOnMousePressed(event -> {
            if (thePlayerShelfie.getCommonGoal1Visible())
                thePlayerShelfie.setCommonGoal1Visible(false);
            if (thePlayerShelfie.getCommonGoal2Visible())
                thePlayerShelfie.setCommonGoal2Visible(false);
            commonGoalCardsPane.setDescriptionVisible(false, 0);
            thePlayerShelfie.restoreTilesOpacity();
        });

        //displaying personal goals on shelfie
        this.personalGoalCard.setOnMouseClicked(
                event -> {
                    if (!thePlayerShelfie.getPersonalGoalVisible()) {
                        //bring down opacity of shelfie tiles
                        thePlayerShelfie.setTilesLowOpacity();
                    } else
                        thePlayerShelfie.restoreTilesOpacity();

                    if (commonGoalCardsPane.getDescriptionVisible())
                        commonGoalCardsPane.setDescriptionVisible(false, 0);

                    //set not visible the commonGoals pattern if visible
                    thePlayerShelfie.setCommonGoal1Visible(false);
                    thePlayerShelfie.setCommonGoal2Visible(false);
                    //change personalGoal visibility
                    thePlayerShelfie.setPersonalGoalVisible(!thePlayerShelfie.getPersonalGoalVisible());
                    personalGoalDescription.setVisible(!personalGoalDescription.isVisible());
                });
        this.personalGoalDescription.setOnMouseClicked(
                event -> {
                    thePlayerShelfie.restoreTilesOpacity();

                    if (commonGoalCardsPane.getDescriptionVisible())
                        commonGoalCardsPane.setDescriptionVisible(false, 0);

                    //set not visible the commonGoals pattern if visible
                    thePlayerShelfie.setCommonGoal1Visible(false);
                    thePlayerShelfie.setCommonGoal2Visible(false);
                    //change personalGoal visibility
                    thePlayerShelfie.setPersonalGoalVisible(!thePlayerShelfie.getPersonalGoalVisible());
                    personalGoalDescription.setVisible(!personalGoalDescription.isVisible());
                });

        this.board = new BoardComponent(resources, game.getBoard());
        this.boardPane = new Pane(board) {
            @Override
            protected void layoutChildren() {
                // Override this to avoid messing with layout
            }
        };
        getChildren().add(this.boardPane);
        getChildren().add(this.pickedTilesPane = new PickedTilesPane(resources));

        this.adjacentItemTiles = new ImageButton();
        adjacentItemTiles.setImage(resources.loadCroppedImage(
                "assets/boards/livingroom.png",
                1977, 2465, 889, 394));
        getChildren().add(adjacentItemTiles);

        //adding the end game token description to the game's pane
        getChildren().add(this.firstFinisherDescription = new FirstFinisherDescription(resources));
        this.firstFinisherDescription.setVisible(false);

        //adding Adjacent Item Tiles description to game's pane
        this.getChildren().add(this.adjacentItemTilesDescription = new AdjacentItemTilesDescription());
        this.adjacentItemTilesDescription.setVisible(false);

        //setting onClick action on finish token to set visible its description
        finishToken.setOnMouseClicked(event -> {
            if (adjacentItemTilesDescription.isVisible())
                adjacentItemTilesDescription.setVisible(false);
            this.firstFinisherDescription.setVisible(!firstFinisherDescription.isVisible());
        });

        //setting onClick action on Adjacent Item Tiles image button to set visible its description
        adjacentItemTiles.setOnMouseClicked(event -> {
            if (firstFinisherDescription.isVisible())
                firstFinisherDescription.setVisible(false);
            this.adjacentItemTilesDescription.setVisible(!adjacentItemTilesDescription.isVisible());
        });

        final BooleanProperty isMakingMove = new SimpleBooleanProperty();
        final var isCurrentTurn = BooleanExpression.booleanExpression(FxProperties.toFxProperty(
                "isCurrentTurn", this, game.thePlayer().isCurrentTurn())).and(isMakingMove.not());
        board.disableProperty().bind(isCurrentTurn.not());
        boardPane.setOnMouseClicked(event -> {
            if (isCurrentTurn.not().get())
                dialogs.setVisible(notCurrentTurnMessage);
        });
        pickedTilesPane.tilesProperty().bindBidirectional(board.pickedTilesProperty());
        pickedTilesPane.setIsTileRemovable(tileAndCoords -> {
            final var list = new ArrayList<>(pickedTilesPane.getTiles());
            list.remove(tileAndCoords);
            return list.size() == 0 || game.getBoard().checkBoardCoord(list);
        });

        //binding this.firstFinisher to game.firstFinisher
        game.firstFinisher().registerWeakObserver(firstFinisherObserver = newValue -> Platform
                .runLater(() -> finishToken.setScore(newValue != null && newValue.equals(game.thePlayer()) ? 1 : 0)));

        //now displaying points for commonGoals if achieved
        game.getCommonGoals().get(0).achieved().registerWeakObserver(achieved1Observer = newValue -> Platform.runLater(() -> {
            if (newValue.contains(game.thePlayer())) {
                int index = newValue.indexOf(game.thePlayer());
                int points;
                switch (index) {
                    case 0 -> points = 8;
                    case 1 -> points = 6;
                    case 2 -> points = 4;
                    case 3 -> points = 2;
                    default -> throw new IllegalStateException("Unexpected value: " + index);
                }
                commonGoalCardsPane.setScore1(points);
            }
        }));
        game.getCommonGoals().get(1).achieved().registerWeakObserver(achieved2Observer = newValue -> Platform.runLater(() -> {
            if (newValue.contains(game.thePlayer())) {
                int index = newValue.indexOf(game.thePlayer());
                int points;
                switch (index) {
                    case 0 -> points = 8;
                    case 1 -> points = 6;
                    case 2 -> points = 4;
                    case 3 -> points = 2;
                    default -> throw new IllegalStateException("Unexpected value: " + index);
                }
                commonGoalCardsPane.setScore2(points);
            }
        }));

        //binding suspended to GameView Provider game.suspended() and adding listener that when detects newValue
        //equals true, disables all current nodes in the game( except suspended game alert and endGamePane, in case
        //no other player reconnects therefore forcing the game to end )
        game.suspended().registerWeakObserver(suspendedObserver = newValue -> Platform.runLater(() -> {
            if (newValue) {
                dialogs.setVisible(suspendedGameMessage);
                suspendedGameMessage.play();
            } else {
                //when suspended comes back to false it restores the game properties back to normal
                dialogs.setVisible(null);
            }
        }));
        suspendedObserver.accept(game.suspended().get()); // Trigger suspension in case it's true

        // add listener to handle disconnections properly
        game.thePlayer().connected().registerWeakObserver(disconnectedObserver = newValue -> Platform.runLater(() -> {
            if (!newValue)
                onDisconnect.accept(new Exception("thePlayer connected state is false"));
        }));
        disconnectedObserver.accept(game.thePlayer().connected().get()); // Trigger disconnection in case it's true

        //binding this.endGame to game's Provider endGame and adding listener that when listens endGame being true
        //disables all nodes in the game and displays the endGamePane
        game.endGame().registerWeakObserver(endGameObserver = newValue -> Platform.runLater(() -> {
            if (newValue) {
                this.endGamePane = new EndGamePane(resources, threadPool, stage, game.getSortedPlayers(), netManager);
                dialogs.register(endGamePane, true);

                // Add a listener to remove the child and the listener itself once the endGamePane is closed
                final var visibleListenerRef = new AtomicReference<ChangeListener<? super Parent>>();
                visibleListenerRef.set((obs, oldVal, newVal) -> {
                    boolean wasEndGamePane = Objects.equals(oldVal, endGamePane) && !Objects.equals(oldVal, newVal);
                    if (!wasEndGamePane)
                        return;

                    getChildren().remove(endGamePane);
                    var visibleListener = visibleListenerRef.getAndSet(null);
                    if (visibleListener != null)
                        dialogs.visibleProperty().removeListener(visibleListener);
                });
                dialogs.visibleProperty().addListener(visibleListenerRef.get());
                getChildren().add(endGamePane);
                dialogs.setVisible(endGamePane);
            }
        }));
        endGameObserver.accept(game.endGame().get()); // Trigger end game in case it's true

        var canSelectColumns = isCurrentTurn.and(
                BooleanExpression.booleanExpression(pickedTilesPane.tilesProperty().map(t -> !t.isEmpty())));
        thePlayerShelfie.mouseTransparentProperty().bind(canSelectColumns.not());
        thePlayerShelfie.columnSelectionModeProperty().bind(canSelectColumns);
        thePlayerShelfie.isColumnSelectableProperty().bind(pickedTilesPane.tilesProperty()
                .map(pickedTiles -> c -> game.thePlayer().getShelfie().checkColumnSpace(c, pickedTiles.size())));
        thePlayerShelfie.onTileActionProperty()
                .bind(thePlayerShelfie.columnSelectionModeProperty().map(columnMode -> !columnMode ? null : (tileAndCoords -> {
                    isMakingMove.set(true);

                    var choosenCoords = pickedTilesPane.getTiles().stream()
                            .map(t -> new BoardCoord(t.row(), t.col()))
                            .toList();
                    pickedTilesPane.tilesProperty().clear();
                    threadPool.execute(() -> {
                        try {
                            controller.makeMove(choosenCoords, tileAndCoords.col());
                        } catch (DisconnectedException e) {
                            onDisconnect.accept(e);
                        } finally {
                            Platform.runLater(() -> isMakingMove.set(false));
                        }
                    });
                })));

        final var otherPlayers = new ArrayList<>(game.getPlayers());
        otherPlayers.remove(game.thePlayer());
        getChildren().add(this.player1Shelfie = otherPlayers.size() >= 1
                ? new PlayerShelfieComponent(resources, otherPlayers.get(0), game.getPersonalGoal(),
                        game.getCommonGoals().get(0), game.getCommonGoals().get(0), true, true)
                : new Pane());
        getChildren().add(this.player2Shelfie = otherPlayers.size() >= 2
                ? new PlayerShelfieComponent(resources, otherPlayers.get(1), game.getPersonalGoal(),
                        game.getCommonGoals().get(0), game.getCommonGoals().get(0), true, true)
                : new Pane());
        getChildren().add(this.player3Shelfie = otherPlayers.size() >= 3
                ? new PlayerShelfieComponent(resources, otherPlayers.get(2), game.getPersonalGoal(),
                        game.getCommonGoals().get(0), game.getCommonGoals().get(0), true, true)
                : new Pane());

        //adding chat to the game initially set not visible, its visibility can be modified by pressing over the
        //chat button
        getChildren().add(this.chatPane = new ChatComponent(
                resources,
                game.getPlayers()
                        .stream().map(PlayerView::getNick)
                        .filter(nick -> !nick.equals(game.thePlayer().getNick()))
                        .toList(),
                game.thePlayer().getNick(), controller, onDisconnect));
        this.chatPane.messagesProperty().bind(FxProperties
                .toFxProperty("messages", this, game.messageList()));
        this.chatPane.setVisible(false);
        this.chatPane.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(-ChatComponent.INSET)))));

        //adding label displaying if there are any new incoming messages when the chat is closed
        this.newMsg = new Label("");
        this.newMsg.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.INDIANRED,
                new CornerRadii(Math.min(100, 100 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        this.newMsg.setVisible(false);

        //change visibility of chatPane when chatBtn is pressed
        //adding chat button and quit game button over pickedTilesPane
        this.quitGameBtn = new QuitGameButton();

        quitGameBtn.setOnMouseClicked(event -> dialogs.setVisible(quitGameMessage));
        this.getChildren().add(quitGameBtn);

        this.newChatBtn = new ChatButton(resources);
        newChatBtn.setOnMouseClicked(event -> {
            newChatBtn.swap();
            this.chatPane.setVisible(!this.chatPane.isVisible());
            if (chatPane.isVisible())
                this.newMsg.setVisible(false);
        });
        this.getChildren().add(newChatBtn);

        this.chatPane.messagesProperty().addListener((observable, oldValue, newValue) -> {
            if (chatPane.isVisible())
                this.newMsg.setVisible(false);
            if (oldValue.size() == 0 && newValue.size() == 1 && !chatPane.isVisible())
                this.newMsg.setVisible(true);
            else if (!oldValue.get(oldValue.size() - 1).equals(newValue.get(newValue.size() - 1)) && !chatPane.isVisible()) {
                this.newMsg.setVisible(true);
            }
        });

        getChildren().add(this.notCurrentTurnMessage);
        getChildren().add(this.suspendedGameMessage);
        getChildren().add(this.quitGameMessage);
        getChildren().add(this.newMsg);
        getChildren().add(this.disconnectedMessage);
        getChildren().add(this.finishToken);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected double computePrefWidth(double height) {
        return 1040d * (height == -1 ? 1 : height / 585d);
    }

    @Override
    protected double computePrefHeight(double width) {
        return 585d * (width == -1 ? 1 : width / 1040d);
    }

    @Override
    protected void layoutChildren() {
        final double scale = Math.min(getWidth() / 1040d, getHeight() / 585d);
        this.thePlayerShelfie.resizeRelocate(0, 0, 365.0 * scale, 384.0 * scale);
        this.thePlayerPoints.resizeRelocate(0, 386.0 * scale, 221.0 * scale, 34 * scale);
        this.commonGoalCardsPane.resizeRelocate(0, 422.0 * scale, 221.0 * scale, 164.0 * scale);
        this.personalGoalCard.resizeRelocate(228.0 * scale, 386.0 * scale, 131.794 * scale, 200.0 * scale);
        this.personalGoalDescription.resizeRelocate((228.0 + 6.0) * scale, (386.0 + 6.0) * scale, (131.794 - 12.0) * scale,
                (200.0 - 12.0) * scale);
        this.board.resize(460.0 * scale, 460.0 * scale);
        this.finishToken.resizeRelocate(743.0 * scale, 322.0 * scale, 46 * scale, 46 * scale);
        this.boardPane.resizeRelocate(370.0 * scale, 0, 460.0 * scale, 460.0 * scale);
        this.adjacentItemTiles.resizeRelocate((370.0 + 305.0) * scale, 380 * scale, 147 * scale, 70 * scale);
        this.firstFinisherDescription.resizeRelocate((370.0 + 305.0) * scale, 5 * scale, 150 * scale, 150 * scale);
        this.adjacentItemTilesDescription.resizeRelocate((370.0 + 305.0) * scale, 5 * scale, 150 * scale, 150 * scale);
        this.pickedTilesPane.resizeRelocate(370.0 * scale, 471.0 * scale, 460.0 * scale, 114.0 * scale);
        this.quitGameBtn.resizeRelocate(707 * scale, (470 + 8) * scale, 115 * scale, 46 * scale);
        this.newChatBtn.resizeRelocate(707 * scale, (470 + 48 + 8 + 5) * scale, 115 * scale, 46 * scale);
        this.player1Shelfie.resizeRelocate(842.0 * scale, 0, 182.0 * scale, 194.0 * scale);
        this.player2Shelfie.resizeRelocate(842.0 * scale, 196.0 * scale, 182.0 * scale, 194.0 * scale);
        this.player3Shelfie.resizeRelocate(842.0 * scale, 392.0 * scale, 182.0 * scale, 194.0 * scale);
        this.notCurrentTurnMessage.resizeRelocate((370.0 + 115.0) * scale, 115.0 * scale, 230 * scale, 230.0 * scale);
        this.suspendedGameMessage.resizeRelocate((getWidth() - 230 * scale) / 2, (getHeight() - 230 * scale) / 2, 230 * scale,
                230.0 * scale);
        this.quitGameMessage.resizeRelocate((getWidth() - 230 * scale) / 2, (getHeight() - 230 * scale) / 2, 230 * scale,
                230.0 * scale);
        if (endGamePane != null)
            this.endGamePane.resizeRelocate((getWidth() - 690 * scale) / 2, (getHeight() - 490 * scale) / 2,
                    690 * scale, 490 * scale);
        this.disconnectedMessage.resizeRelocate((getWidth() - 230 * scale) / 2, (getHeight() - 230 * scale) / 2, 230 * scale,
                230.0 * scale);

        final var newMsgSize = 13 * scale;
        final var chatPaneWidth = 200.0 * scale;
        this.chatPane.resizeRelocate(
                getWidth() - chatPaneWidth + ChatComponent.INSET,
                +ChatComponent.INSET,
                chatPaneWidth - ChatComponent.INSET * 2, getHeight() - ChatComponent.INSET * 2);
        this.newMsg.resizeRelocate(803 * scale, 540 * scale, newMsgSize, newMsgSize);
    }
}
