package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PersonalGoalDescription extends AnchorPane {
    private final Text text = new Text("The personal goal card grants points if you match the highlighted " +
            "spaces with the corresponding item tiles.\n1 pt for one matched tile, 2 pts for two, 4 pts for three, " +
            "6 pts for four, 9 pts for five, 12 pts for six.");

    public PersonalGoalDescription() {
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        setOpacity(0.9);

        text.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(text);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double scale = Math.min(getWidth() / 120d, getHeight() / 190d);
        double border = 6 * scale;

        text.setFont(Font.font(Font.getDefault().getName(), FontWeight.NORMAL, 12 * scale));
        text.setWrappingWidth(getWidth() - 2 * border);
        text.resizeRelocate(border, border, getWidth() - 2 * border, getHeight() - 2 * border);
    }
}
