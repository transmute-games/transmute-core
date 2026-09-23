package games.example.platformer;

import TransmuteCore.core.Manager;
import TransmuteCore.core.interfaces.services.IInputHandler;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.math.Collision;

import java.awt.event.KeyEvent;
import java.util.List;

/** Player with gravity, jump, and Collision.resolveAabb against platforms. */
public final class Player
{
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int COLOR = Color.toPixelInt(100, 200, 255, 255);

    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private boolean onGround;
    private final float gravity = 0.5f;
    private final float jumpStrength = -10f;
    private final float moveSpeed = 3f;

    public Player(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void update(Manager manager, double delta)
    {
        velocityX = 0;
        IInputHandler input = manager.getInputHandler();
        if (input != null)
        {
            if (input.isKeyHeld(KeyEvent.VK_LEFT, KeyEvent.VK_A))
            {
                velocityX = -moveSpeed;
            }
            if (input.isKeyHeld(KeyEvent.VK_RIGHT, KeyEvent.VK_D))
            {
                velocityX = moveSpeed;
            }
            if (input.isKeyPressed(KeyEvent.VK_SPACE) && onGround)
            {
                velocityY = jumpStrength;
                onGround = false;
            }
        }

        if (!onGround)
        {
            velocityY += gravity;
        }

        x += velocityX;
        y += velocityY;
    }

    public void checkCollision(List<Platform> platforms)
    {
        onGround = false;
        for (Platform platform : platforms)
        {
            Collision.Resolution hit = Collision.resolveAabb(
                x, y, WIDTH, HEIGHT,
                platform.getX(), platform.getY(),
                platform.getWidth(), platform.getHeight());
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

        // Probe one pixel down so flush contact still counts as grounded
        if (!onGround)
        {
            for (Platform platform : platforms)
            {
                if (Collision.aabb(
                    x, y + 1, WIDTH, HEIGHT,
                    platform.getX(), platform.getY(),
                    platform.getWidth(), platform.getHeight()))
                {
                    y = platform.getY() - HEIGHT;
                    velocityY = 0;
                    onGround = true;
                    break;
                }
            }
        }
    }

    public void render(Context ctx)
    {
        ctx.renderFilledRectangle((int) x, (int) y, WIDTH, HEIGHT, COLOR);
    }

    public int getX()
    {
        return (int) x;
    }

    public int getY()
    {
        return (int) y;
    }

    public boolean isOnGround()
    {
        return onGround;
    }

    public float getVelocityY()
    {
        return velocityY;
    }
}
