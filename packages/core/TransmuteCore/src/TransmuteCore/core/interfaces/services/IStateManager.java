package TransmuteCore.core.interfaces.services;

import TransmuteCore.state.State;

/**
 * Interface for managing game states.
 * Provides methods for state stack manipulation.
 */
public interface IStateManager
{
    /**
     * Initializes the state manager.
     */
    void init();

    /**
     * Gets the current (top) state from the stack.
     *
     * @return The current state.
     */
    State peek();

    /**
     * Gets the number of states in the stack.
     *
     * @return The stack size.
     */
    int getStackSize();

    /**
     * Pushes a new state onto the stack.
     *
     * @param newState The state to push.
     */
    void push(State newState);
}
