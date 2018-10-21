package Hazel.GameEngine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

/**
 * {@code GameWindow} is a window handling class.
 * <br>
 * This class should be used for handling the creation of a window.
 */
public class GameWindow
{
    private JFrame frame; //The game window object
    private Canvas canvas; //The canvas element holds all the items in the window
    private BufferStrategy bs; //Controls the amount of times the game is updated
    private Graphics g; //The graphics object
    private String windowTitle; //The title of the game window
    private int windowWidth; //The width of the game window
    private int windowHeight; //The height of the game window
    private int windowScale; //The scale of the game window

    /**
     * Creates the game window object.
     *
     * @param gameEngine The game-engine class.
     */
    public GameWindow(Hazel gameEngine)
    {
        createWindow(gameEngine);
    }

    /**
     * Creates the game window object with given
     * title, width, height, and scale.
     *
     * @param windowTitle  The title of the window.
     * @param windowWidth  The width of the window.
     * @param windowHeight The height of the window.
     * @param windowScale  The scale of the window.
     */
    public GameWindow(String windowTitle, int windowWidth, int windowHeight, int windowScale)
    {
        this.windowTitle = windowTitle;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowScale = windowScale;
    }

    /**
     * Method used to create a window based on the
     * title, width, height, and scale.
     *
     * @param gameEngine The game-engine class.
     */
    @SuppressWarnings("static-access")
    public void createWindow(Hazel gameEngine)
    {
        frame = new JFrame(gameEngine.getTitle());
        Dimension wDimension = new Dimension(gameEngine.getScaledWidth(), gameEngine.getScaledHeight());
        frame.setMinimumSize(wDimension);
        frame.setMaximumSize(wDimension);
        frame.setPreferredSize(wDimension);
        frame.setFocusable(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        canvas = new Canvas();
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();

        canvas.createBufferStrategy(gameEngine.getNumBuffers());
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();
        g = bs.getDrawGraphics();
    }

    /**
     * Method used to clean up memory used by
     * certain processes.
     */
    public void cleanUp()
    {
        g.dispose();
        bs.dispose();
        frame.dispose();
    }

    /**
     * @return The game title.
     */
    public String getTitle()
    {
        return windowTitle;
    }

    /**
     * @return The game window's width.
     */
    int getWidth()
    {
        return windowWidth;
    }

    /**
     * @return The game window's height.
     */
    int getHeight()
    {
        return windowHeight;
    }

    /**
     * @return The game window's scaled width.
     */
    public int getScaledWidth()
    {
        return windowWidth * windowScale;
    }

    /**
     * @return The game window's scaled height.
     */
    public int getScaledHeight()
    {
        return windowHeight * windowScale;
    }

    /**
     * @return The game window's scale.
     */
    int getScale()
    {
        return windowScale;
    }

    /**
     * @return The game window's graphic's object.
     */
    public Graphics getGraphics()
    {
        return g;
    }

    /**
     * @return The game window's buffer strategy.
     */
    public BufferStrategy getBufferStrategy()
    {
        return bs;
    }

    /**
     * @return The frame object.
     */
    public JFrame getFrame()
    {
        return frame;
    }

    /**
     * @return The canvas object.
     */
    public Canvas getCanvas()
    {
        return canvas;
    }
}