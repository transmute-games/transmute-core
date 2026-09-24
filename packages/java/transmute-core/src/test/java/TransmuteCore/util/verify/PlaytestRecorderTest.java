package TransmuteCore.util.verify;

import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlaytestRecorderTest
{
    @Test
    public void recordsHoldReleaseAsScriptText()
    {
        PlaytestRecorder rec = new PlaytestRecorder();
        rec.hold(KeyEvent.VK_D);
        rec.advance(10);
        rec.release(KeyEvent.VK_D);
        rec.idle();

        String text = rec.toScriptText();
        assertTrue(text.contains("0 hold D"));
        assertTrue(text.contains("10 release D"));
        assertTrue(text.contains("10 idle"));

        PlaytestScript script = rec.toScript();
        assertEquals(3, script.getSteps().size());
        assertEquals(PlaytestScript.Action.HOLD, script.getSteps().get(0).action());
    }
}
