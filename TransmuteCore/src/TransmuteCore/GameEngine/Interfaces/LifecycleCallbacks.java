package TransmuteCore.GameEngine.Interfaces;

/**
 * Interface for game lifecycle event callbacks.
 * <p>
 * Implement this interface to hook into engine lifecycle events without
 * overriding internal methods. All methods have default empty implementations,
 * so you only need to override the events you care about.
 * <p>
 * <b>Lifecycle Event Order:</b>
 * <ol>
 *   <li>{@link #onEngineStart()} - after engine construction, before game loop thread starts</li>
 *   <li>{@link #onGameLoopStart()} - when game loop thread starts, after cortex.init()</li>
 *   <li>{@link #onPause()} - when game is paused</li>
 *   <li>{@link #onResume()} - when game resumes from pause</li>
 *   <li>{@link #onShutdown()} - before cleanup during shutdown</li>
 * </ol>
 * <p>
 * Example usage:
 * <pre>{@code
 * public class MyGame extends TransmuteCore {
 *     &#64;Override
 *     public void onEngineStart() {
 *         // Load configuration, initialize services
 *     }
 *     
 *     &#64;Override
 *     public void onPause() {
 *         // Save game state, pause audio
 *     }
 *     
 *     &#64;Override
 *     public void onShutdown() {
 *         // Save player progress, close connections
 *     }
 * }
 * }</pre>
 */
public interface LifecycleCallbacks
{
    /**
     * Called after engine construction but before the game loop thread starts.
     * This is the ideal place to perform additional initialization, load
     * configuration, or set up services.
     * <p>
     * This runs on the main thread before {@link #onGameLoopStart()}.
     */
    default void onEngineStart()
    {
        // Default: do nothing
    }
    
    /**
     * Called when the game loop thread starts, immediately after cortex.init().
     * This runs on the game thread and can be used to initialize thread-local
     * resources or perform setup that must happen on the game thread.
     */
    default void onGameLoopStart()
    {
        // Default: do nothing
    }
    
    /**
     * Called when the game is paused.
     * Use this to pause audio, save state, or perform other pause-related tasks.
     * <p>
     * The game loop continues running but update/render are not called while paused.
     */
    default void onPause()
    {
        // Default: do nothing
    }
    
    /**
     * Called when the game resumes from a paused state.
     * Use this to resume audio, restore state, or perform other resume-related tasks.
     */
    default void onResume()
    {
        // Default: do nothing
    }
    
    /**
     * Called before cleanup during shutdown.
     * This is the last chance to save data, close connections, or perform
     * other cleanup before the engine shuts down.
     * <p>
     * This runs on the main thread after the game loop has stopped.
     */
    default void onShutdown()
    {
        // Default: do nothing
    }
}
