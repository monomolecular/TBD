package game.creatures;

import game.*;
import game.items.Effect;
import game.items.Inventory;
import game.items.Item;
import game.items.Spell;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static game.Tile.RATION;

/**
 * Created by Max on 3/10/2016.
 *
 * We'll need something to represent our player and eventually monsters. They'll all have an x and y coordinate, a
 * glyph, and a color. Since they will be interacting with the world, they should have a reference to that too.
 */
public class Creature {
    private World world;

    /**
     * The x and y coordinate are publicly accessible since they'll be used a lot, we don't need to constrain them or
     * do anything when they change, and this is easier than creating getters and setters. Getters and setters are
     * almost always a better idea than public fields but part of software engineering is knowing the rules and part is
     * knowing when to break them. If this turns out to be a bad idea and we wish we used getters and setters instead,
     * then it's not a big deal since most IDE's can automatically create getters and setters and rewrite your code to
     * use those (Refactor > Encapsulate Fields...).
     */
    public int x;
    public int y;
    public int z;

    private char glyph;
    public char  glyph() {
        return glyph;
    }

    private Color color;
    public Color color() {
        return color;
    }

    //Creatures need names.
    private String name;
    public String name() {
        return name;
    }

    /**
     * There's many many different ways to handle how much damage is done but for now we'll stick with something
     * simple: the damage amount is a random number from 1 to the attackers attack value minus the defenders defense
     * value. It's easy to code, easy to understand, and using only two variables worked fine for Castlevania:
     * Symphony Of The Night.
     */
    private int maxHp;
    public int maxHp() {
        return maxHp;
    }

    private int hp;
    public int hp() {
        return hp;
    }

    private void regenerateHealth(){
        regenHpCooldown -= regenHpPer1000;
        if (regenHpCooldown < 0){
            modifyHp(1);
            modifyFood(-1);
            regenHpCooldown += 1000;
        }
    }

    /**
     * Calculate the attacking value (power) of the creature, incorporating the equipment into overall attack values by
     * adding them to the creature's getter.
     */
    private int attackValue;
    public int attackValue() {
        return attackValue
                + (weapon == null ? 0 : weapon.attackValue())
                + (armor == null ? 0 : armor.attackValue());
    }

    /**
     * Calculate the deffensive value of the creature, incorporating the equipment into overall defense values by
     * adding them to the creature's getter.
     */
    private int defenseValue;
    public int defenseValue() {
        return defenseValue
                + (weapon == null ? 0 : weapon.defenseValue())
                + (armor == null ? 0 : armor.defenseValue());
    }


    public Creature(World world, char glyph, Color color, String name, int maxHp, int attack, int defense, int vision) {

        // We'll use constructor injection to set the creatures property values.
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.regenHpPer1000 = 10; // Todo: Use constructor injection or call in the CreatureFactory to vary by creature.
        this.regenManaPer1000 = 10;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = vision;
        this.inventory = new Inventory(20); // Todo: pass in the inventory size based on the type of creature.
        this.maxFood = 1000;
        this.food = maxFood / 3 * 2;
        this.level = 1;
        this.effects = new ArrayList<Effect>();
    }

    /**
     * To implement all the different behaviors of all the different creatures, we could use a bunch of flags
     * representing creature traits, or we could use subclassing, but let's use something that's usually more
     * flexible: delegation. Each creature will have a reference to a CreatureAi and the creatures can let their ai
     * decide what to do. Instead of using what's called constructor injection and passing the CreatureAi in the
     * constructor like we do with the world, glyph, and color, which is usually a good way of doing things, we'll use
     * setter injection.
     * <p>
     * http://misko.hevery.com/2009/02/19/constructor-injection-vs-setter-injection/
     */
    private CreatureAi ai;

    /**
     * This is a "setter" for the creatures AI. It's used to connect the creature and its AI.
     *
     * @param ai
     */
    public void setCreatureAi(CreatureAi ai) {
        this.ai = ai;
    }

    /**
     * Since the caves we have so far aren't all connected, the player can only walk around in the open area he starts
     * in. We could change how we build the world to make sure that all open cave floors are connected but there's a
     * much easier way to make sure the player can explore everything; we'll let creatures dig through the walls.
     * <p>
     * ToDo: Ensure creatures only dig when they have to.
     *
     * @param wx
     * @param wy
     */
    public void dig(int wx, int wy, int wz) {
        modifyFood(-5);
        world.dig(wx, wy, wz);
        doAction("dig");
    }

