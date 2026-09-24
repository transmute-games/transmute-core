package games.eval.collect;

import TransmuteCore.assets.types.AudioPlayer;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.GameSpec;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import TransmuteCore.world.World;

/**
 * Golden solution for the collect-coin agent eval fixture.
 * Uses GameSpec {@code spawn.*} / {@code trigger.*} keys.
 */
public class CollectGame extends TransmuteCore
{
    public static final int TILE = 16;
    public static final int CLEAR = Color.toPixelInt(20, 20, 30, 255);
    public static final int PLAYER_COLOR = Color.toPixelInt(100, 200, 255, 255);

    private final GameSpec spec;
    private World world;
    private Player player;
    private int collected;

    public CollectGame(GameConfig config, GameSpec spec)
    {
        super(config);
        this.spec = spec;
    }

    @Override
    public void init()
    {
        getManager().bootstrapDefaults();
        spec.loadAssets(getManager().getAssetManager());
        AudioPlayer.setMuted(getConfig().isHeadless());

        world = spec.createWorld();
        if (world == null)
        {
            throw new IllegalStateException("gamespec must define world.cols/world.rows");
        }
        world.solidColor(Color.toPixelInt(60, 60, 80, 255));

        world.removeActor("marker");
        player = new Player(TILE * 2, TILE * 2);
        world.add(player);

        var coin = world.findTrigger("coin");
        if (coin == null)
        {
            throw new IllegalStateException("gamespec must define trigger.coin");
        }
        coin.setOnEnter(actor -> collected++);

        if (!"play".equals(spec.getInitialState()))
        {
            throw new IllegalStateException("expected state.initial=play");
        }
    }

    @Override
    public void update(Manager manager, double delta)
    {
        world.update(manager, delta);
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        world.render(manager, renderer);
        Context ctx = (Context) renderer;
        ctx.renderText("COLLECT=" + collected, 10, 10, Color.toPixelInt(255, 255, 255, 255));
    }

    public int getCollected()
    {
        return collected;
    }

    public Player getPlayer()
    {
        return player;
    }

    public static GameSpec loadSpec()
    {
        return GameSpec.loadClasspath("gamespec.properties");
    }

    public static GameConfig headlessConfig(GameSpec spec)
    {
        return new GameConfig.Builder()
            .title(spec.getTitle())
            .version("1.0.0")
            .size(320, 240)
            .scale(1)
            .headless(true)
            .showStartScreen(false)
            .build();
    }

    public static void main(String[] args)
    {
        GameSpec spec = loadSpec();
        boolean headless = args.length > 0 && "--headless".equals(args[0]);
        GameConfig config = new GameConfig.Builder()
            .title(spec.getTitle())
            .version("1.0.0")
            .size(320, 240)
            .scale(headless ? 1 : 2)
            .headless(headless)
            .showStartScreen(false)
            .build();

        if (headless)
        {
            try (GameHarness harness = GameHarness.of(() -> new CollectGame(config, spec)))
            {
                harness.step(1);
                FrameAssert.assertPixel(harness.renderer(), TILE + 1, TILE + 1, CLEAR);
                System.out.println("collect headless ok collected="
                    + ((CollectGame) harness.game()).getCollected()
                    + " hash=0x" + Integer.toHexString(FrameAssert.hash(harness.renderer())));
            }
            return;
        }

        new CollectGame(config, spec).start();
    }
}
