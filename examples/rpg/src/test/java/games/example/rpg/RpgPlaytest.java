package games.example.rpg;

import TransmuteCore.core.GameSpec;
import TransmuteCore.input.SimulatedInput;
import TransmuteCore.util.verify.FrameAssert;
import TransmuteCore.util.verify.GameHarness;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RpgPlaytest
{
    @Test
    public void movesRightUntilBlockedByWall()
    {
        try (GameHarness harness = GameHarness.of(() -> {
            GameSpec spec = RpgGame.loadSpec();
            return new RpgGame(RpgGame.headlessConfig(spec), spec);
        }))
        {
            harness.step(1);
            RpgGame game = (RpgGame) harness.game();
            int startX = game.getPlayer().getX();
            int startY = game.getPlayer().getY();

            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();
            input.holdKey(KeyEvent.VK_RIGHT);
            harness.step(20);

            assertTrue(game.getPlayer().getX() > startX);
            assertEquals(startY, game.getPlayer().getY());
            FrameAssert.assertPixel(
                harness.renderer(),
                game.getPlayer().getX() + Player.SIZE / 2,
                game.getPlayer().getY() + Player.SIZE / 2,
                Player.COLOR);

            harness.step(200);
            int xAtWall = game.getPlayer().getX();
            harness.step(10);
            assertEquals("should stop against solid border", xAtWall, game.getPlayer().getX());
            assertFalse(game.getWorld().blocks(
                game.getPlayer().getX(), game.getPlayer().getY(), Player.SIZE, Player.SIZE));
        }
    }

    @Test
    public void cannotEnterInteriorSolidTiles()
    {
        try (GameHarness harness = GameHarness.of(() -> {
            GameSpec spec = RpgGame.loadSpec();
            return new RpgGame(RpgGame.headlessConfig(spec), spec);
        }))
        {
            harness.step(1);
            RpgGame game = (RpgGame) harness.game();
            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();

            input.holdKey(KeyEvent.VK_DOWN);
            harness.step(50);
            input.releaseKey(KeyEvent.VK_DOWN);
            input.holdKey(KeyEvent.VK_RIGHT);
            harness.step(100);

            assertFalse(
                "player AABB must never overlap solids",
                game.getWorld().blocks(
                    game.getPlayer().getX(),
                    game.getPlayer().getY(),
                    Player.SIZE,
                    Player.SIZE));
        }
    }

    @Test
    public void collectingTriggerFiresWhenWalkingIntoPickup()
    {
        try (GameHarness harness = GameHarness.of(() -> {
            GameSpec spec = RpgGame.loadSpec();
            return new RpgGame(RpgGame.headlessConfig(spec), spec);
        }))
        {
            harness.step(1);
            RpgGame game = (RpgGame) harness.game();
            assertEquals(0, game.getCollected());
            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();
            input.holdKey(KeyEvent.VK_RIGHT);
            harness.step(20);
            assertTrue(game.getCollected() >= 1);
        }
    }
}
