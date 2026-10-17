package TransmuteCore.core;

import TransmuteCore.core.interfaces.Cortex;
import TransmuteCore.core.interfaces.GameLoopExceptionHandler;
import TransmuteCore.core.interfaces.LifecycleCallbacks;
import TransmuteCore.util.Logger;

/**
 * Handles the fixed-timestep game loop with delta time calculations.
 * Provides frame rate control and optional FPS/update logging.
 */
public class GameLoop implements Runnable
{
    private static final String ERROR_MESSAGE = "An error has occurred within the game thread.";
    
    private final Cortex cortex;
    private final GameContext context;
    private final ManagerProvider managerProvider;
    private final LifecycleCallbacks lifecycleCallbacks;
    private final GameLoopExceptionHandler exceptionHandler;
    private final boolean stopOnException;
    private final Runnable updateCallback;
    private final Runnable renderCallback;
    
    /**
     * Functional interface for lazy Manager access.
     */
    @FunctionalInterface
    public interface ManagerProvider {
        Manager getManager();
    }
    
    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;
    private int targetFPS = 60;
    private boolean fpsVerbose = false;
    private double delta = 0;
    
    private Thread gameThread;
    private final FrameMetrics frameMetrics = new FrameMetrics();
    
    /**
     * Creates a new game loop with the specified cortex and context.
     *
     * @param cortex              The game's cortex implementation
     * @param context             The game context with all services
     * @param managerProvider     Provider for lazy Manager access (for backward compatibility)
     * @param lifecycleCallbacks  Callbacks for lifecycle events (pause, resume, etc.)
     * @param exceptionHandler    Handler for exceptions in the game loop (can be null)
     * @param stopOnException     Whether to stop the loop when an exception occurs
     * @param updateCallback      Callback to run after cortex update (e.g., input update)
     * @param renderCallback      Callback to run after cortex render (e.g., buffer swap)
     */
    public GameLoop(Cortex cortex, GameContext context, ManagerProvider managerProvider, LifecycleCallbacks lifecycleCallbacks, GameLoopExceptionHandler exceptionHandler, boolean stopOnException, Runnable updateCallback, Runnable renderCallback)
    {
        this.cortex = cortex;
        this.context = context;
        this.managerProvider = managerProvider;
        this.lifecycleCallbacks = lifecycleCallbacks;
        this.exceptionHandler = exceptionHandler;
        this.stopOnException = stopOnException;
        this.updateCallback = updateCallback;
        this.renderCallback = renderCallback;
    }
    
    /**
     * Starts the game loop in a new thread.
     *
     * @param threadName The name for the game thread
     */
    public synchronized void start(String threadName)
    {
        if (isRunning) return;
        isRunning = true;
        gameThread = new Thread(this, threadName);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }
    
    /**
     * Stops the game loop.
     */
    public synchronized void stop()
    {
        if (!isRunning) return;
        isRunning = false;
    }
    
    /**
     * Pauses the game loop.
     * The game thread continues running but update/render are not called.
     * This method is thread-safe.
     */
    public synchronized void pause()
    {
        if (!isRunning || isPaused) return;
        isPaused = true;
        
        // Call lifecycle hook
        if (lifecycleCallbacks != null)
        {
            lifecycleCallbacks.onPause();
        }
    }
    
    /**
     * Resumes the game loop from a paused state.
     * This method is thread-safe.
     */
    public synchronized void resume()
    {
        if (!isRunning || !isPaused) return;
        isPaused = false;
        
        // Call lifecycle hook
        if (lifecycleCallbacks != null)
        {
            lifecycleCallbacks.onResume();
        }
    }
    
