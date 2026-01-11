package GameEngine;

import java.awt.event.KeyEvent;
import java.io.File;

import GameStates.LoadingState;
import TransmuteCore.GameEngine.GameConfig;
import TransmuteCore.GameEngine.TransmuteCore;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.GameEngine.Interfaces.Services.IRenderer;
import TransmuteCore.Input.Input;
import TransmuteCore.States.StateManager;
import Utilities.Library;

public class Game extends TransmuteCore
{
    private StateManager stateManager;

    public Game(GameConfig config)
    {
        super(config);
    }

    @Override
    public void init()
    {
        stateManager = new StateManager(this);

        getManager().setStateManager(stateManager);

        stateManager.push(new LoadingState(stateManager));
    }

    @Override
    public void update(Manager manager, double delta)
    {
        Input input = getInput();
        if (input != null && input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            shutdown();
        }

        if (stateManager != null) {
            stateManager.update(manager, delta);
        }
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        if (stateManager != null) {
            stateManager.render(manager, renderer);
        }
    }

    public static void main(String[] args)
    {
        GameConfig config = new GameConfig.Builder()
            .title(Library.GAME_TITLE)
            .version(Library.GAME_VERSION)
            .dimensions(640, GameConfig.ASPECT_RATIO_SQUARE)
            .scale(1)
            .fpsVerbose(true)
            .build();
        
        Game game = new Game(config);
        game.start();
    }
}
