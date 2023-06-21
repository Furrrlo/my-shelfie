package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class QuitGameButton extends Pane {

    private final Text quit = new Text("Quit game");

    public QuitGameButton() {
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.INDIANRED,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0)))));

        quit.setTextAlignment(TextAlignment.CENTER);
        quit.wrappingWidthProperty().bind(widthProperty());
        quit.prefWidth(getWidth());
        quit.prefHeight(getHeight());
        getChildren().add(quit);

        setOnMouseEntered(event -> backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.DARKSALMON,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0))))));
        setOnMouseExited(event -> backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.INDIANRED,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0))))));
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 115d, getHeight() / 46d);
        quit.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 14 * scale));
        quit.resizeRelocate(0, (getHeight() - 15 * scale) / 2, getWidth(), getHeight());
    }
}
