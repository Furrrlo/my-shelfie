package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

class ChatButton extends InGameButton {

    private final ImageView openedMail;
    private final ImageView closedMail;

    public ChatButton(FxResourcesLoader resources) {
        super("Chat", Color.LIGHTSEAGREEN);

        backgroundRadiusProperty().bind(widthProperty()
                .map(w -> new CornerRadii(Math.min(10, 10 * (w.doubleValue() / 210d)))));
        setBackgroundInsets(new Insets(0));

        Fonts.changeWeight(fontProperty(), FontWeight.BOLD);

        closedMail = new ImageView(resources.loadImage("fa/message.png"));
        closedMail.setPreserveRatio(true);
        openedMail = new ImageView(resources.loadImage("fa/open-message.png"));
        openedMail.setPreserveRatio(true);

        setGraphic(closedMail);
        setContentDisplay(ContentDisplay.RIGHT);
    }

    @Override
    protected void layoutChildren() {
        double scale = Math.min(getWidth() / 115d, getHeight() / 46d);

        closedMail.setFitWidth(30 * scale);
        openedMail.setFitHeight(30 * scale);

        Fonts.changeSize(fontProperty(), 14 * scale);

        super.layoutChildren();
    }

    public void swap() {
        boolean isOpen = getGraphic().equals(closedMail);
        setGraphic(isOpen ? openedMail : closedMail);
    }
}
