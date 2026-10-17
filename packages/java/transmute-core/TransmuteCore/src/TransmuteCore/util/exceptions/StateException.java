package TransmuteCore.util.exceptions;

/**
 * Exception thrown when there's an error with game state management.
 * This includes issues with pushing, popping, or accessing states.
 */
public class StateException extends TransmuteCoreException {
    
    private final String stateName;
    
    /**
     * Creates a new StateException.
     *
     * @param stateName The name of the state involved in the error.
     * @param message   Details about the error.
     */
    public StateException(String stateName, String message) {
        super("State error for '" + stateName + "': " + message);
        this.stateName = stateName;
    }
    
    /**
     * Creates a new StateException with a cause.
     *
     * @param stateName The name of the state involved in the error.
     * @param message   Details about the error.
     * @param cause     The underlying cause of the error.
     */
    public StateException(String stateName, String message, Throwable cause) {
        super("State error for '" + stateName + "': " + message, cause);
        this.stateName = stateName;
    }
    
    /**
     * @return The name of the state involved in the error.
     */
    public String getStateName() {
        return stateName;
    }
    
    /**
     * Creates an exception for when a state stack is empty.
     *
     * @return A StateException indicating an empty stack.
     */
    public static StateException emptyStack() {
        return new StateException("N/A", 
            "State stack is empty. You must push at least one state before calling peek().\n" +
            "Suggestion: Call stateManager.push(new YourState(stateManager)) in your init() method.");
    }
    
    /**
     * Creates an exception for when attempting to push a duplicate state.
     *
     * @param stateName The name of the duplicate state.
     * @return A StateException indicating a duplicate state.
     */
    public static StateException duplicateState(String stateName) {
        return new StateException(stateName,
            "Attempted to push a state with the same name as the current top state.\n" +
            "Each state in the stack must have a unique name.\n" +
            "Suggestion: Either use a different name or pop the current state before pushing the new one.");
    }
}
