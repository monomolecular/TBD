package game.creatures;

/**
 * Created by Max on 3/11/2016.
 *
 * Apparently a fungus needs an AI too.
 */
public class FungusAi extends CreatureAi {

    private CreatureFactory factory;
    private int spreadcount;

    public FungusAi(Creature creature, CreatureFactory factory) {
        super(creature);
        this.factory = factory;
    }

    /**
     *  We want the fungi to spread to a nearby open space every once in a while as part of it's behavior during
     *  updating.
     */
    @Override
    public void onUpdate(){
        if (spreadcount < 5 && Math.random() < 0.02)
            spread();
    }

    /**
     * You can play around with how far it spreads, how often it spreads, and how many times it can spread. Don't
     * forget to modify the newFungus method to pass itself into the FungusAi constructor. The last thing you need to
     * do is tell the world to let everyone take a turn by calling world.update() in the PlayScreen after handling
     * user input. The user's input makes the player move and each creature can move after that. Since we're relying
     * on Java's event handling it would be very cumbersome to make our code pause and wait for user input during the
     * player's onUpdate like with many other roguelikes.
     */
    private void spread(){
        int x = creature.x + (int)(Math.random() * 3) - 1;
        int y = creature.y + (int)(Math.random() * 3) - 1;

        if (!creature.canEnter(x, y, creature.z)) {
            return;
        }

        creature.doAction("spawn a child");

        Creature child = factory.newFungus(creature.z);
        child.x = x;
        child.y = y;
        child.z = creature.z;
        spreadcount++;
    }
}
