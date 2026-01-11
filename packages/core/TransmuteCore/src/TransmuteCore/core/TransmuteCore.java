package TransmuteCore.core;

import TransmuteCore.core.Interfaces.Cortex;
import TransmuteCore.core.Interfaces.LifecycleCallbacks;
import TransmuteCore.core.Interfaces.WindowEventCallbacks;
import TransmuteCore.core.Interfaces.Services.IAssetManager;
import TransmuteCore.core.Interfaces.Services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.input.Input;
import TransmuteCore.util.Logger;
import TransmuteCore.util.Util;
import TransmuteCore.assets.AssetManager;

/**
 * {@code TransmuteCore} is the main game engine class.
 * <br>
 * This class should be used as an extension to make, making games easier.
 * <p>
 * <b>Thread Safety:</b>
 * <ul>
 *   <li>{@link #start()}, {@link #shutdown()}, {@link #isRunning()} are thread-safe</li>
 *   <li>Getters ({@link #getConfig()}, {@link #getContext()}, {@link #getManager()}, etc.) should only be called from the game thread or after proper synchronization</li>
 *   <li>{@link #getManager()} creates the Manager lazily and is safe to call concurrently</li>
 * </ul>
 * <p>
 * <b>Lifecycle Hooks:</b>
 * Override methods from {@link LifecycleCallbacks} to hook into engine lifecycle events:
 * <ul>
 *   <li>{@link #onEngineStart()} - after construction, before game loop starts</li>
 *   <li>{@link #onGameLoopStart()} - when game loop thread starts</li>
 *   <li>{@link #onPause()} - when game is paused</li>
 *   <li>{@link #onResume()} - when game resumes</li>
 *   <li>{@link #onShutdown()} - before cleanup</li>
 * </ul>
 * <p>
 * <b>Window Event Hooks:</b>
 * Override methods from {@link WindowEventCallbacks} to respond to window events:
 * <ul>
 *   <li>{@link #onWindowFocusLost()} - when window loses focus</li>
 *   <li>{@link #onWindowFocusGained()} - when window gains focus</li>
 *   <li>{@link #onWindowClosing()} - before window closes</li>
 * </ul>
 */
public abstract class TransmuteCore implements Cortex, LifecycleCallbacks, WindowEventCallbacks
{
    public static final String ENGINE_TITLE = "TransmuteCore"; //The game engine title
    private static final String ENGINE_VERSION = "0.1a"; //The game engine version
    
    // Aspect ratio constants
    public static final int WideScreen = 0x0; // 16 x 9 Aspect Ratio
    public static final int Square = 0x1; // 4 x 3 Aspect Ratio

    private GameConfig gameConfig; //The game configuration
    private GameContext gameContext; //The game context with services
    private GameWindow gameWindow; //The game window handler
    private Context ctx; //Game render 'canvas'
    private Input input; //The game input handler
    protected volatile Manager manager; //handler for all game object's (backward compatibility, lazy-loaded)
    private final Object managerLock = new Object(); //Lock for thread-safe Manager initialization
    
    private GameLoop gameLoop; //The game loop handler
    private RenderPipeline renderPipeline; //The render pipeline

    /**
     * Creates the game engine instance with a GameConfig.
     * This constructor does NOT automatically start the game loop.
     * Call {@link #start()} explicitly when ready.
     *
     * @param config The game configuration.
     */
    protected TransmuteCore(GameConfig config)
    {
        if (config == null) {
            throw new IllegalArgumentException("GameConfig cannot be null");
        }
        
        this.gameConfig = config;
        
        if (config.isShowStartScreen()) {
            printStartScreen();
        }
        
        ctx = new Context(config.getWidth() / config.getScale(), config.getHeight() / config.getScale());
        
        // Only create window and input if not in headless mode
        if (!config.isHeadless()) {
            gameWindow = new GameWindow(config.getTitle(), config.getWidth(), config.getHeight(), config.getScale());
            gameWindow.setWindowEventCallbacks(this); // Register for window events
            gameWindow.createWindow(config);
            input = new Input(gameWindow.getCanvas(), config.getScale());
        } else {
            Logger.info("Running in headless mode (no window or input)");
            gameWindow = null;
            input = null;
        }
        
        // Create GameContext
        IAssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) {
            Logger.warn("AssetManager global instance is null. Asset loading may not work correctly.");
        }
        
        gameContext = new GameContext.Builder()
            .config(config)
            .assetManager(assetManager)
            .inputHandler(input)
            .renderer((IRenderer)ctx)
            .gameWindow(gameWindow)
            .build();
        
        // Create render pipeline with lazy Manager provider (only if not headless)
        if (!config.isHeadless()) {
            renderPipeline = new RenderPipeline(
                this,
                (IRenderer)ctx,
                gameWindow,
                this::getManager, // Method reference for lazy Manager access
                config.getBufferCount(),
                config.getWidth(),
                config.getHeight(),
                config.getScale()
            );
        } else {
            renderPipeline = null;
        }
        
