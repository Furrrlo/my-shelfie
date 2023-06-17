package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.PersonalGoalView;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PersonalGoalComponent extends ImageButton {

    public PersonalGoalComponent(FxResourcesLoader resources, PersonalGoalView personalGoal) {
        var img = resources.loadImage(personalGoal.getIndex() == 0
                ? "assets/personal goal cards/Personal_Goals.png"
                : "assets/personal goal cards/Personal_Goals" + (personalGoal.getIndex() + 1) + ".png");
        setImage(img);

        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
            clipProperty().bind(layoutBoundsProperty().map(bounds -> {
                var radius = Math.min(20, 20 * Math.min(bounds.getWidth() / 140d, bounds.getHeight() / 210d));
                Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
                clip.setArcWidth(radius);
                clip.setArcHeight(radius);
                return clip;
            }));
        }

        if (Platform.isSupported(ConditionalFeature.EFFECT)) {
            remappableEffectProperty().bind(remappableEffectProperty().remapNullable(e -> {
                var dropShadow = new DropShadow(5, Color.WHITE);
                if (e == null)
                    return dropShadow;

                dropShadow.setInput(e);
                return dropShadow;
            }));
        }
    }
}
