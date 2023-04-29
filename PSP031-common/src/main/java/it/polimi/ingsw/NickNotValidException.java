package it.polimi.ingsw;

public class NickNotValidException extends Exception {

    public NickNotValidException() {
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
