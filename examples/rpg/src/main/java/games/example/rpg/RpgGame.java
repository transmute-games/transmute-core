package games.example.rpg;

import TransmuteCore.assets.AssetPack;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.GameSpec;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Camera;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import TransmuteCore.world.Trigger;
import TransmuteCore.world.World;

/**
 * Verified top-down RPG: GameSpec → World + Camera + Trigger pickups.
 */
public class RpgGame extends TransmuteCore
{
    public static final int CLEAR = Color.toPixelInt(20, 20, 30, 255);
    public static final int TILE = 16;
    public static final int VIEW_W = 320;
    public static final int VIEW_H = 240;

    private final GameSpec spec;
    private World world;
    private Player player;
    private Camera camera;
    private int collected;

    public RpgGame(GameConfig config, GameSpec spec)
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

        world = spec.createWorld();
        if (world == null)
        {
            throw new IllegalStateException("gamespec.properties must define world.cols/world.rows");
        }
        world.solidColor(Color.toPixelInt(60, 60, 80, 255));

        player = new Player(TILE * 2, TILE * 2);
        world.add(player);
        camera = new Camera(VIEW_W, VIEW_H);

        world.addTrigger(new Trigger(
            TILE * 4, TILE * 2, TILE, TILE,
            actor -> collected++));
    }

    public int getCollected()
    {
        return collected;
    }

    @Override
    public void update(Manager manager, double delta)
    {
        world.update(manager, delta);
        camera.lookAt(player.getX() + player.getWidth() / 2f, player.getY() + player.getHeight() / 2f);
        camera.clampToWorld(world.pixelWidth(), world.pixelHeight());
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        world.render(manager, renderer, camera);
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

    public Camera getCamera()
    {
        return camera;
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
            .size(VIEW_W, VIEW_H)
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
            .size(VIEW_W, VIEW_H)
            .scale(2)
            .headless(headless)
            .showStartScreen(false)
            .build();

        if (headless)
        {
            try (GameHarness harness = GameHarness.of(() -> new RpgGame(config, spec)))
            {
                harness.step(1);
                // Player near origin; camera clamped so screen coords ≈ world coords
                FrameAssert.assertPixel(harness.renderer(), TILE * 2 + 8, TILE * 2 + 8, Player.COLOR);
                System.out.println("rpg headless ok hash=0x"
                    + Integer.toHexString(FrameAssert.hash(harness.renderer())));
            }
            return;
        }

        new RpgGame(config, spec).start();
    }
}
