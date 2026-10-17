package games.example.platformer;

import TransmuteCore.core.Manager;
import TransmuteCore.core.interfaces.services.IInputHandler;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.physics.Body2D;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/** Player driven by {@link Body2D}. */
public final class Player
{
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int COLOR = Color.toPixelInt(100, 200, 255, 255);

    private final Body2D body;
    private final float moveSpeed = 3f;

    public Player(int x, int y)
    {
        this.body = new Body2D(x, y, WIDTH, HEIGHT);
    }

    public void update(Manager manager, List<Platform> platforms)
    {
        IInputHandler input = manager.getInputHandler();
        float vx = 0;
        if (input != null)
        {
            if (input.isKeyHeld(KeyEvent.VK_LEFT, KeyEvent.VK_A))
            {
                vx = -moveSpeed;
            }
            if (input.isKeyHeld(KeyEvent.VK_RIGHT, KeyEvent.VK_D))
            {
                vx = moveSpeed;
            }
            if (input.isKeyPressed(KeyEvent.VK_SPACE))
            {
                body.jump();
            }
        }
        body.setVelocityX(vx);
        body.step(new ArrayList<>(platforms));
    }

    public void render(Context ctx)
    {
        ctx.renderFilledRectangle((int) body.getX(), (int) body.getY(), WIDTH, HEIGHT, COLOR);
    }

    public int getX()
    {
        return (int) body.getX();
    }

    public int getY()
    {
        return (int) body.getY();
    }

    public boolean isOnGround()
    {
        return body.isOnGround();
    }

    public float getVelocityY()
    {
        return body.getVelocityY();
    }
}
