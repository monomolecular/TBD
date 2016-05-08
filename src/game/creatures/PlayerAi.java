package game.creatures;

import game.Tile;

import java.util.List;

/**
 * Created by Max on 3/10/2016.
 *
 * We're going to have a specific ai for the player so it doesn't matter what we have in the CreatureAi class. Here
 * we're just overriding the basic Creature AI.
 */
public class PlayerAi extends CreatureAi {

    /**
     * Instead of creating a getter for the message list we rely on constructor injection. That means the list comes
     * from somewhere else that may already have a reference to it. We can create the list in the PlayScreen and pass
     * it to the creature factory which passes it to the new PlayerAi. Since the PlayScreen already has the list, it
     * can easily display any messages that show up and clear the list afterwards.
     *
     * Since the FieldOfView requires a world to be passed in the constructor and we don't want the ai's to know about
     * the world, we can build the FieldOfView elseware and rely on constructor injection to give it to the PlayerAi.
     * This means it will have to be passed into the CreatureFactory from the PlayScreen too.
     */
    private List<String> messages;
    private FieldOfView fov;

    public PlayerAi(Creature creature, List<String> messages, FieldOfView fov) {
        super(creature);
        this.messages = messages;
        this.fov = fov;
    }

    /**
     * We need to override the onEnter method to dig through walls and walk on ground tiles.
     *
     * If your world has doors then you can make the player automatically open them by walking into them and extending
     * the code below.
     *
     * @param x
     * @param y
     * @param tile
     */
    @Override
    public void onEnter(int x, int y, int z, Tile tile){

        /**
         * Instead of checking the tile type directly we just ask if it can be walked on or dug through like with
         * isDiggable.
         */
        if (tile.isGround()){
            creature.x = x;
            creature.y = y;
            creature.z = z;
        } else if (tile.isDiggable()) {
            creature.dig(x, y, z);
        }
    }

    /**
     *
     * @param message
     */
    @Override
    public void onNotify(String message){
        // Add messages to a list.
        messages.add(message);
    }

    /**
     * Only the player is going to use this advanced field of view, all other creatures can use the default line of
     * sight code. This method overrides the basic one in CreatureAI.
     *
     * @param wx
     * @param wy
     * @param wz
     * @return
     */

    @Override
    public boolean canSee(int wx, int wy, int wz) {
        return fov.isVisible(wx, wy, wz);
    }

    @Override
    public Tile rememberedTile(int wx, int wy, int wz) {
        return fov.tile(wx, wy, wz);
    }

    /**
     * What about when the player gains a level? Shouldn't we show a list of options for the user to choose from? Let's
     * start by making sure the player doesn't automatically get free bonuses.
     */
    @Override
    public void onGainLevel(){
    }
}
