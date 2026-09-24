package TransmuteCore.input;

import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IInputHandler;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.util.verify.GameHarness;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimulatedInputTest
{
    @Test
    public void harnessReceivesSimulatedKeys()
    {
        GameConfig config = new GameConfig.Builder()
            .title("SimInput")
            .version("0")
            .size(32, 32)
            .scale(1)
            .headless(true)
            .showStartScreen(false)
            .build();

        try (GameHarness harness = GameHarness.of(() -> new KeyProbeGame(config)))
        {
            harness.step(1);
            KeyProbeGame game = (KeyProbeGame) harness.game();
            assertTrue(game.handler instanceof SimulatedInput);
            SimulatedInput sim = (SimulatedInput) game.handler;
            sim.pressKey(KeyEvent.VK_SPACE);
            harness.step(1);
            assertEquals(1, game.spacePresses);
            sim.holdKey(KeyEvent.VK_RIGHT);
            harness.step(1);
            assertTrue(game.rightHeld);
        }
    }

    private static final class KeyProbeGame extends TransmuteCore
    {
        IInputHandler handler;
        int spacePresses;
        boolean rightHeld;

        KeyProbeGame(GameConfig config)
        {
            super(config);
        }

        @Override
        public void init()
        {
            handler = getManager().getInputHandler();
        }

        @Override
        public void update(Manager manager, double delta)
        {
            IInputHandler input = manager.getInputHandler();
            if (input != null && input.isKeyPressed(KeyEvent.VK_SPACE))
            {
                spacePresses++;
            }
            rightHeld = input != null && input.isKeyHeld(KeyEvent.VK_RIGHT);
        }

        @Override
        public void render(Manager manager, IRenderer renderer)
        {
            Context ctx = (Context) renderer;
            ctx.renderFilledRectangle(0, 0, 1, 1, Color.toPixelInt(0, 0, 0, 255));
        }
    }
}
