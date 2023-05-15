package it.polimi.ingsw.controller;

import it.polimi.ingsw.BoardCoord;
import it.polimi.ingsw.DisconnectedException;

import java.util.List;

public interface GameController {
    void makeMove(List<BoardCoord> selected, int shelfCol) throws DisconnectedException;

    void sendMessage(String message, String nickReceivingPlayer) throws DisconnectedException;
}
