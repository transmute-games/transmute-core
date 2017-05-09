package GameEngine;

import java.awt.event.KeyEvent;

import GameStates.LoadingState;
import Meteor.GameEngine.Manager;
import Meteor.GameEngine.Meteor;
import Meteor.Graphics.Context;
import Meteor.Input.Input;
import Meteor.States.StateManager;
import Utilities.Library;

public class Game extends Meteor {
	private StateManager stateManager;
	private static GameManager gameManager;
	
	public Game(String gameTitle, String gameVersion, int gameWidth, int gameRatio, int gameScale) {
		super(gameTitle, gameVersion, gameWidth, gameRatio, gameScale);
	}
	
	@Override public void init() {
		setTargetFPS(60);
		setFPSVerbose(true);

		stateManager = new StateManager(this);

        gameManager = new GameManager(this);
		gameManager.setStateManager(stateManager);
		
		stateManager.push(new LoadingState(stateManager));
	}

	@Override public void update(Manager manager, double delta) {
		if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) System.exit(0);
		
		stateManager.update(gameManager, delta);
	}

	@Override public void render(Manager manager, Context ctx) {
		stateManager.render(gameManager, ctx);
	}
	
	public static GameManager getGameManager() {
		return gameManager;
	}
	
	public static void main(String[] args) {
		new Game(Library.GAME_TITLE, Library.GAME_VERSION, 640, Meteor.Square, 1);
	}
}
