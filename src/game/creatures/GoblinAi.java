package game.creatures;

/**
 * Created by Max on 3/22/2016.
 *
 * Since we've got all these nifty items and abilities, we need a new creature capable of using weapons.
 */
public class GoblinAi extends CreatureAi {
    private Creature player;

    public GoblinAi(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    /**
     * The goblins will, in order of priority, try to: ranged attack, throw attack, melee attack, pickup stuff, and
     * wander if they can't do anything else.
     */
    public void onUpdate(){
        if (canUseBetterEquipment())
            useBetterEquipment();
        else if (canRangedWeaponAttack(player))
            creature.rangedWeaponAttack(player);
        else if (canThrowAt(player))
            creature.throwItem(getWeaponToThrow(), player.x, player.y, player.z);
        else if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else if (canPickup())
            creature.pickup();
        else
            wander();
    }

}