package game;

import characterPanel.CharacterPanel;
import game.screens.Screen;
import game.screens.StartScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;

import static game.screens.Screen.SCREEN_HEIGHT;

/**
 * Created by Max on 3/9/2016.
 *
 * Following tutorial:
 * http://trystans.blogspot.com.br/2016/01/roguelike-tutorial-00-table-of-contents.html
 *
 * ApplicationMain class is going to stay simple, it's only responsibility is to create a window and delegate input
 * and output to other things.
 */

public class ApplicationMain extends JFrame implements WindowListener, KeyListener {
    /**
     * The serialVersionUID is suggested by to help to prevent show-stopping failures when serializing different
     * versions of our class. We won't be doing that in this tutorial but it's almost always a good idea to take care
     * of compiler and IDE warning as soon as possible; It will save much trouble down the line.
     */
    private static final long serialVersionUID = 1060623638149583738L;

    private CharacterPanel terminal;
    private Screen screen;

    /**
     * The ApplicationMain constructor has all the set up code. So far that's just creating an CharacterPanel to display
     * some text and making sure the window is the correct size. The CharacterPanel defaults to 80 by 24 characters but
     * you can specify a different size in it's constructor.
     *
     * ApplicationMain needs to display the current screen when the window repaints and pass user input to the current
     * screen. It's delegating input and output to other things, exactly what ApplicationMain is for.
     */
    public ApplicationMain(){
        super("Arcane Lôr");
        //setTitle();

        /**
         * The window icon image needs to be under the directory images which is relative to the application’s
         * directory.
         */
        try {
            Image icon = new javax.swing.ImageIcon("images/Lr.png").getImage();
            setIconImage(icon);
        } catch (Exception e){
            System.err.println("Caught Exception: " +  e.getMessage());
        }

        // Create an application menu bar.
        //applicationMenuBar();

        // Maximize the window.
        //setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Add the ASCII Panel
        try {
            terminal = new CharacterPanel(Screen.SCREEN_WIDTH+1, SCREEN_HEIGHT+1); // default is 80 x 24
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        add(terminal);

        /**
         * Pack the frame: If we don’t want to specify or (don’t know) the exact size of the frame, we can use the
         * method pack() to let the frame resizes itself in a manner which ensures all its subcomponents have their
         * preferred sizes:
         */
        pack();
        screen = new StartScreen();
        addKeyListener(this);
        repaint();
    }

    /**
     * The main method just creates an instance of our window and show's it, making sure that the application exits
     * when the window is closed. Simple as can be.
     * @param args
     */
    public static void main(String[] args) {
        ApplicationMain app = new ApplicationMain();

        Toolkit theKit = app.getToolkit();
        Dimension wndSize = theKit.getScreenSize();

        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }

    private void applicationMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(new exitApp());
        menuFile.add(menuItemExit);
        menuBar.add(menuFile);

        // Add menu bar to the frame
        setJMenuBar(menuBar);
    }

    // Exit app
    static class exitApp implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }

    @Override
    public void repaint(){
        terminal.clear();
        screen.displayOutput(terminal);
        super.repaint();
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
    public void windowActivated(WindowEvent event) {
        terminal.writeCenter("-- The window has been activated. --", SCREEN_HEIGHT / 2);
    }

    @Override
    public void windowClosed(WindowEvent event) {
        terminal.writeCenter("-- The window has been closed. --", SCREEN_HEIGHT / 2);
    }

    @Override
    public void windowClosing(WindowEvent event) {
        terminal.writeCenter("-- About to close the window. --", SCREEN_HEIGHT / 2);
    }

    @Override
    public void windowDeactivated(WindowEvent event) {
        terminal.writeCenter("-- The window has been deactivated. --", SCREEN_HEIGHT / 2);
    }

    @Override
    public void windowDeiconified(WindowEvent event) {
        terminal.writeCenter("-- The window has been restored. --", SCREEN_HEIGHT / 2);
    }

    @Override
    public void windowIconified(WindowEvent event) {
        terminal.writeCenter("-- The window has been minimized. --", SCREEN_HEIGHT / 2);
    }

    @Override
    public void windowOpened(WindowEvent event) {
        terminal.writeCenter("-- The window has been opened. --", SCREEN_HEIGHT / 2);
    }


}