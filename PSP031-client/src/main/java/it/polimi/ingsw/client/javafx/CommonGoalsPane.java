package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.CommonGoalView;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Pane containing common goal cards and score tokens
 */
class CommonGoalsPane extends Pane {

    private final StackPane textBackground;
    private final Text description;
    private final CommonGoalComponent goal1;

    private final ObjectProperty<Integer> score1;
    private final ScoringTokenComponent token1;
    private final CommonGoalComponent goal2;

    private final ObjectProperty<Integer> score2;
    private final ScoringTokenComponent token2;
    private final String text1;
    private final String text2;

    public CommonGoalsPane(FxResourcesLoader resources, CommonGoalView goal1, CommonGoalView goal2, int cg1, int cg2) {
        this.text1 = goal1.getType().getDescription().replaceAll("[\\r\\n]+", " ");
        this.text2 = goal2.getType().getDescription().replaceAll("[\\r\\n]+", " ");

        this.score1 = new SimpleObjectProperty<>(this, "score1", cg1);
        this.score2 = new SimpleObjectProperty<>(this, "score2", cg2);
        this.token1 = new ScoringTokenComponent(resources, score1);
        this.token2 = new ScoringTokenComponent(resources, score2);

        setBackground(new Background(new BackgroundImage(
                resources.loadImage("assets/misc/base_pagina2.jpg"),
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

        this.textBackground = new StackPane();
        textBackground.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(5, 5 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        textBackground.setOpacity(0.9);
        textBackground.setVisible(false);

        this.description = new Text();
        //description.setPadding(new Insets(6));
        //stackPane.getChildren().add(description);
        //description.setWrapText(true);
        //stackPane.setVisible(false);
        description.setTextAlignment(TextAlignment.CENTER);
        //description.setTextOrigin(VPos.CENTER);

        getChildren().add(this.goal1 = new CommonGoalComponent(resources, goal1));
        getChildren().add(this.goal2 = new CommonGoalComponent(resources, goal2));

        EventHandler<ActionEvent> handler = e -> {
            description.setText("The common goal cards grant points to the players who achieve the pattern.\n" +
                    "The first player to achieve it gets 8 point, 6 the second, 4 the third and 2 the last");
            textBackground.setVisible(!textBackground.isVisible());
            //description.setVisible(!description.isVisible());
        };
        this.token1.setOnAction(handler);
        this.token2.setOnAction(handler);
        this.goal1.next.setOnAction(handler);
        this.goal2.next.setOnAction(handler);

        this.textBackground.setOnMousePressed(e -> {
            textBackground.setVisible(!textBackground.isVisible());
            //description.setVisible(!description.isVisible());
        });

        this.getChildren().add(token1);
        this.getChildren().add(token2);
        this.getChildren().add(textBackground);
        this.textBackground.getChildren().add(description);
        StackPane.setAlignment(description, Pos.CENTER);
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 221d, getHeight() / 156d);
        double border = 6 * scale;
        double width = 113 * scale;
        double height = 74 * scale;

        int rows = (int) (description.getText().length() * 5.7 * scale / (getWidth() - 2 * border));
        double textH = 14 * scale * rows;

        description.setTextOrigin(VPos.CENTER);

        Fonts.changeSize(description.fontProperty(), 13 * scale);
        description.setWrappingWidth(getWidth() - 4 * border);

        goal1.resizeRelocate(getWidth() - width - border, border, width, height);
        goal2.resizeRelocate(getWidth() - width - border, getHeight() - height - border, width, height);
        token1.resizeRelocate(2 * border, border, height, height);
        token2.resizeRelocate(2 * border, getHeight() - height - border, height, height);
        textBackground.resizeRelocate(border, border, getWidth() - 2 * border, getHeight() - 2 * border);
    }

    private static class CommonGoalComponent extends AnchorPane {
        private ImageButton card;
        private ScoringTokenComponent next;

        public CommonGoalComponent(FxResourcesLoader resources, CommonGoalView commonGoal) {
            getChildren().add(card = new ImageButton());
            card.setImage(resources.loadImage("assets/common goal cards/" + switch (commonGoal.getType()) {
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
            } + ".jpg"));
            setBottomAnchor(card, 0d);
            setTopAnchor(card, 0d);
            setLeftAnchor(card, 0d);
            setRightAnchor(card, 0d);

            getChildren().add(next = new ScoringTokenComponent(resources,
                    FxProperties.toFxProperty("achieved", this, commonGoal.achieved())
                            .map(achieved -> switch (achieved.size()) {
                                case 0 -> 8;
                                case 1 -> 6;
                                case 2 -> 4;
                                case 3 -> 2;
                                default -> 0;
                            })));
            next.setRotate(-7.5);
            next.setManaged(false);

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

        @Override
        protected void layoutChildren() {
            super.layoutChildren();

            double scale = Math.min(getWidth() / 221d, getHeight() / 156d);
            next.resizeRelocate(140 * scale, 38 * scale, 68 * scale, 68 * scale);
        }
    }

    public void setCommonGoal1Action(EventHandler<ActionEvent> value) {
        this.goal1.card.setOnAction(value);
    }

    public void setCommonGoal2Action(EventHandler<ActionEvent> value) {
        this.goal2.card.setOnAction(value);
    }

    public int getDescriptionNodeIndex() {
        return this.getChildren().indexOf(textBackground);
    }

    public void setDescriptionVisible(boolean visible, int index) {
        this.textBackground.setVisible(visible);
        if (visible) {
            if (index == 1)
                this.description.setText(text1);
            else
                this.description.setText(text2);
        }
    }

    public boolean getDescriptionVisible() {
        return this.textBackground.isVisible();
    }

    public void setScore1(Integer score1) {
        this.score1.set(score1);
    }

    public void setScore2(Integer score2) {
        this.score2.set(score2);
    }
}
