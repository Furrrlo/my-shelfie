package it.polimi.ingsw.client.tui;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.SerializableProperty;
import it.polimi.ingsw.model.Shelfie;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TuiPrintStreamTest {

    @Test
    void translateCursorToCol() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             TuiPrintStream out = new TuiPrintStream(baos, StandardCharsets.UTF_8);
             NoExceptionAutoCloseable ignored = out.saveCursorPos();
             NoExceptionAutoCloseable ignored1 = out.translateCursorToCol(10)) {

            var player = new Player("nick",
                    new Shelfie(),
                    true,
                    true,
                    p -> new SerializableProperty<>(true),
                    p -> new SerializableProperty<>(true),
                    0);
            out.print(player.isCurrentTurn().get() ? "- " : "  ");
            if (player.isStartingPlayer())
                out.print("S ");
            if (player.isFirstFinisher().get())
                out.print("F ");
            out.println(player.getNick() + ": " + player.score().get());
            TuiGameScene.printShelfie(out, player.getShelfie());

            var output = baos.toString(StandardCharsets.UTF_8);
            output = output.replace(String.valueOf(TuiPrintStream.FIRST_ESC_CHAR), "ESC");
            output = output.replace("\r", "");
            assertEquals("""
                    ESC7ESC[sESC[10G- S F nick: 0
                    ESC[10G   1  2  3  4  5\s
                    ESC[10G1 | || || || || |
                    ESC[10G2 | || || || || |
                    ESC[10G3 | || || || || |
                    ESC[10G4 | || || || || |
                    ESC[10G5 | || || || || |
                    ESC[10G6 | || || || || |
                    ESC[10G""",
                    output);
        }
    }
}