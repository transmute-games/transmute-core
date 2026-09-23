package games.example.platformer;

import TransmuteCore.assets.AssetPack;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Verified platformer recipe: Collision + SimulatedInput playthrough.
 * Agents should copy this pattern for action games.
 */
public class PlatformerGame extends TransmuteCore
{
    public static final int SCREEN_W = 320;
    public static final int SCREEN_H = 240;
    public static final int CLEAR = Color.toPixelInt(40, 60, 80, 255);
    public static final int GROUND_Y = 220;

    private Player player;
    private List<Platform> platforms;

    public PlatformerGame(GameConfig config)
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

        player = new Player(10, 50);
        platforms = new ArrayList<>();
        platforms.add(new Platform(0, GROUND_Y, SCREEN_W, 20));
        platforms.add(new Platform(80, 180, 80, 20));
        platforms.add(new Platform(200, 140, 80, 20));
        platforms.add(new Platform(50, 100, 60, 20));
    }

    @Override
    public void update(Manager manager, double delta)
    {
        player.update(manager, delta);
        player.checkCollision(platforms);
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        Context ctx = (Context) renderer;
        ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), CLEAR);
        for (Platform platform : platforms)
        {
            platform.render(ctx);
        }
        player.render(ctx);
        ctx.renderText("SPACE JUMP", 10, 10, Color.toPixelInt(255, 255, 255, 255));
    }

    public Player getPlayer()
    {
        return player;
    }

    public List<Platform> getPlatforms()
    {
        return Collections.unmodifiableList(platforms);
    }

    public static GameConfig headlessConfig()
    {
        return new GameConfig.Builder()
            .title("Platformer Example")
            .version("1.0.0")
            .size(SCREEN_W, SCREEN_H)
            .scale(1)
            .headless(true)
            .showStartScreen(false)
            .build();
    }

    public static void main(String[] args)
    {
        boolean headless = args.length > 0 && "--headless".equals(args[0]);
        GameConfig config = new GameConfig.Builder()
            .title("Platformer Example")
            .version("1.0.0")
            .size(SCREEN_W, SCREEN_H)
            .scale(2)
            .headless(headless)
            .showStartScreen(false)
            .build();

        if (headless)
        {
            try (GameHarness harness = GameHarness.of(() -> new PlatformerGame(config)))
            {
                harness.step(90);
                PlatformerGame game = (PlatformerGame) harness.game();
                FrameAssert.assertPixel(harness.renderer(), 0, 0, CLEAR);
                System.out.println("platformer headless ok onGround="
                    + game.getPlayer().isOnGround()
                    + " y=" + game.getPlayer().getY()
                    + " hash=0x" + Integer.toHexString(FrameAssert.hash(harness.renderer())));
            }
            return;
        }

        new PlatformerGame(config).start();
    }
}
