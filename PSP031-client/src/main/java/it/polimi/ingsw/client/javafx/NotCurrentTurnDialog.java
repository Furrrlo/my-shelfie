package it.polimi.ingsw.client.javafx;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Message dialog warning you that it is not your turn
 */
class NotCurrentTurnDialog extends DialogVbox {

    private final Button ok;

    public NotCurrentTurnDialog() {
        super("Not your turn",
                Color.LIGHTGRAY, Color.LIGHTSEAGREEN,
                "It's not your current turn, please wait when is your turn before you can " +
                        "select tiles from board again");

        //HBox hbox = new HBox();
        //hbox.prefWidthProperty().bind(widthProperty());

        this.ok = new DialogButton("ok", Color.LIGHTSEAGREEN);
        ok.setOnMouseClicked(e -> this.setVisible(false));

        this.getChildren().add(ok);

        //hbox.getChildren().add(ok);
        //hbox.setAlignment(Pos.CENTER_RIGHT);
        //this.getChildren().add(hbox);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double scale = Math.min(getWidth() / 230d, getHeight() / 230d);

        this.setBorder(new Border(new BorderStroke(Color.LIGHTSEAGREEN,
                BorderStrokeStyle.SOLID, new CornerRadii(Math.min(10, 10 * (getWidth() / 230d))), BorderWidths.DEFAULT)));

        double border = 5 * scale;
        double width = getWidth() / 2 - 2 * border;
        double text_height = 14 * scale;
        double button_height = 28 * scale;

        Fonts.changeSize(ok.fontProperty(), text_height);
        Fonts.changeWeight(ok.fontProperty(), FontWeight.BOLD);

        this.ok.resizeRelocate(3 * border + width, getHeight() - button_height - border, getWidth() / 2 - 2 * border,
                button_height);
    }
}
