package game.creatures;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 3/21/2016.
 *
 * We need something to track all the possible options and enforce some of our level-up logic. We'll call it a
 * LevelUpController — even though classes with Manager or Controller in the name are usually vague and messy and not
 * the best way to do things.
 *
 * Here are a few simple options based on the stats we already have, but it's possible to do better. These are
 * anonymous classes - Anonymous classes can make some things very clear and succinct - other things are best left to
 * regular classes.
 */
public class LevelUpController {

    /**
     * when you gain a level you look tougher or stronger etc. You may even notice something else looking stronger or more aware. That's why I think it's so cool to have a method like doAction; you can, if you're lucky, see the rare and subtle events like these.
     */
    private static LevelUpOption[] options = new LevelUpOption[]{

            new LevelUpOption("Increased attack value"){
                public void invoke(Creature creature) { creature.modifyAttackValue(2); }
            },
            new LevelUpOption("Increased defense value"){
                public void invoke(Creature creature) { creature.modifyDefenseValue(2); }
            },
            new LevelUpOption("Increased vision"){
                public void invoke(Creature creature) { creature.modifyVisionRadius(2); }
            },
            new LevelUpOption("Increased hit points"){
                public void invoke(Creature creature) { creature.gainMaxHp(); }
            },
            new LevelUpOption("Increased hit point regeneration"){
                public void invoke(Creature creature) {
                    creature.modifyRegenHpPer1000(5);
                }
            },
            new LevelUpOption("Increased mana"){
                public void invoke(Creature creature) { creature.gainMaxMana(); }
            },
            new LevelUpOption("Increased mana regeneration"){
                public void invoke(Creature creature) { creature.gainRegenMana(); }
            }
    };

     public List<String> getLevelUpOptions(){
        List<String> names = new ArrayList<String>();
        for (LevelUpOption option : options){
            names.add(option.name());
        }
        return names;
    }

    public LevelUpOption getLevelUpOption(String name){
        for (LevelUpOption option : options){
            if (option.name().equals(name))
                return option;
        }
        return null;
    }

    /**
     * This LevelUpController should be able to select one option at random and apply it to a given creature.
     *
     * @param creature
     */
    public void autoLevelUp(Creature creature){
        options[(int)(Math.random() * options.length)].invoke(creature);
    }
}