    /**
     * Creatures will move around in the world. What happens when they try to enter a new tile is up to the creature's
     * ai. This method just handles the movement, and then asks the AI what to do.
     * <p>
     * If the target space contains a creature however, attack!
     *
     * @param mx
     * @param my
     */
    public void moveBy(int mx, int my, int mz) {

        Tile tile = world.tile(x + mx, y + my, z + mz);

        if (mx == 0 && my == 0 && mz == 0) {
            // Bail out early if we're not actually moving.
            return;
        } else if (mz == -1) {
            // Trying to head up a level.
            if (tile == Tile.STAIRS_DOWN) {
                doAction("walk up the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go up but are stopped by the cave ceiling");
                return;
            }
        } else if (mz == 1) {
            if (tile == Tile.STAIRS_UP) {
                doAction("walk down the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go down but are stopped by the cave floor");
                return;
            }
        }

        Creature other = world.creature(x + mx, y + my, z + mz);

        if (other == null) {
            ai.onEnter(x + mx, y + my, z + mz, tile);
        } else {
            meleeAttack(other);
        }
    }

    /**
     * Now we can grant experience based on some experience value the creature has, or on it's level, or on the killers
     * level, or by some combination. It's a simple formula for now.
     *
     * This ensures that tougher creatures are worth more and by subtracting the killer's level, easy creatures will
     * soon be worth nothing. It's not perfect but it's simple to explain, understand, and code.
     *
     * @param other
     */
    public void gainXp(Creature other){
        int amount = other.maxHp
                + other.attackValue()
                + other.defenseValue()
                - level * 2;

        if (amount > 0)
            modifyXp(amount);
    }

    public void modifyHp(int amount) {
        hp += amount;

        if (hp < 1) {
            doAction("die");
            leaveCorpse();
            world.remove(this);
        }
    }

    /**
     * We also need to make sure that when a creature dies it drops anything it was holding.
     */
    private void leaveCorpse(){
        Item corpse = new Item(RATION.glyph(), color, name + " corpse");
        corpse.modifyFoodValue(maxHp);
        world.addAtEmptySpace(corpse, x, y, z);
        for (Item item : inventory.getItems()){
            if (item != null)
                drop(item);
        }
    }

