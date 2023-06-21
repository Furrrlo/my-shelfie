package it.polimi.ingsw.client.javafx;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import static it.polimi.ingsw.client.javafx.FxProperties.compositeObservableValue;

/**
 * Button with an image as background
 */
class ImageButton extends Button {

    private final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
    private final BooleanProperty highlight = new SimpleBooleanProperty(this, "highlight");
    private final ObjectProperty<Color> highlightColor = new SimpleObjectProperty<>(this, "highlightColor");

    private final RemappableObjectProperty<Effect> remappableEffect;

    public ImageButton() {
        // Change style class to prevent picking up stuff from the default css
        getStyleClass().setAll("image-button");

        remappableEffect = new RemappableObjectProperty<>(effectProperty());

        setPadding(Insets.EMPTY);
        backgroundProperty().bind(FxProperties.compositeObservableValue(image, layoutBoundsProperty()).map(ignored -> {
            var img = image.get();
            if (img == null)
                return Background.fill(Color.TRANSPARENT);

            var scale = Math.min(getLayoutBounds().getWidth() / img.getWidth(),
                    getLayoutBounds().getHeight() / img.getHeight());
            return new Background(new BackgroundImage(
                    img,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            img.getWidth() * scale,
                            img.getHeight() * scale,
                            false, false, false, false)));
        }));

        highlightProperty().bind(disabledProperty().not().and(armedProperty().or(hoverProperty())));
        highlightColorProperty().bind(compositeObservableValue(armedProperty(), hoverProperty())
                .map(ignored -> isArmed() ? Color.WHITE.darker() : Color.WHITE));

        borderProperty().bind(compositeObservableValue(highlightProperty(), highlightColorProperty())
                .map(ignored -> isHighlight()
                        ? new Border(new BorderStroke(getHighlightColor(), BorderStrokeStyle.SOLID,
                                new CornerRadii(2), new BorderWidths(4)))
                        : Border.EMPTY));

        if (Platform.isSupported(ConditionalFeature.EFFECT)) {
            remappableEffectProperty().bind(disabledProperty().map(o -> {
                if (!o)
                    return null;

                var colorAdjust = new ColorAdjust(0, 0, -0.3f, 0);
                colorAdjust.setInput(new GaussianBlur(3.5));
                return colorAdjust;
            }));
        }

        this.textProperty().set("");
        this.ellipsisStringProperty().set("");
    }

    public Image getImage() {
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public boolean isHighlight() {
        return highlight.get();
    }

    public BooleanProperty highlightProperty() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight.set(highlight);
    }

    public Color getHighlightColor() {
        return highlightColor.get();
    }

    public ObjectProperty<Color> highlightColorProperty() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor.set(highlightColor);
    }

    public RemappableObjectProperty<Effect> remappableEffectProperty() {
        return remappableEffect;
    }
}
