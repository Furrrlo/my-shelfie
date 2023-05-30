package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.GameController;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
    //TODO : add quit button to GameScene, change position of ChatButton going over fourth shelfie
    //      and add firstFinisherTile ; 
    private final PlayerShelfieComponent thePlayerShelfie;
    private final Pane thePlayerPoints;
    private final CommonGoalsPane commonGoalCardsPane;
    private final PersonalGoalComponent personalGoalCard;
    private final BoardComponent board;
    private final Pane boardPane;
    private final PickedTilesPane pickedTilesPane;
    private final Pane player1Shelfie;
    private final Pane player2Shelfie;
    private final Pane player3Shelfie;
    private final Button chatBtn;
    private final Label newMsg;
    private final ChatComponent chatPane;

    private final DialogVbox notCurrentTurnMessage;
    private final DialogVbox suspendedGameMessage;
    private EndGamePane endGamePane;

    private final ObjectProperty<Consumer<@Nullable Throwable>> onDisconnect = new SimpleObjectProperty<>();

    private final ObjectProperty<Boolean> suspended = new SimpleObjectProperty<>(this, "suspended");

    private final ObjectProperty<Boolean> endGame = new SimpleObjectProperty<>(this, "endGame");

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

    public GamePane(GameView game, GameController controller) {
        //set new alerts for suspended game and disconnected player ( making them initially not visible,
        // will be later made visible when corresponding state is caught )
        this.notCurrentTurnMessage = new DialogVbox(DialogVbox.NOT_CURRENT_TURN);
        notCurrentTurnMessage.setVisible(false);
        this.suspendedGameMessage = new DialogVbox(DialogVbox.DISCONNECTED);
        suspendedGameMessage.setVisible(false);

        //initializing endGamePane ( otherwise having problems not being initialized )
        this.endGamePane = new EndGamePane(game.getPlayers().stream().toList());
        endGamePane.setVisible(false);

        //adding game components
        getChildren()
                .add(this.thePlayerShelfie = new PlayerShelfieComponent(game.thePlayer()));
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
        //getChildren().add(this.board = new BoardComponent(game.getBoard()));
        this.board = new BoardComponent(game.getBoard());
        this.boardPane = new Pane(board);
        getChildren().add(this.boardPane);
        getChildren().add(this.pickedTilesPane = new PickedTilesPane());

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
                var sortedPlayers = game.getPlayers().stream()
                        .sorted(Comparator.comparing((PlayerView p) -> p.score().get())).toList();
                this.endGamePane = new EndGamePane(sortedPlayers);
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
                ? new PlayerShelfieComponent(otherPlayers.get(0), true, true)
                : new Pane());
        getChildren().add(this.player2Shelfie = otherPlayers.size() >= 2
                ? new PlayerShelfieComponent(otherPlayers.get(1), true, true)
                : new Pane());
        getChildren().add(this.player3Shelfie = otherPlayers.size() >= 3
                ? new PlayerShelfieComponent(otherPlayers.get(2), true, true)
                : new Pane());

        //adding chat button
        getChildren().add(this.chatBtn = new Button());
        var imgView = new ImageView(new Image(FxResources.getResourceAsStream("fa/message.png")));
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(30);
        imgView.setFitHeight(30);
        this.chatBtn.setGraphic(imgView);
        this.chatBtn.setBackground(Background.fill(Color.LIGHTGRAY));
        this.chatBtn.setShape(new Circle(37));

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
        getChildren().add(this.newMsg = new Label(""));
        this.newMsg.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(100, 100 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        this.newMsg.setVisible(false);

        //change visibility of chatPane when chatBtn is pressed
        this.chatBtn.setOnAction(e -> {
            this.chatPane.setVisible(!this.chatPane.isVisible());
            if (chatPane.isVisible())
                this.newMsg.setVisible(false);
        });
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
        this.notCurrentTurnMessage.toFront();
        this.suspendedGameMessage.toFront();
        getChildren().add(this.notCurrentTurnMessage);
        getChildren().add(this.suspendedGameMessage);
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
        this.board.resize(460.0 * scale, 460.0 * scale);
        this.boardPane.resizeRelocate(370.0 * scale, 0, 460.0 * scale, 460.0 * scale);
        this.pickedTilesPane.resizeRelocate(370.0 * scale, 471.0 * scale, 460.0 * scale, 114.0 * scale);
        this.player1Shelfie.resizeRelocate(842.0 * scale, 0, 182.0 * scale, 194.0 * scale);
        this.player2Shelfie.resizeRelocate(842.0 * scale, 196.0 * scale, 182.0 * scale, 194.0 * scale);
        this.player3Shelfie.resizeRelocate(842.0 * scale, 392.0 * scale, 182.0 * scale, 194.0 * scale);
        this.notCurrentTurnMessage.resizeRelocate((370.0 + 115.0) * scale, 115.0 * scale, 230 * scale, 230.0 * scale);
        this.suspendedGameMessage.resizeRelocate((370.0 + 115.0) * scale, 115.0 * scale, 230 * scale, 230.0 * scale);
        this.endGamePane.resizeRelocate((getWidth() - 690 * scale) / 2, (getHeight() - 490 * scale) / 2, 690 * scale,
                490 * scale);

        final var btnSize = 45 * scale;
        final var newMsgSize = 15 * scale;
        final var chatPaneWidth = 200.0 * scale;
        if (chatPane.isVisible())
            this.chatBtn.resizeRelocate(
                    getWidth() - chatPaneWidth - btnSize - (double) ChatComponent.INSET * 3 / 2,
                    getHeight() - btnSize, btnSize, btnSize);
        else
            this.chatBtn.resizeRelocate(getWidth() - btnSize, getHeight() - btnSize, btnSize, btnSize);
        this.chatPane.resizeRelocate(
                getWidth() - chatPaneWidth + ChatComponent.INSET,
                +ChatComponent.INSET,
                chatPaneWidth - ChatComponent.INSET * 2, getHeight() - ChatComponent.INSET * 2);
        this.newMsg.resizeRelocate(getWidth() - btnSize + newMsgSize * 3 / 2, getHeight() - btnSize - newMsgSize / 3,
                newMsgSize,
                newMsgSize);
    }
}
