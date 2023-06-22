package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Component representing a description of the first finisher token
 */
class FirstFinisherDescription extends Pane {
    private final ImageView achieved;
    private final Text description = new Text(
            "The first player who completely fills their bookshelf scores 1 additional point," +
                    " and receives the end game token:");

    public FirstFinisherDescription(FxResourcesLoader resources) {
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0)))));

        description.setTextAlignment(TextAlignment.CENTER);

        this.achieved = new ImageView(resources.loadImage("assets/scoring tokens/end game.jpg"));
        achieved.setPreserveRatio(true);

        this.getChildren().add(description);
        this.getChildren().add(achieved);
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 221d, getHeight() / 156d);
        double border = 6 * scale;
        double middleW = getWidth() / 2;
        double middleH = getHeight() / 2;
        double width = getWidth() / 3;
        double text_height = 16 * scale;

        achieved.setFitWidth(width);
        description.setWrappingWidth(getWidth() - 2 * border);
        Fonts.changeSize(description.fontProperty(), text_height);

        description.resizeRelocate(border, border, getWidth() - 2 * border, text_height);
        achieved.resizeRelocate(middleW - width / 2, middleH + 3 * border, width, width);
    }
}
