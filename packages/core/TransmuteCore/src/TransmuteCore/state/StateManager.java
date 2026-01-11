package TransmuteCore.state;

import java.util.Stack;

import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.Manager;
import TransmuteCore.core.Interfaces.Cortex;
import TransmuteCore.core.Interfaces.Services.IRenderer;
import TransmuteCore.util.exceptions.StateException;

/**
 * {@code GameStateManager} is the state manager class.
 * <br>
 * This class should be used to manage various game states in a game.
 */
public class StateManager implements Cortex
{
    public static String CLASS_NAME = "stateManager"; //The key of the class

    private Stack<State> stateStack = new Stack<>(); //The stack of game state's
    private TransmuteCore gameEngine; //The engine manager object

    /**
     * The constructor used to create a game state manager object.
     *
     * @param gameEngine The game engine object.
     */
    public StateManager(TransmuteCore gameEngine)
    {
        if (gameEngine == null) {
            throw new IllegalArgumentException("Game engine cannot be null");
        }
        this.gameEngine = gameEngine;
    }

    @Override
    public void init()
    {
    }

    @Override
    public void update(Manager manager, double deltaTime)
    {
        peek().update(manager, deltaTime);
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        peek().render(manager, renderer);
    }


    /**
     * Method used to peek at the current game state.
     *
     * @return Current game state.
     */
    public State peek()
    {
        if (stateStack.size() == 0) {
            throw StateException.emptyStack();
        }

        return stateStack.peek();
    }

    /**
     * Get the number of states in the stack.
     *
     * @return Size of the state stack
     */
    public int getStackSize()
    {
        return stateStack.size();
    }

    /**
     * Method used to add a new state to the game.
     *
     * @param newState The state to add the game.
     */
    public void push(State newState)
    {
        if (newState == null) {
            throw new IllegalArgumentException("Cannot push null state");
        }
        if (stateStack.size() != 0 && peek().getName().equalsIgnoreCase(newState.getName())) {
            throw StateException.duplicateState(newState.getName());
        }
        stateStack.push(newState);
    }

    /**
     * Method used to remove the current state of the game.
     */
    @SuppressWarnings("unused")
    private void pop()
    {
        stateStack.pop();
    }

    /**
     * @return The game engine object.
     */
    public TransmuteCore getGameEngine()
    {
        return gameEngine;
    }
}
