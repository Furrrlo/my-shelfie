package it.polimi.ingsw.client.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ChatButton extends HBox {
    private final ImageView openedMail;
    private final ImageView closedMail;
    private final Text open;
    private final Text close;

    public ChatButton() {
        setAlignment(Pos.CENTER);
        setSpacing(5);

        backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(10, 10 * (width.doubleValue() / 210d))),
                new Insets(0)))));

        open = new Text("Show chat");
        close = new Text("Close chat");

        open.setTextAlignment(TextAlignment.CENTER);
        close.setTextAlignment(TextAlignment.CENTER);

        close.setVisible(false);
        getChildren().add(open);
        getChildren().add(close);

        closedMail = new ImageView(new Image(FxResources.getResourceAsStream("fa/message.png")));
        openedMail = new ImageView(new Image(FxResources.getResourceAsStream("fa/open-message.png")));
        openedMail.setVisible(false);

        closedMail.setPreserveRatio(true);
        openedMail.setPreserveRatio(true);

        getChildren().add(closedMail);
        getChildren().add(openedMail);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double scale = Math.min(getWidth() / 115d, getHeight() / 46d);
        double border = 6 * scale;

        closedMail.setFitWidth(30 * scale);
        openedMail.setFitHeight(30 * scale);

        open.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD, 14 * scale));
        close.setFont(Font.font(Font.getDefault().getName(), FontWeight.EXTRA_BOLD, 14 * scale));

        open.resizeRelocate(7 * scale, (getHeight() - 15 * scale) / 2, getWidth() / 2, getHeight());
        close.resizeRelocate(7 * scale, (getHeight() - 15 * scale) / 2, getWidth() / 2, getHeight());
        openedMail.resizeRelocate(getWidth() / 2 + 3 * border, (getHeight() - 30 * scale) / 2, getWidth(), getHeight());
        closedMail.resizeRelocate(getWidth() / 2 + 3 * border, (getHeight() - 30 * scale) / 2, getWidth(), getHeight());

    }

    public void swap() {
        open.setVisible(!open.isVisible());
        close.setVisible(!close.isVisible());
        openedMail.setVisible(!openedMail.isVisible());
        closedMail.setVisible(!closedMail.isVisible());
    }
}
