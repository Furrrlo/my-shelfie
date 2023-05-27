package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.CommonGoalView;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CommonGoalsPane extends Pane {

    private final Label description;
    private final CommonGoalComponent goal1;
    private final CommonGoalComponent goal2;

    public CommonGoalsPane(CommonGoalView goal1, CommonGoalView goal2) {
        setBackground(new Background(new BackgroundImage(
                new Image(FxResources.getResourceAsStream("assets/misc/base_pagina2.jpg")),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
            clipProperty().bind(layoutBoundsProperty().map(bounds -> {
                var radius = Math.min(20, 20 * Math.min(bounds.getWidth() / 210d, bounds.getHeight() / 170d));
                Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
                clip.setArcWidth(radius);
                clip.setArcHeight(radius);
                return clip;
            }));
        }
        this.description = new Label();
        //TODO : blurrz the background instead of filling with plain color
        description.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(-6)))));

        description.setWrapText(true);
        description.setVisible(false);

        getChildren().add(this.goal1 = new CommonGoalComponent(goal1));
        getChildren().add(this.goal2 = new CommonGoalComponent(goal2));

        this.goal1.setOnMousePressed(e -> {
            description.setText(goal1.getType().getDescription().replaceAll("[\\r\\n]+", " "));
            description.setVisible(!description.isVisible());
        });
        this.goal2.setOnMousePressed(e -> {
            description.setText(goal2.getType().getDescription().replaceAll("[\\r\\n]+", " "));
            description.setVisible(!description.isVisible());
        });
        this.description.setOnMousePressed(e -> {
            description.setVisible(!description.isVisible());
        });
        getChildren().add(this.description);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double scale = Math.min(getWidth() / 221d, getHeight() / 156d);
        double border = 6 * scale;
        double width = 113 * scale;
        double height = 74 * scale;
        goal1.resizeRelocate(getWidth() - width - border, border, width, height);
        goal2.resizeRelocate(getWidth() - width - border, getHeight() - height - border, width, height);
        description.resizeRelocate(2 * border, 2 * border, getWidth() - 4 * border, getHeight() - 4 * border);
    }

    private static class CommonGoalComponent extends ImageButton {

        public CommonGoalComponent(CommonGoalView commonGoal) {
            setImage(new Image(FxResources.getResourceAsStream("assets/common goal cards/" + switch (commonGoal.getType()) {
                case TWO_SQUARES -> 1;
                case TWO_ALL_DIFF_COLUMNS -> 2;
                case FOUR_QUADRIPLETS -> 3;
                case SIX_COUPLES -> 4;
                case THREE_COLUMNS -> 5;
                case TWO_ALL_DIFF_ROWS -> 6;
                case FOUR_ROWS -> 7;
                case ALL_CORNERS -> 8;
                case EIGHT_EQUAL_TILES -> 9;
                case CROSS -> 10;
                case DIAGONAL -> 11;
                case TRIANGLE -> 12;
            } + ".jpg")));

            if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
                clipProperty().bind(layoutBoundsProperty().map(bounds -> {
                    var radius = Math.min(20, 20 * Math.min(bounds.getWidth() / 140d, bounds.getHeight() / 210d));
                    Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
                    clip.setArcWidth(radius);
                    clip.setArcHeight(radius);
                    return clip;
                }));
            }
        }
    }
}
