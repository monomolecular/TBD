package game.screens;

import characterPanel.CharacterPanel;
import game.Tile;
import game.creatures.Creature;
import game.items.Item;
import game.items.Spell;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Max on 3/26/2016.
 */
public class ReadSpellScreen implements Screen {

    protected Creature player;
    private String letters;
    private Item item;
    private int sx;
    private int sy;

    public ReadSpellScreen(Creature player, int sx, int sy, Item item){
        this.player = player;
        this.letters = "abcdefghijklmnopqrstuvwxyz";
        this.item = item;
        this.sx = sx;
        this.sy = sy;
    }

    public void displayOutput(CharacterPanel terminal) {
        ArrayList<String> lines = getList();

        int y = 23 - lines.size();
        int x = 4;

        if (lines.size() > 0)
            terminal.clear(Tile.UNKNOWN.glyph(), x, y, 20, lines.size());

        for (String line : lines){
            terminal.write(line, x, y++);
        }

        terminal.clear(Tile.UNKNOWN.glyph(), 0, SCREEN_HEIGHT, SCREEN_WIDTH, 1);
        terminal.write("What would you like to read?", 2, SCREEN_HEIGHT);

        terminal.repaint();
    }

    private ArrayList<String> getList() {
        ArrayList<String> lines = new ArrayList<String>();

        for (int i = 0; i < item.writtenSpells().size(); i++){
            Spell spell = item.writtenSpells().get(i);

            String line = letters.charAt(i) + " - " + spell.name() + " (" + spell.manaCost() + " mana)";

            lines.add(line);
        }
        return lines;
    }

    public Screen respondToUserInput(KeyEvent key) {
        char c = key.getKeyChar();

        Item[] items = player.inventory().getItems();

        if (letters.indexOf(c) > -1
                && items.length > letters.indexOf(c)
                && items[letters.indexOf(c)] != null) {
            return use(item.writtenSpells().get(letters.indexOf(c)));
        } else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
            return null;
        } else {
            return this;
        }
    }

    protected Screen use(Spell spell){
        return new CastSpellScreen(player, "", sx, sy, spell);
    }
}