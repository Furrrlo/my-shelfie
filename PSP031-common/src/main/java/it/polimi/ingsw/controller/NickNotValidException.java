package it.polimi.ingsw.controller;

/**
 * Exception which indicates that the nickname the player tried to connect with
 * is already in use by another player
 */
public class NickNotValidException extends Exception {

    public NickNotValidException() {
        super("Nick not valid");
    }

    public NickNotValidException(String message) {
        super(message);
    }

    public NickNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public NickNotValidException(Throwable cause) {
        super(cause);
    }
}
