package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.UserMessage;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

import static it.polimi.ingsw.client.javafx.FxProperties.compositeObservableValue;

public class ChatComponent extends VBox {

    private final ChatScrollComponent chatScrollComponent;
    public final static int INSET = 15;

    public ChatComponent(List<String> recipients, String thePlayer, GameController controller) {
        //setBackground(Background.fill(Color.WHITE));
        setSpacing(20);

        this.chatScrollComponent = new ChatScrollComponent(thePlayer);
        chatScrollComponent.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(15, 15 * (width.doubleValue() / 210d))),
                new Insets(-5)))));

        ChoiceBox<String> recipient = new ChoiceBox<>();
        recipient.getItems().add(UserMessage.EVERYONE_RECIPIENT);
        recipient.getItems().addAll(recipients);
        recipient.getSelectionModel().selectFirst();
        recipient.prefWidthProperty().bind(chatScrollComponent.widthProperty());
        recipient.minWidth(USE_PREF_SIZE);
        recipient.maxWidth(USE_PREF_SIZE);
        recipient.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(15, 15 * (width.doubleValue() / 210d))),
                new Insets(-5)))));

        TextArea text = new TextArea();

        Button send = new Button();
        send.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(50, 50 * (width.doubleValue() / 210d))),
                new Insets(-5)))));

        var imgView = new ImageView(new Image(FxResources.getResourceAsStream("fa/paper-plane.png")));
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(20);
        imgView.setFitHeight(20);
        send.setGraphic(imgView);

        send.setOnAction(event -> {
            try {
                String message = text.getText();
                if (message.equals(""))
                    throw new IllegalArgumentException();
                String nickRecipient = recipient.getValue();
                if (!recipients.contains(nickRecipient) && !nickRecipient.equals(UserMessage.EVERYONE_RECIPIENT))
                    throw new IllegalArgumentException("There is no such a player");

                controller.sendMessage(message, nickRecipient);

                //restores TextArea after message is sent
                text.setText("");
            } catch (DisconnectedException e) {
                throw new RuntimeException(e);
            }
        });

        var hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.setPadding(Insets.EMPTY);
        hbox.setSpacing(15);
        hbox.getChildren().add(text);
        text.prefWidthProperty().bind(hbox.widthProperty().subtract(imgView.getFitWidth()));
        text.setBackground(Background.EMPTY);
        text.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(15, 15 * (width.doubleValue() / 210d))),
                new Insets(-5)))));
        text.minWidth(USE_PREF_SIZE);
        text.maxWidth(USE_PREF_SIZE);
        hbox.getChildren().add(send);

        setVgrow(chatScrollComponent, Priority.SOMETIMES);
        getChildren().add(chatScrollComponent);
        getChildren().add(recipient);
        setVgrow(hbox, Priority.ALWAYS);
        text.setWrapText(true);

        text.prefRowCountProperty()
                .bind(compositeObservableValue(text.fontProperty(), text.textProperty(), text.widthProperty()).map(i -> {
                    var textComponent = new Text("");
                    textComponent.setFont(text.getFont());
                    var singleLineHeight = textComponent.getLayoutBounds().getHeight();

                    textComponent.setText(text.getText());
                    textComponent.setWrappingWidth(text.getWidth());
                    var wrappedHeight = textComponent.getLayoutBounds().getHeight();
                    var rows = (int) Math.round(wrappedHeight / singleLineHeight);
                    return Math.min(4, rows);
                }));
        hbox.setMinHeight(USE_PREF_SIZE);
        hbox.setMaxHeight(USE_PREF_SIZE);
        getChildren().add(hbox);
    }

    public List<UserMessage> getMessages() {
        return chatScrollComponent.getMessages();
    }

    public ObjectProperty<List<UserMessage>> messagesProperty() {
        return chatScrollComponent.messagesProperty();
    }

    public void setMessages(List<UserMessage> messages) {
        chatScrollComponent.setMessages(messages);
    }
}
