package game;

import game.creatures.Creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Max on 3/20/2016.
 *
 * The following code that implements the A Star algorithm and is specialized for our creatures.
 *
 * Adding pathfinding to a game is a big deal. The PathFinder we're using for now is good enough but has some major
 * inefficiencies. I'm using a HashMap of points rather than an array so we don't have to worry about the world size or
 * anything like that. This will take up less memory and handle aarbitrarily large maps but it will be much much slower.
 * I tried to keep the pathfinding stuff completely unaware of the World and that really shaped the implementation. It
 * turns out that's not good for performance, debugging, or overall clarity.
 *
 * A* pathfinding is a simple enough idea that you could probably swap this implementation for another.
 *
 * ToDo: Implemnent other pathfinding algoritm.
 */
public class PathFinder {
    private ArrayList<Point> open;
    private ArrayList<Point> closed;
    private HashMap<Point, Point> parents;
    private HashMap<Point,Integer> totalCost;

    public PathFinder() {
        this.open = new ArrayList<Point>();
        this.closed = new ArrayList<Point>();
        this.parents = new HashMap<Point, Point>();
        this.totalCost = new HashMap<Point, Integer>();
    }

    private int heuristicCost(Point from, Point to) {
        return Math.max(Math.abs(from.x - to.x), Math.abs(from.y - to.y));
    }

    private int costToGetTo(Point from) {
        return parents.get(from) == null ? 0 : (1 + costToGetTo(parents.get(from)));
    }

    private int totalCost(Point from, Point to) {
        if (totalCost.containsKey(from))
            return totalCost.get(from);

        int cost = costToGetTo(from) + heuristicCost(from, to);
        totalCost.put(from, cost);
        return cost;
    }

    private void reParent(Point child, Point parent){
        parents.put(child, parent);
        totalCost.remove(child);
    }

    public ArrayList<Point> findPath(Creature creature, Point start, Point end, int maxTries) {
        open.clear();
        closed.clear();
        parents.clear();
        totalCost.clear();

        open.add(start);

        for (int tries = 0; tries < maxTries && open.size() > 0; tries++){
            Point closest = getClosestPoint(end);

            open.remove(closest);
            closed.add(closest);

            if (closest.equals(end))
                return createPath(start, closest);
            else
                checkNeighbors(creature, end, closest);
        }
        return null;
    }

    private Point getClosestPoint(Point end) {
        Point closest = open.get(0);
        for (Point other : open){
            if (totalCost(other, end) < totalCost(closest, end))
                closest = other;
        }
        return closest;
    }

    private void checkNeighbors(Creature creature, Point end, Point closest) {
        for (Point neighbor : closest.neighbors8()) {
            if (closed.contains(neighbor)
                    || !creature.canEnter(neighbor.x, neighbor.y, creature.z)
                    && !neighbor.equals(end))
                continue;

            if (open.contains(neighbor))
                reParentNeighborIfNecessary(closest, neighbor);
            else
                reParentNeighbor(closest, neighbor);
        }
    }

    private void reParentNeighbor(Point closest, Point neighbor) {
        reParent(neighbor, closest);
        open.add(neighbor);
    }

    private void reParentNeighborIfNecessary(Point closest, Point neighbor) {
        Point originalParent = parents.get(neighbor);
        double currentCost = costToGetTo(neighbor);
        reParent(neighbor, closest);
        double reparentCost = costToGetTo(neighbor);

        if (reparentCost < currentCost)
            open.remove(neighbor);
        else
            reParent(neighbor, originalParent);
    }

    private ArrayList<Point> createPath(Point start, Point end) {
        ArrayList<Point> path = new ArrayList<Point>();

        while (!end.equals(start)) {
            path.add(end);
            end = parents.get(end);
        }

        Collections.reverse(path);
        return path;
    }
}
