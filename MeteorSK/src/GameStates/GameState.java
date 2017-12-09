package GameStates;

import GameEngine.Game;
import GameEngine.GameManager;
import Meteor.GameEngine.Manager;
import Meteor.Graphics.Context;
import Meteor.Graphics.Sprites.Spritesheet;
import Meteor.States.State;
import Meteor.States.StateManager;
import Meteor.System.Asset.Type.Fonts.Font;

public class GameState extends State
{

    public GameState(StateManager sManager)
    {
        super("gameState", sManager);
        init();
    }

    @Override
    public void init()
    {
        @SuppressWarnings("unused")
        GameManager gManager = Game.getGameManager();
    }

    @Override
    public void update(Manager manager, double delta)
    {
    }

    @Override
    public void render(Manager manager, Context ctx)
    {
        
    }
}
