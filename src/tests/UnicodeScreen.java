package tests;

import characterPanel.CharacterPanel;
import game.screens.Screen;

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
public class UnicodeScreen implements Screen {

    public void displayOutput(CharacterPanel terminal) {
        terminal.write("Unicode Glyphs.", 1, 1);


        terminal.writeCenter("-- press any key to return --", 23);
    }

    public Screen respondToUserInput(KeyEvent key) {
        return null;
    }

}
