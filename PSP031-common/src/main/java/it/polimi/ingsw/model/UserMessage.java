package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Object representing a message sent by a user
 *
 * @param nickSendingPlayer nick of the player which sent the message
 * @param sendingColor console color unique to the player which sent the message
 * @param message text of the message which was sent
 * @param nickReceivingPlayer nick of the player to which the message was addressed
 * @param receivingColor console color unique to the player which received the message
 */
public record UserMessage(String nickSendingPlayer,
        String sendingColor,
        String message,
        String nickReceivingPlayer,
        String receivingColor) implements Serializable {

    /** Placeholder used as the receiving player nick for messages addressed to the whole game lobby */
    public static final String EVERYONE_RECIPIENT = "";

    /** Creates a message object which is sent to everyone */
    public static UserMessage forEveryone(String nickSendingPlayer, String sendingColor, String message) {
        return new UserMessage(nickSendingPlayer, sendingColor, message, EVERYONE_RECIPIENT, "");
    }

    /** Returns whether this message was sent to everyone in the game */
    public boolean isForEveryone() {
        return nickReceivingPlayer.equals(EVERYONE_RECIPIENT);
    }

    /** Returns a string to be printed in the console */
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
