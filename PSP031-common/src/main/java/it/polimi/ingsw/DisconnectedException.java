package it.polimi.ingsw;

public class DisconnectedException extends Exception {

    public DisconnectedException() {
    }

    public DisconnectedException(String message) {
        super(message);
    }

    public DisconnectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisconnectedException(Throwable cause) {
        super(cause);
    }
}
