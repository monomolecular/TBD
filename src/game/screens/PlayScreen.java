package game.screens;

import characterPanel.CharacterPanel;
import game.Tile;
import game.World;
import game.WorldBuilder;
import game.creatures.Creature;
import game.creatures.CreatureFactory;
import game.creatures.FieldOfView;
import game.items.Item;
import game.items.ItemFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static characterPanel.CharacterData.createFontGlyph;
import static characterPanel.CharacterPanel.glyphMap;

/**
 * Created by Max on 3/9/2016.
 *
 * The PlayScreen class will be responsible for showing the dungeon and all it's inhabitants and loot — but since we
 * don't have that yet we'll just tell the player how much fun he's having. It will also respond to user input by
 * moving the player and either setting us in "win" mode if we won the game, or "lose" mode if we lost.
 *
 * For additional screens the player will interact with while playing, we'll have the PlayScreen know if we're working
 * with a sub screen and delegate input and output to it. Once the subscreen is done, it get's set to null and the
 * PlayScreen works as normal.
 *
 * The PlayScreen is getting a little out of hand now that it creates a new world, displays the world, handles user
 * commands and deals with subscreens. Maybe the part about setting up a new game should be moved somewhere else.
 * ToDo: Move game setup out of PlayScreen.
  */

public class PlayScreen implements Screen {
    /**
     * We need to track the world we're looking at, what part we're looking at, and how much of the screen is used for
     * displaying the world. Here's the variables and constructor to add to the PlayScreen:
     */

    private World world;
    private Creature player;
    private List<String> messages;
    private FieldOfView fov;
    private Screen subscreen;

    public PlayScreen() throws UnsupportedEncodingException {

        messages = new ArrayList<String>();
        createWorld();
        fov = new FieldOfView(world);

        CreatureFactory creatureFactory = new CreatureFactory(world);
        createCreatures(creatureFactory);

        ItemFactory itemFactory = new ItemFactory(world);
        createItems(itemFactory);
    }

    public static void createTiles() throws UnsupportedEncodingException {

        // Loop through the game defined tiles and create glyphs for each character.
        for (Tile t : Tile.values()){
            char character = t.glyph();

            BufferedImage img = createFontGlyph(character, "DejaVu Sans Mono",15,0,false);
            glyphMap.put(character, img);
        }

        // Add glyphs for the basic alpha-numeric and punctuation characters.
        for (int i = 32; i <= 126; i++) {
            BufferedImage img = createFontGlyph(i, "DejaVu Sans Mono",15,0,false);
            glyphMap.put((char)i, img);
        }
    }

    /**
     * The createWorld method does exactly that, create's a world. I have a feeling this is going to expand as we make
     * the world more interesting so putting it in a separate method will reduce how tangled it get's with other code
     * and make changes easier later on.
     */
    private void createWorld(){
        // world = new WorldBuilder(90, 32, 5)
        world = new WorldBuilder(SCREEN_WIDTH, SCREEN_HEIGHT, 5)
                .makeCaves()
                .build();
    }

    /**
     * Populate the world with both our player and also a bunch of fungus.
     *
     * @param creatureFactory
     */
    private void createCreatures(CreatureFactory creatureFactory){
        player = creatureFactory.newPlayer(messages, fov); //Create the player "creature".

        for (int z = 0; z < world.depth(); z++){

            // Two fungus among us.
            for (int i = 0; i < 2; i++) {
                creatureFactory.newFungus(z);
            }

            // How about 20 bats per level.
            for (int i = 0; i < 20; i++){
                creatureFactory.newBat(z);
            }

            // Add a couple of zombies and goblins to each level.
            for (int i = 0; i < z + 3; i++){
                creatureFactory.newZombie(z, player);
                creatureFactory.newGoblin(z, player);
            }

            // Just one alicorn per level.
            creatureFactory.newAlicorn(z);
        }
    }

    private void createItems(ItemFactory itemFactory) {

        // Scatter some rocks around.
        for (int z = 0; z < world.depth(); z++){
            for (int i = 0; i < world.width() * world.height() / 20; i++){
                itemFactory.newRock(z);
            }

            // Drop a few food rations per level.
            for (int i = 0; i < world.width() * world.height() / 1000; i++){
                itemFactory.newRation(z);
            }

            for (int i = 0; i < 5; i++){
                itemFactory.randomArmor(z);
            }

            for (int i = 0; i < 5; i++){
                itemFactory.randomWeapon(z);
            }

            for (int i = 0; i < 5; i++) {
                itemFactory.randomPotion(z);
            }

            for (int i=0; i < 20; i++) {
                itemFactory.randomSpellBook(z);
            }
        }

        // Find this and return it to the surface and you win the game.
        itemFactory.newVictoryItem(world.depth() - 1);
    }
    /**
     * We need a method to tell us how far along the X axis we should scroll. This makes sure we never try to scroll
     * too far to the left or right.
     *
     * @return
     */
    public int getScrollX() {
        return Math.max(0, Math.min(player.x - SCREEN_WIDTH / 2, world.width() - SCREEN_WIDTH));
    }

