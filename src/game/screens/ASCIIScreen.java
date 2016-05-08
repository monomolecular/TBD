package game.screens;

import characterPanel.CharacterPanel;

import java.awt.event.KeyEvent;


/**
 * Created by Max on 3/9/2016.
 *
 * ASCII CodePage 437:
 * http://www.ascii-codes.com/
 *
 * If I can find a way to leverage the entire unicode characterset That's probably all I need for a long time...
 * http://unicode-table.com/en/
 */
public class ASCIIScreen implements Screen {

    public void displayOutput(CharacterPanel terminal) {
        terminal.write("ASCII Codes.", 1, 1);

        int asciiCode = 0;

        // Loop through all the tiles on the screen...
        for (int x = 2; x < 70; x += 8) {
            for (int y = 2; y < 23; y++) {
                int wx = x;
                int wy = y;

                if (asciiCode < 256) {
                    terminal.write(asciiCode + " - " + (char) asciiCode++, x, y, CharacterPanel.brightWhite);
                }
            }
        }

        terminal.writeCenter("-- press any key to return --", 23);
    }

    public Screen respondToUserInput(KeyEvent key) {
        return null;
    }

}
