package game.creatures;

import game.Line;
import game.Path;
import game.Point;
import game.Tile;
import game.items.Item;

import java.util.List;

/**
 * Created by Max on 3/10/2016.
 *
 * Here we're wiring up the creature and the creature's ai. The ai also needs to do deal with the creature trying to
 * enter a new tile. We're going to have a specific ai for the player so it doesn't matter what you use here since we
 * are just going to override it.
 *
  */

public class CreatureAi {
    protected Creature creature;

    public CreatureAi(Creature creature) {

        // We can rely on constructor injection to set the creature AI property values.
        this.creature = creature;
        this.creature.setCreatureAi(this);
    }

    /**
     * Provides a default movement and behavior. Intended to be overriden by the extending class for a specific
     * creature.
     *
     * @param x
     * @param y
     * @param z
     * @param tile
     */
    public void onEnter(int x, int y, int z, Tile tile) {
        if (tile.isGround()) {
            creature.x = x;
            creature.y = y;
            creature.z = z;
        } else {
            creature.doAction("bump into a wall");
        }
    }

    public void onUpdate() {
        // Empy on pupose. Intended to be overriden by the extending class for a specific creature.
    }

    /**
     * At minimum, the PlayerAi class will use onNotify to add the messages to a list. Other CreatureAi's will just
     * ignore it.
     *
     * @param messages
     */
    public void onNotify(String messages) {
        // Empy on pupose. Intended to be overriden by the extending class for a specific creature.
    }


    /**
     * Determine if the ceature (player) can see other creatures or the ground around them.
     *
     * @param wx
     * @param wy
     * @param wz
     * @return
     */
    public boolean canSee(int wx, int wy, int wz) {
        if (creature.z != wz)
            return false;

        if ((creature.x - wx) * (creature.x - wx) + (creature.y - wy) * (creature.y - wy) > creature.visionRadius() * creature.visionRadius())
            return false;

        for (Point p : new Line(creature.x, creature.y, wx, wy)) {
            if (creature.realTile(p.x, p.y, wz).isGround() || p.x == wx && p.y == wy)
                continue;

            return false;
        }

        return true;
    }

    /**
     * Here's a good-enough-for-now implementation of the CreatureAi rememberedTile method since they don't actually
     * have a memory.
     *
     * @param wx
     * @param wy
     * @param wz
     * @return
     */
    public Tile rememberedTile(int wx, int wy, int wz) {
        return Tile.UNKNOWN;
    }

    /**
     * Simple movement of moving randomly. This common behavior can be called by any subclass.
     *
     * You could also make it keep trying until mx != 0 && my != 0, that way it would never stand in the same spot. You
     * may want to make sure it doesn't try to move into a wall or make it able to go up or down stairs.
     *
     * ToDo: Enable creatures to use stairs.
     */
    public void wander() {
        int mx = (int)(Math.random() * 3) - 1;
        int my = (int)(Math.random() * 3) - 1;

        Creature other = creature.creature(creature.x + mx, creature.y + my, creature.z);

        // Make sure creatures don't fight other's like them.
        if (other != null && other.glyph() == creature.glyph()) {
            return;
        } else {
            creature.moveBy(mx, my, 0);
        }
    }

    /**
     * When something gains a level it get's some stat bonus; increased hp, increased attack, etc. The player will be
     * shown a list to chose from but other creatures will get one at random.
     */
    public void onGainLevel() {
        // Automatically gain some benefit when a creature gains a level.
        new LevelUpController().autoLevelUp(creature);
    }

    public void hunt(Creature target){
        List<Point> points = new Path(creature, target.x, target.y).points();

        int mx = points.get(0).x - creature.x;
        int my = points.get(0).y - creature.y;

        try {
            creature.moveBy(mx, my, 0);
        } catch(Exception e) {
            System.out.println("creature.moveBy("+mx+", " +my+" ,0); through an error...");
        }
    }

    protected boolean canRangedWeaponAttack(Creature other){
        return creature.weapon() != null
                && creature.weapon().rangedAttackValue() > 0
                && creature.canSee(other.x, other.y, other.z);
    }

    protected boolean canThrowAt(Creature other) {
        return creature.canSee(other.x, other.y, other.z)
                && getWeaponToThrow() != null;
    }

    protected Item getWeaponToThrow() {
        Item toThrow = null;

        for (Item item : creature.inventory().getItems()){
            if (item == null || creature.weapon() == item || creature.armor() == item)
                continue;

            if (toThrow == null || item.thrownAttackValue() > toThrow.attackValue())
                toThrow = item;
        }

        return toThrow;
    }

    protected boolean canPickup() {
        return creature.item(creature.x, creature.y, creature.z) != null
                && !creature.inventory().isFull();
    }

    protected boolean canUseBetterEquipment() {
        int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().attackValue() + creature.weapon().rangedAttackValue();
        int currentArmorRating = creature.armor() == null ? 0 : creature.armor().defenseValue();

        for (Item item : creature.inventory().getItems()){
            if (item == null)
                continue;

            boolean isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();

            if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
                    || isArmor && item.defenseValue() > currentArmorRating)
                return true;
        }

        return false;
    }

    protected void useBetterEquipment() {
        int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().attackValue() + creature.weapon().rangedAttackValue();
        int currentArmorRating = creature.armor() == null ? 0 : creature.armor().defenseValue();

        for (Item item : creature.inventory().getItems()){
            if (item == null)
                continue;

            boolean isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();

            if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
                    || isArmor && item.defenseValue() > currentArmorRating) {
                creature.equip(item);
            }
        }
    }
}
