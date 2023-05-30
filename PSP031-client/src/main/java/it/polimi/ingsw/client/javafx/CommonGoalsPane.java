package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.CommonGoalView;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

public class CommonGoalsPane extends Pane {

    private final StackPane stackPane;
    private final Label description;
    private final CommonGoalComponent goal1;

    private final ObjectProperty<Integer> score1;
    private final ScoringTokenComponent token1;
    private final CommonGoalComponent goal2;

    private final ObjectProperty<Integer> score2;
    private final ScoringTokenComponent token2;

    public CommonGoalsPane(CommonGoalView goal1, CommonGoalView goal2) {
        this.score1 = new SimpleObjectProperty<>(this, "score1");
        this.score2 = new SimpleObjectProperty<>(this, "score2");
        this.token1 = new ScoringTokenComponent();
        this.token2 = new ScoringTokenComponent();
        token1.setScore(ScoringTokenComponent.EMPTY);
        token2.setScore(ScoringTokenComponent.EMPTY);
        score1.addListener(((observable, oldValue, newValue) -> {
            token1.setScore(newValue);
        }));
        score2.addListener(((observable, oldValue, newValue) -> {
            token2.setScore(newValue);
        }));

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
        this.stackPane = new StackPane();
        stackPane.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        stackPane.setOpacity(0.9);
        this.description = new Label();
        description.setPadding(new Insets(6));
        stackPane.getChildren().add(description);
        description.setWrapText(true);
        stackPane.setVisible(false);
        description.setTextAlignment(TextAlignment.CENTER);

        getChildren().add(this.goal1 = new CommonGoalComponent(goal1));
        getChildren().add(this.goal2 = new CommonGoalComponent(goal2));

        this.goal1.setOnMousePressed(e -> {
            description.setText(goal1.getType().getDescription().replaceAll("[\\r\\n]+", " "));
            stackPane.setVisible(!stackPane.isVisible());
        });
        this.goal2.setOnMousePressed(e -> {
            description.setText(goal2.getType().getDescription().replaceAll("[\\r\\n]+", " "));
            stackPane.setVisible(!stackPane.isVisible());
        });
        this.stackPane.setOnMousePressed(e -> {
            stackPane.setVisible(!stackPane.isVisible());
        });

        this.getChildren().add(stackPane);
        this.getChildren().add(token1);
        this.getChildren().add(token2);
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
        token1.resizeRelocate(2 * border, border, height, height);
        token2.resizeRelocate(2 * border, getHeight() - height - border, height, height);
        stackPane.resizeRelocate(border, border, getWidth() - 2 * border, getHeight() - 2 * border);
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

    public Integer getScore1() {
        return score1.get();
    }

    public ObjectProperty<Integer> score1Property() {
        return score1;
    }

    public void setScore1(Integer score1) {
        this.score1.set(score1);
    }

    public Integer getScore2() {
        return score2.get();
    }

    public ObjectProperty<Integer> score2Property() {
        return score2;
    }

    public void setScore2(Integer score2) {
        this.score2.set(score2);
    }
}
