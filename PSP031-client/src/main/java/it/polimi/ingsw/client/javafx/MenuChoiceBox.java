package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class MenuChoiceBox<T> extends InGameChoiceBox<T> {
    public MenuChoiceBox() {
        super(Color.LIGHTSEAGREEN);
        backgroundRadiusProperty().bind(widthProperty()
                .map(w -> new CornerRadii(Math.min(10, 10 * (w.doubleValue() / 210d)))));
        setBackgroundInsets(new Insets(0));

        //fonts.changeWeight(fontProperty(), FontWeight.BOLD);
        this.setStyle("-fx-font-weight: bold;");
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 115d, getHeight() / 46d);
        //  Fonts.changeSize(fontProperty(), 25 * scale);

        super.layoutChildren();
    }
}
