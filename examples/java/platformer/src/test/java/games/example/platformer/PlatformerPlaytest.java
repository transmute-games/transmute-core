package games.example.platformer;

import TransmuteCore.input.SimulatedInput;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import TransmuteCore.util.verify.PlaytestScript;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Agent-style playtest: fall, then jump via PlaytestScript + pixel asserts.
 */
public class PlatformerPlaytest
{
    @Test
    public void scriptedJumpLeavesGround()
    {
        try (GameHarness harness = GameHarness.of(() -> new PlatformerGame(PlatformerGame.headlessConfig())))
        {
            harness.step(1);
            PlatformerGame game = (PlatformerGame) harness.game();
            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();

            int guard = 0;
            while (!game.getPlayer().isOnGround() && guard++ < 200)
            {
                harness.step(1);
            }
            assertTrue(game.getPlayer().isOnGround());
            int groundedY = game.getPlayer().getY();

            PlaytestScript.loadClasspath("playtests/jump.script").play(harness, input);

            assertTrue("player should be above grounded Y after jump script",
                game.getPlayer().getY() < groundedY);
            FrameAssert.assertPixel(harness.renderer(), 0, 0, PlatformerGame.CLEAR);
        }
    }

    @Test
    public void landsBeforeJumpWindow()
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
            assertTrue(game.getPlayer().isOnGround());
            assertEquals(PlatformerGame.GROUND_Y - Player.HEIGHT, game.getPlayer().getY());
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
