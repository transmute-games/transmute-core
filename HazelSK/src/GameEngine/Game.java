package GameEngine;

import java.awt.event.KeyEvent;

import GameStates.LoadingState;
import Hazel.GameEngine.Hazel;
import Hazel.GameEngine.Manager;
import Hazel.Graphics.Context;
import Hazel.Input.Input;
import Hazel.States.StateManager;
import Utilities.Library;

public class Game extends Hazel
{
    private StateManager stateManager;

    public Game(String gameTitle, String gameVersion, int gameWidth, int gameRatio, int gameScale)
    {
        super(gameTitle, gameVersion, gameWidth, gameRatio, gameScale);
    }

    @Override
    public void init()
    {
        setFPSVerbose(true);

        stateManager = new StateManager(this);

        manager.setStateManager(stateManager);

        stateManager.push(new LoadingState(stateManager));
    }

    @Override
    public void update(Manager manager, double delta)
    {
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) System.exit(0);

        stateManager.update(manager, delta);
    }

    @Override
    public void render(Manager manager, Context ctx)
    {
        stateManager.render(manager, ctx);
    }

    public static void main(String[] args)
    {
        new Game(Library.GAME_TITLE, Library.GAME_VERSION, 640, Hazel.Square, 1);
    }
}
