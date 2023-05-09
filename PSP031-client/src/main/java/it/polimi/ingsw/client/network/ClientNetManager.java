package it.polimi.ingsw.client.network;

import it.polimi.ingsw.LobbyAndController;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.model.LobbyView;

import java.util.ArrayList;
import java.util.List;

public interface ClientNetManager {

    String getHost();

    int getPort();

    LobbyAndController<? extends LobbyView> joinGame(String nick) throws Exception;

    default void disconnectPlayer(Lobby lobby, String nick) {
        var game = lobby.game().get();
        if (game == null) {
            lobby.joinedPlayers().update(players -> {
                List<LobbyPlayer> l = new ArrayList<>(players);
                l.removeIf(p -> p.getNick().equals(nick));
                return l;
            });
        } else {
            game.game().thePlayer().connected().set(false);
        }
    }
}
