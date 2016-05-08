package game.creatures;

import game.Line;
import game.Point;
import game.Tile;
import game.World;

/**
 * Created by Max on 3/17/2016.
 *
 * There are several different ways of determining what is in the player's field of view. The simplest, is called
 * raycasting.
 * http://www.roguebasin.com/index.php?title=Line_of_sight
 *
 * It's exactly what we're already doing: draw a line from the viewer to the tile in question to see if
 * anything is blocking the vision. Raycasting is probably the slowest way, but it's quick enough and arguably has the
 * best overall look. Other methods perform differently when columns and doorways are involved.
 *
 * We can slightly extend the common definition to not only determine what is in view but to remember what has already
 * been seen too. What's visible now and what was seen earlier are technically two different things and possibly should
 * be implemented by two different classes, but they're close enough and we can change it later if necessary.
 */
public class FieldOfView {
    private World world;
    private int depth;

    private boolean[][] visible;

    // Check if a tile is visible.
    public boolean isVisible(int x, int y, int z){
        return z == depth && x >= 0 && y >= 0 && x < visible.length && y < visible[0].length && visible[x][y];
    }

    private Tile[][][] tiles;
    public Tile tile(int x, int y, int z){
        return tiles[x][y][z];
    }

    public FieldOfView(World world){
        this.world = world;
        this.visible = new boolean[world.width()][world.height()];
        this.tiles = new Tile[world.width()][world.height()][world.depth()];

        for (int x = 0; x < world.width(); x++){
            for (int y = 0; y < world.height(); y++){
                for (int z = 0; z < world.depth(); z++){
                    tiles[x][y][z] = Tile.UNKNOWN;
                }
            }
        }
    }

    // Update what's visible and has been seen.
    public void update(int wx, int wy, int wz, int r){
        depth = wz;
        visible = new boolean[world.width()][world.height()];

        for (int x = -r; x < r; x++){
            for (int y = -r; y < r; y++){
                if (x*x + y*y > r*r)
                    continue;

                if (wx + x < 0 || wx + x >= world.width()
                        || wy + y < 0 || wy + y >= world.height())
                    continue;

                for (Point p : new Line(wx, wy, wx + x, wy + y)){
                    Tile tile = world.tile(p.x, p.y, wz);
                    visible[p.x][p.y] = true;
                    tiles[p.x][p.y][wz] = tile;

                    if (!tile.isGround())
                        break;
                }
            }
        }
    }
}
