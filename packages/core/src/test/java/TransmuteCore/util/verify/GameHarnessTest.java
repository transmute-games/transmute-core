package TransmuteCore.util.verify;

import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameHarnessTest
{
    private static final int RED = Color.toPixelInt(255, 0, 0, 255);
    private static final int BG = Color.toPixelInt(10, 20, 30, 255);

    @Test
    public void stepsHeadlessAndAssertsPixels()
    {
        GameConfig config = new GameConfig.Builder()
            .title("HarnessTest")
            .version("0")
            .dimensions(64, GameConfig.ASPECT_RATIO_SQUARE)
            .scale(1)
            .headless(true)
            .showStartScreen(false)
            .build();

        try (GameHarness harness = GameHarness.of(() -> new ProbeGame(config)))
        {
            harness.step(1);
            FrameAssert.assertPixel(harness.renderer(), 0, 0, BG);
            FrameAssert.assertPixel(harness.renderer(), 8, 8, RED);

            int hash = FrameAssert.hash(harness.renderer());
            harness.step(3);
            FrameAssert.assertHash(harness.renderer(), hash);
            assertEquals(4, ((ProbeGame) harness.game()).updates);
            assertTrue(harness.renderer().getWidth() > 0);
        }
    }

    private static final class ProbeGame extends TransmuteCore
    {
        int updates;

        ProbeGame(GameConfig config)
        {
            super(config);
        }

        @Override
        public void init()
        {
        }

        @Override
        public void update(Manager manager, double delta)
        {
            updates++;
        }

        @Override
        public void render(Manager manager, IRenderer renderer)
        {
            Context ctx = (Context) renderer;
            ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), BG);
            ctx.renderFilledRectangle(5, 5, 10, 10, RED);
        }
    }
}
