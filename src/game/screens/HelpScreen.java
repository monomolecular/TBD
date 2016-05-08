package game.screens;

import characterPanel.CharacterPanel;

import java.awt.event.KeyEvent;

/**
 * Created by Max on 3/21/2016.
 *
 * Initial Help Screen is sufficient but kind of lame. Need a better story and maybe say something about what all the
 * symbols are.
 */
public class HelpScreen implements Screen {

    public void displayOutput(CharacterPanel terminal) {
        terminal.clear();
        terminal.writeCenter("roguelike help", 1);
        terminal.write("Descend the Caves Of Slight Danger, find the lost Teddy Bear, and return to", 1, 3);
        terminal.write("the surface to win. Use what you find to avoid dying.", 1, 4);

        int y = 6;
        terminal.write("[h] or [?] for help", 2, y++);
        terminal.write("[g] or [,] to pick up", 2, y++);
        terminal.write("[d] to drop", 2, y++);
        terminal.write("[e] to eat", 2, y++);
        terminal.write("[q] to quaff", 2, y++);
        terminal.write("[r] to read", 2, y++);
        terminal.write("[w] to wear or wield", 2, y++);
        terminal.write("[x] to examine your items", 2, y++);
        terminal.write("[;] to look around", 2, y++);
        terminal.write("[t] to throw something", 2, y++);
        terminal.write("[f] to fire a ranged weapon", 2, y++);
        terminal.write("Number Keypad & [y,u,b,n,h,j,k,l] to move", 2, y++);

        terminal.writeCenter("-- press any key to continue --", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        return null;
    }
}
