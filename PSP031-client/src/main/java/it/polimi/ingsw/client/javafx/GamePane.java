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
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

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
    private final PickedTilesPane pickedTilesPane;
    private final Pane player1Shelfie;
    private final Pane player2Shelfie;
    private final Pane player3Shelfie;
    private final Button chatBtn;
    private final ChatComponent chatPane;

    private final ObjectProperty<Consumer<@Nullable Throwable>> onDisconnect = new SimpleObjectProperty<>();

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
        getChildren()
                .add(this.thePlayerShelfie = new PlayerShelfieComponent(game.thePlayer()));
        getChildren().add(this.thePlayerPoints = new PlayerPointsComponent(game.thePlayer().score()));
        getChildren().add(
                this.commonGoalCardsPane = new CommonGoalsPane(game.getCommonGoals().get(0), game.getCommonGoals().get(1)));
        getChildren().add(this.personalGoalCard = new PersonalGoalComponent(game.getPersonalGoal()));
        getChildren().add(this.board = new BoardComponent(game.getBoard()));
        getChildren().add(this.pickedTilesPane = new PickedTilesPane());

        final BooleanProperty isMakingMove = new SimpleBooleanProperty();
        final var isCurrentTurn = BooleanExpression.booleanExpression(FxProperties.toFxProperty(
                "isCurrentTurn", this, game.thePlayer().isCurrentTurn())).and(isMakingMove.not());
        board.disableProperty().bind(isCurrentTurn.not());
        pickedTilesPane.tilesProperty().bindBidirectional(board.pickedTilesProperty());
        pickedTilesPane.setIsTileRemovable(tileAndCoords -> {
            final var list = new ArrayList<>(pickedTilesPane.getTiles());
            list.remove(tileAndCoords);
            return list.size() == 0 || game.getBoard().checkBoardCoord(list);
        });

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

        getChildren().add(this.chatBtn = new Button("C"));
        this.chatBtn.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(50, 50 * (width.doubleValue() / 210d))),
                new Insets(4)))));
        getChildren().add(this.chatPane = new ChatComponent(
                game.getPlayers()
                        .stream().map(PlayerView::getNick)
                        .filter(nick -> !nick.equals(game.thePlayer().getNick()))
                        .toList(),
                game.thePlayer().getNick(), controller));
        this.chatPane.messagesProperty().bind(FxProperties
                .toFxProperty("messages", this, game.messageList()));
        this.chatPane.setVisible(false);
        //change visibility of chatPane when chatBtn is pressed
        this.chatBtn.setOnAction(e -> {
            this.chatPane.setVisible(!this.chatPane.isVisible());
        });
        this.chatPane.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(-ChatComponent.INSET)))));
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
        this.board.resizeRelocate(370.0 * scale, 0, 460.0 * scale, 460.0 * scale);
        this.pickedTilesPane.resizeRelocate(370.0 * scale, 471.0 * scale, 460.0 * scale, 114.0 * scale);
        this.player1Shelfie.resizeRelocate(842.0 * scale, 0, 182.0 * scale, 194.0 * scale);
        this.player2Shelfie.resizeRelocate(842.0 * scale, 196.0 * scale, 182.0 * scale, 194.0 * scale);
        this.player3Shelfie.resizeRelocate(842.0 * scale, 392.0 * scale, 182.0 * scale, 194.0 * scale);
        final var btnSize = 45 * scale;
        final var chatPaneWidth = 200.0 * scale;
        if (chatPane.isVisible())
            this.chatBtn.resizeRelocate(
                    getWidth() - chatPaneWidth - btnSize - ChatComponent.INSET * 2,
                    getHeight() - btnSize, btnSize, btnSize);
        else
            this.chatBtn.resizeRelocate(getWidth() - btnSize, getHeight() - btnSize, btnSize, btnSize);
        this.chatPane.resizeRelocate(
                getWidth() - chatPaneWidth + ChatComponent.INSET,
                +ChatComponent.INSET,
                chatPaneWidth - ChatComponent.INSET * 2, getHeight() - ChatComponent.INSET * 2);
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
}
