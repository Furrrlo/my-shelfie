package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.GameView;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.ArrayList;

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

    public GamePane(GameView game) {
        setBackground(new Background(new BackgroundImage(
                new Image(FxResources.getResourceAsStream("assets/misc/sfondo parquet.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true))));

        getChildren()
                .add(this.thePlayerShelfie = new PlayerShelfieComponent(game.thePlayer()));
        getChildren().add(this.thePlayerPoints = new PlayerPointsComponent(game.thePlayer().score()));
        getChildren().add(
                this.commonGoalCardsPane = new CommonGoalsPane(game.getCommonGoals().get(0), game.getCommonGoals().get(1)));
        getChildren().add(this.personalGoalCard = new PersonalGoalComponent(game.getPersonalGoal()));
        getChildren().add(this.board = new BoardComponent(game.getBoard()));
        getChildren().add(this.pickedTilesPane = new PickedTilesPane());
        this.pickedTilesPane.tilesProperty().bindBidirectional(this.board.pickedTilesProperty());

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
    }
}
