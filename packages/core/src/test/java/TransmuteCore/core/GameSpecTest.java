package TransmuteCore.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GameSpecTest
{
    @Test
    public void parsesClasspathManifest()
    {
        GameSpec spec = GameSpec.loadClasspath("gamespec-fixture.properties");
        assertEquals("Fixture Game", spec.getTitle());
        GameConfig config = spec.toGameConfig();
        assertEquals(160, config.getWidth());
        assertEquals(90, config.getHeight());
        assertEquals(2, config.getScale());
        assertFalse(config.isHeadless());
    }
}
