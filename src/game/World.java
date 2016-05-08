package game;

import game.creatures.Creature;
import game.items.Item;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Max on 3/9/2016.
 *
 * Now that we have cave walls and floors (in the Tile Class), we need a World to hold them.
 *
 * This class gives us details about our world of tiles but we don't have a way of creating the tiles a World is made
 * of. We could add a bunch of methods to create a World, but it's simpler for the World class to only be responsible
 * for the running of a world not creating it.
 *
 * Creating a new world is an entirely different and complicated subject that's only relevant at the beginning of a
 * game and should be forgotten about right after we have a world to work with. Something else needs to create, or
 * build, a world. That will be the WorldBuilder Class.
 */

public class World {
    private Tile[][][] tiles;

    /**
     * Instead of a list of all items I'm going to try something different — I'm only going to allow one item per tile.
     * Good idea or bad, let's go ahead with that for now.
     */
    private Item[][][] items;

    private int width;
    public int width() {
        return width;
    }

    private int height;
    public int height() {
        return height;
    }

    private int depth;
    public int depth() {
        return depth;
    }

    private List<Creature> creatures; // Our world's going to have a bunch of creatures

    public World(Tile[][][] tiles){
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.depth = tiles[0][0].length;
        this.creatures = new ArrayList<Creature>();
        this.items = new Item[width][height][depth];
    }

    /**
     * We need a way to get the creature at a specific location.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Creature creature(int x, int y, int z) {
        for (Creature c : creatures) {
            if (c.x == x && c.y == y && c.z == z) {
                return c;
            }
        }
        return null;
    }

    /**
     * We need a way to determine what item is in a location.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Item item(int x, int y, int z){
        return items[x][y][z];
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Tile tile(int x, int y, int z){

        /**
         * By checking for bounds here we don't need to worry about out of bounds errors and check every time we ask
         * the world about a location.
          */
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) {
            return Tile.BOUNDS;
        } else {
            return tiles[x][y][z];
        }
    }

    public char glyph(int x, int y, int z){
        Creature creature = creature(x, y, z);

        if (creature != null)
            return creature.glyph();

        if (item(x,y,z) != null)
            return item(x,y,z).glyph();

        return tile(x, y, z).glyph();
    }

    public Color color(int x, int y, int z){
        Creature creature = creature(x, y, z);

        if (creature != null)
            return creature.color();

        if (item(x,y,z) != null)
            return item(x,y,z).color();

        return tile(x, y, z).color();
    }

    /**
     * Make it possible to dig through a wall and turn the wall into floor.
     *
     * ToDO: Maybe it should leave some rubble behind, or "very small rocks".
     *
     * @param x
     * @param y
     */
    public void dig(int x, int y, int z) {
        if (tile(x,y,z).isDiggable())
            tiles[x][y][z] = Tile.FLOOR;
    }

    /**
     * Since the creature needs to start on some empty space and we don't really care which one, the addAtEmptyLocation
     * method will make sure the drop lands in a safe place.
     *
     * @param creature
     */
    public void addAtEmptyLocation(Creature creature, int z){
        int x;
        int y;

        do { // Search for an empty ground tile.
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        } while (!tile(x,y,z).isGround() || creature(x,y,z) != null);

        creature.x = x;
        creature.y = y;
        creature.z = z;
        creatures.add(creature);
    }

    /**
     *
     * @param item
     * @param depth
     */
    public void addAtEmptyLocation(Item item, int depth) {
        int x;
        int y;

        do {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        } while (!tile(x,y,depth).isGround() || item(x,y,depth) != null);

        items[x][y][depth] = item;
    }

    /**
     * Each creature should have something special it does instead of all being the same things with slightly different
     * stats to make things interesting. What if the fungi were able to reproduce and spread? We first need to let each
     * creature know when it's time to update itself and whatever else it wants to do for it's turn. This method lets
     * each creature know it's time to take a turn.
     */
    public void update(){
        List<Creature> toUpdate = new ArrayList<Creature>(creatures);
        for (Creature creature : toUpdate) {
            creature.update();
        }
    }

    /**
     * Remove a creature.
     *
     * @param other
     */
    public void remove(Creature other) {
        creatures.remove(other);
    }

    /**
     *  Remove an item.
     * @param x
     * @param y
     * @param z
     */
    public void remove(int x, int y, int z) {
        items[x][y][z] = null;
    }

    /**
     * This allows us to remove an object even if we don't know where it is.
     *
     * @param item
     */
    public void remove(Item item) {
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                for (int z = 0; z < depth; z++){
                    if (items[x][y][z] == item) {
                        items[x][y][z] = null;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Find an empty spot to put an item.
     *
     * Adding an item to a specific place is more complicated since we only allow one item per tile. Because of that,
     * we need to check adjacent tiles for an open space and repeat until we find one or run out of open spaces.
     *
     * ToDo: Change item mechanic so items can stack.
     *
     * @param item
     * @param x
     * @param y
     * @param z
     */
    public boolean addAtEmptySpace(Item item, int x, int y, int z){
        if (item == null)
            return true;

        List<Point> points = new ArrayList<Point>();
        List<Point> checked = new ArrayList<Point>();

        points.add(new Point(x, y, z));

        while (!points.isEmpty()){
            Point p = points.remove(0);
            checked.add(p);

            if (!tile(p.x, p.y, p.z).isGround())
                continue;

            if (items[p.x][p.y][p.z] == null){
                items[p.x][p.y][p.z] = item;
                Creature c = this.creature(p.x, p.y, p.z);
                if (c != null)
                    c.notify("A %s lands between your feet.", item.name());
                return true;
            } else {
                List<Point> neighbors = p.neighbors8();
                neighbors.removeAll(checked);
                points.addAll(neighbors);
            }
        }
        return false;
    }

}

