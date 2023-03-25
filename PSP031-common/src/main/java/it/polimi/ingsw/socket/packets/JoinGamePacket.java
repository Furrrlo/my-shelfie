package it.polimi.ingsw.socket.packets;

public record JoinGamePacket(String nick) implements C2SPacket {
    @Override
    public String toString() {
        return "JoinGamePacket{" +
                "nick='" + nick + '\'' +
                '}';
    }
}