        // Create game loop with callbacks and lazy Manager provider
        // Default exception handler: logs to console and uses stopOnException config
        gameLoop = new GameLoop(
            this,
            gameContext,
            this::getManager, // Method reference for lazy Manager access
            this, // Lifecycle callbacks
            null, // Default exception handler (logs and uses stopOnException)
            config.isStopOnException(),
            this::updateCallback,
            this::renderCallback
        );
        gameLoop.setTargetFPS(config.getTargetFPS());
        gameLoop.setFpsVerbose(config.isFpsVerbose());
        
        // Call lifecycle hook after engine initialization
        onEngineStart();
    }

    /**
     * Starts the game loop by creating a new thread which starts
     * the {@code run()} method.
     */
    public synchronized void start()
    {
        if (gameLoop.isRunning()) return;
        gameLoop.start(gameConfig.getTitle() + " " + gameConfig.getVersion());
    }

    /**
     * Checks if the game loop is currently running.
     * This method is thread-safe.
     *
     * @return True if the game loop is running, false otherwise.
     */
    public synchronized boolean isRunning()
    {
        return gameLoop.isRunning();
    }
    
    /**
     * Pauses the game loop.
     * The game thread continues running but update/render are not called.
     * This method is thread-safe.
     */
    public synchronized void pause()
    {
        gameLoop.pause();
    }
    
    /**
     * Resumes the game loop from a paused state.
     * This method is thread-safe.
     */
    public synchronized void resume()
    {
        gameLoop.resume();
    }
    
    /**
     * Checks if the game loop is currently paused.
     * This method is thread-safe.
     *
     * @return True if the game loop is paused, false otherwise.
     */
    public synchronized boolean isPaused()
    {
        return gameLoop.isPaused();
    }

    /**
     * Gracefully shuts down the game loop and cleans up resources.
     * This method is thread-safe.
     */
    public synchronized void shutdown()
    {
        if (!gameLoop.isRunning()) return;
        gameLoop.stop();
        
        // Call lifecycle hook before cleanup
        onShutdown();
        
        cleanUp();
    }


    /**
     * Callback invoked after each game update.
     * Updates input state (if not in headless mode).
     */
    private void updateCallback()
    {
        if (input != null) {
            input.update();
        }
    }

    /**
     * Callback invoked for each render frame.
     * Delegates to the render pipeline (if not in headless mode).
     */
    private void renderCallback()
    {
        if (renderPipeline != null) {
            renderPipeline.render();
        }
    }

    /**
     * Method used to clean up memory used by
     * certain processes.
     */
    private void cleanUp()
    {
        if (renderPipeline != null) {
            renderPipeline.cleanUp();
        }
        if (gameWindow != null) {
            gameWindow.cleanUp();
        }
        // Clean up via global instance if available
        AssetManager globalAssetManager = AssetManager.getGlobalInstance();
        if (globalAssetManager != null)
        {
            globalAssetManager.cleanUp();
        }
    }

    /**
     * Prints the tile screen.
     */
    private void printStartScreen()
    {
        Logger.info(
                "\t\t-[" + ENGINE_TITLE + "]-" +
                        "\n[Engine Version]: " + ENGINE_VERSION +
                        "\n[Unique Build Number]: " + Util.generateCode(16) +
                        "\n-------------------------------"
        );
    }


    /**
     * @return The game configuration.
     */
    public GameConfig getConfig()
    {
        return gameConfig;
    }

    /**
     * @return The game context with all services.
     */
    public GameContext getContext()
    {
        return gameContext;
    }


    /**
     * @return The game window object, or null if running in headless mode.
     */
    public GameWindow getGameWindow()
    {
        return gameWindow;
    }

    /**
     * @return The input object, or null if running in headless mode.
     */
    public Input getInput()
    {
        return input;
    }

    /**
     * @return The system manager object (for backward compatibility).
     * It is recommended to use {@link #getContext()} for accessing services.
     * The Manager is created lazily on first access.
     * This method is thread-safe using double-checked locking.
     */
    public Manager getManager()
    {
        // First check without synchronization (fast path)
        if (manager == null)
        {
            synchronized (managerLock)
            {
                // Second check with synchronization (slow path)
                if (manager == null)
                {
                    Manager temp = new Manager(this);
                    if (gameWindow != null) {
                        temp.setGameWindow(gameWindow);
                    }
                    if (input != null) {
                        temp.setInput(input);
                    }
                    manager = temp; // Assign only after full initialization
                }
            }
        }
        return manager;
    }

    /**
     * @return The game loop instance. Useful for advanced customization.
     */
    protected GameLoop getGameLoop()
    {
        return gameLoop;
    }

    /**
     * @return The render pipeline instance, or null if running in headless mode.
     * Useful for advanced customization.
     */
    protected RenderPipeline getRenderPipeline()
    {
        return renderPipeline;
    }
    
    /**
     * @return The frame metrics tracker for performance monitoring.
     * Provides FPS, UPS, and timing information to identify bottlenecks.
     */
    public FrameMetrics getFrameMetrics()
    {
        return gameLoop.getFrameMetrics();
    }
}
