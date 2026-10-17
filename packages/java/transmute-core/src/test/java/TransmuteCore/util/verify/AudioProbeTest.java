package TransmuteCore.util.verify;

import TransmuteCore.assets.types.AudioPlayer;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AudioProbeTest
{
    @After
    public void tearDown()
    {
        AudioPlayer.setMuted(false);
        AudioProbe active = AudioProbe.current();
        if (active != null)
        {
            active.close();
        }
    }

    @Test
    public void recordsPlayWhileMutedWithoutDevice()
    {
        AudioPlayer.setMuted(true);
        try (AudioProbe probe = AudioProbe.install())
        {
            AudioPlayer.play("jump");
            AudioPlayer.play("jump");
            AudioPlayer.loop("bgm");
            AudioPlayer.stop("bgm");

            probe.assertPlayed("jump");
            probe.assertPlayCount("jump", 2);
            probe.assertLooped("bgm");
            probe.assertStopped("bgm");
            assertEquals(4, probe.events().size());
        }
    }

    @Test
    public void assertNothingPlayedWhenIdle()
    {
        try (AudioProbe probe = AudioProbe.install())
        {
            probe.assertNothingPlayed();
        }
    }

    @Test(expected = AssertionError.class)
    public void assertPlayedFailsWhenMissing()
    {
        try (AudioProbe probe = AudioProbe.install())
        {
            probe.assertPlayed("missing");
        }
    }

    @Test
    public void uninstallsOnClose()
    {
        AudioProbe probe = AudioProbe.install();
        assertTrue(AudioProbe.current() == probe);
        probe.close();
        assertEquals(null, AudioProbe.current());
        AudioPlayer.setMuted(true);
        AudioPlayer.play("ignored");
        // no probe — must not throw
    }
}
