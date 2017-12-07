package Meteor.GameEngine.Interfaces;

/**
 * {@code Initializable} is the main game engine's initializing interface class.
 * <br>
 * This class is used to force other classes implementing {@code Initializable} to implement its methods.
 */
public interface Initializable
{
    /**
     * A method that takes field variable(s) and instantiates them.
     */
    void init();
}
