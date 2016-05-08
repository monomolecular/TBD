package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Max on 3/12/2016.
 *
 * In order to build better caves we're going to have to work with coordinates a lot. We should make a class to
 * represent a point in space. This is that class.
 */

public class Point {
    public int x;
    public int y;
    public int z;

    public Point(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    /**
     * Two points that represent the same location should be treated as equal. These are known as value objects as
     * opposed to reference objects. We can tell Java that by overriding the hashCode and equals methods.
     * Here's what Eclipse generated...
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Point))
            return false;
        Point other = (Point) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        if (z != other.z)
            return false;
        return true;
    }

    /**
     * We're also going to spend a lot of time working with points that are adjacent to something. This will be much
     * easier if we can just ask a point for a list of it's eight neighbors.
     *
     * We shuffle the list before returning it so we don't introduce bias. Otherwise the upper left neighbor would
     * always be checked first and the lower right would be last which may lead to some odd things.
     *
     * @return
     */
    public List<Point> neighbors8(){
        List<Point> points = new ArrayList<Point>();

        for (int ox = -1; ox < 2; ox++){
            for (int oy = -1; oy < 2; oy++){
                if (ox == 0 && oy == 0)
                    continue;

                int nx = x+ox;
                int ny = y+oy;

                points.add(new Point(nx, ny, z));
            }
        }

        Collections.shuffle(points);
        return points;
    }
}