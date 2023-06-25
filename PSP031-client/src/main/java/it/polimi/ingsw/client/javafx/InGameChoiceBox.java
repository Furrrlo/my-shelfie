package it.polimi.ingsw.client.javafx;

import org.jetbrains.annotations.Nullable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class InGameChoiceBox<T> extends ChoiceBox<T> {

    private final ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>(this, "backgroundColor");
    private final ObjectProperty<CornerRadii> backgroundRadius = new SimpleObjectProperty<>(this, "backgroundRadius");
    private final ObjectProperty<Insets> backgroundInsets = new SimpleObjectProperty<>(this, "backgroundInsets");

    public InGameChoiceBox() {
        this(null);
    }

    public InGameChoiceBox(@Nullable Color bgColor) {
        backgroundColor.set(bgColor);
        backgroundProperty().bind(FxProperties.compositeObservableValue(
                hoverProperty(),
                backgroundColorProperty(),
                backgroundRadiusProperty(),
                backgroundInsetsProperty())
                .map(i -> {
                    var color = backgroundColor.get();
                    return new Background(new BackgroundFill(
                            color == null ? null
                                    : isHover() ? color.brighter()
                                            : color,
                            getBackgroundRadius(),
                            getBackgroundInsets()));
                }));
    }

    public Color getBackgroundColor() {
        return backgroundColor.get();
    }

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor.set(backgroundColor);
    }

    public CornerRadii getBackgroundRadius() {
        return backgroundRadius.get();
    }

    public ObjectProperty<CornerRadii> backgroundRadiusProperty() {
        return backgroundRadius;
    }

    public void setBackgroundRadius(CornerRadii backgroundRadius) {
        this.backgroundRadius.set(backgroundRadius);
    }

    public Insets getBackgroundInsets() {
        return backgroundInsets.get();
    }

    public ObjectProperty<Insets> backgroundInsetsProperty() {
        return backgroundInsets;
    }

    public void setBackgroundInsets(Insets backgroundInsets) {
        this.backgroundInsets.set(backgroundInsets);
    }
}
