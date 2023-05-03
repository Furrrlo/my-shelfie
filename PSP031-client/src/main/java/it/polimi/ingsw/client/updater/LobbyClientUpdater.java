package it.polimi.ingsw.client.updater;

import it.polimi.ingsw.GameAndController;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.LobbyPlayer;
import it.polimi.ingsw.updater.GameUpdater;
import it.polimi.ingsw.updater.LobbyUpdater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LobbyClientUpdater implements LobbyUpdater {

    protected final Lobby lobby;

    public LobbyClientUpdater(Lobby lobby) {
        this.lobby = lobby;
    }

    private LobbyPlayer findPlayerBy(String nick) {
        return lobby.joinedPlayers().get().stream()
                .filter(p -> p.getNick().equals(nick))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public void updateRequiredPlayers(int requiredPlayers) {
        lobby.requiredPlayers().set(requiredPlayers);
    }

    @Override
    public void updateJoinedPlayers(List<String> joinedPlayers) {
        lobby.joinedPlayers().update(players -> {
            var newP = new ArrayList<>(players);
            var oldPlayers = players.stream().collect(Collectors.toMap(
                    LobbyPlayer::getNick,
                    Function.identity()));

            for (String s : joinedPlayers) {
                if (!oldPlayers.containsKey(s))
                    newP.add(new LobbyPlayer(s, false));
            }

            oldPlayers.forEach((nick, player) -> {
                if (!joinedPlayers.contains(nick))
                    newP.remove(player);
            });
            return Collections.unmodifiableList(newP);
        });
    }

    @Override
    public void updatePlayerReady(String nick, boolean ready) {
        findPlayerBy(nick).ready().set(ready);
    }

    @Override
    public GameUpdater updateGame(GameAndController<Game> gameAndController) {
        final GameUpdater res = createGameUpdater(gameAndController);
        lobby.game().set(gameAndController);
        return res;
    }

    protected abstract GameUpdater createGameUpdater(GameAndController<Game> gameAndController);
}
