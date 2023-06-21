package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.UserMessage;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatScrollComponent extends ScrollPane {
    private final MessageVBox messagesVBox;

    public ChatScrollComponent(String thePlayer) {
        setContent(this.messagesVBox = new MessageVBox(thePlayer));
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(true);
        setBackground(Background.EMPTY);
        this.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                this.setVvalue(1.0);
            }
        });
    }

    public List<UserMessage> getMessages() {
        return messagesVBox.messages.get();
    }

    public ObjectProperty<List<UserMessage>> messagesProperty() {
        return messagesVBox.messages;
    }

    public void setMessages(List<UserMessage> messages) {
        this.messagesVBox.messages.set(messages);
    }

    private static class MessageVBox extends VBox {

        private final ObjectProperty<List<UserMessage>> messages = new SimpleObjectProperty<>(this, "messages");

        public MessageVBox(String thePlayer) {
            setPadding(new Insets(10));
            setBackground(Background.EMPTY);

            messages.addListener((obs, old, newList) -> {
                var newComponents = new ArrayList<Node>();
                for (UserMessage msg : newList) {
                    var hbox = new HBox();
                    //if msg is from the player we align to the right, else to the left
                    if (msg.nickSendingPlayer().equals(thePlayer))
                        hbox.setAlignment(Pos.TOP_RIGHT);
                    else
                        hbox.setAlignment(Pos.TOP_LEFT);
                    var m = new MessageComponent(msg, thePlayer);
                    var text = new Text(m.getText());
                    text.setFont(m.getFont());
                    var textWidth = text.getLayoutBounds().getWidth();
                    m.maxWidthProperty().bind(widthProperty()
                            .map(vboxWidth -> Math.min(vboxWidth.doubleValue() / 1.5d, textWidth + 10)));
                    hbox.getChildren().add(m);
                    newComponents.add(hbox);
                }
                getChildren().setAll(newComponents);
            });

            setSpacing(12);
        }
    }

    private static class MessageComponent extends VBox {
        private final Label text;

        public MessageComponent(UserMessage message, String thePlayer) {
            Text nick = new Text();
            Fonts.enforceWeight(nick.fontProperty(), FontWeight.EXTRA_BOLD);
            text = new Label();
            text.setWrapText(true);
            if (!message.nickSendingPlayer().equals(thePlayer)) {
                //message from other players
                if (message.nickReceivingPlayer().equals("all"))
                    nick.setText(message.nickSendingPlayer());
                else
                    nick.setText(message.nickSendingPlayer() + " @" + message.nickReceivingPlayer());
                text.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                        Color.LIGHTGRAY,
                        new CornerRadii(15),
                        new Insets(-5)))));
                text.setText(message.message());
                setAlignment(Pos.TOP_LEFT);
                this.setSpacing(6);
                this.getChildren().add(nick);
                this.getChildren().add(text);
            } else {
                //message sent by the player
                if (!message.nickReceivingPlayer().equals("all")) {
                    nick.setText(" @" + message.nickReceivingPlayer());
                    this.getChildren().add(nick);
                }
                text.backgroundProperty().bind(widthProperty().map(width -> new Background(new BackgroundFill(
                        Color.LIGHTSEAGREEN,
                        new CornerRadii(15),
                        new Insets(-5)))));
                text.setText(message.message());
                this.getChildren().add(text);
                setAlignment(Pos.TOP_RIGHT);
                this.setSpacing(6);
            }
        }

        public String getText() {
            return text.getText();
        }

        public Font getFont() {
            return text.getFont();
        }
    }
}
