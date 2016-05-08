package game.items;

/**
 * Created by Max on 3/26/2016.
 *
 * This Spell class to tie together a spell name, cost, and effect.
 */
public class Spell {

    private String name;
    public String name() {
        return name;
    }

    private int manaCost;
    public int manaCost() {
        return manaCost;
    }

    private Effect effect;
    public Effect effect() {
        return (Effect)effect.clone();
    }

    public boolean requiresTarget;
    public boolean requiresTarget() { return requiresTarget; }

    public Spell(String name, int manaCost, Effect effect, boolean requiresTarget){
        this.name = name;
        this.manaCost = manaCost;
        this.effect = effect;
        this.requiresTarget = requiresTarget;
    }
}
