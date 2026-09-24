package games.eval.jump;

import TransmuteCore.assets.AssetPack;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.physics.Body2D;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class JumpGame extends TransmuteCore
{
    public static final int CLEAR = Color.toPixelInt(40, 60, 80, 255);
    public static final int GROUND_Y = 220;

    private Body2D body;
    private List<Platform> platforms;

    public JumpGame(GameConfig config)
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
        body = new Body2D(40, 50, 16, 16);
        platforms = new ArrayList<>();
        platforms.add(new Platform(0, GROUND_Y, 320, 20));
    }

    @Override
    public void update(Manager manager, double delta)
    {
        var input = manager.getInputHandler();
        if (input != null && input.isKeyPressed(KeyEvent.VK_SPACE))
        {
            body.jump();
        }
        body.setVelocityX(0);
        body.step(new ArrayList<>(platforms));
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        Context ctx = (Context) renderer;
        ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), CLEAR);
        for (Platform p : platforms)
        {
            p.render(ctx);
        }
        ctx.renderFilledRectangle((int) body.getX(), (int) body.getY(), 16, 16,
            Color.toPixelInt(100, 200, 255, 255));
    }

    public Body2D getBody()
    {
        return body;
    }

    public static GameConfig headlessConfig()
    {
        return new GameConfig.Builder()
            .title("Jump Eval").version("1.0.0").size(320, 240).scale(1)
            .headless(true).showStartScreen(false).build();
    }

    public static void main(String[] args)
    {
        boolean headless = args.length > 0 && "--headless".equals(args[0]);
        GameConfig config = new GameConfig.Builder()
            .title("Jump Eval").version("1.0.0").size(320, 240).scale(headless ? 1 : 2)
            .headless(headless).showStartScreen(false).build();
        if (headless)
        {
            try (GameHarness harness = GameHarness.of(() -> new JumpGame(config)))
            {
                harness.step(90);
                FrameAssert.assertPixel(harness.renderer(), 0, 0, CLEAR);
                System.out.println("jump headless ok onGround="
                    + ((JumpGame) harness.game()).getBody().isOnGround());
            }
            return;
        }
        new JumpGame(config).start();
    }

    static final class Platform implements Body2D.Solid
    {
        private final float x, y, w, h;

        Platform(float x, float y, float w, float h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        void render(Context ctx)
        {
            ctx.renderFilledRectangle((int) x, (int) y, (int) w, (int) h,
                Color.toPixelInt(100, 100, 100, 255));
        }

        public float getX() { return x; }
        public float getY() { return y; }
        public float getWidth() { return w; }
        public float getHeight() { return h; }
    }
}
