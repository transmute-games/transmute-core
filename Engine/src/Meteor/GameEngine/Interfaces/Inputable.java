package Meteor.GameEngine.Interfaces;

import Meteor.Input.Input;

/**
 * {@code Inputable} is the main game engine's input interface class.
 * <br>
 * This class is used to force other classes implementing {@code Inputable} to implement its methods.
 */
public interface Inputable
{
    /**
     * The required input method to handle input with a given object that implements it.
     *
     * @param in The input class instance used to handle mouse and keyboard input.
     */
    void input(Input in);
}
