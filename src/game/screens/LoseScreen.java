package game.screens;

import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

import characterPanel.CharacterPanel;

/**
 * Created by Max on 3/9/2016.
 *
 * The LoseScreen will eventually display how lame our foolish hero was and ask if they'd like to play again. But not yet.
 */
public class LoseScreen implements Screen {

    @Override
    public void displayOutput(CharacterPanel terminal) {
        terminal.write("You lost.", 1, 1);
        terminal.writeCenter("-- press [enter] to restart --", SCREEN_HEIGHT);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) throws UnsupportedEncodingException {
        return (key.getKeyCode() == KeyEvent.VK_ENTER) ? new PlayScreen() : this;
    }
}
