package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.UserMessage;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

import static it.polimi.ingsw.client.javafx.FxProperties.compositeObservableValue;

public class ChatComponent extends VBox {

    private final ChatScrollComponent chatScrollComponent;
    public final static int INSET = 10;
    public final static int SPACING = INSET * 3 / 2;
    public final static double RADIUS = INSET - (double) INSET / 4;
    public final static int INNER_INSET = INSET / 2;

    public ChatComponent(FxResourcesLoader resources,
                         List<String> recipients,
                         String thePlayer,
                         GameController controller,
                         Consumer<Throwable> onDisconnect) {
        setSpacing(SPACING);

        this.chatScrollComponent = new ChatScrollComponent(thePlayer);
        chatScrollComponent.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(RADIUS, RADIUS * (width.doubleValue() / 210d))),
                new Insets(-INNER_INSET)))));

        ChoiceBox<String> recipient = new ChoiceBox<>();
        recipient.getItems().add(UserMessage.EVERYONE_RECIPIENT);
        recipient.getItems().addAll(recipients);
        recipient.getSelectionModel().selectFirst();
        recipient.prefWidthProperty().bind(chatScrollComponent.widthProperty());
        recipient.minWidth(USE_PREF_SIZE);
        recipient.maxWidth(USE_PREF_SIZE);
        recipient.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.LIGHTSEAGREEN,
                new CornerRadii(Math.min(RADIUS, RADIUS * (width.doubleValue() / 210d))),
                new Insets(-INNER_INSET)))));

        TextArea text = new TextArea();

        InGameButton send = new InGameButton(Color.LIGHTSEAGREEN);
        send.backgroundRadiusProperty().bind(parentProperty()
                .flatMap(p -> p instanceof Region r ? r.widthProperty() : null)
                .map(w -> new CornerRadii(Math.min(50, 50 * (w.doubleValue() / 210d)))));
        send.setBackgroundInsets(new Insets(-INNER_INSET));

        var imgView = new ImageView(resources.loadImage("fa/paper-plane.png"));
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
                onDisconnect.accept(e);
            }
        });

        var hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.setPadding(Insets.EMPTY);
        hbox.setSpacing(SPACING);
        hbox.getChildren().add(text);
        text.prefWidthProperty().bind(hbox.widthProperty().subtract(imgView.getFitWidth()));
        text.setBackground(Background.EMPTY);
        text.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(RADIUS, RADIUS * (width.doubleValue() / 210d))),
                new Insets(-INNER_INSET)))));
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
