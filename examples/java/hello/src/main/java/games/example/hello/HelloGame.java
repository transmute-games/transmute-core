package games.example.hello;

import TransmuteCore.assets.AssetPack;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.GameSpec;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;

/**
 * Minimal reference game: GameSpec + AssetPack + Manager bootstrap.
 * Agents should copy this pattern.
 */
public class HelloGame extends TransmuteCore
{
    private final int clearColor;

    public HelloGame(GameConfig config, int clearColor)
    {
        super(config);
        this.clearColor = clearColor;
    }

    @Override
    public void init()
    {
        Manager manager = getManager();
        manager.bootstrapDefaults();
        AssetPack.create(manager.getAssetManager())
            .font(AssetPack.DEFAULT_FONT_RESOURCE)
            .ensureDefaultFont();
    }

    @Override
    public void update(Manager manager, double delta)
    {
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        Context ctx = (Context) renderer;
        ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), clearColor);
        ctx.renderFilledRectangle(20, 20, 40, 40, Color.toPixelInt(220, 180, 60, 255));
        ctx.renderText("HELLO", 20, 70, Color.toPixelInt(255, 255, 255, 255));
    }

    public static void main(String[] args)
    {
        GameSpec spec = GameSpec.loadClasspath("gamespec.properties");
        boolean headless = args.length > 0 && "--headless".equals(args[0]);

        GameConfig config = new GameConfig.Builder()
            .title(spec.getTitle())
            .version("1.0.0")
            .size(320, 180)
            .scale(2)
            .headless(headless)
            .showStartScreen(false)
            .build();

        if (headless)
        {
            try (GameHarness harness = GameHarness.of(() -> new HelloGame(config, spec.getClearColor())))
            {
                harness.step(1);
                FrameAssert.assertPixel(harness.renderer(), 0, 0, spec.getClearColor());
                System.out.println("hello headless ok hash=0x"
                    + Integer.toHexString(FrameAssert.hash(harness.renderer())));
            }
            return;
        }

        new HelloGame(config, spec.getClearColor()).start();
    }
}
