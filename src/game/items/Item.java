package game.items;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Max on 3/19/2016.
 *
 * Items don't have an x, y, or z coordinate because items don't need to know where they are; it kind of makes since to
 * have them when laying on the ground but what about when they're in a container or being carried around by a c
 * reature? Eventually, maybe we create a Location interface. That way an item's location could be a point in the
 * world, a creature that's carrying it (or it's point in the world), or a container it's in. That would also be useful
 * because an item would have a reference to wherever it is and whoever is carrying it.
 *
 * We have the CreatureFactory handle the details of creating a new creature so let's do the same for items. We could
 * create an ItemFactory but it's not clear if it's better to keep the two separate or not. We'll renamne it to
 * StuffFactoryu and find out.
 */
public class Item {

    private char glyph;
    public char glyph() {
        return glyph;
    }

    private Color color;
    public Color color() {
        return color;
    }

    private String name;
    public String name() {
        return name;
    }

    public Item(char glyph, Color color, String name){
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.writtenSpells = new ArrayList<Spell>();
    }

    private int foodValue;
    public int foodValue() {
        return foodValue;
    }

    public void modifyFoodValue(int amount) {
        foodValue += amount;
    }

    /**
     * Since we have a very simple Attack value and Defense value for creatures, let's use that for our weapons and
     * armor.
     */
    private int attackValue;
    public int attackValue() {
        return attackValue;
    }
    public void modifyAttackValue(int amount) {
        attackValue += amount;
    }

    private int defenseValue;
    public int defenseValue() {
        return defenseValue;
    }

    public void modifyDefenseValue(int amount) {
        defenseValue += amount;
    }

    /**
     * Helper class which gathers details about an item for the examine screen.
     *
     * @return
     */
    public String details() {
        String details = "";

        if (attackValue != 0)
            details += "     attack:" + attackValue;

        if (defenseValue != 0)
            details += "     defense:" + defenseValue;

        if (foodValue != 0)
            details += "     food:" + foodValue;

        return details;
    }

    /**
     * Now we can throw stuff.
     */
    private int thrownAttackValue;
    public int thrownAttackValue() {
        return thrownAttackValue;
    }

    public void modifyThrownAttackValue(int amount) {
        thrownAttackValue += amount;
    }

    /**
     * Now we can shoot stuff.
     *
     * Anything that has a non zero rangedAttackValue is a ranged weapon. This way a weapon can have separate attack
     * values for melee, thrown, and ranged combat. We'll keep it simple and say that ranged weapons can hit anything
     * we can see that isn't blocked by terrain.
     */
    private int rangedAttackValue;
    public int rangedAttackValue() {
        return rangedAttackValue;
    }

    public void modifyRangedAttackValue(int amount) {
        rangedAttackValue += amount;
    }

    private Effect quaffEffect;
    public Effect quaffEffect() { return quaffEffect; }
    public void setQuaffEffect(Effect effect) { this.quaffEffect = effect; }

    /**
     * This should allow us to create scrolls, spell books, notes, or even engraved items. Very simple and flexible.
     * If we create an effect that just displays a note to the user then we could even let the player add his own
     * non-magical engravings to items.
     */
    private ArrayList<Spell> writtenSpells;
    public ArrayList<Spell> writtenSpells() {
        return writtenSpells;
    }

    public void addWrittenSpell(String name, int manaCost, Effect effect, Boolean requiresTarget){
        writtenSpells.add(new Spell(name, manaCost, effect, requiresTarget));
    }
}
