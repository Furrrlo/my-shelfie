package it.polimi.ingsw.model;

import java.io.Serializable;

public record UserMessage(String nickSendingPlayer, String message, String nickReceivingPlayer) implements Serializable {
    @Override
    public String toString() {
        if (nickReceivingPlayer.equals("all")) {
            return '[' + nickSendingPlayer + "]: " + message;
        } else {
            return '[' + nickSendingPlayer + " to " + nickReceivingPlayer + "]: " + message;
        }

    }
}
