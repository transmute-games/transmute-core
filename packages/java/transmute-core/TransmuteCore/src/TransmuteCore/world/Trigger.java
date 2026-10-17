package TransmuteCore.world;

import TransmuteCore.assets.types.AudioPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Axis-aligned trigger volume inside a {@link World}.
 * Fires {@code onEnter} once when an actor overlaps until it exits.
 */
public final class Trigger
{
    private final String id;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private Consumer<World.Actor> onEnter;
    private final List<World.Actor> inside = new ArrayList<>();

    public Trigger(int x, int y, int width, int height, Consumer<World.Actor> onEnter)
    {
        this(null, x, y, width, height, onEnter);
    }

    public Trigger(String id, int x, int y, int width, int height, Consumer<World.Actor> onEnter)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.onEnter = onEnter != null ? onEnter : a -> {};
    }

    /**
     * Declares a named trigger that optionally plays an audio cue on enter.
     * Game code can still {@link #setOnEnter(Consumer)} to add logic.
     */
    public static Trigger named(String id, int x, int y, int width, int height, String audioCue)
    {
        Objects.requireNonNull(id, "id");
        Trigger trigger = new Trigger(id, x, y, width, height, null);
        if (audioCue != null && !audioCue.isBlank())
        {
            final String cue = audioCue.trim();
            trigger.setOnEnter(a -> AudioPlayer.play(cue));
        }
        return trigger;
    }

    public void setOnEnter(Consumer<World.Actor> onEnter)
    {
        Consumer<World.Actor> next = onEnter != null ? onEnter : a -> {};
        Consumer<World.Actor> previous = this.onEnter;
        this.onEnter = actor -> {
            previous.accept(actor);
            next.accept(actor);
        };
    }

    /** Replaces the enter handler (does not chain). */
    public void replaceOnEnter(Consumer<World.Actor> onEnter)
    {
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

    public String getId()
    {
        return id;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
