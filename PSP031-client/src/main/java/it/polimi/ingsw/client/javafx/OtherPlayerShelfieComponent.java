package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.PlayerView;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * Component containing the shelfie and the tokens of a remote player
 *
 * @see PlayerShelfieComponent
 */
class OtherPlayerShelfieComponent extends PlayerShelfieComponent {

    private final Pane tokenFirstFinisher;

    private final ObjectProperty<Integer> scoreCommonGoal1;
    private final ObjectProperty<Integer> scoreCommonGoal2;
    private final ScoringTokenComponent tokenCommonGoal1;
    private final ScoringTokenComponent tokenCommonGoal2;

    public OtherPlayerShelfieComponent(FxResourcesLoader resources, PlayerView player) {
        super(resources, player, true, true);

        scoreCommonGoal1 = new SimpleObjectProperty<>();
        scoreCommonGoal2 = new SimpleObjectProperty<>();

        ImageView tokenFirstFinisherImgView = new ImageView(resources.loadImage("assets/scoring tokens/end game.jpg"));
        getChildren().add(this.tokenFirstFinisher = new AnchorPane(tokenFirstFinisherImgView));
        tokenFirstFinisher.setVisible(false);
        AnchorPane.setTopAnchor(tokenFirstFinisherImgView, 0.0);
        AnchorPane.setBottomAnchor(tokenFirstFinisherImgView, 0.0);
        AnchorPane.setLeftAnchor(tokenFirstFinisherImgView, 0.0);
        AnchorPane.setRightAnchor(tokenFirstFinisherImgView, 0.0);
        tokenFirstFinisherImgView.fitWidthProperty().bind(tokenFirstFinisher.widthProperty());
        tokenFirstFinisherImgView.fitHeightProperty().bind(tokenFirstFinisher.heightProperty());

        getChildren().add(this.tokenCommonGoal1 = new ScoringTokenComponent(resources, scoreCommonGoal1));

        getChildren().add(this.tokenCommonGoal2 = new ScoringTokenComponent(resources, scoreCommonGoal2));
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double scale = Math.min(getWidth() / 180d, getHeight() / 194d);
        double size = 30 * scale;

        double shelfieOffsetY = Math.min(20, 14 * scale);
        double shelfieWidth = 180d * scale;

        double labelWidth = Math.min(300, shelfieWidth - 2 * 28 * scale);
        this.label.resizeRelocate((shelfieWidth - labelWidth) / 2, 0, labelWidth, Math.min(30, 21 * scale));

        this.shelfieComponent.resizeRelocate(0, shelfieOffsetY, shelfieWidth, getHeight() - shelfieOffsetY);

        double chairWidth = 35 * scale, chairHeight = 33 * scale;
        this.chair.resizeRelocate(shelfieWidth - chairWidth, getHeight() - chairHeight, chairWidth, chairHeight);

        this.tokenFirstFinisher.resizeRelocate(getWidth() - size, getHeight() - size - 40 * 3 * scale, size, size);
        this.tokenCommonGoal1.resizeRelocate(getWidth() - size, getHeight() - size - 40 * 2 * scale, size, size);
        this.tokenCommonGoal2.resizeRelocate(getWidth() - size, getHeight() - size - 40 * scale, size, size);
    }

    public void displayFirstFinisherImage() {
        tokenFirstFinisher.setVisible(true);
    }

    public void setTokenCommonGoal1Score(int score) {
        scoreCommonGoal1.set(score);
    }

    public void setTokenCommonGoal2Score(int score) {
        scoreCommonGoal2.set(score);
    }

}
