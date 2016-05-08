package game.screens;

import game.creatures.Creature;
import game.items.Item;

/**
 * Created by Max on 3/20/2016.
 *
 * Maybe the EquipScreen shouldn't let us equip what we're already using. Or maybe wearing or wielding what's already
 * equipped should un-wear or un-weild it? That way the 'w' key can equip or unequip. It's your game so it's up to you.
 * Implementing those is left as an exercise.
 *
 * ToDo:Improve EquipScreen.
 *
 */
public class EquipScreen extends InventoryBasedScreen {

    public EquipScreen(Creature player) {
        super(player);
    }

    protected String getVerb() {
        return "wear or wield";
    }

    protected boolean isAcceptable(Item item) {
        return item.attackValue() > 0 || item.defenseValue() > 0;
    }

    protected Screen use(Item item) {
        player.equip(item);
        return null;
    }
}