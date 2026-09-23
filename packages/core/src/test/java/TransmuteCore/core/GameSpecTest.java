package TransmuteCore.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void createWorldFromManifest()
    {
        GameSpec spec = GameSpec.loadClasspath("gamespec-world-fixture.properties");
        var world = spec.createWorld();
        assertEquals(8, world.getCols());
        assertEquals(6, world.getRows());
        assertTrue(world.isSolid(0, 0));
        assertTrue(world.isSolid(3, 2));
    }
}
