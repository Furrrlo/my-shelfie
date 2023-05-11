package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class TileComponent extends Button {

    /** Used instead of null for mapping the tile property, as otherwise the map method discards null tiles */
    private static final Tile NULL_TILE = new Tile(Color.YELLOW);

    private final ObjectProperty<@Nullable Tile> tile = new SimpleObjectProperty<>(this, "tile");

    public TileComponent() {
        this(null);
    }

    public TileComponent(@Nullable Tile tile) {
        this.tile.set(tile);
        this.backgroundProperty().bind(tileProperty().orElse(NULL_TILE).map(t -> new Background(new BackgroundImage(
                new Image(FxResources.getResourceAsStream("assets/item tiles/Cornici1.1.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, false, Side.TOP, 0, false),
                new BackgroundSize(100, 100, true, true, false, true)))));
        this.textProperty().set("");
        this.ellipsisStringProperty().set("");
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
