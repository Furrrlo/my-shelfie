package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.PlayerView;
import it.polimi.ingsw.model.Provider;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.TileAndCoords;
import org.jetbrains.annotations.Nullable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

/**
 * Base component for local and remote players
 *
 * @see ShelfieComponent
 */
abstract class PlayerShelfieComponent extends Pane {

    private final NickLabel label;
    private final ShelfieComponent shelfieComponent;
    private final Pane chair;

    public PlayerShelfieComponent(FxResourcesLoader resources,
                                  PlayerView player,
                                  boolean mouseTransparent,
                                  boolean showScore) {
        getChildren().add(this.shelfieComponent = new ShelfieComponent(resources, player.getShelfie()));

        setMouseTransparent(mouseTransparent);

        getChildren().add(this.label = new NickLabel(player.getNick(), player.score(), showScore));

        final var chairImgView = new ImageView(resources.loadImage("assets/misc/firstplayertoken.png"));
        getChildren().add(this.chair = new AnchorPane(chairImgView));
        AnchorPane.setTopAnchor(chairImgView, 0.0);
        AnchorPane.setBottomAnchor(chairImgView, 0.0);
        AnchorPane.setLeftAnchor(chairImgView, 0.0);
        AnchorPane.setRightAnchor(chairImgView, 0.0);
        chairImgView.fitWidthProperty().bind(chair.widthProperty());
        chairImgView.fitHeightProperty().bind(chair.heightProperty());

        this.chair.setVisible(player.isStartingPlayer());
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 180d, getHeight() / 194d);

        double labelWidth = Math.min(300, getWidth() - 2 * 28 * scale);
        this.label.resizeRelocate((getWidth() - labelWidth) / 2d, 0, labelWidth, Math.min(30, 21 * scale));

        double shelfieOffsetY = Math.min(20, 14 * scale);
        double shelfieWidth = 180d * scale;
        this.shelfieComponent.resizeRelocate((getWidth() - shelfieWidth) / 2, shelfieOffsetY, shelfieWidth,
                getHeight() - shelfieOffsetY);

        double chairWidth = 35 * scale, chairHeight = 33 * scale;
        this.chair.resizeRelocate(getWidth() - chairWidth, getHeight() - chairHeight, chairWidth, chairHeight);
    }

    public void setTilesLowOpacity() {
        this.shelfieComponent.setLowOpacity();
    }

    public void restoreTilesOpacity() {
        this.shelfieComponent.restoreOpacity();
    }

    public boolean isColumnSelectionMode() {
        return shelfieComponent.isColumnSelectionMode();
    }

    public BooleanProperty columnSelectionModeProperty() {
        return shelfieComponent.columnSelectionModeProperty();
    }

    public void setColumnSelectionMode(boolean columnSelectionMode) {
        shelfieComponent.setColumnSelectionMode(columnSelectionMode);
    }

    public IntPredicate getIsColumnSelectable() {
        return shelfieComponent.getIsColumnSelectable();
    }

    public ObjectProperty<IntPredicate> isColumnSelectableProperty() {
        return shelfieComponent.isColumnSelectableProperty();
    }

    public void setIsColumnSelectable(IntPredicate isColumnSelectable) {
        shelfieComponent.setIsColumnSelectable(isColumnSelectable);
    }

    public @Nullable Consumer<TileAndCoords<@Nullable Tile>> getOnTileAction() {
        return shelfieComponent.getOnTileAction();
    }

    public ObjectProperty<@Nullable Consumer<TileAndCoords<@Nullable Tile>>> onTileActionProperty() {
        return shelfieComponent.onTileActionProperty();
    }

    public void setOnTileAction(@Nullable Consumer<TileAndCoords<@Nullable Tile>> onTileAction) {
        shelfieComponent.setOnTileAction(onTileAction);
    }

    private static class NickLabel extends HBox {

        private final Label label;

        public NickLabel(String nick, Provider<Integer> score, boolean showScore) {
            backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    new Insets(1)))));
            borderProperty().bind(widthProperty().map(width -> new Border(new BorderStroke(
                    Color.rgb(129, 33, 0),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                    BorderStroke.THICK))));

            setAlignment(Pos.CENTER);

            label = new Label();
            if (showScore)
                label.textProperty().bind(FxProperties.toFxProperty("score", this, score).map(s -> nick + ": " + s + " pt"));
            else
                label.setText(nick);
            getChildren().add(label);
        }

        @Override
        protected void layoutChildren() {
            Fonts.changeSize(label.fontProperty(), 12d * getHeight() / 28d);
            super.layoutChildren();
        }
    }
}
