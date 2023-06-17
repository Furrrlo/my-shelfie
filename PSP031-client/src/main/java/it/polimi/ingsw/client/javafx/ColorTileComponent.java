package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.shape.Rectangle;

public class ColorTileComponent extends AnchorPane {

    public ColorTileComponent(FxResourcesLoader resources, @Nullable Tile tile) {
        this(resources, tile == null ? null : tile.getColor());
    }

    public ColorTileComponent(FxResourcesLoader resources, @Nullable Color color) {
        if (color != null) {
            var resourcePath = "assets/personal goal cards/Personal_Goals.png";
            final Image croppedTilePic = switch (color) {
                case PINK -> resources.loadCroppedImage(resourcePath, 110, 105, 198, 198);
                case GREEN -> resources.loadCroppedImage(resourcePath, 1058, 334, 198, 198);
                case YELLOW -> resources.loadCroppedImage(resourcePath, 347, 792, 198, 198);
                case BLUE -> resources.loadCroppedImage(resourcePath, 586, 105, 198, 198);
                case WHITE -> resources.loadCroppedImage(resourcePath, 822, 562, 198, 198);
                case LIGHTBLUE -> resources.loadCroppedImage(resourcePath, 586, 1249, 198, 198);
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
