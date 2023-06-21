package it.polimi.ingsw.client.javafx;

import org.jetbrains.annotations.Nullable;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Base class for all message dialogs
 */
abstract class DialogVbox extends VBox {

    public DialogVbox(String errorTitle, Color bgColor, Color errorBgColor, String errorMessage) {
        setSpacing(15);
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                bgColor,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(-10)))));

        Label errorName = new Label("Error: " + errorTitle);
        Fonts.changeWeight(errorName.fontProperty(), FontWeight.EXTRA_BOLD);
        errorName.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                errorBgColor,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(-5)))));
        errorName.setAlignment(Pos.CENTER);
        errorName.prefWidthProperty().bind(this.widthProperty());
        this.getChildren().add(errorName);

        Label dialogue = new Label(errorMessage);
        dialogue.setWrapText(true);
        dialogue.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(-5)))));
        dialogue.setAlignment(Pos.CENTER);
        dialogue.setTextAlignment(TextAlignment.CENTER);
        dialogue.setGraphicTextGap(20);
        this.getChildren().add(dialogue);
        dialogue.prefWidthProperty().bind(widthProperty());
        dialogue.prefHeightProperty().bind(heightProperty());
    }

    protected static class DialogButton extends InGameButton {

        public DialogButton(String text, @Nullable Color bgColor) {
            super(text, bgColor);

            backgroundRadiusProperty().bind(parentProperty()
                    .flatMap(p -> p instanceof Region r ? r.widthProperty() : null)
                    .map(w -> new CornerRadii(Math.min(5, 5 * (w.doubleValue() / 210d)))));
            setBackgroundInsets(new Insets(-5));
        }
    }
}
