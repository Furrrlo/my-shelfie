package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Tile;
import org.jetbrains.annotations.Nullable;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PickedTilesPane extends Pane {

    private final ListProperty<@Nullable Tile> tiles = new SimpleListProperty<>(this, "tiles");
    private final TileComponent tile1;
    private final TileBorder tile1Border;
    private final TileComponent tile2;
    private final TileBorder tile2Border;
    private final TileComponent tile3;
    private final TileBorder tile3Border;

    public PickedTilesPane() {
        setBackground(new Background(new BackgroundImage(
                new Image(FxResources.getResourceAsStream("assets/misc/base_pagina2.jpg")),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
            clipProperty().bind(layoutBoundsProperty().map(bounds -> {
                var radius = Math.min(20, 20 * bounds.getWidth() / 114.0);
                Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
                clip.setArcWidth(radius);
                clip.setArcHeight(radius);
                return clip;
            }));
        }

        getChildren().add(this.tile1Border = new TileBorder());
        getChildren().add(this.tile1 = new TileComponent());
        this.tile1.tileProperty().bind(tiles.map(tiles -> tiles.size() >= 1 ? tiles.get(0) : null));

        getChildren().add(this.tile2Border = new TileBorder());
        getChildren().add(this.tile2 = new TileComponent());
        this.tile2.tileProperty().bind(tiles.map(tiles -> tiles.size() >= 2 ? tiles.get(1) : null));

        getChildren().add(this.tile3Border = new TileBorder());
        getChildren().add(this.tile3 = new TileComponent());
        this.tile3.tileProperty().bind(tiles.map(tiles -> tiles.size() >= 3 ? tiles.get(2) : null));
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double scale = getHeight() / 114.0;
        double border = 6 * scale;
        double tileSize = getHeight() - 4 * border;

        double x = (getWidth() - ((border + tileSize + border) * 3 + 2 * border)) / 2.0;
        tile1Border.resizeRelocate(x, border, tileSize + 2 * border, tileSize + 2 * border);
        tile1.resizeRelocate(x + border, 2 * border, tileSize, tileSize);

        x += border + tileSize + border + border;
        tile2Border.resizeRelocate(x, border, tileSize + 2 * border, tileSize + 2 * border);
        tile2.resizeRelocate(x + border, 2 * border, tileSize, tileSize);

        x += border + tileSize + border + border;
        tile3Border.resizeRelocate(x, border, tileSize + 2 * border, tileSize + 2 * border);
        tile3.resizeRelocate(x + border, 2 * border, tileSize, tileSize);
    }

    public ObservableList<Tile> getTiles() {
        return tiles.get();
    }

    public ListProperty<Tile> tilesProperty() {
        return tiles;
    }

    private static class TileBorder extends Pane {

        public TileBorder() {
            setBackground(Background.EMPTY);
            borderProperty().bind(layoutBoundsProperty().map(bounds -> new Border(new BorderStroke(
                    Color.GRAY,
                    BorderStrokeStyle.DASHED,
                    new CornerRadii(Math.min(20, 20 * bounds.getHeight() / 102d)),
                    BorderStroke.MEDIUM))));
        }
    }
}
