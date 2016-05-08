package game.creatures;

/**
 * Created by Max on 3/19/2016.
 */
public class AlicornAi extends CreatureAi {

    public AlicornAi(Creature creature) {
        super(creature);
    }

    // Simple random movement and the alicorn is alive!.
    public void onUpdate() {
        wander();
    }
}
