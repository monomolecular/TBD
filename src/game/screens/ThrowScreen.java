package game.screens;

import game.creatures.Creature;
import game.items.Item;

/**
 * Created by Max on 3/21/2016.
 *
 * ThrowScreen is also a simple InventoryBasedScreen except we need to pass some values that aren't needed by the
 * ThrowScreen but are used by the ThrowAtScreen.
 */
public class ThrowScreen extends InventoryBasedScreen {
    private int sx;
    private int sy;

    public ThrowScreen(Creature player, int sx, int sy) {
        super(player);
        this.sx = sx;
        this.sy = sy;
    }

    protected String getVerb() {
        return "throw";
    }

    protected boolean isAcceptable(Item item) {
        return true;
    }

    protected Screen use(Item item) {
        return new ThrowAtScreen(player, sx, sy, item);
    }
}