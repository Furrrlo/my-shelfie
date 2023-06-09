package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.shape.Rectangle;

public class ColorTileComponent extends AnchorPane {

    public ColorTileComponent(@Nullable Tile tile) {
        this(tile == null ? null : tile.getColor());
    }

    public ColorTileComponent(@Nullable Color color) {
        if (color != null) {
            final PixelReader reader = new Image(
                    FxResources.getResourceAsStream("assets/personal goal cards/Personal_Goals.png")).getPixelReader();
            final Image croppedTilePic = switch (color) {
                case PINK -> new WritableImage(reader, 110, 105, 198, 198);
                case GREEN -> new WritableImage(reader, 1058, 334, 198, 198);
                case YELLOW -> new WritableImage(reader, 347, 792, 198, 198);
                case BLUE -> new WritableImage(reader, 586, 105, 198, 198);
                case WHITE -> new WritableImage(reader, 822, 562, 198, 198);
                case LIGHTBLUE -> new WritableImage(reader, 586, 1249, 198, 198);
            };
            final ImageView tilePic = new ImageView(croppedTilePic);
            tilePic.fitWidthProperty().bind(widthProperty());
            tilePic.fitHeightProperty().bind(heightProperty());
            getChildren().add(tilePic);
        } else {
            setBackground(Background.EMPTY);
        }

        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
            clipProperty().bind(layoutBoundsProperty().map(bounds -> {
                var radius = Math.min(20, 10 * Math.min(bounds.getWidth() / 45d, bounds.getHeight() / 45d));
                Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
                clip.setArcWidth(radius);
                clip.setArcHeight(radius);
                return clip;
            }));
        }
    }
}
