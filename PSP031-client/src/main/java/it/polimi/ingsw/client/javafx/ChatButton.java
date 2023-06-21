package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

public class ChatButton extends InGameButton {

    private final ImageView openedMail;
    private final ImageView closedMail;
    private final String open;
    private final String close;

    public ChatButton(FxResourcesLoader resources) {
        super(Color.LIGHTSEAGREEN);

        backgroundRadiusProperty().bind(widthProperty()
                .map(w -> new CornerRadii(Math.min(10, 10 * (w.doubleValue() / 210d)))));
        setBackgroundInsets(new Insets(0));

        open = "Show chat";
        close = "Close chat";

        setText(open);
        Fonts.enforceWeight(fontProperty(), FontWeight.BOLD);

        closedMail = new ImageView(resources.loadImage("fa/message.png"));
        closedMail.setPreserveRatio(true);
        openedMail = new ImageView(resources.loadImage("fa/open-message.png"));
        openedMail.setPreserveRatio(true);

        setGraphic(closedMail);
        setContentDisplay(ContentDisplay.RIGHT);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double scale = Math.min(getWidth() / 115d, getHeight() / 46d);

        closedMail.setFitWidth(30 * scale);
        openedMail.setFitHeight(30 * scale);

        Fonts.changeSize(fontProperty(), 14 * scale);
    }

    public void swap() {
        boolean isOpen = getText().equals(open);
        setText(isOpen ? close : open);
        setGraphic(isOpen ? openedMail : closedMail);
    }
}
