package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class AdjacentItemTilesDescription extends AnchorPane {
    private final Text title = new Text("Adjacent Item Tiles");
    private final Text description = new Text(
            "Groups of adjacent tiles of the same type on your shelfie " +
                    "grant points depending on how many tiles are connected.\n" +
                    "3 tiles gives 2 pts, 4 gives 3 pts, 5 gives 5 pts and 6 or more gives 8 pts");

    public AdjacentItemTilesDescription() {
        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTGRAY,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0)))));
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD, Font.getDefault().getSize()));
        description.setTextAlignment(TextAlignment.CENTER);

        getChildren().add(title);
        getChildren().add(description);

    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double scale = Math.min(getWidth() / 221d, getHeight() / 156d);
        double titleHeight = 20 * scale;
        double textHeight = 17 * scale;
        double border = 6 * scale;

        description.setWrappingWidth(getWidth() - 2 * border);
        description.setFont(Font.font(textHeight));
        title.setWrappingWidth(getWidth() - 2 * border);
        title.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD, titleHeight));

        title.resizeRelocate(border, 2 * border, getWidth() - 2 * border, titleHeight);
        description.resizeRelocate(border, 4 * border + titleHeight, getWidth() - 2 * border,
                getHeight() - 4 * border - titleHeight);
    }

}
