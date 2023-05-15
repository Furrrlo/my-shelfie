package it.polimi.ingsw.model;

public record UserMessage(String nickSendingPlayer, String message, String nickReceivingPlayer) {
    @Override
    public String toString() {
        return '[' + nickSendingPlayer + "]: " + message;
    }
}
