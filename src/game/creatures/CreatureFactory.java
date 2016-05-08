package game.creatures;

import static game.Tile.*;
import game.World;
import java.util.List;

import game.items.ItemFactory;

/**
 * Created by Max on 3/10/2016.
 *
 * We're going to create a lot of creatures that have the same values, all goblins will have a g glyph etc, and we need
 * to make sure we always wire up the correct ai for each new creature. To centralize and hide all this assembly we'll
 * create a class that's responsible for nothing else: the CreatureFactory. Using a factory means the other code
 * doesn't have to deal with all this assembly each time a new creature is created.
 */

public class CreatureFactory {
    private World world;

    public CreatureFactory(World world){
        this.world = world;
    }

    // Creature(World world, char glyph, Color color, int maxHp, int attack, int defense, int vision)

    /**
     * The only creature we're assembling so far is the player so that's the only method we need to add.
     */
    public Creature newPlayer(List<String> messages, FieldOfView fov){
        Creature player = new Creature(world, HUMAN.glyph(), HUMAN.color(), HUMAN.label(), 1000, 20, 5, 9); //@
        world.addAtEmptyLocation(player, 0);
        new PlayerAi(player, messages, fov);
        return player;
    }

    /**
     * There's fungus among us.
     *
     * @return
     */
    public Creature newFungus(int depth){
        Creature fungus = new Creature(world, FUNGUS.glyph(), FUNGUS.color(), FUNGUS.label(), 10, 0, 0, 0); //f
        world.addAtEmptyLocation(fungus, depth);
        new FungusAi(fungus, this);
        return fungus;
    }

    /**
     * Basic bat... low hp and attack so they could nibble on you a bit but shouldn't be too much of a problem.
     * @param depth
     * @return
     */
    public Creature newBat(int depth){
        Creature bat = new Creature(world, BAT.glyph(), BAT.color(), BAT.label(), 15, 5, 0, 8); //b
        world.addAtEmptyLocation(bat, depth);
        new BatAi(bat);
        return bat;
    }

    /**
     * Alexa's favorit mythical creature of all time is an alicorn. The mythical cross between a unicorn and a pegasi.
     *
     * @return
     */
    public Creature newAlicorn(int depth){
        Creature alicorn = new Creature(world, ALICORN.glyph(), ALICORN.color(), ALICORN.label(), 100, 10, 9, 9); // Æ
        world.addAtEmptyLocation(alicorn, depth);
        new AlicornAi(alicorn);
        return alicorn;
    }

    // ToDo: Create a unicorn and pegasi.

    public Creature newZombie(int depth, Creature player){
        Creature zombie = new Creature(world, ZOMBIE.glyph(), ZOMBIE.color(), ZOMBIE.label(), 50, 10, 10, 20); // z
        world.addAtEmptyLocation(zombie, depth);
        new ZombieAi(zombie, player);
        return zombie;
    }

    public Creature newGoblin(int depth, Creature player){
        Creature goblin = new Creature(world, GOBLIN.glyph(), GOBLIN.color(), GOBLIN.label(), 66, 15, 5, 20); // g
        ItemFactory itemFactory = new ItemFactory(world);
        goblin.equip(itemFactory.randomWeapon(depth));
        goblin.equip(itemFactory.randomArmor(depth));
        world.addAtEmptyLocation(goblin, depth);
        new GoblinAi(goblin, player);
        return goblin;
    }
}