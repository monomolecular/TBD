package game;

import characterPanel.CharacterPanel;

import java.awt.*;

/**
 * Created by Max on 3/9/2016.
 *
 * Roguelikes happen somewhere. A somewhere made of floors, walls, rivers, trees, caves, doors, or whatever you can
 * imagine.
 *
 * Each Tile needs to be displayed so we need a glyph to display and a color to display it with. Since we only have a
 * few different tile types, and all tiles of the same type look and behave the same, we can represent the tiles as a
 * java enum.
 *
 * TODO: Tweak Enums to public final member fields.
 * Comment from Trystan's blog... I think that with an enum like your tile enum, you should use public final member
 * fields, and skip the accessor methods. The types of the fields themselves are immutable, so the enum will still be
 * immutable as a whole.
 *
 * TODO: Add more tile types.
 * Just add them to the Tile enum like the floor and wall then add it to the WorldBuilder somewhere. Maybe call an
 * addRivers method or addTree method after the makeCaves method.
 */

public enum Tile {
    /**
     * CharacterPanel supports code page 437
     * http://en.wikipedia.org/wiki/Code_page_437
     *
     * It's often useful to have another kind of tile that represents out of bounds. That way instead of always
     * checking if something is out of bounds before checking the map about a specific tile, we can just ask and the map
     * and it can tell us it's out of bounds and we can handle that however we want. If you're familiar with the
     * NullObject design pattern then it's very similar; I guess you could call it an OutOfBoundsObject.
     * http://en.wikipedia.org/wiki/Null_Object_pattern
     */

    // Basic map tiles...
    FLOOR("Floor", '\u002E', CharacterPanel.yellow, "A dirt and rock cave floor."),
    WALL("Wall", '\u2592', CharacterPanel.yellow, "A dirt and rock cave wall."),  // CP437 - 177 Medium Shade Block, Unicode 2592
    BOUNDS("Out of Bounds", '\u00D7', CharacterPanel.lightGray, "Beyond the edge of the world."),
    STAIRS_DOWN("Stairs Down", '\u003E', CharacterPanel.white, "A stone staircase that goes down."),
    STAIRS_UP("Stairs Up", '\u003C', CharacterPanel.white, "A stone staircase that goes up."),
    TARGETING_LINE("Targeting Line", '\u2022', CharacterPanel.brightCyan, "Your line of site to whatever it is you're lookign at."),
    UNKNOWN("Unknown", '\u0020', CharacterPanel.white, "A thing/place that has not yet been seen."),

    // Creature tiles
    HUMAN("Player", '\u0040', CharacterPanel.brightWhite, "A rather unassuming biped."), // @
    FUNGUS("Fungus", '\u0066', CharacterPanel.green, "The fungus is among us."),
    BAT("Bat", '\u0062', CharacterPanel.yellow, "A small bat with a small bite."),
    ALICORN("Alicorn", '\u1FBA', CharacterPanel.brightMagenta, "A truely mythical creature, half unicorn and half pegasus."), // \00C1 = Á
    ZOMBIE("Zombie", '\u007A', CharacterPanel.brightMagenta, "Brainz..."),
    GOBLIN("Goblin", '\u0067', CharacterPanel.brightMagenta, "An dirty little green brute, smarter."),

    // Item tiles
    ROCK("Rock", '\u002C', CharacterPanel.yellow, "Very small rocks."),
    SPELL_BOOK("Spell Book", '\u002B', CharacterPanel.white, "Power eminates from this old tome."),
    VICTORY("Victory Item", '\u00A7', CharacterPanel.brightYellow, "Oh, it glows... the splendor. Victory is mine!"),
    // -- Consumables
    RATION("Food Ration", '\u0025', CharacterPanel.white, "Mmm... reconstituted meat ration"),
    POTION("Potion", '\u0021', CharacterPanel.white, "Strange liquid in a vial, why not drink it?"),
    BAGUETTE("Stale Baguette", '\u0021', CharacterPanel.white, "How long has this been down here? Hrm... crunchy!."),
    // -- Weapons
    DAGGER("Dagger", '\u0029', CharacterPanel.white, "A simple dagger, fairly well balanced though."),
    SWORD("Sword", '\u0029', CharacterPanel.brightWhite, "A basic one-handed sword, it's definitely seen some action."),
    BO_STAFF("Bo Staff", '\u0029', CharacterPanel.yellow, "A staff made of flexible wood, approximately 6' long."),
    SHORT_BOW("Short Bow", '\u0029', CharacterPanel.white, "This small bow might actually be useful in close to medium range combat."),
    // -- Armor
    TUNIC("Tunic", '\u005B', CharacterPanel.green, "This simple piece of green cloth could hardly be considered armor."),
    CHAINMAILLE("Chainmaille", '\u005B', CharacterPanel.white, "It's got a little rust, but should offer some real protection."),
    PLATED_MAIL("Plated Mail", '\u005B', CharacterPanel.white, "This combination of chain and plate armor should really help.");

    private String label;
    public String label() {
        return label;
    }

    private char glyph;
    public char glyph() {
        return glyph;
    }

    private Color color;
    public Color color() {
        return color;
    }

    private String details;
    public String details() {
        return details;
    }

    Tile(String label, char glyph, Color color, String details) {
        this.label = label;
        this.glyph = glyph;
        this.color = color;
        this.details = details;
    }

    /**
     * Indicate if a tile is diggable. This way we don't even have to know what the tile is we can just care about if
     * it can be dug through. If we later add new tiles, no-dig zones, or something else we just need to update this
     * method.
     *
     * @return
     */
    public boolean isDiggable() {
        return this == Tile.WALL;
    }

    /**
     * Indicate if the tile is walkable. Instead of checking the tile type directly we just ask if it can be walked on
     * (or dug through like with isDiggable).
     * @return
     */
    public boolean isGround() {
        return this != WALL && this != BOUNDS;
    }
}