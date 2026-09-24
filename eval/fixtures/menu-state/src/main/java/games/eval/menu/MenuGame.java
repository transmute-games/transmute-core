package games.eval.menu;

import TransmuteCore.assets.AssetPack;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.GameSpec;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.state.State;
import TransmuteCore.state.StateManager;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;

import java.awt.event.KeyEvent;

public class MenuGame extends TransmuteCore
{
    public static final int CLEAR = Color.toPixelInt(10, 20, 30, 255);
    public static final int PLAY_CLEAR = Color.toPixelInt(20, 40, 60, 255);

    private final GameSpec spec;

    public MenuGame(GameConfig config, GameSpec spec)
    {
        super(config);
        this.spec = spec;
    }

    @Override
    public void init()
    {
        getManager().bootstrapDefaults();
        AssetPack.create(getManager().getAssetManager())
            .font(AssetPack.DEFAULT_FONT_RESOURCE)
            .ensureDefaultFont();

        StateManager states = getManager().getStateManager();
        String initial = spec.getInitialState();
        if ("menu".equals(initial))
        {
            states.push(new MenuState(states));
        }
        else
        {
            states.push(new PlayState(states));
        }
    }

    @Override
    public void update(Manager manager, double delta)
    {
        manager.getStateManager().update(manager, delta);
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        manager.getStateManager().render(manager, renderer);
    }

    public StateManager states()
    {
        return getManager().getStateManager();
    }

    public static GameSpec loadSpec()
    {
        return GameSpec.loadClasspath("gamespec.properties");
    }

    public static GameConfig headlessConfig(GameSpec spec)
    {
        return new GameConfig.Builder()
            .title(spec.getTitle()).version("1.0.0").size(320, 180).scale(1)
            .headless(true).showStartScreen(false).build();
    }

    public static void main(String[] args)
    {
        GameSpec spec = loadSpec();
        boolean headless = args.length > 0 && "--headless".equals(args[0]);
        GameConfig config = new GameConfig.Builder()
            .title(spec.getTitle()).version("1.0.0").size(320, 180)
            .scale(headless ? 1 : 2).headless(headless).showStartScreen(false).build();
        if (headless)
        {
            try (GameHarness harness = GameHarness.of(() -> new MenuGame(config, spec)))
            {
                harness.step(1);
                FrameAssert.assertPixel(harness.renderer(), 0, 0, CLEAR);
                System.out.println("menu headless ok state="
                    + ((MenuGame) harness.game()).states().peek().getName());
            }
            return;
        }
        new MenuGame(config, spec).start();
    }

    static final class MenuState extends State
    {
        MenuState(StateManager sm)
        {
            super("menu", sm);
        }

        @Override
        public void init()
        {
        }

        @Override
        public void update(Manager manager, double delta)
        {
            var input = manager.getInputHandler();
            if (input != null && input.isKeyPressed(KeyEvent.VK_ENTER))
            {
                stateManager.push(new PlayState(stateManager));
            }
        }

        @Override
        public void render(Manager manager, IRenderer renderer)
        {
            Context ctx = (Context) renderer;
            ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), CLEAR);
            ctx.renderText("ENTER PLAY", 80, 80, Color.toPixelInt(255, 255, 255, 255));
        }
    }

    static final class PlayState extends State
    {
        PlayState(StateManager sm)
        {
            super("play", sm);
        }

        @Override
        public void init()
        {
        }

        @Override
        public void update(Manager manager, double delta)
        {
        }

        @Override
        public void render(Manager manager, IRenderer renderer)
        {
            Context ctx = (Context) renderer;
            ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), PLAY_CLEAR);
            ctx.renderText("PLAYING", 100, 80, Color.toPixelInt(255, 255, 0, 255));
        }
    }
}
