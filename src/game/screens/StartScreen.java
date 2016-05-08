package game.screens;

import characterPanel.CharacterPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

/**
 * Created by Max on 3/9/2016.
 *
 * The first screen players will see is the StartScreen. This is just a screen that displays some info and sets us in
 * "play" mode when the user hits enter.
 */
public class StartScreen implements Screen {

    @Override
    public void displayOutput(CharacterPanel terminal) {
        terminal.write("Tempted By Dragons", 1, 1);
        terminal.writeCenter("-- press [space] to start --", SCREEN_HEIGHT);
        terminal.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) throws UnsupportedEncodingException {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                try {
                    return new PlayScreen();  // Start up the game and go to the "Play" Screen
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            case KeyEvent.VK_ENTER:
                if (key.isControlDown()) {
                    System.out.println("test");
                }

        }
        return this;
    }
}