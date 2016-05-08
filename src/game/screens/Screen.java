package game.screens;

import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

import characterPanel.CharacterPanel;

/**
 * Created by Max on 3/9/2016.
 *
 * Nearly all games follow the same basic main loop:
 *
 *      while the game isn't over:
 *          show stuff to the user
 *          get user input
 *          respond to user input
 *
 * Roguelikes are no different. But showing stuff to the user, getting user input, and responding to the input doesn't
 * always mean the same thing. Usually we're showing the world and waiting for a player's command but sometimes we're
 * showing a list of spells and waiting for the user to tell us which one to cast or maybe we're showing the player how
 * he died and asking if he wants to play again. Said another way, sometimes we're in "play" mode, sometimes in "select
 * spell" mode, and sometimes "you lost" mode. Each mode has a different way of handling input and output, and I've
 * found that having a different class for each mode with it's own input and output logic is a good way of handling
 * that — much better than having a big mess of if statements and mode-related variables.
 *
 * Each mode will be represented by a different screen. Each screen displays output on our CharacterPanel and responds to
 * user input; this abstraction can be represented as a simple Screen interface.
 *
 * ToDo: Implement a "screen stack"
 * One variation I did on a recent project of mine was to use a screen stack. User input goes straight to the topmost
 * screen and when it's time to draw, each screen is drawn from the bottom of the stack to the top. enterScreen puts a
 * new screen on the top of the stack, exitScreen pops the topmost screen off the stack and switchScreen pops then
 * pushes a new screen. This way individual screens don't have to keep track of the previous screen for drawing and
 * returning to.
 */
public interface Screen {

    int SCREEN_WIDTH = 100;
    int SCREEN_HEIGHT = 30;
    /**
     * The displayOutput method takes an CharacterPanel to display itself on and the respondToUserInput takes the KeyEvent
     * and can return the new screen. This way pressing a key can result in looking at a different screen.
     * @param terminal
     */
    void displayOutput(CharacterPanel terminal);

    Screen respondToUserInput(KeyEvent key) throws UnsupportedEncodingException;
}
