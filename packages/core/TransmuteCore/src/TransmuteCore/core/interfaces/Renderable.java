package TransmuteCore.core.interfaces;

import TransmuteCore.core.Manager;
import TransmuteCore.core.interfaces.services.IRenderer;

/**
 * {@code Renderable} is the main game engine's rendering interface class.
 * <br>
 * This class is used to force other classes implementing {@code Renderable} to implement its methods.
 */
public interface Renderable
{
    /**
     * The required render() method for the {@code TransmuteCore} object.
     * Renders all of the game objects based on the back-end game clock.
     *
     * @param manager  The engine manager object.
     * @param renderer The renderer interface (Context implementation).
     */
    void render(Manager manager, IRenderer renderer);
}
