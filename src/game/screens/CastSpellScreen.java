package game.screens;

import game.creatures.Creature;
import game.items.Spell;

/**
 * Created by Max on 3/26/2016.
 *
 * Screen to select a target for the spell.
 */
public class CastSpellScreen extends TargetBasedScreen {
    private Spell spell;

    public CastSpellScreen(Creature player, String caption, int sx, int sy, Spell spell) {
        super(player, caption, sx, sy);
        this.spell = spell;
    }

    public void selectWorldCoordinate(int x, int y, int screenX, int screenY){
        player.castSpell(spell, x, y);
    }
}