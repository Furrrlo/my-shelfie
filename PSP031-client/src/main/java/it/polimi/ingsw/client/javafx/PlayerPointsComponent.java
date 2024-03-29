package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Provider;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Component representing the score of a player
 */
class PlayerPointsComponent extends HBox {

    private final Label label;

    public PlayerPointsComponent(Provider<Integer> score) {
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(1)))));
        borderProperty().bind(widthProperty().map(width -> new Border(new BorderStroke(
                Color.rgb(129, 33, 0),
                BorderStrokeStyle.SOLID,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                BorderStroke.THICK))));

        spacingProperty().bind(widthProperty().map(w -> 10 * (w.doubleValue() / 210d)));
        setAlignment(Pos.CENTER);

        label = new Label();
        label.textProperty().bind(FxProperties.toFxProperty("score", this, score).map(s -> s + " pt"));
        getChildren().add(label);
    }

    @Override
    protected void layoutChildren() {
        Fonts.changeSize(label.fontProperty(), 12d * getHeight() / 28d);
        super.layoutChildren();
    }
}
