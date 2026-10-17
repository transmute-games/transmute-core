package TransmuteCore.world;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class TriggerTest
{
    @Test
    public void firesOnceOnEnter()
    {
        World world = World.grid(8, 8, 16);
        AtomicInteger enters = new AtomicInteger();
        world.addTrigger(new Trigger(32, 32, 16, 16, a -> enters.incrementAndGet()));

        World.Actor actor = World.Actor.colored(16, 32, 16, 16, 0xFFFFFFFF);
        world.add(actor);
        world.update(null, 1);
        assertEquals(0, enters.get());

        actor.tryMove(16, 0);
        world.update(null, 1);
        assertEquals(1, enters.get());

        world.update(null, 1);
        assertEquals(1, enters.get()); // still inside — no re-fire
    }
}
