package game.screens;

import characterPanel.CharacterPanel;
import game.Line;
import game.Point;
import game.Tile;
import game.creatures.Creature;

import java.awt.event.KeyEvent;

/**
 * Created by Max on 3/21/2016.
 *
 * Screen to let us look around. We'll let the user pick a tile and then tell them what it is. If you think about it,
 * this isn't the only time the user will pick a tile through. Throwing, firing bows, and aiming spells all involve
 * picking a tile.
 *
 * We'll keep track of the player, a caption representing what we're targeting, the screen coordinates where the player
 * is looking from, and the s and y offset of where we're targeting. The player and caption are protected so our
 * subclasses can use them. Don't worry, it will make sense.
 */
public abstract class TargetBasedScreen implements Screen {

    protected Creature player;
    protected String caption;
    private int sx;
    private int sy;
    private int x;
    private int y;

    public TargetBasedScreen(Creature player, String caption, int sx, int sy){
        this.player = player;
        this.caption = caption;
        this.sx = sx;
        this.sy = sy;
    }

    /**
     * When it's time to display the output, we need to draw a line from the player to the target. I chose a line of
     * magenta *s, but that's up to you. We also need to display the caption to the user.
     *
     * @param terminal
     */
    public void displayOutput(CharacterPanel terminal) {
        for (Point p : new Line(sx, sy, sx + x, sy + y)){
            if (p.x < 0 || p.x > SCREEN_WIDTH || p.y < 0 || p.y > SCREEN_HEIGHT)
                continue;

            terminal.write(Tile.TARGETING_LINE.glyph(), p.x, p.y, Tile.TARGETING_LINE.color());
        }

        terminal.clear(Tile.UNKNOWN.glyph(), 0, SCREEN_HEIGHT, SCREEN_WIDTH, 1);
        terminal.write(caption, 0, SCREEN_HEIGHT);
    }

    /**
     * The user can change what's being targeted with the movement keys, select a target with Enter, or cancel with
     * Escape. If the user tries to target something it can't, like firing out of range, then we go back to where we
     * were targeting before.
     *
     * @param key
     * @return
     */
    public Screen respondToUserInput(KeyEvent key) {
        int px = x;
        int py = y;

        switch (key.getKeyCode()){
            case KeyEvent.VK_NUMPAD4:                                                   // Move Left
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_H: x--; break;
            case KeyEvent.VK_NUMPAD6:                                                   // Move Right
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_L: x++; break;
            case KeyEvent.VK_NUMPAD8:                                                   // Move Up
            case KeyEvent.VK_UP:
            case KeyEvent.VK_J: y--; break;
            case KeyEvent.VK_NUMPAD2:                                                   // Move Down
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_K: y++; break;
            case KeyEvent.VK_NUMPAD7:                                                   // Move Up & Left
            case KeyEvent.VK_HOME:
            case KeyEvent.VK_Y: x--; y--; break;
            case KeyEvent.VK_NUMPAD9:                                                   // Move Up & Right
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_U: x++; y--; break;
            case KeyEvent.VK_NUMPAD1:                                                   // Move Down & Left
            case KeyEvent.VK_END:
            case KeyEvent.VK_B: x--; y++; break;
            case KeyEvent.VK_NUMPAD3:                                                   // Move Down & Right
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_N: x++; y++; break;
            case KeyEvent.VK_ENTER: selectWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y); return null;
            case KeyEvent.VK_ESCAPE: return null;
        }

        if (!isAcceptable(player.x + x, player.y + y)){
            x = px;
            y = py;
        }

        enterWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y);

        return this;
    }

    /**
     * We'll provide a simple method to determine if a tile is an acceptable target. Subclasses can override this if
     * they want something more specific.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isAcceptable(int x, int y) {
        return true;
    }

    /**
     * After each time the target moves, we let subclasses do whatever they want, usually this will be to update the
     * caption or do nothing.
     *
     * @param x
     * @param y
     * @param screenX
     * @param screenY
     */
    public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
    }

    /**
     * And we do the same once the user has selected a specific location.
     */
    public void selectWorldCoordinate(int x, int y, int screenX, int screenY){
    }
}
