package games.eval.menu;

import TransmuteCore.input.SimulatedInput;
import TransmuteCore.util.verify.GameHarness;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;

public class MenuEvalTest
{
    @Test
    public void enterFromMenuPushesPlay()
    {
        var spec = MenuGame.loadSpec();
        assertEquals("menu", spec.getInitialState());

        try (GameHarness harness = GameHarness.of(() -> new MenuGame(MenuGame.headlessConfig(spec), spec)))
        {
            harness.step(1);
            MenuGame game = (MenuGame) harness.game();
            assertEquals("menu", game.states().peek().getName());

            SimulatedInput input = (SimulatedInput) game.getManager().getInputHandler();
            input.pressKey(KeyEvent.VK_ENTER);
            harness.step(1);

            assertEquals("play", game.states().peek().getName());
        }
    }
}
