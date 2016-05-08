package game;

import game.creatures.Creature;

import java.util.List;

/**
 * Created by Max on 3/20/2016.
 *
 * Points and Lines have all the work done in their constructors and we're extend this idea to Paths. So the Path class
 * hides the details to keep things simple.
 *
 * Creating a new path each turn may not be the best idea but we'll only have a few creatures and rogulikes are turn
 * based so it shouldn't be too much of a problem. If it does be come a performance problem we can fix it.
 *
 * ToDo: Verify if turn-based path creation is too slow.
 */
public class Path {

    private static PathFinder pf = new PathFinder();

    private List<Point> points;
    public List<Point> points() { return points; }

    /**
     * If having our Line path do all that work in the constructor was questionable then this is far more questionable.
     * I may end up regretting this and making sure future employers never see this but for now I'll try it and we'll
     * see if it becomes a problem.
     *
     * @param creature
     * @param x
     * @param y
     */
    public Path(Creature creature, int x, int y){
        points = pf.findPath(creature,
                new Point(creature.x, creature.y, creature.z),
                new Point(x, y, creature.z),
                300);
    }
}