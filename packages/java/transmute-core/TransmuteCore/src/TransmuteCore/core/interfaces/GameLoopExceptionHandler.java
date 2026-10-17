package TransmuteCore.core.interfaces;

/**
 * Functional interface for handling exceptions that occur in the game loop.
 * <p>
 * Implement this interface to provide custom error handling for exceptions
 * that occur during cortex.init(), cortex.update(), or cortex.render().
 * <p>
 * Example usage:
 * <pre>{@code
 * GameLoopExceptionHandler handler = (phase, exception) -> {
 *     Logger.error("Error in " + phase, exception);
 *     // Optionally save crash report, notify user, etc.
 *     return true; // Stop the game loop
 * };
 * }</pre>
 */
@FunctionalInterface
public interface GameLoopExceptionHandler
{
    /**
     * Called when an exception occurs in the game loop.
     *
     * @param phase     The phase where the exception occurred ("init", "update", or "render")
     * @param exception The exception that was thrown
     * @return true to stop the game loop, false to continue running
     */
    boolean handleException(String phase, Exception exception);
}
