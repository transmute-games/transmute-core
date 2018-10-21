package Hazel.GameEngine.Interfaces;

import Hazel.GameEngine.Manager;
import Hazel.Graphics.Context;

/**
 * {@code Cortex} is the main game engine's interface class.
 * <br>
 * This class is used to force other classes extending {@code Hazel} to implement its methods.
 */
public interface Cortex
{
    /**
     * A method that takes field variable(s) and instantiates them.
     */
    void init();

    /**
     * The required update() method for the {@code Hazel} object.
     * Updates the game based on the back-end game clock.
     *
     * @param manager The engine manager object.
     * @param delta   Time elapsed between each frame.
     */
    void update(Manager manager, double delta);

    /**
     * The required render() method for the {@code Hazel} object.
     * Renders all of the game objects based on the back-end game clock.
     *
     * @param manager The engine manager object.
     * @param ctx     The Game render 'canvas'.
     */
    void render(Manager manager, Context ctx);
}
