package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.Objects;

public record UserMessage(String nickSendingPlayer,
        String sendingColor,
        String message,
        String nickReceivingPlayer,
        String receivingColor) implements Serializable {

    public static final String EVERYONE_RECIPIENT = "";

    public static UserMessage forEveryone(String nickSendingPlayer, String sendingColor, String message) {
        return new UserMessage(nickSendingPlayer, sendingColor, message, EVERYONE_RECIPIENT, "");
    }

    public boolean isForEveryone() {
        return nickReceivingPlayer.equals(EVERYONE_RECIPIENT);
    }

    public String toConsoleString() {
        if (isForEveryone()) {
            return '[' + sendingColor + nickSendingPlayer + "\033[0m" + "]: " + message;
        } else {
            return '[' + sendingColor + nickSendingPlayer + "\033[0m" + " to " + receivingColor + nickReceivingPlayer
                    + "\033[0m" + "]: " + message;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserMessage that))
            return false;
        return nickSendingPlayer.equals(that.nickSendingPlayer) && message.equals(that.message)
                && nickReceivingPlayer.equals(that.nickReceivingPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickSendingPlayer, message, nickReceivingPlayer);
    }
}
