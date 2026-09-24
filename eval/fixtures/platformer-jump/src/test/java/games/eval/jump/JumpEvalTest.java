package games.eval.jump;

import TransmuteCore.input.SimulatedInput;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import TransmuteCore.util.verify.PlaytestScript;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JumpEvalTest
{
    @Test
    public void scriptedJumpLeavesGround()
    {
        try (GameHarness harness = GameHarness.of(() -> new JumpGame(JumpGame.headlessConfig())))
        {
            harness.step(1);
            JumpGame game = (JumpGame) harness.game();
            int guard = 0;
            while (!game.getBody().isOnGround() && guard++ < 200)
            {
                harness.step(1);
            }
            assertTrue(game.getBody().isOnGround());
            int groundedY = (int) game.getBody().getY();

            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();
            PlaytestScript.loadClasspath("playtests/jump.script").play(harness, input);

            assertTrue(game.getBody().getY() < groundedY);
            FrameAssert.assertPixel(harness.renderer(), 0, 0, JumpGame.CLEAR);
        }
    }
}