    /**
     * The main game loop with fixed timestep and delta time.
     */
    @Override
    public void run()
    {
        // Initialize cortex with exception handling
        try
        {
            cortex.init();
            
            // Call lifecycle hook after cortex initialization
            if (lifecycleCallbacks != null)
            {
                lifecycleCallbacks.onGameLoopStart();
            }
        }
        catch (Exception e)
        {
            if (handleGameLoopException("init", e))
            {
                return; // Stop the game loop
            }
        }
        
        long then = System.nanoTime();
        double unprocessed = 0;
        double nsPerFrame = 1000000000.0d / targetFPS;
        int frames = 0, updates = 0;
        long lastVerbose = System.currentTimeMillis();
        
        while (isRunning)
        {
            // If paused, sleep and skip update/render
            if (isPaused)
            {
                try
                {
                    Thread.sleep(100); // Sleep longer when paused
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    Logger.error(ERROR_MESSAGE);
                }
                continue;
            }
            
            long now = System.nanoTime();
            delta = unprocessed += (now - then) / nsPerFrame;
            then = now;
            boolean shouldRender = false;
            
            while (unprocessed >= 1)
            {
                long updateStart = System.nanoTime();
                try
                {
                    cortex.update(managerProvider.getManager(), delta);
                    if (updateCallback != null)
                    {
                        updateCallback.run();
                    }
                    updates++;
                    
                    // Record update time
                    long updateEnd = System.nanoTime();
                    frameMetrics.recordUpdateTime((updateEnd - updateStart) / 1_000_000.0);
                }
                catch (Exception e)
                {
                    if (handleGameLoopException("update", e))
                    {
                        isRunning = false;
                        return; // Stop the game loop
                    }
                }
                
                unprocessed -= 1;
                shouldRender = true;
            }
            
            try
            {
                Thread.sleep(2);
            } catch (InterruptedException e)
            {
                Logger.error(ERROR_MESSAGE);
            }
            
            if (shouldRender)
            {
                long renderStart = System.nanoTime();
                try
                {
                    if (renderCallback != null)
                    {
                        renderCallback.run();
                    }
                    frames++;
                    
                    // Record render time
                    long renderEnd = System.nanoTime();
                    frameMetrics.recordRenderTime((renderEnd - renderStart) / 1_000_000.0);
                }
                catch (Exception e)
                {
                    if (handleGameLoopException("render", e))
                    {
                        isRunning = false;
                        return; // Stop the game loop
                    }
                }
            }
            
            if (fpsVerbose && System.currentTimeMillis() - lastVerbose > 1000)
            {
                // Update metrics with current rates
                frameMetrics.updateRates(frames, updates);
                
                System.out.printf("[%d fps, %d ups] avgUpdate=%.2fms, avgRender=%.2fms%n", 
                    frames, updates, frameMetrics.getAverageUpdateTime(), frameMetrics.getAverageRenderTime());
                lastVerbose += 1000;
                frames = 0;
                updates = 0;
            }
        }
    }
    
    /**
     * @return Whether the game loop is running
     */
    public boolean isRunning()
    {
        return isRunning;
    }
    
    /**
     * @return Whether the game loop is paused
     */
    public boolean isPaused()
    {
        return isPaused;
    }
    
    /**
     * @return The current delta time
     */
    public double getDelta()
    {
        return delta;
    }
    
    /**
     * Sets the target FPS for the game loop.
     *
     * @param target The desired FPS (must be between 1 and 1000)
     */
    public void setTargetFPS(int target)
    {
        if (target <= 0)
        {
            throw new IllegalArgumentException(
                String.format("Target FPS must be positive. Got: %d", target)
            );
        }
        if (target > 1000)
        {
            throw new IllegalArgumentException(
                String.format("Target FPS seems unreasonably high: %d. Maximum allowed is 1000.", target)
            );
        }
        this.targetFPS = target;
    }
    
    /**
     * @return The target FPS
     */
    public int getTargetFPS()
    {
        return targetFPS;
    }
    
    /**
     * Enables or disables FPS logging.
     *
     * @param verbose true to enable FPS logging
     */
    public void setFpsVerbose(boolean verbose)
    {
        this.fpsVerbose = verbose;
    }
    
    /**
     * @return Whether FPS logging is enabled
     */
    public boolean isFpsVerbose()
    {
        return fpsVerbose;
    }
    
    /**
     * @return The frame metrics tracker for performance monitoring
     */
    public FrameMetrics getFrameMetrics()
    {
        return frameMetrics;
    }
    
    /**
     * Handles an exception that occurred in the game loop.
     *
     * @param phase     The phase where the exception occurred
     * @param exception The exception that was thrown
     * @return true if the game loop should stop, false to continue
     */
    private boolean handleGameLoopException(String phase, Exception exception)
    {
        // Log the error
        Logger.error("Exception in game loop (%s): %s", phase, exception.getMessage());
        exception.printStackTrace();
        
        // Call custom exception handler if provided
        if (exceptionHandler != null)
        {
            return exceptionHandler.handleException(phase, exception);
        }
        
        // Default behavior: stop on exception if configured
        return stopOnException;
    }
}
