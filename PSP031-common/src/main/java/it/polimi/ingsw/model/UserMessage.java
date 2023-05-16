package it.polimi.ingsw.model;

import java.io.Serializable;

public record UserMessage(String nickSendingPlayer, String sendingColor, String message, String nickReceivingPlayer,
        String receivingColor) implements Serializable {
    @Override
    public String toString() {
        if (nickReceivingPlayer.equals("all")) {
            return '[' + sendingColor + nickSendingPlayer + "\033[0m" + "]: " + message;
        } else {
            return '[' + sendingColor + nickSendingPlayer + "\033[0m" + " to " + receivingColor + nickReceivingPlayer
                    + "\033[0m" + "]: " + message;
        }

    }
}
