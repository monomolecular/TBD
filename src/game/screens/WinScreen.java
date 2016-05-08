package game.screens;

import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

import characterPanel.CharacterPanel;

/**
 * Created by Max on 3/9/2016.
 *
 * The WinScreen will eventually display how awesome our brave hero is and ask if they'd like to play again. But not yet.
 */
public class WinScreen implements Screen {

    public void displayOutput(CharacterPanel terminal) {
        terminal.write("You won.", 1, 1);
        terminal.writeCenter("-- press [enter] to restart --", SCREEN_HEIGHT);
    }

    public Screen respondToUserInput(KeyEvent key) throws UnsupportedEncodingException {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }
}
