package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Max on 3/17/2016.
 *
 * It's super useful in a roguelike to understand the player's (and monster's) line of sight, and if something is in
 * it. To do this we get all the points in between us and what we want to look at and see if any of them block our
 * vision. For this, we can create a new Line class that uses Bresenham's line algorithm to find all the points along
 * the line.
 *
 * https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
 *
 * Note: If you look this all the work is done in the constructor - that's a bad sign. So says Misko Hevery of Google
 * fame, Martian Feathers of Working Effectively With Legacy Code, and anyone who's had to deal with this before. On
 * the other hand, it doesn't do that much work; it just creates a list of points. The points are value objects and the
 * line itself could be a value object.
 *
 * http://misko.hevery.com/code-reviewers-guide/flaw-constructor-does-real-work/
 * http://c2.com/cgi/wiki?ConstructorDoesTheWork
 */

public class Line implements Iterable<Point> {

    private List<Point> points;
    public List<Point> getPoints() {
        return points;
    }

    public Line(int x0, int y0, int x1, int y1) {
        points = new ArrayList<Point>();

        int dx = Math.abs(x1-x0);
        int dy = Math.abs(y1-y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx-dy;

        while (true){
            points.add(new Point(x0, y0, 0));

            if (x0==x1 && y0==y1)
                break;

            int e2 = err * 2;
            if (e2 > -dx) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx){
                err += dx;
                y0 += sy;
            }
        }
    }

    /**
     * To make things a tiny bit more convenient to loop through the points in a line, we can make the class implement
     * Iterable<Point>. All we have to do is declare that the Line implements Iterable<Point> and add the following
     * method:
     */
    @Override
    public Iterator<Point> iterator() {
        return points.iterator();
    }
}