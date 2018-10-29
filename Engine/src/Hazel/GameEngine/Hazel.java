package Hazel.GameEngine;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

import Hazel.GameEngine.Interfaces.Cortex;
import Hazel.Graphics.Context;
import Hazel.Input.Input;
import Hazel.System.Error;
import Hazel.System.Util;
import Hazel.System.Asset.AssetManager;

/**
 * {@code Hazel} is the main game engine class.
 * <br>
 * This class should be used as an extension to make, making games easier.
 */
public abstract class Hazel implements Runnable, Cortex
{
    public static final String ENGINE_TITLE = "Hazel"; //The game engine title
    private static final String ENGINE_VERSION = "0.1a"; //The game engine version

    private static int gameWidth; //Width of the game window
    private static int gameHeight; //Height of the game window
    private static int gameScale; //The scale of the game window
    @SuppressWarnings("unused")
    private int gameRatio; //The aspect ratio of the game window
    private static String gameTitle; //The title of the game window
    private static String gameVersion; //The version of the game
    private static final int WideScreen = 0x0; //16 x 9 Aspect Ratio
    public static final int Square = 0x1; // 4 x 3 Aspect Ratio

    private boolean isRunning = false; //The variable that controls if game is running or not
    private static final String ERROR_MESSAGE = "Failed to Load " + gameTitle + " " + gameVersion; //Basic error message
    @SuppressWarnings("unused")
    private long start = System.currentTimeMillis(); //The tile timer

    private boolean fpsVerbose = false; //The global debug mode variable

    private Hazel gameEngine; //The game object
    private GameWindow gameWindow; //The game window handler
    private Context ctx; //Game render 'canvas'
    private VolatileImage nativeImage; //Native hardware accelerated canvas image
    private int numBuffers = 3; //Number of BufferStrategy to use (higher prevents flicker, but slows performance)
    private int targetFPS = 60; //Desired FPS performance (Default Value = 60 FPS)
    private double delta = 0d; //Time elapsed between each frame
    private Input input; //The game input handler

    protected static Manager manager; //handler for all game object's

    /**
     * Creates the game engine instance.
     *
     * @param gameTitle   The title of the game.
     * @param gameVersion The version of the game.
     * @param gameWidth   The width of the game window.
     * @param gameRatio   The aspect ratio of the game window based on the {@code gameWidth}.
     * @param gameScale   The scale of the game window.
     */
    @SuppressWarnings("static-access")
    public Hazel(String gameTitle, String gameVersion, int gameWidth, int gameRatio, int gameScale)
    {
        printStartScreen();

        this.gameTitle = gameTitle;
        this.gameVersion = gameVersion;
        this.gameWidth = gameWidth;
        this.gameRatio = gameRatio;
        this.gameScale = gameScale;
        if (gameRatio == WideScreen) this.gameHeight = gameWidth / 16 * 9;
        else this.gameHeight = gameWidth / 4 * 3;

        ctx = new Context(gameWidth / gameScale, gameHeight / gameScale);

        this.gameEngine = this;
        gameWindow = new GameWindow(this);
        input = new Input(this);

        manager = new Manager(this);
        manager.setGameWindow(gameWindow);
        manager.setInput(input);

        start();
    }

    /**
     * Creates the game engine instance.
     *
     * @param window      The window object or gameWindow of the game.
     * @param gameVersion The version of the game.
     */
    @SuppressWarnings("static-access")
    public Hazel(GameWindow window, String gameVersion)
    {
        printStartScreen();

        this.gameWindow = window;
        this.gameTitle = window.getTitle();
        this.gameVersion = gameVersion;
        this.gameWidth = window.getWidth();
        this.gameHeight = window.getHeight();
        this.gameScale = window.getScale();

        ctx = new Context(gameWidth / gameScale, gameHeight / gameScale);

        this.gameEngine = this;
        gameWindow.createWindow(this);
        input = new Input(this);

        manager = new Manager(this);
        manager.setGameWindow(gameWindow);
        manager.setInput(input);

        start();
    }


    /**
     * Starts the game loop by creating a new thread which starts
     * the {@code run()} method.
     */
    public synchronized void start()
    {
        if (isRunning) return;
        isRunning = true;
        Thread gameThread = new Thread(this, gameTitle + " " + gameVersion);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }

    /**
     * Stops the game loop by setting the {@code isRunning} variable
     * to false.
     */
    private synchronized void stop()
    {
        if (!isRunning) return;
        isRunning = false;
    }

