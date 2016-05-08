package game.screens;

import game.creatures.Creature;
import game.items.Item;

/**
 * Created by Max on 3/26/2016.
 */
public class ReadScreen extends InventoryBasedScreen {

    private int sx;
    private int sy;

    public ReadScreen(Creature player, int sx, int sy) {
        super(player);
        this.sx = sx;
        this.sy = sy;
    }

    protected String getVerb() {
        return "read";
    }

    protected boolean isAcceptable(Item item) {
        return !item.writtenSpells().isEmpty();
    }

    protected Screen use(Item item) {
        return new ReadSpellScreen(player, sx, sy, item);
    }
}