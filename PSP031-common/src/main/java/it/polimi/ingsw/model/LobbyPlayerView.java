package it.polimi.ingsw.model;

import java.io.Serializable;

public interface LobbyPlayerView extends Serializable {
    String getNick();

    Provider<Boolean> ready();
}
