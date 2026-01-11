package GameStates;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.GameEngine.Interfaces.Services.IRenderer;
import TransmuteCore.States.State;
import TransmuteCore.States.StateManager;
import TransmuteCore.System.Asset.AssetManager;
import Utilities.ResourceLoader;

public class LoadingState extends State
{

    public LoadingState(StateManager stateManager)
    {
        super("loadingState", stateManager);
        init();
    }

    @Override
    public void init()
    {
        ResourceLoader.load();
        
        // Assets are loaded via the global AssetManager instance
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager != null) {
            assetManager.load();
        }
    }

    @Override
    public void update(Manager manager, double delta)
    {
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager != null && assetManager.isLoaded()) {
            stateManager.push(new GameState(stateManager));
        }
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
    }
}
