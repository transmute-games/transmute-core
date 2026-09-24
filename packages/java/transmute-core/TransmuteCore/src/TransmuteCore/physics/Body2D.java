package TransmuteCore.physics;

import TransmuteCore.math.Collision;

import java.util.List;

/**
 * Deep 2D rigid body for platformers: gravity, jump, AABB resolve against solids.
 */
public final class Body2D
{
    public interface Solid
    {
        float getX();

        float getY();

        float getWidth();

        float getHeight();
    }

    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private final float width;
    private final float height;
    private boolean onGround;
    private float gravity = 0.5f;
    private float jumpStrength = -10f;

    public Body2D(float x, float y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Body2D gravity(float gravity)
    {
        this.gravity = gravity;
        return this;
    }

    public Body2D jumpStrength(float jumpStrength)
    {
        this.jumpStrength = jumpStrength;
        return this;
    }

    public void setVelocityX(float velocityX)
    {
        this.velocityX = velocityX;
    }

    public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void jump()
    {
        if (onGround)
        {
            velocityY = jumpStrength;
            onGround = false;
        }
    }

    /**
     * Integrates velocity and resolves against {@code solids}.
     */
    public void step(List<? extends Solid> solids)
    {
        if (!onGround)
        {
            velocityY += gravity;
        }
        x += velocityX;
        y += velocityY;
        resolve(solids);
    }

    private void resolve(List<? extends Solid> solids)
    {
        onGround = false;
        for (Solid solid : solids)
        {
            Collision.Resolution hit = Collision.resolveAabb(
                x, y, width, height,
                solid.getX(), solid.getY(), solid.getWidth(), solid.getHeight());
            if (hit.collided)
            {
                x = hit.x;
                y = hit.y;
                if (hit.landed())
                {
                    velocityY = 0;
                    onGround = true;
                }
                else if (hit.hitBottom)
                {
                    velocityY = 0;
                }
                else if (hit.hitHorizontal)
                {
                    velocityX = 0;
                }
            }
        }
        if (!onGround)
        {
            for (Solid solid : solids)
            {
                if (Collision.aabb(
                    x, y + 1, width, height,
                    solid.getX(), solid.getY(), solid.getWidth(), solid.getHeight()))
                {
                    y = solid.getY() - height;
                    velocityY = 0;
                    onGround = true;
                    break;
                }
            }
        }
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public float getVelocityY()
    {
        return velocityY;
    }

    public boolean isOnGround()
    {
        return onGround;
    }
}
