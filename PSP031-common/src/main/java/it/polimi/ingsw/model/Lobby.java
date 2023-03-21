package it.polimi.ingsw.model;

import it.polimi.ingsw.GameAndController;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Lobby implements LobbyView {

    private final int requiredPlayers;
    private final Property<List<String>> joinedPlayers;
    private final Property<@Nullable GameAndController<Game>> game;

    /**
     * Creates Lobby with #requiredPlayer = requiredPlayer and Empty Property list of Joined Players
     */
    public Lobby(int requiredPlayers) {
        this(requiredPlayers, new ArrayList<>());
    }

    public Lobby(int requiredPlayers, List<String> joinedPlayers) {
        this.requiredPlayers = requiredPlayers;
        this.joinedPlayers = new PropertyImpl<>(joinedPlayers);
    }

    @Override
    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    @Override
    public Property<List<String>> joinedPlayers() {
        return joinedPlayers;
    }
}
