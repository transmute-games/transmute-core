package games.example.rpg;

import TransmuteCore.core.Manager;
import TransmuteCore.world.World;

import java.awt.event.KeyEvent;

/** Player actor that moves with WASD/arrows against World solids. */
public final class Player extends World.Actor
{
    public static final int SIZE = 16;
    public static final int COLOR = 0xFF6496FF;
    private final int moveSpeed = 2;

    public Player(int x, int y)
    {
        super(x, y, SIZE, SIZE, COLOR);
    }

    @Override
    public void update(Manager manager, double delta)
    {
        var input = manager.getInputHandler();
        if (input == null)
        {
            return;
        }
        int dx = 0;
        int dy = 0;
        if (input.isKeyHeld(KeyEvent.VK_A, KeyEvent.VK_LEFT))
        {
            dx -= moveSpeed;
        }
        if (input.isKeyHeld(KeyEvent.VK_D, KeyEvent.VK_RIGHT))
        {
            dx += moveSpeed;
        }
        if (input.isKeyHeld(KeyEvent.VK_W, KeyEvent.VK_UP))
        {
            dy -= moveSpeed;
        }
        if (input.isKeyHeld(KeyEvent.VK_S, KeyEvent.VK_DOWN))
        {
            dy += moveSpeed;
        }
        if (dx != 0)
        {
            tryMove(dx, 0);
        }
        if (dy != 0)
        {
            tryMove(0, dy);
        }
    }
}
