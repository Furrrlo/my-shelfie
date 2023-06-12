package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.client.network.ClientNetManager;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.BoardCoord;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.PlayerView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.application.Platform;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class GamePane extends AnchorPane {
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

    private final DialogVbox notCurrentTurnMessage;
    private final DialogVbox suspendedGameMessage;
    private final DialogVbox quitGameMessage;
    private EndGamePane endGamePane;
    private final ScoringTokenComponent finishToken;

    private final ObjectProperty<Consumer<@Nullable Throwable>> onDisconnect = new SimpleObjectProperty<>();

    private final ObjectProperty<Boolean> suspended = new SimpleObjectProperty<>(this, "suspended");

    private final ObjectProperty<Boolean> endGame = new SimpleObjectProperty<>(this, "endGame");

    private final ObjectProperty<PlayerView> firstFinisher = new SimpleObjectProperty<>(this,
            "firsFinisher");
    private final ObjectProperty<List<? extends PlayerView>> achieved1 = new SimpleObjectProperty<>(this, "commonGoal1");
    private final ObjectProperty<List<? extends PlayerView>> achieved2 = new SimpleObjectProperty<>(this, "commonGoal1");

    private final ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactory() {

        private final AtomicInteger n = new AtomicInteger();

        @Override
        public Thread newThread(@NotNull Runnable r) {
            var th = new Thread(r);
            th.setName("jfx-controller-executor-" + n.getAndIncrement());
            th.setDaemon(false);
            return th;
        }
    });

    public GamePane(GameView game, GameController controller, ClientNetManager netManager) {
        //set new alerts for suspended game and disconnected player ( making them initially not visible,
        // will be later made visible when corresponding state is caught )
        this.notCurrentTurnMessage = new DialogVbox(DialogVbox.NOT_CURRENT_TURN, null);
        notCurrentTurnMessage.setVisible(false);
        this.suspendedGameMessage = new DialogVbox(DialogVbox.DISCONNECTED, null);
        suspendedGameMessage.setVisible(false);
        //set alert for quitGame message
        this.quitGameMessage = new DialogVbox(DialogVbox.QUIT_GAME, netManager);
        quitGameMessage.setVisible(false);

        //initializing endGamePane ( otherwise having problems not being initialized )
        this.endGamePane = new EndGamePane(game.getPlayers().stream().toList(), netManager);
        endGamePane.setVisible(false);

        //adding first finisher token on game pane otherwise it would have been disabled when is not
        //the player's turn
        this.finishToken = new ScoringTokenComponent();
        finishToken.setRotate(9);
        finishToken.setScore(
                game.firstFinisher().get() != null && Objects.equals(game.firstFinisher().get(), game.thePlayer()) ? 1 : 0);

        //adding game components
        getChildren()
                .add(this.thePlayerShelfie = new PlayerShelfieComponent(game.thePlayer(), game.getPersonalGoal(),
                        game.getCommonGoals().get(0), game.getCommonGoals().get(1)));
        getChildren().add(this.thePlayerPoints = new PlayerPointsComponent(game.thePlayer().score()));
        getChildren().add(
                this.commonGoalCardsPane = new CommonGoalsPane(game.getCommonGoals().get(0), game.getCommonGoals().get(1),
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

        getChildren().add(this.personalGoalCard = new PersonalGoalComponent(game.getPersonalGoal()));

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

        this.board = new BoardComponent(game.getBoard());
        this.boardPane = new Pane(board);
        getChildren().add(this.boardPane);
        getChildren().add(this.pickedTilesPane = new PickedTilesPane());

        this.adjacentItemTiles = new ImageButton();
        adjacentItemTiles.setImage(new WritableImage(
                new Image(FxResources.getResourceAsStream("assets/boards/livingroom.png")).getPixelReader(),
                1977, 2465, 889, 394));
        getChildren().add(adjacentItemTiles);

        //adding the end game token description to the game's pane
        getChildren().add(this.firstFinisherDescription = new FirstFinisherDescription());
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
                this.notCurrentTurnMessage.setVisible(true);
        });
        pickedTilesPane.tilesProperty().bindBidirectional(board.pickedTilesProperty());
        pickedTilesPane.setIsTileRemovable(tileAndCoords -> {
            final var list = new ArrayList<>(pickedTilesPane.getTiles());
            list.remove(tileAndCoords);
            return list.size() == 0 || game.getBoard().checkBoardCoord(list);
        });

        //binding this.firstFinisher to game.firstFinisher
        this.firstFinisher.bind(FxProperties.toFxProperty("firstFinisher", firstFinisher, game.firstFinisher()));
        this.firstFinisher.addListener((observable, oldValue, newValue) -> {
            finishToken.setScore(newValue != null && newValue.equals(game.thePlayer()) ? 1 : 0);
        });

        //now displaying points for commonGoals if achieved
        this.achieved1.bind(FxProperties.toFxProperty("achieved1", this, game.getCommonGoals().get(0).achieved()));
        achieved1.addListener((observable, oldValue, newValue) -> {
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
        });
        this.achieved2.bind(FxProperties.toFxProperty("achieved2", this, game.getCommonGoals().get(1).achieved()));
        achieved2.addListener((observable, oldValue, newValue) -> {
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
        });

        //binding suspended to GameView Provider game.suspended() and adding listener that when detects newValue
        //equals true, disables all current nodes in the game( except suspended game alert and endGamePane, in case
        //no other player reconnects therefore forcing the game to end )
        this.suspended.bind(FxProperties.toFxProperty("suspended", this, game.suspended()));
        suspended.addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                for (Node n : getChildren()) {
                    if (!n.equals(suspendedGameMessage)) {
                        n.setDisable(true);
                        n.setOpacity(0.5);
                    }
                }
                suspendedGameMessage.setVisible(true);
                suspendedGameMessage.play();

            } else {
                //when suspended comes back to false it restores the game properties back to normal
                suspendedGameMessage.setVisible(false);
                //restore all the children ( setDisable = true )
                for (Node n : getChildren()) {
                    n.setDisable(false);
                    n.setOpacity(1);
                }
            }
        }));

        //binding this.endGame to game's Provider endGame and adding listener that when listens endGame being true
        //disables all nodes in the game and displays the endGamePane
        this.endGame.bind(FxProperties.toFxProperty("endGame", this, game.endGame()));
        endGame.addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                //disable all the nodes ( when entering this state there is no coming back )
                for (Node n : getChildren()) {
                    n.setDisable(true);
                    n.setOpacity(0.5);
                }
                this.endGamePane = new EndGamePane(game.getSortedPlayers(), netManager);
                endGamePane.toFront();
                endGamePane.setAlignment(Pos.CENTER);
                this.getChildren().add(endGamePane);
            }
        }));

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
                    threadPool.submit(() -> {
                        try {
                            controller.makeMove(choosenCoords, tileAndCoords.col());
                        } catch (DisconnectedException e) {
                            var disc = onDisconnect.get();
                            if (disc != null)
                                disc.accept(e);
                        } finally {
                            Platform.runLater(() -> isMakingMove.set(false));
                        }
                    });
                })));

        final var otherPlayers = new ArrayList<>(game.getPlayers());
        otherPlayers.remove(game.thePlayer());
        getChildren().add(this.player1Shelfie = otherPlayers.size() >= 1
                ? new PlayerShelfieComponent(otherPlayers.get(0), game.getPersonalGoal(), game.getCommonGoals().get(0),
                        game.getCommonGoals().get(0), true, true)
                : new Pane());
        getChildren().add(this.player2Shelfie = otherPlayers.size() >= 2
                ? new PlayerShelfieComponent(otherPlayers.get(1), game.getPersonalGoal(), game.getCommonGoals().get(0),
                        game.getCommonGoals().get(0), true, true)
                : new Pane());
        getChildren().add(this.player3Shelfie = otherPlayers.size() >= 3
                ? new PlayerShelfieComponent(otherPlayers.get(2), game.getPersonalGoal(), game.getCommonGoals().get(0),
                        game.getCommonGoals().get(0), true, true)
                : new Pane());

        //adding chat to the game initially set not visible, its visibility can be modified by pressing over the
        //chat button
        getChildren().add(this.chatPane = new ChatComponent(
                game.getPlayers()
                        .stream().map(PlayerView::getNick)
                        .filter(nick -> !nick.equals(game.thePlayer().getNick()))
                        .toList(),
                game.thePlayer().getNick(), controller));
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

        quitGameBtn.setOnMouseClicked(event -> {
            for (Node n : getChildren()) {
                if (!n.equals(quitGameMessage)) {
                    n.setDisable(true);
                    n.setOpacity(0.5);
                }
            }
            quitGameMessage.setVisible(true);
        });
        this.getChildren().add(quitGameBtn);

        this.newChatBtn = new ChatButton();
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

        //adding the Alerts as last nodes so that they will be on front
        this.newMsg.toFront();
        this.notCurrentTurnMessage.toFront();
        this.suspendedGameMessage.toFront();
        this.quitGameMessage.toFront();
        getChildren().add(this.notCurrentTurnMessage);
        getChildren().add(this.suspendedGameMessage);
        getChildren().add(this.quitGameMessage);
        getChildren().add(this.newMsg);

        finishToken.toFront();
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
        super.layoutChildren();

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
        this.endGamePane.resizeRelocate((getWidth() - 690 * scale) / 2, (getHeight() - 490 * scale) / 2, 690 * scale,
                490 * scale);

        final var newMsgSize = 13 * scale;
        final var chatPaneWidth = 200.0 * scale;
        this.chatPane.resizeRelocate(
                getWidth() - chatPaneWidth + ChatComponent.INSET,
                +ChatComponent.INSET,
                chatPaneWidth - ChatComponent.INSET * 2, getHeight() - ChatComponent.INSET * 2);
        this.newMsg.resizeRelocate(803 * scale, 540 * scale, newMsgSize, newMsgSize);
    }
}
