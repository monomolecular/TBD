package game.creatures;

/**
 * Created by Max on 3/20/2016.
 */
public abstract class LevelUpOption {
    private String name;
    public String name() { return name; }

    public LevelUpOption(String name){
        this.name = name;
    }

    public abstract void invoke(Creature creature);
}