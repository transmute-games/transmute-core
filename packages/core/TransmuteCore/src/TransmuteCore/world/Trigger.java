package TransmuteCore.world;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Axis-aligned trigger volume inside a {@link World}.
 * Fires {@code onEnter} once when an actor overlaps until it exits.
 */
public final class Trigger
{
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Consumer<World.Actor> onEnter;
    private final List<World.Actor> inside = new ArrayList<>();

    public Trigger(int x, int y, int width, int height, Consumer<World.Actor> onEnter)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.onEnter = onEnter != null ? onEnter : a -> {};
    }

    public void evaluate(Iterable<World.Actor> actors)
    {
        List<World.Actor> nowInside = new ArrayList<>();
        for (World.Actor actor : actors)
        {
            if (overlaps(actor))
            {
                nowInside.add(actor);
                if (!inside.contains(actor))
                {
                    onEnter.accept(actor);
                }
            }
        }
        inside.clear();
        inside.addAll(nowInside);
    }

    private boolean overlaps(World.Actor actor)
    {
        return TransmuteCore.math.Collision.aabb(
            x, y, width, height,
            actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}
