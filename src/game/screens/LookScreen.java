package game.screens;

import game.Tile;
import game.creatures.Creature;
import game.items.Item;

/**
 * Created by Max on 3/21/2016.
 *
 * The simplest targeting action is looking at surroundings; a LookScreen. This will display details to the user about
 * whatever they are targeting. If you use this code then you'll need to create a few methods to get details about
 * creatures and tiles.
 */
public class LookScreen extends TargetBasedScreen {

    public LookScreen(Creature player, String caption, int sx, int sy) {
        super(player, caption, sx, sy);
    }

    public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
        Creature creature = player.creature(x, y, player.z);
        if (creature != null){
            caption = creature.glyph() + " "     + creature.name() + creature.details();
            return;
        }

        Item item = player.item(x, y, player.z);
        if (item != null){
            caption = item.glyph() + " "     + item.name() + item.details();
            return;
        }

        Tile tile = player.tile(x, y, player.z);
        caption = tile.glyph() + " " + tile.details();
    }
}