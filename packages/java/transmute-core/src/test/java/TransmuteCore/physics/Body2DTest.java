package TransmuteCore.physics;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Body2DTest
{
    @Test
    public void fallsAndLandsThenJumps()
    {
        Body2D body = new Body2D(10, 50, 16, 16);
        Body2D.Solid ground = solid(0, 220, 320, 20);

        int guard = 0;
        while (!body.isOnGround() && guard++ < 200)
        {
            body.setVelocityX(0);
            body.step(List.of(ground));
        }
        assertTrue(body.isOnGround());
        assertEquals(204f, body.getY(), 0.01f);

        body.jump();
        body.step(List.of(ground));
        assertTrue(!body.isOnGround());
        assertTrue(body.getVelocityY() < 0);
        assertTrue(body.getY() < 204f);
    }

    private static Body2D.Solid solid(float x, float y, float w, float h)
    {
        return new Body2D.Solid()
        {
            @Override
            public float getX()
            {
                return x;
            }

            @Override
            public float getY()
            {
                return y;
            }

            @Override
            public float getWidth()
            {
                return w;
            }

            @Override
            public float getHeight()
            {
                return h;
            }
        };
    }
}
