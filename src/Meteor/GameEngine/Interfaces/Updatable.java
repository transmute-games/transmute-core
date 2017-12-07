package Meteor.GameEngine.Interfaces;

import Meteor.GameEngine.Manager;

/**
 * {@code Updatable} is the main game engine's update interface class.
 * <br>
 * This class is used to force other classes implementing {@code Updatable} to implement its methods.
 */
public interface Updatable
{
    /**
     * The required update() method for the {@code Meteor} object.
     * Updates the game based on the back-end game clock.
     *
     * @param manager The engine manager object.
     * @param delta   Time elapsed between each frame.
     */
    void update(Manager manager, double delta);
}
