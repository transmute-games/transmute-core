package TransmuteCore.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollisionTest
{
    @Test
    public void aabbDetectsOverlap()
    {
        assertTrue(Collision.aabb(0, 0, 10, 10, 5, 5, 10, 10));
        assertFalse(Collision.aabb(0, 0, 10, 10, 20, 20, 5, 5));
    }

    @Test
    public void resolvePushesMoverOntoFloor()
    {
        // Mover falling into a platform below
        Collision.Resolution r = Collision.resolveAabb(10, 18, 8, 8, 0, 20, 40, 10);
        assertTrue(r.collided);
        assertTrue(r.landed());
        assertEquals(10f, r.x, 0.001f);
        assertEquals(12f, r.y, 0.001f); // 20 - 8
    }
}
