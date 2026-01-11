package TransmuteCore.core.Interfaces.Services;

import java.awt.Canvas;
import java.awt.image.BufferStrategy;

/**
 * Interface for game window management.
 * Provides methods for window operations and canvas access.
 */
public interface IGameWindow
{
    /**
     * Gets the window title.
     *
     * @return The title.
     */
    String getTitle();

    /**
     * Gets the window width.
     *
     * @return The width.
     */
    int getWidth();

    /**
     * Gets the window height.
     *
     * @return The height.
     */
    int getHeight();

    /**
     * Gets the window scale factor.
     *
     * @return The scale.
     */
    int getScale();

    /**
     * Gets the rendering canvas.
     *
     * @return The canvas.
     */
    Canvas getCanvas();

    /**
     * Gets the buffer strategy.
     *
     * @return The buffer strategy.
     */
    BufferStrategy getBufferStrategy();

    /**
     * Cleans up window resources.
     */
    void cleanUp();
}
