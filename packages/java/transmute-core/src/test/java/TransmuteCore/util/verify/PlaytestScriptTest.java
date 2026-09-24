package TransmuteCore.util.verify;

import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.input.SimulatedInput;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlaytestScriptTest
{
    @Test
    public void resolvesKeysAndPlaysHold()
    {
        assertEquals(KeyEvent.VK_SPACE, PlaytestScript.resolveKey("SPACE"));
        assertEquals(KeyEvent.VK_D, PlaytestScript.resolveKey("D"));
        assertEquals(KeyEvent.VK_RIGHT, PlaytestScript.resolveKey("RIGHT"));

        GameConfig config = new GameConfig.Builder()
            .title("Script")
            .version("0")
            .size(32, 32)
            .scale(1)
            .headless(true)
            .showStartScreen(false)
            .build();

        try (GameHarness harness = GameHarness.of(() -> new Probe(config)))
        {
            harness.step(1);
            Probe game = (Probe) harness.game();
            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();
            PlaytestScript script = PlaytestScript.loadClasspath("playtests/hold-right.script");
            int frames = script.play(harness, input);
            assertTrue(frames >= 5);
            assertTrue(game.rightFrames > 0);
        }
    }

    private static final class Probe extends TransmuteCore
    {
        int rightFrames;

        Probe(GameConfig config)
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
            var input = manager.getInputHandler();
            if (input != null && input.isKeyHeld(KeyEvent.VK_RIGHT))
            {
                rightFrames++;
            }
        }

        @Override
        public void render(Manager manager, IRenderer renderer)
        {
            Context ctx = (Context) renderer;
            ctx.renderFilledRectangle(0, 0, 1, 1, Color.toPixelInt(0, 0, 0, 255));
        }
    }
}
