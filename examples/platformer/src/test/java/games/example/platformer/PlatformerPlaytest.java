package games.example.platformer;

import TransmuteCore.input.SimulatedInput;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Agent-style playtest: fall onto ground, jump with SimulatedInput, assert motion + pixels.
 */
public class PlatformerPlaytest
{
    @Test
    public void fallsOntoGroundThenJumpsOnSpace()
    {
        try (GameHarness harness = GameHarness.of(() -> new PlatformerGame(PlatformerGame.headlessConfig())))
        {
            harness.step(1); // init + first frame
            PlatformerGame game = (PlatformerGame) harness.game();

            // Settle onto the ground platform
            int guard = 0;
            while (!game.getPlayer().isOnGround() && guard++ < 200)
            {
                harness.step(1);
            }
            assertTrue("player should land within 200 frames", game.getPlayer().isOnGround());
            assertEquals(PlatformerGame.GROUND_Y - Player.HEIGHT, game.getPlayer().getY());

            int groundedY = game.getPlayer().getY();
            FrameAssert.assertPixel(
                harness.renderer(),
                game.getPlayer().getX() + Player.WIDTH / 2,
                groundedY + Player.HEIGHT / 2,
                Player.COLOR);

            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();
            input.pressKey(KeyEvent.VK_SPACE);
            harness.step(1);
            assertTrue("jump should leave the ground", !game.getPlayer().isOnGround());
            assertTrue("velocity should be upward after jump", game.getPlayer().getVelocityY() < 0);

            harness.step(3);
            assertTrue(
                "player should be above grounded Y after jump frames",
                game.getPlayer().getY() < groundedY);

            // Hold right and move onto the ground again eventually
            input.holdKey(KeyEvent.VK_RIGHT);
            harness.step(5);
            assertTrue(game.getPlayer().getX() > 10);
        }
    }

    @Test
    public void clearColorStableWhenIdleOnGround()
    {
        try (GameHarness harness = GameHarness.of(() -> new PlatformerGame(PlatformerGame.headlessConfig())))
        {
            harness.step(1);
            PlatformerGame game = (PlatformerGame) harness.game();
            int guard = 0;
            while (!game.getPlayer().isOnGround() && guard++ < 200)
            {
                harness.step(1);
            }
            FrameAssert.assertPixel(harness.renderer(), 0, 0, PlatformerGame.CLEAR);
            int hash = FrameAssert.hash(harness.renderer());
            harness.step(5);
            FrameAssert.assertHash(harness.renderer(), hash);
        }
    }
}
