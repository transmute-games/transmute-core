package Meteor.GameEngine;

import Meteor.Graphics.Sprites.SpriteManager;
import Meteor.Input.Input;
import Meteor.Objects.ObjectManager;
import Meteor.States.StateManager;
import Meteor.System.Asset.AssetManager;

/**
 * {@code Manager} is the main game manager class.
 * <br>
 * This class should be used to handle all of the game object's.
 */
public class Manager {
    private Meteor gameEngine; //The game object
    private GameWindow gameWindow; //The game gameWindow handler
    private Input input; //The game input handler
    private StateManager stateManager; //The state manager
    private AssetManager assetManager; //The asset manager
    private ObjectManager objectManager; //The object manager
    private SpriteManager spriteManager; //The sprite manager

    /**
     * The constructor used to define the game manager object.
     * 
     * @param gameEngine The main game engine.
     */
    public Manager(Meteor gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Method used to set the main game object.
     * 
     * @param gameEngine The main game object.
     */
    public void setGame(Meteor gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * @return The game object.
     */
    public Meteor getGame() {
        return gameEngine;
    }

    /**
     * Method used to set the gameWindow of the game.
     * 
     * @param gameWindow The gameWindow of the game.
     */
    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    /**
     * @return The game gameWindow object.
     */
    public GameWindow getGameWindow() {
        return gameWindow;
    }

    /**
     * Method used to set the input object.
     * 
     * @param input The input object.
     */
    public void setInput(Input input) {
        this.input = input;
    }

    /**
     * @return The input object.
     */
    public Input getInput() {
        return input;
    }
    
    /**
     * @return The state manager object.
     */
    public StateManager getStateManager() {
        return stateManager;
    }

    /**
     * Method used to set the state manager object.
     * 
     * @param stateManager The state manager object.
     */
    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    /**
     * @return The asset manager object.
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Method used to set the asset manager object.
     * 
     * @param assetManager The asset manager object.
     */
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    /**
     * @return The object manager object.
     */
    public ObjectManager getObjectManager() {
        return objectManager;
    }

    /**
     * Method used to set the object manager object.
     * 
     * @param objectManager The object manager object.
     */
    public void setObjectManager(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
    
    /**
     * @return The sprite manager object.
     */
    public SpriteManager getSpriteManager() {
        return spriteManager;
    }

    /**
     * Method used to set the sprite manager object.
     * 
     * @param spriteManager The sprite manager object.
     */
    public void setSpriteManager(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }
}