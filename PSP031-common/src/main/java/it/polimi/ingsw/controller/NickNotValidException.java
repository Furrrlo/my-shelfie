package it.polimi.ingsw.controller;

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
