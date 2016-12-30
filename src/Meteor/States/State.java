package Meteor.States;

import Meteor.GameEngine.Interfaces.Cortex;

/**
 * {@code State} is the generic game state for a game. 
 * <br>
 * This class is a generic representation of a state in a game.
 */
public abstract class State implements Cortex {
	private String name; //The key of the state
	protected StateManager stateManager; //The game state manager object

	/**
	 * The constructor used to create a game state.
	 * 
	 * @param name The key of the state.
	 * @param stateManager The game state manager object.
	 */
	public State(String name, StateManager stateManager) {
		this.name = name;
		this.stateManager = stateManager;
	}

	/**
	 * @return The key of the state.
     */
	public String getName() {
		return name;
	}

	/**
	 * @return The state manager object.
     */
	public StateManager getStateManager() {
		return stateManager;
	}
}