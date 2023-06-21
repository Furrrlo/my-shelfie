package it.polimi.ingsw.client.javafx;

import javafx.geometry.Orientation;
import javafx.scene.layout.Pane;

/**
 * Pane component which lays out children in its center and tries to maximize their size,
 * but still fits them to its layout bounds
 */
class CenteringFitPane extends Pane {

    @Override
    protected void layoutChildren() {
        for (var child : getManagedChildren()) {
            var insets = getInsets();
            var contentWidth = getWidth() - insets.getLeft() - insets.getRight();
            var contentHeight = getHeight() - insets.getTop() - insets.getBottom();

            double w;
            double h;
            if (child.getContentBias() == Orientation.VERTICAL) {
                w = contentWidth;
                h = child.prefHeight(w);

                if (h > contentHeight) {
                    h = contentHeight;
                    w = child.prefWidth(h);
                }
            } else {
                h = contentHeight;
                w = child.prefWidth(h);

                if (w > contentWidth) {
                    w = contentWidth;
                    h = child.prefHeight(w);
                }
            }

            child.resizeRelocate(
                    insets.getLeft() + (contentWidth - w) / 2.0,
                    insets.getTop() + (contentHeight - h) / 2.0,
                    w, h);
        }
    }
}
