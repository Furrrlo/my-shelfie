package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Rectangle;

/**
 * ImageButton representing a tile.
 */
public class TileComponent extends ImageButton {

    /** Used instead of null for mapping the tile property, as otherwise the map method discards null tiles */
    private static final Tile NULL_TILE = new Tile(it.polimi.ingsw.model.Color.YELLOW);

    private final ObjectProperty<@Nullable Tile> tile = new SimpleObjectProperty<>(this, "tile");

    public TileComponent(@NamedArg(FxResourcesLoader.NAMED_ARG_NAME) FxResourcesLoader resources) {
        this(resources, null);
    }

    public TileComponent(FxResourcesLoader resources, @Nullable Tile tile) {
        this.tile.set(tile);

        imageProperty().bind(tileProperty().orElse(NULL_TILE).map(ignored -> {
            var currTile = this.tile.get();
            return currTile == null ? null : resources.loadImage(switch (currTile.getColor()) {
                case GREEN -> "assets/item tiles/Gatti1." + (currTile.getPicIndex() + 1) + ".png";
                case WHITE -> "assets/item tiles/Libri1." + (currTile.getPicIndex() + 1) + ".png";
                case YELLOW -> "assets/item tiles/Giochi1." + (currTile.getPicIndex() + 1) + ".png";
                case BLUE -> "assets/item tiles/Cornici1." + (currTile.getPicIndex() + 1) + ".png";
                case LIGHTBLUE -> "assets/item tiles/Trofei1." + (currTile.getPicIndex() + 1) + ".png";
                case PINK -> "assets/item tiles/Piante1." + (currTile.getPicIndex() + 1) + ".png";
            });
        }));

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

    public @Nullable Tile getTile() {
        return tile.get();
    }

    public Property<@Nullable Tile> tileProperty() {
        return tile;
    }

    public void setTile(@Nullable Tile tile) {
        this.tile.set(tile);
    }
}
