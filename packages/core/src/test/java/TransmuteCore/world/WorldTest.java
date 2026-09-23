package TransmuteCore.world;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorldTest
{
    @Test
    public void borderAndBlocksWork()
    {
        World world = World.grid(10, 8, 16);
        world.fillBorder(World.SOLID);
        world.setTile(5, 4, World.SOLID);

        assertTrue(world.isSolid(0, 0));
        assertFalse(world.isSolid(1, 1));
        assertTrue(world.blocks(0, 0, 16, 16));
        assertFalse(world.blocks(16, 16, 16, 16));
        assertTrue(world.blocks(5 * 16, 4 * 16, 8, 8));
    }

    @Test
    public void actorTryMoveRespectsSolids()
    {
        World world = World.grid(8, 8, 16);
        world.fillBorder(World.SOLID);
        World.Actor actor = World.Actor.colored(16, 16, 16, 16, 0xFFFFFFFF);
        world.add(actor);

        assertTrue(actor.tryMove(16, 0));
        assertEquals(32, actor.getX());
        assertFalse(actor.tryMove(-32, 0)); // into left wall
        assertEquals(32, actor.getX());
    }
}
