package it.polimi.ingsw;

/**
 * Exception thrown to indicate that a remote operation failed because
 * the connection was lost
 */
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
