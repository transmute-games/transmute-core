package games.example.rpg;

import TransmuteCore.assets.AssetPack;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import TransmuteCore.world.World;

/**
 * Verified top-down RPG recipe built on {@link World}.
 */
public class RpgGame extends TransmuteCore
{
    public static final int CLEAR = Color.toPixelInt(20, 20, 30, 255);
    public static final int TILE = 16;

    private World world;
    private Player player;

    public RpgGame(GameConfig config)
    {
        super(config);
    }

    @Override
    public void init()
    {
        getManager().bootstrapDefaults();
        AssetPack.create(getManager().getAssetManager())
            .font(AssetPack.DEFAULT_FONT_RESOURCE)
            .ensureDefaultFont();

        world = World.grid(20, 15, TILE)
            .clearColor(CLEAR)
            .solidColor(Color.toPixelInt(60, 60, 80, 255));
        world.fillBorder(World.SOLID);
        for (int x = 5; x < 10; x++)
        {
            world.setTile(x, 7, World.SOLID);
        }
        for (int y = 3; y < 8; y++)
        {
            world.setTile(15, y, World.SOLID);
        }

        player = new Player(TILE * 2, TILE * 2);
        world.add(player);
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
        ctx.renderText("WASD MOVE", 10, 10, Color.toPixelInt(255, 255, 255, 255));
    }

    public Player getPlayer()
    {
        return player;
    }

    public World getWorld()
    {
        return world;
    }

    public static GameConfig headlessConfig()
    {
        return new GameConfig.Builder()
            .title("RPG Example")
            .version("1.0.0")
            .size(20 * TILE, 15 * TILE)
            .scale(1)
            .headless(true)
            .showStartScreen(false)
            .build();
    }

    public static void main(String[] args)
    {
        boolean headless = args.length > 0 && "--headless".equals(args[0]);
        GameConfig config = new GameConfig.Builder()
            .title("RPG Example")
            .version("1.0.0")
            .size(20 * TILE, 15 * TILE)
            .scale(2)
            .headless(headless)
            .showStartScreen(false)
            .build();

        if (headless)
        {
            try (GameHarness harness = GameHarness.of(() -> new RpgGame(config)))
            {
                harness.step(1);
                FrameAssert.assertPixel(harness.renderer(), TILE * 2 + 8, TILE * 2 + 8, Player.COLOR);
                System.out.println("rpg headless ok hash=0x"
                    + Integer.toHexString(FrameAssert.hash(harness.renderer())));
            }
            return;
        }

        new RpgGame(config).start();
    }
}
