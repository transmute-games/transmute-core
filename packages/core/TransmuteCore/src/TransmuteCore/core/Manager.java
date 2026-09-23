package TransmuteCore.core;

import TransmuteCore.graphics.sprites.SpriteManager;
import TransmuteCore.input.Input;
import TransmuteCore.core.interfaces.services.IInputHandler;
import TransmuteCore.ecs.ObjectManager;
import TransmuteCore.state.StateManager;
import TransmuteCore.assets.AssetManager;

/**
 * {@code Manager} is the primary authoring seam for game code.
 * <br>
 * Call {@link #bootstrapDefaults()} once from {@code init()} to wire AssetManager,
 * StateManager, and ObjectManager. Prefer Manager over {@link GameContext} in
 * game subclasses, tutorials, and agent-generated projects.
 */
public class Manager
{
    private TransmuteCore gameEngine; //The game object
    private GameWindow gameWindow; //The game gameWindow handler
    private Input input; //The game input handler (windowed)
    private IInputHandler inputHandler; // Real Input or SimulatedInput
    private StateManager stateManager; //The state manager
    private AssetManager assetManager; //The asset manager
    private ObjectManager objectManager; //The object manager
    private SpriteManager spriteManager; //The sprite manager

    /**
     * The constructor used to define the game manager object.
     *
     * @param gameEngine The main game engine.
     */
    public Manager(TransmuteCore gameEngine)
    {
        this.gameEngine = gameEngine;
    }

    /**
     * Method used to set the main game object.
     *
     * @param gameEngine The main game object.
     */
    public void setGame(TransmuteCore gameEngine)
    {
        this.gameEngine = gameEngine;
    }

    /**
     * @return The game object.
     */
    public TransmuteCore getGame()
    {
        return gameEngine;
    }

    /**
     * Method used to set the gameWindow of the game.
     *
     * @param gameWindow The gameWindow of the game.
     */
    public void setGameWindow(GameWindow gameWindow)
    {
        this.gameWindow = gameWindow;
    }

    /**
     * @return The game gameWindow object.
     */
    public GameWindow getGameWindow()
    {
        return gameWindow;
    }

    /**
     * Method used to set the input object.
     *
     * @param input The input object.
     */
    public void setInput(Input input)
    {
        this.input = input;
        if (input != null)
        {
            this.inputHandler = input;
        }
    }

    /**
     * Installs a scriptable or alternate input adapter (e.g. {@link TransmuteCore.input.SimulatedInput}).
     */
    public void setInputHandler(IInputHandler inputHandler)
    {
        this.inputHandler = inputHandler;
    }

    /**
     * @return The concrete window Input, or null in headless mode.
     */
    public Input getInput()
    {
        return input;
    }

    /**
     * @return Window Input or SimulatedInput — prefer this in game update code.
     */
    public IInputHandler getInputHandler()
    {
        return inputHandler != null ? inputHandler : input;
    }

    /**
     * @return The state manager object.
     */
    public StateManager getStateManager()
    {
        return stateManager;
    }

    /**
     * Method used to set the state manager object.
     *
     * @param stateManager The state manager object.
     */
    public void setStateManager(StateManager stateManager)
    {
        this.stateManager = stateManager;
    }

    /**
     * @return The asset manager object.
     */
    public AssetManager getAssetManager()
    {
        return assetManager;
    }

    /**
     * Method used to set the asset manager object.
     *
     * @param assetManager The asset manager object.
     */
    public void setAssetManager(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    /**
     * @return The object manager object.
     */
    public ObjectManager getObjectManager()
    {
        return objectManager;
    }

    /**
     * Method used to set the object manager object.
     *
     * @param objectManager The object manager object.
     */
    public void setObjectManager(ObjectManager objectManager)
    {
        this.objectManager = objectManager;
    }

    /**
     * @return The sprite manager object.
     */
    public SpriteManager getSpriteManager()
    {
        return spriteManager;
    }

    /**
     * Method used to set the sprite manager object.
     *
     * @param spriteManager The sprite manager object.
     */
    public void setSpriteManager(SpriteManager spriteManager)
    {
        this.spriteManager = spriteManager;
    }

    /**
     * Wires default AssetManager, StateManager, and ObjectManager if missing.
     * Call once from {@code init()} so agents have a single authoring seam via Manager.
     */
    public void bootstrapDefaults()
    {
        if (assetManager == null)
        {
            assetManager = new AssetManager();
            AssetManager.setGlobalInstance(assetManager);
        }
        if (stateManager == null)
        {
            stateManager = new StateManager(gameEngine);
        }
        if (objectManager == null)
        {
            objectManager = new ObjectManager();
        }
    }
}