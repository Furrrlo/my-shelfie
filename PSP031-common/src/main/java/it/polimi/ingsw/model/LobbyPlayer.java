package it.polimi.ingsw.model;

import java.util.Objects;

public final class LobbyPlayer implements LobbyPlayerView {
    private final String nick;
    private final Property<Boolean> ready;

    public LobbyPlayer(String nick, boolean ready) {
        this.nick = nick;
        this.ready = new SerializableProperty<>(ready);
    }

    public LobbyPlayer(String nick) {
        this(nick, false);
    }

    @Override
    public String getNick() {
        return nick;
    }

    @Override
    public Property<Boolean> ready() {
        return ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LobbyPlayer that = (LobbyPlayer) o;
        return nick.equals(that.nick) && ready.equals(that.ready);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick, ready);
    }

    @Override
    public String toString() {
        return "LobbyPlayer{" +
                "nick='" + nick + '\'' +
                ", ready=" + ready +
                '}';
    }
}
