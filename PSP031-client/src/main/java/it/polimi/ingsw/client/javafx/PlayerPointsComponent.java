package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Provider;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class PlayerPointsComponent extends HBox {

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

        var label = new Label();
        label.textProperty().bind(FxProperties.toFxProperty("score", this, score).map(s -> s + " pt"));
        getChildren().add(label);
    }
}
