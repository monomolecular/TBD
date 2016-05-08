package game.creatures;

import game.Path;
import game.Point;

import java.util.List;

/**
 * Created by Max on 3/20/2016.
 */
public class ZombieAi extends CreatureAi {
    private Creature player;

    public ZombieAi(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    /**
     * During the zombie's turn it will move to the player if it can see him, otherwise it will wander around. Since
     * zombies are a little slow, I gave them a chance of doing nothing during their turn for just a little bit of
     * interest.
     */
    public void onUpdate(){
        if (Math.random() < 0.2)
            return;

        if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else
            wander();
    }

    /**
     * The hunt method finds a path to the target and moves to it.
     *
     * @param target
     */
    public void hunt(Creature target){
        List<Point> points = new Path(creature, target.x, target.y).points();

        try {
            int mx = points.get(0).x - creature.x;
            int my = points.get(0).y - creature.y;

            creature.moveBy(mx, my, 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}
