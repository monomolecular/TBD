package game;

import java.applet.Applet;
import characterPanel.CharacterPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.UnsupportedEncodingException;

import game.screens.Screen;
import game.screens.StartScreen;

/**
 * Created by Max on 3/9/2016.
 *
 * For extra awesomeness you can make your roguelike run from the users browser as an applet. Just add a file like this
 * to your project:
 */

public class AppletMain extends Applet implements KeyListener {

    private static final long serialVersionUID = 2560255315130084198L;

    private CharacterPanel terminal;
    private Screen screen;

    public AppletMain(){
        super();
        try {
            terminal = new CharacterPanel();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        add(terminal);
        screen = new StartScreen();
        addKeyListener(this);
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            screen = screen.respondToUserInput(e);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //
    }

    @Override
    public void init(){
        super.init();
        this.setSize(terminal.getWidth() + 20, terminal.getHeight() + 20);
    }

    @Override
    public void repaint(){
        terminal.clear();
        screen.displayOutput(terminal);
        super.repaint();
    }
}