    public boolean canEnter(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz).isGround() && world.creature(wx, wy, wz) == null;
    }

    /**
     * Simple method that let's a creature update each turn, but the action is delegated to the Creature AI.
     */
    public void update() {
        // modifyFood(-1); handled by regenerateHealth now???
        regenerateHealth();
        regenerateMana();
        updateEffects();
        ai.onUpdate();
    }

    /**
     * Messages are meant for the player so maybe the PlayerAi should be the receiver of the messages? That kind of
     * make sense because it already gets called by the creature class and creatures are probably going to be the
     * source of most messages. We can pass messages to the ai and any non-player ai can just ignore the messages.
     * <p>
     * This class is intended to make it easier for the callers to build messages. It can take the string and any
     * parameters the caller passes and format the string for them. A nice convenience.
     *
     * @param message
     * @param params
     */
    public void notify(String message, Object... params) {
        ai.onNotify(String.format(message, params));
    }

    /**
     * Notify nearby creatures when something happens.
     * <p>
     * ToDo: Add canHear mechanic.
     * Perhaps add some notion of volume so that if something happens outside of your range of vision, you could still
     * hear it happen and be notified.
     * <p>
     * Alternate idea for doAction. Currently if a creature has a visionRadius higher than 9, they won't be able to see
     * an action if they stand, say, 10 squares away. Get rid of the r variable in doAction and instead have it loop
     * over every square in that layer, broadcasting to any creatures within a range defined by their own visionRadius.
     * ToDo: Upgrade vision mechanic.
     *
     * @param message
     * @param params
     */
    public void doAction(String message, Object... params) {
        int r = 9;
        for (int ox = -r; ox < r + 1; ox++) {
            for (int oy = -r; oy < r + 1; oy++) {
                if (ox * ox + oy * oy > r * r)
                    continue;

                Creature other = world.creature(x + ox, y + oy, z);

                if (other == null) {
                    continue;
                }

                if (other == this) {
                    other.notify("You " + message + ".", params);
                } else if (other.canSee(x, y, z)) { // Creature has to be able to see it happen to be notified.
                    other.notify(String.format("The %s %s.", name + " (" + glyph + ")", makeSecondPerson(message)), params);
                }
            }
        }
    }

    /**
     * Do a small bit of string manipulation to make it grammatically correct. It assumes the first word is the verb,
     * but that's easy enough to do as long as you don't plan on supporting other languages. It's best to avoid
     * implicit rules like this since the only way to know about it is to already know it or watch it fail when you
     * don't follow the implicit rule. It feels dirty to have gramer rules in with the Creature code so remember to
     * move it somewhere better.
     * <p>
     * ToDo: Relocate grammer code somewhere else.
     *
     * @param text
     * @return
     */
    private String makeSecondPerson(String text) {
        String[] words = text.split(" ");
        words[0] = words[0] + "s";

        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(" ");
            builder.append(word);
        }

        return builder.toString().trim();
    }

    /**
     * Since creatures are the ones who are doing the seeing, it makes since to give creatures a new stat to say how
     * far they can see and a couple methods for looking at the world.
     *
     * I set the vision radius to 9 in the creature's constructor but you should use whatever value you prefer or even
     * have it passed in from the creatureFactory. Since we delegate the task to seeing to the CreatureAi, that's where
     * the work is done and what we'll add to next.
     */
    private int visionRadius;
    public int visionRadius() {
        return visionRadius;
    }

    public boolean canSee(int wx, int wy, int wz) {
        return (detectCreatures > 0 && world.creature(wx, wy, wz) != null
                || ai.canSee(wx, wy, wz));
    }

    // ToDo: Implement a hearing radius.
    private int hearingRadius;
    private int hearingRadius() {
        return hearingRadius;
    }

    public boolean canHear() {
        // Not yet implemented, neet idea though.
        return true;
    }

    public Tile realTile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }

    /**
     * Creatures should also be able to see what tiles are in the world. But only if they've been seen before.
     *
     * @param wx
     * @param wy
     * @param wz
     * @return
     */
    public Tile tile(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.tile(wx, wy, wz);
        else
            return ai.rememberedTile(wx, wy, wz);
    }

    /**
     * Creatures should also be able to see what other creatures are in the world. But only if they've been seem before.
     *
     * @param wx
     * @param wy
     * @param wz
     * @return
     */
    public Creature creature(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.creature(wx, wy, wz);
        else
            return null;
    }

    /**
     * Creatures should also be able to see itms in the world. But only if they've seen them before.
     *
     * @param wx
     * @param wy
     * @param wz
     * @return
     */
    public Item item(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.item(wx, wy, wz);
        else
            return null;
    }

    /**
     * Give all creatures an inventory. This means that potentially any creature can have an inventory (Spoiler alert!)
     */
    private Inventory inventory;
    public Inventory inventory() {
        return inventory;
    }

    /**
     * Creatures need to be able to pickup and drop stuff, moving it from the world to the creatures inventory or back.
     */
    public void pickup(){
        Item item = world.item(x, y, z);

        if (inventory.isFull() || item == null){
            doAction("grab at the ground");
        } else {
            doAction("pickup a %s", item.name());
            world.remove(x, y, z);
            inventory.add(item);
        }
    }

    /**
     * A funky side effect of only having one item per location in the game is that if there are no open spaces then
     * the item can't be added so we have to keep it in inventory and prevent the drop.
     *
     * Also, remove the item from the inventory when we drop it.
     *
     * @param item
     */
    public void drop(Item item){
        if (world.addAtEmptySpace(item, x, y, z)){
            doAction("drop a " + item.name());
            inventory.remove(item);
            unequip(item);
        } else {
            notify("There's nowhere to drop the %s.", item.name());
        }
    }

    private int maxFood;
    public int maxFood() {
        return maxFood;
    }

    private int food;
    public int food() {
        return food;
    }

    /**
     * We only want the player to be able to die of starvation since it would be boring if every monster dropped dead
     * of starvation and if they need to eat they'd have to go around killing each other. We could have an entire
     * ecosystem of bats farming fungus, that would introduce some neat gameplay options.
     * ToDo: Update so player slowly starves and looses HP.
     *
     * @param amount
     */
    public void modifyFood(int amount) {
        food += amount;
        if (food > maxFood) {
            maxFood = maxFood + food / 2;
            food = maxFood;
            notify("You can't believe your stomach can hold that much!");
            modifyHp(-1);
        } else if (food < 1 && isPlayer()) {
            modifyHp(-1000);
        }
    }

    /**
     * This ia still a bit of a hack, but it's isolated for now. Later if we have other creatures with an (char)1 glyph
     * or if the player can assume other forms, we can update this one isolated place. Ugly hacks are inevitable, but you
     * can always isolate them so the callers don't need to deal with it.
     *
     * ToDo: Remove isPlayer hack.
     *
     * @return
     */
    public boolean isPlayer(){
        return glyph == Tile.HUMAN.glyph();
    }

    /**
     * Eat stuff... and remove it from inventory when we do.
     *
     * @param item
     */
    public void eat(Item item){
        doAction("eat a " + item.name());
        consume(item);
    }

    public void quaff(Item item){
        doAction("quaff a " + item.name());
        consume(item);
    }

    private void consume(Item item){
        if (item.foodValue() < 0)
            notify("Gross!");

        addEffect(item.quaffEffect());

        modifyFood(item.foodValue());
        getRidOf(item);
    }

    private void addEffect(Effect effect){
        if (effect == null)
            return;

        effect.start(this);
        effects.add(effect);
    }

    /**
     *  Some methods to the creature class to equip and unequip weapons and armor. For now, creatures can wield one
     *  weapon and wear one pice of armor at a time. If you want separate armor slots for helmet, rings, shoes, etc,
     *  you can do that too. I'm also going to use the same methods to deal with armor or weapons.
     *
     */

    private Item weapon;
    public Item weapon() {
        return weapon;
    }

    private Item armor;
    public Item armor() {
        return armor;
    }

    public void unequip(Item item){
        if (item == null)
            return;

        if (item == armor){
            doAction("remove a " + item.name());
            armor = null;
        } else if (item == weapon) {
            doAction("put away a " + item.name());
            weapon = null;
        }
    }

    public void equip(Item item){
        if (!inventory.contains(item)) {
            if (inventory.isFull()) {
                notify("Can't equip %s since you're holding too much stuff.", item.name());
                return;
            } else {
                world.remove(item);
                inventory.add(item);
            }
        }

        if (item.attackValue() == 0 && item.rangedAttackValue() == 0 && item.defenseValue() == 0)
            return;

        if (item.attackValue() + item.rangedAttackValue() >= item.defenseValue()){
            unequip(weapon);
            doAction("wield a " + item.name());
            weapon = item;
        } else {
            unequip(armor);
            doAction("put on a " + item.name());
            armor = item;
        }
    }

    /**
     *  creatures can gain levels too. Why? Just because I haven't seen it done before (at least not that I've noticed).
     *  It also means that positioning weak monsters between you and the big bad guy may just make the big bad guy gain
     *  levels.
     *
     *  Our creatures need xp, a level, and a way to gain xp and levels. When the xp passes the next level threshold,
     *  the level should be incremented, the ai should get notified, and the creature should heal a bit.
     */
    private int xp;
    public int xp() {
        return xp;
    }

    /**
     * The starting level is initialized to 1 in the constructor. I'm using a formula to determine how much experience
     * is needed for the next level but you can use a lookup table or some other formula. Many interesting things have
     * been said about leveling and power curves so read up, try different things, and do what works for you.
     *
     * @param amount
     */
    public void modifyXp(int amount) {
        xp += amount;

        notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);

        while (xp > (int)(Math.pow(level, 1.5) * 20)) {
            level++;
            doAction("advance to level %d", level);
            ai.onGainLevel();
            modifyHp(level * 2);
        }
    }

    private int level;
    public int level() {
        return level;
    }

    public void gainMaxHp() {
        maxHp += 10;
        hp += 10;
        doAction("look healthier");
    }

    public void modifyDefenseValue(int amount) {
        defenseValue += amount;
        if (amount > 0)
            doAction("look tougher");
        else if (amount < 0)
            doAction("look more vulnerable");
    }

    public void modifyAttackValue(int amount) {
        attackValue += amount;
        if (amount > 0)
            doAction("look stronger");
        else if (amount < 0)
            doAction("look weaker");
    }

    public int getVisionRadius() {
        return visionRadius;
    }

    public void modifyVisionRadius(int amount) {
        visionRadius += amount;
        doAction("look more aware");
    }

    public String details() {
        return String.format("     level:%d     attack:%d     defense:%d     hp:%d", level, attackValue(), defenseValue(), hp);
    }

    /**
     * Throw an item to a location and one damages any creature there.
     *
     * @param item
     * @param wx
     * @param wy
     * @param wz
     */
    public void throwItem(Item item, int wx, int wy, int wz) {
        Point end = new Point(x, y, 0);

        for (Point p : new Line(x, y, wx, wy)){
            if (!realTile(p.x, p.y, z).isGround())
                break;
            end = p;
        }

        wx = end.x;
        wy = end.y;

        Creature c = creature(wx, wy, wz);

        // Check if the target is a creature, if so, attack.
        if (c != null) throwAttack(item, c);
        // Otherwise, your just throwing it.
        else doAction("throw a %s", item.name());

        // Remove the item if it has a quaffEffect and the target was a creature
        if (item.quaffEffect() != null && c != null) getRidOf(item);
        // otherwise it should add to the world
        else putAt(item, wx, wy, wz);
    }

    /**
     * Basic attack
     *
     * @param other
     */
    public void meleeAttack(Creature other) {
        commonAttack(other, attackValue(), "attack the %s for %d damage", other.name);
    }

    /**
     * And now actual attacks with thrown weapons. I'll add half the base attack value of the thrower since thrown
     * weapons should generally do less damage than mele weapons.
     *
     * @param item
     * @param other
     */
    public void throwAttack(Item item, Creature other) {
        commonAttack(other, attackValue / 2 + item.thrownAttackValue(), "throw a %s at the %s for %d damage", item.name(), other.name);
        other.addEffect(item.quaffEffect());
    }

    /**
     * Creatures need a way to attack with ranged weapons. I'll add half the attack value like with thrown items.
     *
     * @param other
     */
    public void rangedWeaponAttack(Creature other){
        commonAttack(other, attackValue / 2 + weapon.rangedAttackValue(), "fire a %s at the %s for %d damage", weapon.name(), other.name);
    }

    private void commonAttack(Creature other, int attack, String action, Object ... params) {
        modifyFood(-1);

        int amount = Math.max(0, attack - other.defenseValue());

        amount = (int)(Math.random() * amount) + 1;

        Object[] params2 = new Object[params.length+1];
        for (int i = 0; i < params.length; i++){
            params2[i] = params[i];
        }
        params2[params2.length - 1] = amount;

        doAction(action, params2);

        other.modifyHp(-amount);

        if (other.hp < 1) gainXp(other);
    }

    /**
     * Creatures sometimes use up an item and it no longer exists. Other times they no longer have the item but it
     * still exists in the world. Either way, we need to make sure they no longer have it equipped and that they no
     * longer have it in their inventory. One way to do this is to create two helper methods and call these when
     * possible.
     */

    private void getRidOf(Item item){
        inventory.remove(item);
        unequip(item);
    }

    private void putAt(Item item, int wx, int wy, int wz){
        inventory.remove(item);
        unequip(item);
        world.addAtEmptySpace(item, wx, wy, wz);
    }

    private int regenHpCooldown;
    private int regenHpPer1000;
    public void modifyRegenHpPer1000(int amount) {
        regenHpPer1000 += amount;
    }

    /**
     * List of effects that are currently applied to rhe creature.
     */
    private List<Effect> effects;
    public List<Effect> effects(){
        return effects;
    }

    /**
     * Update the effects each turn and remove any that are done.
     */
    private void updateEffects(){
        List<Effect> done = new ArrayList<Effect>();

        for (Effect effect : effects){
            effect.update(this);
            if (effect.isDone()) {
                effect.end(this);
                done.add(effect);
            }
        }

        effects.removeAll(done);
    }

    private int maxMana;
    public int maxMana() {
        return maxMana;
    }

    private int mana;
    public int mana() {
        return mana;
    }

    public void modifyMana(int amount) {
        mana = Math.max(0, Math.min(mana+amount, maxMana));
    }

    private int regenManaCooldown;
    private int regenManaPer1000;

    public void modifyRegenManaPer1000(int amount) {
        regenManaPer1000 += amount;
    }

    private void regenerateMana(){
        regenManaCooldown -= regenManaPer1000;
        if (regenManaCooldown < 0){
            if (mana < maxMana) {
                modifyMana(1);
                modifyFood(-1);
            }
            regenManaCooldown += 1000;
        }
    }

    public void gainMaxMana() {
        maxMana += 5;
        mana += 5;
        doAction("look more magical");
    }

    public void gainRegenMana(){
        regenManaPer1000 += 5;
        doAction("look a little less tired");
    }

    public void summon(Creature other) {
        world.addAtEmptyLocation(other,z);
        doAction("feel a new presence nearby");
    }

    private int detectCreatures;
    public void modifyDetectCreatures(int amount) {
        detectCreatures += amount;
    }

    public void castSpell(Spell spell, int x2, int y2) {
        Creature other = creature(x2, y2, z);

        if (spell.manaCost() > mana){
            doAction("point and mumble but nothing happens");
            return;
        } else if (other == null) {
            doAction("point and mumble at nothing");
            return;
        }

        other.addEffect(spell.effect());
        modifyMana(-spell.manaCost());
    }
}