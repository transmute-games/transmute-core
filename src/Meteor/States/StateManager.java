package Meteor.States;

import java.util.Stack;

import Meteor.GameEngine.Manager;
import Meteor.GameEngine.Meteor;
import Meteor.GameEngine.Interfaces.Cortex;
import Meteor.Graphics.Context;
import Meteor.System.Error;

/**
 * {@code GameStateManager} is the state manager class. 
 * <br>
 * This class should be used to manage various game states in a game.
 */
public class StateManager implements Cortex {
	public static String CLASS_NAME = "stateManager"; //The key of the class

	private Stack<State> stateStack = new Stack<>(); //The stack of game state's
	private Meteor gameEngine; //The engine manager object
	
	/**
	 * The constructor used to create a game state manager object.
	 * 
	 * @param gameEngine The game engine object.
	 */
	public StateManager(Meteor gameEngine) {
		this.gameEngine = gameEngine;
	}

	@Override public void init() {}

	@Override public void update(Manager manager, double deltaTime) {
		peek().update(manager, deltaTime);
	}

	@Override public void render(Manager manager, Context context) {
		peek().render(manager, context);
	}

	
	/**
	 * Method used to peek at the current game state.
	 * 
	 * @return Current game state.
	 */
	private State peek() {
		//if (stateStack.size() != 0) new Error("[" + StateManager.CLASS_NAME + "]: The stack is empty.");
		
		return stateStack.peek();
	}
	
	/**
	 * Method used to add a new state to the game.
	 * 
	 * @param newState The state to add the game.
	 */
	public void push(State newState) {
		if (stateStack.size() != 0 && peek().getName().equalsIgnoreCase(newState.getName()))
			new Error("[" + StateManager.CLASS_NAME + "]: The current state has the same name as the inputted state.");
		else stateStack.push(newState);
	}
	
	/**
	 * Method used to remove the current state of the game.
	 */
	@SuppressWarnings("unused")
	private void pop() {
		stateStack.pop();
	}

	/**
	 * @return The game engine object.
	 */
	public Meteor getGameEngine() {
		return gameEngine;
	}
}
