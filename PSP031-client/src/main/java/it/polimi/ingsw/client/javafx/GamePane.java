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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class GamePane extends AnchorPane {

    private final PlayerShelfieComponent thePlayerShelfie;
    private final Pane thePlayerPoints;
    private final Pane commonGoalCardsPane;
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

    private final EndGamePane endGamePane;

    private final ObjectProperty<Consumer<@Nullable Throwable>> onDisconnect = new SimpleObjectProperty<>();

    private final ObjectProperty<Boolean> suspended = new SimpleObjectProperty<>(this, "suspended");

    private final ObjectProperty<Boolean> endGame = new SimpleObjectProperty<>(this, "endGame");

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
        this.notCurrentTurnMessage = new DialogVbox(DialogVbox.NOT_CURRENT_TURN);
        notCurrentTurnMessage.setVisible(false);
        this.suspendedGameMessage = new DialogVbox(DialogVbox.DISCONNECTED);
        suspendedGameMessage.setVisible(false);
        this.endGamePane = new EndGamePane(game.getPlayers());
        this.endGamePane.setVisible(false);

        getChildren()
                .add(this.thePlayerShelfie = new PlayerShelfieComponent(game.thePlayer()));
        getChildren().add(this.thePlayerPoints = new PlayerPointsComponent(game.thePlayer().score()));
        getChildren().add(
                this.commonGoalCardsPane = new CommonGoalsPane(game.getCommonGoals().get(0), game.getCommonGoals().get(1)));
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
        this.suspended.bind(FxProperties.toFxProperty("suspended", this, game.suspended()));
        suspended.addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                //this.setDisable(true);
                for (Node n : getChildren()) {
                    if (!n.equals(suspendedGameMessage)) {
                        n.setDisable(true);
                        n.setOpacity(0.5);
                    }
                }
                suspendedGameMessage.setVisible(true);
                suspendedGameMessage.play();

            } else {
                suspendedGameMessage.setVisible(false);
                for (Node n : getChildren()) {
                    if (!n.equals(suspendedGameMessage)) {
                        n.setDisable(false);
                        n.setOpacity(1);
                    }
                }
                //this.setDisable(false);
            }
        }));
        //added endGame Listener
        this.endGame.bind(FxProperties.toFxProperty("endGame", this, game.endGame()));
        endGame.addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                for (Node n : getChildren()) {
                    //if (!n.equals(/*TODO: insert node for end game*/)) {
                    //    n.setDisable(true);
                    //    n.setOpacity(0.5);
                    //}
                }
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

        getChildren().add(this.chatBtn = new Button());
        var imgView = new ImageView(new Image(FxResources.getResourceAsStream("fa/message.png")));
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(30);
        imgView.setFitHeight(30);
        this.chatBtn.setGraphic(imgView);
        this.chatBtn.setBackground(Background.fill(Color.LIGHTGRAY));
        this.chatBtn.setShape(new Circle(37));
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

        this.notCurrentTurnMessage.toFront();
        this.suspendedGameMessage.toFront();
        this.endGamePane.toFront();
        getChildren().add(this.notCurrentTurnMessage);
        getChildren().add(this.suspendedGameMessage);
        getChildren().add(this.endGamePane);
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
        this.endGamePane.resizeRelocate(20 * scale, 10 * scale, 960 * scale, 540 * scale);
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

    public Consumer<@Nullable Throwable> getOnDisconnect() {
        return onDisconnect.get();
    }

    public ObjectProperty<Consumer<@Nullable Throwable>> onDisconnectProperty() {
        return onDisconnect;
    }

    public void setOnDisconnect(Consumer<@Nullable Throwable> onDisconnect) {
        this.onDisconnect.set(onDisconnect);
    }

    public Boolean getSuspended() {
        return suspended.get();
    }

    public ObjectProperty<Boolean> suspendedProperty() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended.set(suspended);
    }
}
