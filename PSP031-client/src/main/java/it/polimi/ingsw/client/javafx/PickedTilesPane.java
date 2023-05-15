package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TileAndCoords;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Collections;
import java.util.function.BiConsumer;

public class PickedTilesPane extends Pane {

    private final ListProperty<TileAndCoords<Tile>> tiles = new SimpleListProperty<>(this, "tiles");
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
        this.tile1.tileProperty().bind(tiles.map(tiles -> tiles.size() >= 1 ? tiles.get(0).tile() : null));
        this.tile1.setOnAction(e -> {
            if (tiles.size() >= 1)
                tiles.remove(0);
        });

        getChildren().add(this.tile2Border = new TileBorder());
        getChildren().add(this.tile2 = new TileComponent());
        this.tile2.tileProperty().bind(tiles.map(tiles -> tiles.size() >= 2 ? tiles.get(1).tile() : null));
        this.tile2.setOnAction(e -> {
            if (tiles.size() >= 2)
                tiles.remove(1);
        });

        getChildren().add(this.tile3Border = new TileBorder());
        getChildren().add(this.tile3 = new TileComponent());
        this.tile3.tileProperty().bind(tiles.map(tiles -> tiles.size() >= 3 ? tiles.get(2).tile() : null));
        this.tile3.setOnAction(e -> {
            if (tiles.size() >= 3)
                tiles.remove(2);
        });

        var tileDataFormat = new DataFormat("my-shelfie/swap-tile-n-coords");
        BiConsumer<TileComponent, Integer> installDnD = (component, tileIdx) -> {
            var tileVal = tiles.map(tiles -> tiles.size() >= tileIdx + 1 ? tiles.get(tileIdx).tile() : null);
            component.setOnDragDetected(event -> {
                var tile = tileVal.getValue();
                if (tile == null)
                    return;

                // Snapshot the tile to get an image with the correct dimensions
                var imgView = new ImageView(component.getImage());
                imgView.setSmooth(false);
                imgView.setPreserveRatio(true);
                imgView.setFitWidth(component.getWidth());
                imgView.setFitHeight(component.getHeight());
                var img = new WritableImage((int) component.getWidth(), (int) component.getHeight());
                imgView.snapshot(null, img);

                Dragboard db = component.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.put(tileDataFormat, tileIdx);
                content.putImage(img);
                db.setContent(content);
                event.consume();
            });
            component.setOnDragOver(event -> {
                var tile = tileVal.getValue();
                if (tile != null && event.getGestureSource() != component && event.getDragboard().hasContent(tileDataFormat))
                    event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            });
            component.setOnDragDropped(event -> {
                boolean success = false;

                Dragboard db = event.getDragboard();
                if (event.getDragboard().hasContent(tileDataFormat)) {
                    var toReceive_ = db.getContent(tileDataFormat);
                    if (toReceive_ instanceof Integer toReceive) {
                        Collections.swap(tiles, toReceive, tileIdx);
                        success = true;
                    }
                }

                event.setDropCompleted(success);
                event.consume();
            });
        };

        installDnD.accept(tile1, 0);
        installDnD.accept(tile2, 1);
        installDnD.accept(tile3, 2);
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

    public ObservableList<TileAndCoords<Tile>> getTiles() {
        return tiles.get();
    }

    public ListProperty<TileAndCoords<Tile>> tilesProperty() {
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