package game.items;

import game.creatures.Creature;

/**
 * Created by Max on 3/23/2016.
 *
 * When the player, or smart monster, quaffs a potion it's effect will be applied to the creature. What can we say
 * about effects? Most effects will only last for a certain duration. Some will apply every turn or every few turns
 * (like poison or a slow heal). Others will have a change that lasts for the duration (like confuse or resist cold).
 * This can be done with a start method that that applies the change when first quaffed, an end method that unapplies
 * the change when the duration has run out, and an update method that is called every turn in between.
 *
 * Had to implement clonable and with that, and the overriden clone() we can give separate instances of effect to each
 * monster affected, and everything works as intended.
 */
public class Effect implements Cloneable {
    protected int duration;

    public boolean isDone() {
        return duration < 1;
    }

    public Effect(int duration){
        this.duration = duration;
    }

    /**
     * We're going to have spells and each will have it's own effect. But when we add that effect to a creature, we
     * want each creature to get it's own effect, otherwise weird things will happen because the spell will have an
     * effect being applied to many creatures and the shared state (like duration) will be wonky. Instead, a spell
     * will have an effect and when applying it to something we can create a copy and apply the copy. That way each
     * time you cast a spell the effect will have it's own state.
     *
     * To accomplish this we'll add a copy constructor to the Effect class:
     * https://msdn.microsoft.com/en-us/library/ms173116.aspx
     * https://www.agiledeveloper.com/articles/cloning072002.htm
     *
     * A different, and almost certainly better, way of handling effects for each spell. Instead of each spell having a
     * reference to an Effect and using the Effect's copy constructor, the spell class should act as an Effect factory
     * and have an abstract newEffect method. Each individual spell would subclass Spell and implement newEffect.
     *
     * ToDo: Change over to an effect factory.
     *
     * @param other
     */
    public Effect(Effect other){
        this.duration = other.duration;
    }

    public void update(Creature creature){
        duration--;
    }

    public void start(Creature creature){}

    public void end(Creature creature){}

    @Override
    public Object clone()
    {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            // This should never happen
            throw new InternalError(e.toString());
        }
    }
}