    /**
     * The game loop handles the frame rate of the game.
     */
    @Override
    public void run()
    {
        init();

        long then = System.nanoTime();
        double unprocessed = 0;
        double nsPerFrame = 1000000000.0d / targetFPS;
        int frames = 0, updates = 0;
        long lastVerbose = System.currentTimeMillis();

        while (isRunning)
        {
            long now = System.nanoTime();
            delta = unprocessed += (now - then) / nsPerFrame;
            then = now;
            boolean shouldRender = false;

            while (unprocessed >= 1)
            {
                update();
                updates++;

                unprocessed -= 1;
                shouldRender = true;
            }

            try
            {
                Thread.sleep(2);
            } catch (InterruptedException e)
            {
                new Error(ERROR_MESSAGE);
            }

            if (shouldRender)
            {
                render();
                frames++;
            }

            if (fpsVerbose && System.currentTimeMillis() - lastVerbose > 1000)
            {
                System.out.printf("[%d fps, %d updates]\n", frames, updates);
                lastVerbose += 1000;
                frames = 0;
                updates = 0;
            }
        }

        cleanUp();
        stop();
    }

    /**
     * The parent update method which handles internal engine
     * object updates before invoking <pre>update()</pre>.
     */
    private void update()
    {
        update(manager, delta);
        input.update();
    }

    /**
     * The parent render method which handles internal engine
     * component rendering before invoking <pre>render()</pre>.
     */
    private void render()
    {
        int ctxWidth = ctx.getWidth(), ctxHeight = ctx.getHeight();
        if (nativeImage == null)
        {
            nativeImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration().createCompatibleVolatileImage(ctxWidth, ctxHeight, VolatileImage.TRANSLUCENT);
        }

        BufferStrategy bs = this.gameWindow.getBufferStrategy();
        if (bs == null)
        {
            this.gameWindow.getCanvas().createBufferStrategy(numBuffers);
            this.gameWindow.getCanvas().requestFocus();
            return;
        }

        Graphics g = bs.getDrawGraphics();
        ctx.clear();

        render(manager, ctx);

        Graphics2D _g = nativeImage.createGraphics();
        _g.drawImage(ctx.getImage(), 0, 0, null);
        _g.dispose();
        g.drawImage(nativeImage, 0, 0, getWidth() * getScale(), getHeight() * getScale(), null);
        g.dispose();
        bs.show();
    }

    /**
     * Method used to clean up memory used by
     * certain processes.
     */
    private void cleanUp()
    {
        gameWindow.cleanUp();
        AssetManager.cleanUp();
    }

    /**
     * Prints the tile screen.
     */
    private void printStartScreen()
    {
        Util.log(
                "\t\t-[" + ENGINE_TITLE + "]-" +
                        "\n[Engine Version]: " + ENGINE_VERSION +
                        "\n[Unique Build Number]: " + Util.generateCode(16) +
                        "\n-------------------------------"
        );
    }

    /**
     * Sets the desired FPS.
     *
     * @param target Desired FPS.
     */
    public void setTargetFPS(int target)
    {
        this.targetFPS = target;
    }

    /**
     * @return The current desired FPS.
     */
    public int getTargetFPS()
    {
        return targetFPS;
    }


    /**
     * Sets the output of FPS value per second.
     *
     * @param verbose FPS verbose flag.
     */
    public void setFPSVerbose(boolean verbose)
    {
        this.fpsVerbose = verbose;
    }

    /**
     * @return FPS verbose flag.
     */
    public boolean isFPSVerbose()
    {
        return fpsVerbose;
    }

    /**
     * @return The game title.
     */
    public static String getTitle()
    {
        return gameTitle;
    }

    /**
     * @return The game window's width.
     */
    public static int getWidth()
    {
        return gameWidth;
    }

    /**
     * @return The game window's height.
     */
    public static int getHeight()
    {
        return gameHeight;
    }

    /**
     * @return The game window's scale.
     */
    public static int getScale()
    {
        return gameScale;
    }

    /**
     * @return The game window's scaled width.
     */
    public static int getScaledWidth()
    {
        return gameWidth * gameScale;
    }

    /**
     * @return The game window's scaled height.
     */
    public static int getScaledHeight()
    {
        return gameHeight * gameScale;
    }

    /**
     * @return The number of BufferStrategy to use (higher prevents flicker but slows performance).
     */
    public int getNumBuffers()
    {
        return numBuffers;
    }

    /**
     * @return The game version.
     */
    public static String getVersion()
    {
        return gameVersion;
    }

    /**
     * @return The game window object.
     */
    public GameWindow getGameWindow()
    {
        return gameWindow;
    }

    /**
     * @return The input object.
     */
    public Input getInput()
    {
        return input;
    }

    /**
     * @return The game-engine object.
     */
    public Hazel getMeteor()
    {
        return gameEngine;
    }

    /**
     * @return The system manager object.
     */
    public static Manager getManager()
    {
        return manager;
    }
}
