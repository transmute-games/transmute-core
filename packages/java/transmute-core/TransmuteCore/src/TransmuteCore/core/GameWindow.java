package TransmuteCore.core;

import TransmuteCore.core.interfaces.services.IGameWindow;
import TransmuteCore.core.interfaces.WindowEventCallbacks;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

/**
 * {@code GameWindow} is a window handling class.
 * <br>
 * This class should be used for handling the creation of a window.
 */
public class GameWindow implements IGameWindow
{
    private JFrame frame; //The game window object
    private Canvas canvas; //The canvas element holds all the items in the window
    private BufferStrategy bs; //Controls the amount of times the game is updated
    private Graphics g; //The graphics object
    private String windowTitle; //The title of the game window
    private int windowWidth; //The width of the game window
    private int windowHeight; //The height of the game window
    private int windowScale; //The scale of the game window
    private WindowEventCallbacks windowEventCallbacks; //Optional window event callbacks

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
     * Sets the window event callbacks.
     *
     * @param callbacks The callback handler for window events
     */
    public void setWindowEventCallbacks(WindowEventCallbacks callbacks)
    {
        this.windowEventCallbacks = callbacks;
    }
    
    /**
     * Method used to create a window using the internal window properties.
     *
     * @param config The game configuration (used for buffer strategy setup).
     */
    public void createWindow(GameConfig config)
    {
        if (config == null) {
            throw new IllegalArgumentException("GameConfig cannot be null");
        }
        frame = new JFrame(windowTitle);
        Dimension wDimension = new Dimension(windowWidth * windowScale, windowHeight * windowScale);
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

        // Add window event listeners
        if (windowEventCallbacks != null)
        {
            frame.addWindowFocusListener(new WindowFocusListener()
            {
                @Override
                public void windowGainedFocus(WindowEvent e)
                {
                    windowEventCallbacks.onWindowFocusGained();
                }
                
                @Override
                public void windowLostFocus(WindowEvent e)
                {
                    windowEventCallbacks.onWindowFocusLost();
                }
            });
            
            frame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    windowEventCallbacks.onWindowClosing();
                }
            });
        }
        
        canvas.createBufferStrategy(config.getBufferCount());
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();
        g = bs.getDrawGraphics();
    }

    /**
     * Method used to clean up memory used by
     * certain processes.
     */
    @Override
    public void cleanUp()
    {
        g.dispose();
        bs.dispose();
        frame.dispose();
    }

    /**
     * @return The game title.
     */
    @Override
    public String getTitle()
    {
        return windowTitle;
    }

    /**
     * @return The game window's width.
     */
    @Override
    public int getWidth()
    {
        return windowWidth;
    }

    /**
     * @return The game window's height.
     */
    @Override
    public int getHeight()
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
    @Override
    public int getScale()
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
    @Override
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
    @Override
    public Canvas getCanvas()
    {
        return canvas;
    }
}