package TransmuteCore.GameEngine.Interfaces;

/**
 * Interface for window event callbacks.
 * <p>
 * Implement this interface to respond to window events like focus changes,
 * window closing, and resizing. All methods have default empty implementations.
 * <p>
 * Example usage:
 * <pre>{@code
 * public class MyGame extends TransmuteCore implements WindowEventCallbacks {
 *     &#64;Override
 *     public void onWindowFocusLost() {
 *         pause(); // Auto-pause when window loses focus
 *     }
 *     
 *     &#64;Override
 *     public void onWindowFocusGained() {
 *         resume(); // Auto-resume when window gains focus
 *     }
 *     
 *     &#64;Override
 *     public void onWindowClosing() {
 *         // Save game state before closing
 *         saveProgress();
 *     }
 * }
 * }</pre>
 */
public interface WindowEventCallbacks
{
    /**
     * Called when the game window loses focus.
     * This is a good place to auto-pause the game.
     */
    default void onWindowFocusLost()
    {
        // Default: do nothing
    }
    
    /**
     * Called when the game window gains focus.
     * This is a good place to auto-resume the game.
     */
    default void onWindowFocusGained()
    {
        // Default: do nothing
    }
    
    /**
     * Called when the user attempts to close the window.
     * This is called before the window actually closes, allowing you
     * to save game state or show a confirmation dialog.
     * <p>
     * Note: This does not prevent the window from closing. If you need
     * to prevent closing, handle it in your window close operation.
     */
    default void onWindowClosing()
    {
        // Default: do nothing
    }
    
    /**
     * Called when the window is resized.
     * Note: By default, TransmuteCore windows are not resizable.
     *
     * @param newWidth  New window width
     * @param newHeight New window height
     */
    default void onWindowResized(int newWidth, int newHeight)
    {
        // Default: do nothing
    }
}
