package TransmuteCore.GameEngine.Interfaces;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;

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
     * @param manager The engine manager object.
     * @param ctx     The Game render 'canvas'.
     */
    void render(Manager manager, Context ctx);
}
