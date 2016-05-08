package game.screens;

import game.creatures.Creature;
import game.items.Item;

/**
 * Created by Max on 3/19/2016.
 */
public class DropScreen extends InventoryBasedScreen {

    public DropScreen(Creature player) {
        super(player);
    }

    /**
     * We're asking the use what they want to drop so the getVerb should return that.
     * @return
     */
    protected String getVerb() {
        return "drop";
    }

    /**
     * Since anything can be dropped, all items are acceptable.
     *
     * @param item
     * @return
     */
    protected boolean isAcceptable(Item item) {
        return true;
    }

    /**
     * Once the user selects what to drop we tell the player to do the work and return null since we are done with
     * the DropScreen.
     *
     * @param item
     * @return
     */
    protected Screen use(Item item) {
        player.drop(item);
        return null;
    }

}
