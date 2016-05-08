package game.screens;

import game.creatures.Creature;
import game.items.Item;

/**
 * Created by Max on 3/25/2016.
 */
public class QuaffScreen extends InventoryBasedScreen {

    public QuaffScreen(Creature player) {
        super(player);
    }

    protected String getVerb() {
        return "quaff";
    }

    protected boolean isAcceptable(Item item) {
        return item.quaffEffect() != null;
    }

    protected Screen use(Item item) {
        player.quaff(item);
        return null;
    }
}