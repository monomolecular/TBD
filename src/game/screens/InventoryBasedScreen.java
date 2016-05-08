package game.screens;

import characterPanel.CharacterPanel;
import game.Tile;
import game.creatures.Creature;
import game.items.Item;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Max on 3/19/2016.
 *
 * In order to be able to drop and use things we need a way to interact with our inventory. Ideally the user will press
 * the 'd' key, the GUI will ask what to drop, the user types the letter of the thing to drop, the player drops it, and
 * we go back to the game.
 *
 * What we want to do with inventory? Here's a few scenarios:
 * press 'd', ask what to drop, the user selects something that can be dropped, drop it
 * press 'q', ask what to quaff, the user selects something that can be quaffed, quaff it
 * press 'r', ask what to read, the user selects something that can be read, read it
 * press 't', ask what to throw, the user selects something that can be thrown, throw it
 * press 'e', ask what to eat, the user selects something that can be eaten, eat it
 *
 * There's a pattern here where a key that get's pressed, some verb (drop, quaff, read), some check against the items
 * (droppable, quaffable, readable), and some action (drop, quaff, read). The common behavior can be put in one class
 * called InventoryBasedScreen (this one) and the specific details can be in subclasses. That way we can have a
 * DropScreen, QuaffScreen, ReadScreen and others that all subclass the InventoryBasedScreen and just provide a few
 * simple details.
 */
public abstract class InventoryBasedScreen implements Screen {

    protected Creature player;
    private String letters;

    /**
     * We've got abstract methods so our subclasses can specify the verb, what items are acceptable for the
     * action, and a method to actually perform the action. Using an item returns a Screen since it may lead to a
     * different screen, e.g. if we're going to throw something then we can transition into some sort of targeting
     * screen.
     */
    protected abstract String getVerb();
    protected abstract boolean isAcceptable(Item item);
    protected abstract Screen use(Item item);

    public InventoryBasedScreen(Creature player){

        /**
         * We need the reference to the player because that's the one who's going to do the work of dropping, quaffing,
         * eating, etc. It's protected so that the subclasses can use it.
         */
        this.player = player;

        /**
         * The letters are so we can assign a letter to each inventory slot (If you allow the inventory to be larger
         * then you need to add more characters). Maybe this should be part of the inventory class but I think this is
         * the only place where we will use it so I'll put it here for now.
         */
        this.letters = "abcdefghijklmnopqrstuvwxyz";
    }

    /**
     * Since this is a screen it needs to actually display some output. We not only ask what they want to use but go
     * ahead and show a list of acceptable items. Write the list in the lower left hand corner and ask the user what to
     * do. If you allow a larger inventory then you'll have to show two columns or scroll the list or something.
     *
     * @param terminal
     */
    public void displayOutput(CharacterPanel terminal) {
        ArrayList<String> lines = getList();

        int y = SCREEN_HEIGHT - lines.size();
        int x = 4;

        if (lines.size() > 0)
            terminal.clear(Tile.UNKNOWN.glyph(), x, y, 20, lines.size());

        for (String line : lines){
            terminal.write(line, x, y++);
        }

        terminal.clear(Tile.UNKNOWN.glyph(), 0, SCREEN_HEIGHT, SCREEN_WIDTH, 1);
        terminal.write("What would you like to " + getVerb() + "?", 2, SCREEN_HEIGHT);

        terminal.repaint();
    }

    /**
     * The getList method will make a list of all the acceptable items and the letter for each corresponding inventory
     * slot.
     *
     * @return
     */
    private ArrayList<String> getList() {
        ArrayList<String> lines = new ArrayList<String>();
        Item[] inventory = player.inventory().getItems();

        for (int i = 0; i < inventory.length; i++){
            Item item = inventory[i];

            if (item == null || !isAcceptable(item)) continue;

            String line = letters.charAt(i) + " - " + item.glyph() + " " + item.name();

            if(item == player.weapon() || item == player.armor())
                line += " (equipped)";

            lines.add(line);
        }
        return lines;
    }

    /**
     * Now that we've got some output we need to respond to user input. The user can press escape to go back to playing
     * the game, select a valid character to use, or some invalid key that will do nothing and keep them on the current
     * screen. Use it, exit, or ask again.
     *
     * @param key
     * @return
     */
    public Screen respondToUserInput(KeyEvent key) {
        char c = key.getKeyChar();

        Item[] items = player.inventory().getItems();

        if (letters.indexOf(c) > -1
                && items.length > letters.indexOf(c)
                && items[letters.indexOf(c)] != null
                && isAcceptable(items[letters.indexOf(c)]))
            return use(items[letters.indexOf(c)]);
        else if (key.getKeyCode() == KeyEvent.VK_ESCAPE)
            return null;
        else
            return this;
    }
}
