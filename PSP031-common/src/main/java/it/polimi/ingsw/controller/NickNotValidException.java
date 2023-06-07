package it.polimi.ingsw.controller;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Exception which indicates that the nickname the player tried to connect with
 * is already in use by another player
 */
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

    @Override
    public @NotNull String getMessage() {
        return Objects.requireNonNullElse(super.getMessage(), "Nick not valid");
    }
}
