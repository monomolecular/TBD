package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Max on 3/9/2016.
 *
 * The World class runs the world. Here in the WorldBuilder Class lies the responsibility is building a new world, you
 * could use the Builder pattern and therefore we call it WorldBuilder.
 * http://en.wikipedia.org/wiki/Builder_pattern
 *
 * To create a WorldBuilder you need a world size. Then you can call methods, in fluent style, to build up a world.
 * Once you've specified how to build the world you want, you call the build method and you get a new World to play
 * with.
 * https://en.wikipedia.org/wiki/Fluent_interface
 */

public class WorldBuilder {
    private int width;
    private int height;
    private int depth;
    private Tile[][][] tiles;
    private int[][][] regions;
    private int nextRegion;

    public WorldBuilder(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.tiles = new Tile[width][height][depth];
        this.regions = new int[width][height][depth];
        this.nextRegion = 1;
    }

    /**
     * Create a World of Tiles to play around in. But in order to play in our new world of cave floors and cave walls,
     * we need to display it via the PlayScreen class, which makes since because it's responsible for displaying the
     * world we're playing in and reacting to player input.
     *
     * @return
     */
    public World build() {
        return new World(tiles);
    }

    /**
     * One of the simplest interesting (i.e. randomized) worlds is a world of caves. It can be done with a fairly basic
     * algorithm, it's a simple form of cellular automata. The process is to fill the area with cave floors and walls
     * at random then to smooth everything out by turning areas with mostly neighboring walls into walls and areas with
     * mostly neighboring floors into floors. Repeat the smoothing process a couple times and you have an interesting
     * mix of cave walls and floors.
     *
     * http://www.roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels
     *
     * So the builder should be able to randomize the tiles.
     *
     * PS - You can use System.arraycopy to resize arrays. Technically you can do it with Arrays.copyOf, but that only
     * works for single dimensional arrays for some odd reason.
     * @return
     */
    private WorldBuilder randomizeTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    tiles[x][y][z] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
                }
            }
        }
        return this;
    }

    /**
     * ...and then repeatedly smooth them out.
     *
     * We put the new tile into tempTiles because it's usually a bad idea to update data that you're using as input to
     * next updates. It's hard to explain but if you change the code to not use the tempTiles variable you'll see what
     * I mean.
     *
     * I don't like all those nested loops. Arrow code like this is usually a bad sign but this is simple enough
     * and only used during world gen so I'll leave it as it is for now. This is also just part of working with
     * multi-dimentional arrays in java.
     *
     * http://www.codinghorror.com/blog/2006/01/flattening-arrow-code.html
     *
     * The process is to fill the area with cave floors and walls at random then to smooth everything out by
     * turning areas with mostly neighboring walls into walls and areas with mostly neighboring floors into floors.
     * Repeat the smoothing process a couple times and you have an interesting mix of cave walls and floors.
     *
     * @param times
     * @return
     */
    private WorldBuilder smooth(int times) {
        Tile[][][] tempTiles = new Tile[width][height][depth];

        // loop so many times
        for (int time = 0; time < times; time++) {

            // loop through all the tiles
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        int floors = 0;
                        int rocks = 0;

                        // The neighbour is the tile -1 and +1
                        // x and y combinated it's a 3x3 field of neighbours that is being checked
                        for (int ox = -1; ox < 2; ox++) {
                            for (int oy = -1; oy < 2; oy++) {
                                // if the neighbour position is out of bound just continue
                                if (x + ox < 0 || x + ox >= width || y + oy < 0 || y + oy >= height) continue;
                                // count if the neighbour tiles are floors or rocks
                                if (tiles[x + ox][y + oy][z] == Tile.FLOOR) floors++;
                                else rocks++;
                            }
                        }
                        // if the neighbour tiles are mostly floors make this tile also a floor
                        tempTiles[x][y][z] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
                    }
                }
            }
            // in the end, store tempTiles in the real tiles
            tiles = tempTiles;
        }
        return this;
    }

    /**
     * Create a region map. Each location has a number that identifies what region of contiguous open space it belongs
     * to; i.e. if two locations have the same region number, then you can walk from one to the other without digging
     * through walls.
     *
     * This will look at every space in the world. If it is not a wall and it does not have a region assigned then that
     * empty space, and all empty spaces it's connected to, will be given a new region number. If the region is to
     * small it gets removed. When this method is done, all open tiles will have a region assigned to it and we can use
     * the regions array to see if two tiles are part of the same open space.
     *
     * @return
     */

    private WorldBuilder createRegions(){
        /**
         * The region variable is just a way to uniquely combine two numbers into one. This way we just need a list of
         * the region strings instead of some list of pairs or something. If java had tuples then we could use that
         * instead of this way.
         */
        regions = new int[width][height][depth];

        for (int z = 0; z < depth; z++){
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){
                    if (tiles[x][y][z] != Tile.WALL && regions[x][y][z] == 0){
                        int size = fillRegion(nextRegion++, x, y, z);
                        if (size < 25) removeRegion(nextRegion - 1, z);
                    }
                }
            }
        }
        return this;
    }

    /**
     * The removeRegion method does what it sounds like. It just zero's out the region number and fills in the cave so
     * it's solid wall. I prefer caves where the smaller areas have been filled in but this step isn't necessary.
     *
     * @param region
     * @param z
     */
    private void removeRegion(int region, int z){
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                if (regions[x][y][z] == region){
                    regions[x][y][z] = 0;
                    tiles[x][y][z] = Tile.WALL;
                }
            }
        }
    }

    /**
     * The fillRegion method does a flood-fill starting with an open tile. It, and any open tile it's connected to,
     * gets assigned the same region number. This is repeated until there are no unassigned empty neighboring tiles.
     *
     * @param region
     * @param x
     * @param y
     * @param z
     * @return
     */
    private int fillRegion(int region, int x, int y, int z) {
        int size = 1;
        ArrayList<Point> open = new ArrayList<Point>();
        open.add(new Point(x,y,z));
        regions[x][y][z] = region;

        while (!open.isEmpty()) {
            Point p = open.remove(0);

            for (Point neighbor : p.neighbors8()) {
                if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= width || neighbor.y >= height)
                    continue;

                if (regions[neighbor.x][neighbor.y][neighbor.z] > 0
                        || tiles[neighbor.x][neighbor.y][neighbor.z] == Tile.WALL)
                    continue;

                size++;
                regions[neighbor.x][neighbor.y][neighbor.z] = region;
                open.add(neighbor);
            }
        }
        return size;
    }

    /**
     * To connect all the regions with stairs we just start at the top and connect them one layer at a time.
     *
     * @return
     */
    public WorldBuilder connectRegions(){
        for (int z = 0; z < depth-1; z++){
            connectRegionsDown(z);
        }
        return this;
    }

    /**
     * To connect two adjacent layers we look at each region that sits above another region. If they haven't been
     * connected then we connect them.
     *
     * @param z
     */
    private void connectRegionsDown(int z){
        List<String> connected = new ArrayList<String>();

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                String region = regions[x][y][z] + "," + regions[x][y][z+1];
                if (tiles[x][y][z] == Tile.FLOOR
                        && tiles[x][y][z+1] == Tile.FLOOR
                        && !connected.contains(region)){
                    connected.add(region);
                    connectRegionsDown(z, regions[x][y][z], regions[x][y][z+1]);
                }
            }
        }
    }

    /**
     * To connect two regions, we get a list of all the locations where one is directly above the other. Then, based on
     * how much area overlaps, we connect them with stairs going up and stairs going down.
     *
     * @param z
     * @param r1
     * @param r2
     */
    private void connectRegionsDown(int z, int r1, int r2){
        List<Point> candidates = findRegionOverlaps(z, r1, r2);

        int stairs = 0;
        do{
            Point p = candidates.remove(0);
            tiles[p.x][p.y][z] = Tile.STAIRS_DOWN;
            tiles[p.x][p.y][z+1] = Tile.STAIRS_UP;
            stairs++;
        }
        while (candidates.size() / stairs > 250);
    }

    /**
     * Finding which locations of two regions overlap is pretty straight forward.
     *
     * @param z
     * @param r1
     * @param r2
     * @return
     */
    public List<Point> findRegionOverlaps(int z, int r1, int r2) {
        ArrayList<Point> candidates = new ArrayList<Point>();

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                if (tiles[x][y][z] == Tile.FLOOR
                        && tiles[x][y][z+1] == Tile.FLOOR
                        && regions[x][y][z] == r1
                        && regions[x][y][z+1] == r2){
                    candidates.add(new Point(x,y,z));
                }
            }
        }

        Collections.shuffle(candidates);
        return candidates;
    }

    /**
     * Make the caves...
     *
     * @return
     */
    public WorldBuilder makeCaves() {
        return randomizeTiles()
                .smooth(8)
                .createRegions()
                .connectRegions()
                .addExitStairs();
    }

    private WorldBuilder addExitStairs() {
        int x = -1;
        int y = -1;

        do {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        }
        while (tiles[x][y][0] != Tile.FLOOR);

        tiles[x][y][0] = Tile.STAIRS_UP;
        return this;
    }
}


