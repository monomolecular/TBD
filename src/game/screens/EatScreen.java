package game.screens;

import game.creatures.Creature;
import game.items.Item;

/**
 * Created by Max on 3/20/2016.
 *
 * EatScreen allows player to eat something in their inventory.
 */
public class EatScreen extends InventoryBasedScreen {

    public EatScreen(Creature player) {
        super(player);
    }

    protected String getVerb() {
        return "eat";
    }

    protected boolean isAcceptable(Item item) {
        return item.foodValue() != 0;
    }

    protected Screen use(Item item) {
        player.eat(item);
        return null;
    }
}
