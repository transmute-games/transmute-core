package games.eval.collect;

import TransmuteCore.assets.types.AudioPlayer;
import TransmuteCore.input.SimulatedInput;
import TransmuteCore.util.verify.AudioProbe;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import TransmuteCore.util.verify.PlaytestScript;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Pass criteria for the collect-coin agent eval fixture.
 */
public class CollectEvalTest
{
    @After
    public void tearDown()
    {
        AudioPlayer.setMuted(false);
    }

    @Test
    public void scriptWalksOntoCoinAndPlaysPickup()
    {
        var spec = CollectGame.loadSpec();
        try (GameHarness harness = GameHarness.of(() -> new CollectGame(CollectGame.headlessConfig(spec), spec));
             AudioProbe probe = AudioProbe.install())
        {
            AudioPlayer.setMuted(true);
            harness.step(1);
            CollectGame game = (CollectGame) harness.game();
            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();

            PlaytestScript.loadClasspath("playtests/collect.script").play(harness, input);

            assertEquals(1, game.getCollected());
            assertTrue(game.getPlayer().getX() >= CollectGame.TILE * 5);
            probe.assertPlayed("pickup");
            // Interior clear (border tiles are solid-colored at the edges)
            FrameAssert.assertPixel(harness.renderer(), CollectGame.TILE + 1, CollectGame.TILE + 1, CollectGame.CLEAR);
        }
    }
}