    /**
     * And we need a method to tell us how far along the Y axis we should scroll. This makes sure we never try to
     * scroll too far to the top or bottom.
     *
     * @return
     */
    public int getScrollY() {
        return Math.max(0, Math.min(player.y - SCREEN_HEIGHT / 2, world.height() - SCREEN_HEIGHT));
    }

    /**
     * We need a method to display some tiles. This takes a left and top to know which section of the world it should
     * display.
     *
     * This is actually a very inefficient way to do this. It would be far better to draw all the tiles and then, for
     * each creature, draw it if it is in the viewable region of left to left+screenWidth and top to top+screenHeight.
     * That way we loop through screenWidth * screenHeight tiles + the number of creatures. The way I wrote we loop
     * through screenWidth * screenHeight * the number of creatures. That's much worse. I don't know why I didn't
     * realize this when I first wrote this since I've always drawn the creatures after the tiles. Consider this an
     * example of one way to not do it.
     *
     * ToDo: Change displayTiles to a more optimal implementation.
     *
     * Instead of checking if there's a creature at each point and then drawing it or the ground, draw all the ground
     * tiles and then loop through the creatures and draw the ones that should be on the screen. That way you loop
     * through the creatures only once instead of once for each space you want to draw.
     *
     * @param terminal
     * @param left
     * @param top
     */
    private void displayTiles(CharacterPanel terminal, int left, int top) {
        fov.update(player.x, player.y, player.z, player.visionRadius());

        for (int x = 0; x < SCREEN_WIDTH; x++){
            for (int y = 0; y < SCREEN_HEIGHT; y++){
                int wx = x + left;
                int wy = y + top;

                if (player.canSee(wx, wy, player.z)) {
                    terminal.write(world.glyph(wx, wy, player.z), x, y, world.color(wx, wy, player.z));
                } else {
                    terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, Color.darkGray);
                }
            }
        }
    }


    @Override
    public void displayOutput(CharacterPanel terminal) {
        /**
         *  Show the section we're looking at on part of the screen - the rest of the screen is for user stats,
         *  messages, etc.
         */
        int left = getScrollX();
        int top = getScrollY();

        displayTiles(terminal, left, top);
        displayMessages(terminal, messages);

        /**
         * Yes, using escape and enter to lose and win is pretty lame, but we know it's temporary and we can swap it
         * out for real stuff later.
         */
        //terminal.writeCenter("-- press [escape] to lose or [enter] to win --", 23);

        /**
         * Write out our hero glyph to the play screen.
         */
        //terminal.write(player.glyph(), player.x - left, player.y - top, player.color());

        /**
         * Write out some stats for our player on the play screen.
         */
        String stats = String.format(" %3d/%3d hp  %d/%d mana  %8s",
                player.hp(), player.maxHp(), player.mana(), player.maxMana(), hunger());
        terminal.write(stats, 1, SCREEN_HEIGHT);

        /**
         * After we displayOutput the subscreen should get a chance to display. This way the current game world will be
         * a background to whatever the subscreen wants to show.
         */
        if (subscreen != null)
            subscreen.displayOutput(terminal);
    }

    /**
     * Helper method for the player stats. These should probably be refactored into the creature class.
     *
     * ToDo: Refactor hunger levels somewhere else.
     *
     * @return
     */
    private String hunger(){
        if (player.food() < player.maxFood() * 0.1)
            return "Starving";
        else if (player.food() < player.maxFood() * 0.2)
            return "Hungry";
        else if (player.food() > player.maxFood() * 0.9)
            return "Stuffed";
        else if (player.food() > player.maxFood() * 0.8)
            return "Full";
        else
            return "Satiated";
    }

    /**
     * Displaying messages can also be done many different ways. Starting out simply, just list them all on the screen
     * at once.
     *
     * @param terminal
     * @param messages
     */
    private void displayMessages(CharacterPanel terminal, List<String> messages) {
        int top = SCREEN_HEIGHT - messages.size();
        for (int i = 0; i < messages.size(); i++){
            terminal.writeCenter(messages.get(i), top + i);
        }
        messages.clear();
    }

    private boolean userIsTryingToExit(){
        return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
    }

    private Screen userExits(){
        for (Item item : player.inventory().getItems()){
            if (item != null && item.name().equals("victory item"))
                return new WinScreen();
        }
        return new LoseScreen();
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) throws UnsupportedEncodingException {
        // Before the user does anything, we need to record the player's level.
        int level = player.level();

        /**
         * Any user input needs to be sent to the subscreen if it exists. The subscreen will also tell the
         * PlayScreen what the new subscreen is. We also need to handle the users pressing the 'd' key to drop
         * items from inventory.
         */
        if (subscreen != null) {
            subscreen = subscreen.respondToUserInput(key);
        } else {
            switch (key.getKeyCode()){
                //case KeyEvent.VK_R:         return new PlayScreen(); // Restart and generate a new play screen.
                // case KeyEvent.VK_ESCAPE:    return new LoseScreen(); // Quit and go to the lose screen.
                // case KeyEvent.VK_ENTER:     return new WinScreen();  // "Win" the game and go to the win screen.
                case KeyEvent.VK_A:         subscreen = new ASCIIScreen(); break;           // ASCII
                case KeyEvent.VK_D:         subscreen = new DropScreen(player); break;      // Drop
                case KeyEvent.VK_E:         subscreen = new EatScreen(player); break;       // Eat
                case KeyEvent.VK_W:         subscreen = new EquipScreen(player); break;     // Wield/Wear
                case KeyEvent.VK_Q:         subscreen = new QuaffScreen(player); break;     // Quaff
                case KeyEvent.VK_R:         subscreen = new ReadScreen(player,              // Read
                                                            player.x - getScrollX(),
                                                            player.y - getScrollY()); break;
                case KeyEvent.VK_SEMICOLON: subscreen = new LookScreen(player, "Looking",   // Look
                                                            player.x - getScrollX(),
                                                            player.y - getScrollY()); break;
                case KeyEvent.VK_T:         subscreen = new ThrowScreen(player,             // Throw Stuff
                                                            player.x - getScrollX(),
                                                            player.y - getScrollY()); break;
                case KeyEvent.VK_F:
                    if (player.weapon() == null || player.weapon().rangedAttackValue() == 0)
                        player.notify("You don't have a ranged weapon equiped.");
                    else
                                            subscreen = new FireWeaponScreen(player,        // Fire Weapon
                                                            player.x - getScrollX(),
                                                            player.y - getScrollY()); break;

                case KeyEvent.VK_NUMPAD4:                                                   // Move Left
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_H:         player.moveBy(-1, 0, 0); break;
                case KeyEvent.VK_NUMPAD6:                                                   // Move Right
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_L:         player.moveBy(1, 0, 0); break;
                case KeyEvent.VK_NUMPAD8:                                                   // Move Up
                case KeyEvent.VK_UP:
                case KeyEvent.VK_K:         player.moveBy(0, -1, 0); break;
                case KeyEvent.VK_NUMPAD2:                                                   // Move Down
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_J:         player.moveBy(0, 1, 0); break;
                case KeyEvent.VK_NUMPAD7:                                                   // Move Up & Left
                case KeyEvent.VK_HOME:
                case KeyEvent.VK_Y:         player.moveBy(-1, -1, 0); break;
                case KeyEvent.VK_NUMPAD9:                                                   // Move Up & Right
                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_U:         player.moveBy(1, -1, 0); break;
                case KeyEvent.VK_NUMPAD1:                                                   // Move Down & Left
                case KeyEvent.VK_END:
                case KeyEvent.VK_B:         player.moveBy(-1, 1, 0); break;
                case KeyEvent.VK_NUMPAD3:                                                   // Move Down & Right
                case KeyEvent.VK_PAGE_DOWN:
                case KeyEvent.VK_N:         player.moveBy(1, 1, 0); break;
            }

            switch (key.getKeyChar()) {
                case '?':                   subscreen = new HelpScreen(); break;            // Help
                case 'x':                   subscreen = new ExamineScreen(player); break;   // Examine Invemtory
                case 'g':
                case ',':                   player.pickup(); break;
                case '<':                   if (userIsTryingToExit()) return userExits();
                                            else  player.moveBy( 0, 0, -1); break;
                case '>':                   player.moveBy(0, 0, 1); break;
            }
        }

        if (subscreen == null) world.update(); // Update the world only if we don't have a subscreen.

        if (player.hp() < 1) return new LoseScreen();

        /**
         * After responding to the player's input, we need to see if that resulted in a level up. If so, we jump into a
         * LevelUpScreen and tell it how many bonuses the player get's to pick.
         */
        if (player.level() > level)
            subscreen = new LevelUpScreen(player, player.level() - level);

        return this;
    }
}
