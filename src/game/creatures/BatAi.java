package game.creatures;

/**
 * Created by Max on 3/18/2016.
 */
public class BatAi extends CreatureAi {

    public BatAi(Creature creature) {
        super(creature);
    }

    // Simple random movement and bats move twice for every one of your moves.
    public void onUpdate() {
        wander();
        wander();
    }
}