package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.DisconnectedException;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.UserMessage;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static it.polimi.ingsw.client.javafx.FxProperties.compositeObservableValue;

/**
 * VBox containing the {@link ChatScrollComponent} and components needed to send a message
 */
class ChatComponent extends VBox {

    private final ChatScrollComponent chatScrollComponent;
    public final static int INSET = 10;
    public final static int SPACING = INSET * 3 / 2;
    public final static double RADIUS = INSET - (double) INSET / 4;
    public final static int INNER_INSET = INSET / 2;

    public ChatComponent(FxResourcesLoader resources,
                         ExecutorService threadPool,
                         List<String> recipients,
                         String thePlayer,
                         GameController controller,
                         Consumer<Throwable> onDisconnect) {
        InGameChoiceBox<String> recipient = new InGameChoiceBox<>();
        TextArea text = new TextArea();
        InGameButton send = new InGameButton(Color.LIGHTSEAGREEN);

        var everyoneRecipientLabel = "Everyone";
        Runnable sendMessage = () -> {
            String message = text.getText();
            // Ignore empty messages
            if (message.isBlank())
                return;

            String nickRecipient = recipient.getValue();
            if (!recipients.contains(nickRecipient) && !nickRecipient.equals(everyoneRecipientLabel))
                throw new IllegalArgumentException("There is no such a player " + nickRecipient);

            text.setText(""); // restore TextArea as soon as we know it's a valid message
            threadPool.execute(() -> {
                try {
                    if (nickRecipient.equals(everyoneRecipientLabel))
                        controller.sendMessage(message, UserMessage.EVERYONE_RECIPIENT);
                    else
                        controller.sendMessage(message, nickRecipient);
                } catch (DisconnectedException e) {
                    onDisconnect.accept(e);
                }
            });
        };

        this.chatScrollComponent = new ChatScrollComponent(thePlayer);
        chatScrollComponent.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(RADIUS, RADIUS * (width.doubleValue() / 210d))),
                new Insets(-INNER_INSET)))));

        recipient.backgroundColorProperty().set(Color.LIGHTSEAGREEN);
        recipient.backgroundRadiusProperty().bind(parentProperty()
                .flatMap(p -> p instanceof Region r ? r.widthProperty() : null)
                .map(w -> new CornerRadii(Math.min(RADIUS, RADIUS * (w.doubleValue() / 210d)))));
        recipient.setBackgroundInsets(new Insets(-INNER_INSET));
        recipient.getItems().add(everyoneRecipientLabel);
        recipient.getItems().addAll(recipients);
        recipient.getSelectionModel().selectFirst();
        recipient.prefWidthProperty().bind(chatScrollComponent.widthProperty());
        recipient.minWidth(USE_PREF_SIZE);
        recipient.maxWidth(USE_PREF_SIZE);

        send.backgroundRadiusProperty().bind(parentProperty()
                .flatMap(p -> p instanceof Region r ? r.widthProperty() : null)
                .map(w -> new CornerRadii(Math.min(50, 50 * (w.doubleValue() / 210d)))));
        send.setBackgroundInsets(new Insets(-INNER_INSET));

        var imgView = new ImageView(resources.loadImage("fa/paper-plane.png"));
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(20);
        imgView.setFitHeight(20);
        send.setGraphic(imgView);

        send.disableProperty().bind(text.textProperty().map(String::isBlank));
        send.setOnAction(event -> sendMessage.run());

        var hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.setPadding(Insets.EMPTY);
        hbox.setSpacing(SPACING);
        hbox.setMinHeight(USE_PREF_SIZE);
        hbox.setMaxHeight(USE_PREF_SIZE);

        text.setWrapText(true);
        // Special handling for enter
        text.setOnKeyPressed(event -> {
            if (event.getCode() != KeyCode.ENTER)
                return;

            event.consume();
            // On shift+enter append a new line, otherwise send the message
            if (event.isShiftDown())
                text.appendText("\n"); // We need to do this explicitly 'cause by default shit+enter does nothing 
            else
                sendMessage.run();
        });
        text.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                Color.WHITE,
                new CornerRadii(Math.min(RADIUS, RADIUS * (width.doubleValue() / 210d))),
                new Insets(-INNER_INSET)))));
        text.prefWidthProperty().bind(hbox.widthProperty().subtract(imgView.getFitWidth()));
        text.minWidth(USE_PREF_SIZE);
        text.maxWidth(USE_PREF_SIZE);
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

        hbox.getChildren().add(text);
        hbox.getChildren().add(send);

        setSpacing(SPACING);
        setVgrow(chatScrollComponent, Priority.SOMETIMES);
        getChildren().add(chatScrollComponent);
        getChildren().add(recipient);
        setVgrow(hbox, Priority.ALWAYS);
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
