package game.items;

import characterPanel.CharacterPanel;
import game.World;
import game.creatures.Creature;
import game.creatures.CreatureFactory;

import static game.Tile.*;

/**
 * Created by Max on 3/19/2016.
 *
 * Piles of items could be implemented as just a list of items (e.g., the world would have List[][][]) or as a new Item
 * that acts as a pile. Will have to update some of the methods that deal with getting or placing items in the world.
 * Searching through piles would best be implemented as another screen.
 *
 * ToDo: Consider piles of items.
 */

public class ItemFactory {
    private World world;

    public ItemFactory(World world){
        this.world = world;
    }

    public Item newRock(int depth){
        Item item = new Item(ROCK.glyph(), ROCK.color(), ROCK.label());
        world.addAtEmptyLocation(item, depth);
        item.modifyThrownAttackValue(1);
        return item;
    }

    public Item newVictoryItem(int depth){
        Item item = new Item(VICTORY.glyph(), VICTORY.color(), VICTORY.label());
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newRation(int depth) {
        Item ration = new Item(RATION.glyph(), RATION.color(), RATION.label());
        ration.modifyFoodValue(500);
        world.addAtEmptyLocation(ration, depth);
        return ration;
    }

    public Item newDagger(int depth){
        Item item = new Item(DAGGER.glyph(), DAGGER.color(), DAGGER.label());
        item.modifyAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newSword(int depth){
        Item item = new Item(SWORD.glyph(), SWORD.color(), SWORD.label());
        item.modifyAttackValue(10);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newStaff(int depth){
        Item item = new Item(BO_STAFF.glyph(), BO_STAFF.color(), BO_STAFF.label());
        item.modifyAttackValue(5);
        item.modifyDefenseValue(3);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newLightArmor(int depth){
        Item item = new Item(TUNIC.glyph(), TUNIC.color(), TUNIC.label());
        item.modifyDefenseValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newMediumArmor(int depth){
        Item item = new Item(CHAINMAILLE.glyph(), CHAINMAILLE.color(), CHAINMAILLE.label());
        item.modifyDefenseValue(4);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newHeavyArmor(int depth){
        Item item = new Item(PLATED_MAIL.glyph(), PLATED_MAIL.color(), PLATED_MAIL.label());
        item.modifyDefenseValue(6);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newEdibleWeapon(int depth){
        Item item = new Item(BAGUETTE.glyph(), BAGUETTE.color(), BAGUETTE.label());
        item.modifyAttackValue(3);
        item.modifyFoodValue(50);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newBow(int depth){
        Item item = new Item(SHORT_BOW.glyph(), SHORT_BOW.color(), SHORT_BOW.label());
        item.modifyAttackValue(1);
        item.modifyRangedAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item randomWeapon(int depth){
        switch ((int)(Math.random() * 5)){
            case 0: return newDagger(depth);
            case 1: return newSword(depth);
            case 2: return newStaff(depth);
            case 3: return newBow(depth);
            default: return newEdibleWeapon(depth);
        }
    }

    public Item randomArmor(int depth){
        switch ((int)(Math.random() * 3)){
            case 0: return newLightArmor(depth);
            case 1: return newMediumArmor(depth);
            default: return newHeavyArmor(depth);
        }
    }

    /**
     * Aimple one-time potion where the work happens in the start method.
     * @param depth
     * @return
     */
    public Item newPotionOfHealth(int depth){
        Item item = new Item(POTION.glyph(), CharacterPanel.white, "health potion");
        item.setQuaffEffect(new Effect(1){
            public void start(Creature creature){
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(15);
                creature.doAction("look healthier");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newPotionOfMana(int depth){
        Item item = new Item(POTION.glyph(), CharacterPanel.white, "mana potion");
        item.setQuaffEffect(new Effect(1){
            public void start(Creature creature){
                if (creature.mana() == creature.maxMana())
                    return;

                creature.modifyMana(15);
                creature.doAction("look electrified");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Here is a potion that affects the creature each turn.
     * @param depth
     * @return
     */
    public Item newPotionOfPoison(int depth){
        Item item = new Item(POTION.glyph(), CharacterPanel.white, "poison potion");
        item.setQuaffEffect(new Effect(20){
            public void start(Creature creature){
                creature.doAction("look sick");
            }

            public void update(Creature creature){
                super.update(creature);
                creature.modifyHp(-1);
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Here's one that will affect the creature at the start and restore it at the end.
     * @param depth
     * @return
     */
    public Item newPotionOfWarrior(int depth){
        Item item = new Item(POTION.glyph(), CharacterPanel.white, "warrior's potion");
        item.setQuaffEffect(new Effect(20){
            public void start(Creature creature){
                creature.modifyAttackValue(5);
                creature.modifyDefenseValue(5);
                creature.doAction("look stronger");
            }
            public void end(Creature creature){
                creature.modifyAttackValue(-5);
                creature.modifyDefenseValue(-5);
                creature.doAction("look less strong");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Randomizer to help us add the new potions.
     *
     * @param depth
     * @return
     */
    public Item randomPotion(int depth){
        switch ((int)(Math.random() * 4)){
            case 0: return newPotionOfHealth(depth);
            case 1: return newPotionOfMana(depth);
            case 2: return newPotionOfPoison(depth);
            default: return newPotionOfWarrior(depth);
        }
    }

    public Item newWhiteMagesSpellbook(int depth) {
        Item item = new Item(SPELL_BOOK.glyph(), CharacterPanel.brightWhite, "white mage's spellbook");
        item.addWrittenSpell("minor heal", 4, new Effect(1){
            public void start(Creature creature){
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(20);
                creature.doAction("look healthier");
            }
        }, false);

        item.addWrittenSpell("major heal", 8, new Effect(1){
            public void start(Creature creature){
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(50);
                creature.doAction("look healthier");
            }
        }, false);

        item.addWrittenSpell("slow heal", 12, new Effect(50){
            public void update(Creature creature){
                super.update(creature);
                creature.modifyHp(2);
            }
        }, false);

        item.addWrittenSpell("inner strength", 16, new Effect(50){
            public void start(Creature creature){
                creature.modifyAttackValue(2);
                creature.modifyDefenseValue(2);
                creature.modifyVisionRadius(1);
                creature.modifyRegenHpPer1000(10);
                creature.modifyRegenManaPer1000(-10);
                creature.doAction("seem to glow with inner strength");
            }
            public void update(Creature creature){
                super.update(creature);
                if (Math.random() < 0.25)
                    creature.modifyHp(1);
            }
            public void end(Creature creature){
                creature.modifyAttackValue(-2);
                creature.modifyDefenseValue(-2);
                creature.modifyVisionRadius(-1);
                creature.modifyRegenHpPer1000(-10);
                creature.modifyRegenManaPer1000(10);
            }
        }, false);

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newBlueMagesSpellbook(int depth) {
        Item item = new Item(SPELL_BOOK.glyph(), CharacterPanel.brightBlue, "blue mage's spellbook");
        CreatureFactory creatureFactory = new CreatureFactory(world);

        item.addWrittenSpell("blood to mana", 1, new Effect(1){
            public void start(Creature creature){
                int amount = Math.min(creature.hp() - 1, creature.maxMana() - creature.mana());
                creature.modifyHp(-amount);
                creature.modifyMana(amount);
            }
        }, true);

        item.addWrittenSpell("blink", 6, new Effect(1){
            public void start(Creature creature){
                creature.doAction("fade out");

                int mx = 0;
                int my = 0;

                do
                {
                    mx = (int)(Math.random() * 11) - 5;
                    my = (int)(Math.random() * 11) - 5;
                }
                while (!creature.canEnter(creature.x+mx, creature.y+my, creature.z)
                        && creature.canSee(creature.x+mx, creature.y+my, creature.z));

                creature.moveBy(mx, my, 0);

                creature.doAction("fade in");
            }
        }, true);

        item.addWrittenSpell("summon bats", 11, new Effect(1){
            public void start(Creature creature){
                for (int ox = -1; ox < 2; ox++){
                    for (int oy = -1; oy < 2; oy++){
                        int nx = creature.x + ox;
                        int ny = creature.y + oy;
                        if (ox == 0 && oy == 0
                                || creature.creature(nx, ny, creature.z) != null)
                            continue;

                        Creature bat = creatureFactory.newBat(depth);

                        if (!bat.canEnter(nx, ny, creature.z)){
                            world.remove(bat);
                            continue;
                        }

                        bat.x = nx;
                        bat.y = ny;
                        bat.z = creature.z;

                        creature.summon(bat);
                    }
                }
            }
        }, false);

        item.addWrittenSpell("detect creatures", 16, new Effect(75){
            public void start(Creature creature){
                creature.doAction("look far off into the distance");
                creature.modifyDetectCreatures(1);
            }
            public void end(Creature creature){
                creature.modifyDetectCreatures(-1);
            }
        }, false);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item randomSpellBook(int depth){
        switch ((int)(Math.random() * 2)){
            case 0: return newWhiteMagesSpellbook(depth);
            default: return newBlueMagesSpellbook(depth);
        }
    }
}