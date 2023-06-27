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

    private final Label errorName;
    private final Label dialogue;

    public DialogVbox(String errorTitle, Color bgColor, Color errorBgColor, String errorMessage) {
        setSpacing(15);
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                bgColor,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 230d))),
                new Insets(0)))));

        this.errorName = new Label("Error: " + errorTitle);
        Fonts.changeWeight(errorName.fontProperty(), FontWeight.EXTRA_BOLD);
        errorName.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                errorBgColor,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(0)))));

        errorName.setAlignment(Pos.CENTER);
        this.getChildren().add(errorName);

        this.dialogue = new Label(errorMessage);
        dialogue.setWrapText(true);
        dialogue.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        dialogue.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().add(dialogue);

    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double scale = Math.min(getWidth() / 230d, getHeight() / 230d);

        final double border = 5 * scale;
        double text_height = 14 * scale;
        double header_height = 28 * scale;

        //5 + 28 + 5 + messaggio + 5 + bottone + 28 + 5 = 230

        this.errorName.resizeRelocate(border, border, getWidth() - 2 * border, header_height);
        this.dialogue.resizeRelocate(border, text_height * 2 + 2 * border, getWidth() - 2 * border,
                154 * scale);

        dialogue.setGraphicTextGap(40);

        Fonts.changeSize(errorName.fontProperty(), text_height);
        Fonts.changeSize(dialogue.fontProperty(), text_height);

    }

    protected static class DialogButton extends InGameButton {
        public DialogButton(String text, @Nullable Color bgColor) {
            super(text, bgColor);

            backgroundRadiusProperty().bind(parentProperty()
                    .flatMap(p -> p instanceof Region r ? r.widthProperty() : null)
                    .map(w -> new CornerRadii(Math.min(5, 5 * (w.doubleValue() / 210d)))));
            setBackgroundInsets(new Insets(0));//anche qui erano 5
        }

    }
}
