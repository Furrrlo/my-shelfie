package it.polimi.ingsw.client.javafx;

import org.jetbrains.annotations.Nullable;

import javafx.beans.binding.DoubleExpression;
import javafx.beans.value.ObservableDoubleValue;
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

    public DialogVbox(String errorTitle, Color bgColor, Color errorBgColor, Color borderColor, String errorMessage) {
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                bgColor,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 230d))),
                new Insets(0)))));
        borderProperty().bind(widthProperty().map(w -> new Border(new BorderStroke(
                borderColor,
                BorderStrokeStyle.SOLID,
                new CornerRadii(Math.min(10, 10 * (w.doubleValue() / 230d))),
                BorderWidths.DEFAULT))));
        paddingProperty().bind(borderSize().map(border -> new Insets(border.doubleValue())));
        spacingProperty().bind(borderSize());
        setFillWidth(true);

        this.errorName = new Label("Error: " + errorTitle);
        Fonts.changeWeight(errorName.fontProperty(), FontWeight.EXTRA_BOLD);
        errorName.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                errorBgColor,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        errorName.paddingProperty().bind(innerComponentPadding().map(border -> new Insets(border.doubleValue())));
        errorName.prefWidthProperty().bind(this.widthProperty());
        errorName.setAlignment(Pos.CENTER);
        this.getChildren().add(errorName);

        this.dialogue = new Label(errorMessage);
        dialogue.setWrapText(true);
        dialogue.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        dialogue.paddingProperty().bind(innerComponentPadding().map(border -> new Insets(border.doubleValue())));
        dialogue.setTextAlignment(TextAlignment.CENTER);
        dialogue.prefWidthProperty().bind(widthProperty());
        dialogue.prefHeightProperty().bind(heightProperty());
        VBox.setVgrow(dialogue, Priority.ALWAYS);
        this.getChildren().add(dialogue);
    }

    protected ObservableDoubleValue borderSize() {
        return DoubleExpression
                .doubleExpression(FxProperties.compositeObservableValue(widthProperty(), heightProperty()).map(i -> {
                    double scale = Math.min(getWidth() / 230d, getHeight() / 230d);
                    return 5 * scale;
                }));
    }

    protected ObservableDoubleValue innerComponentPadding() {
        return DoubleExpression
                .doubleExpression(FxProperties.compositeObservableValue(widthProperty(), heightProperty()).map(i -> {
                    double scale = Math.min(getWidth() / 230d, getHeight() / 230d);
                    return 7 * scale;
                }));
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 230d, getHeight() / 230d);
        double textHeight = 14 * scale;

        Fonts.changeSize(errorName.fontProperty(), textHeight);
        Fonts.changeSize(dialogue.fontProperty(), textHeight);

        super.layoutChildren();
    }

    protected class DialogButton extends InGameButton {
        public DialogButton(String text, @Nullable Color bgColor) {
            super(text, bgColor);

            backgroundRadiusProperty().bind(parentProperty()
                    .flatMap(p -> p instanceof Region r ? r.widthProperty() : null)
                    .map(w -> new CornerRadii(Math.min(5, 5 * (w.doubleValue() / 210d)))));
            setBackgroundInsets(new Insets(0));
            paddingProperty().bind(innerComponentPadding().map(border -> new Insets(border.doubleValue())));

            FxProperties.compositeObservableValue(DialogVbox.this.widthProperty(), DialogVbox.this.heightProperty())
                    .addListener((obs, i0, i1) -> {
                        double scale = Math.min(DialogVbox.this.getWidth() / 230d, DialogVbox.this.getHeight() / 230d);
                        Fonts.changeSize(fontProperty(), 14 * scale);
                    });
        }
    }
}
