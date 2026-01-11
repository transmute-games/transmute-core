package TransmuteCore.Objects.Type;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Level.TiledLevel;
import TransmuteCore.Objects.Object;
import TransmuteCore.Units.Tuple2i;

public abstract class Mob extends Object
{
    public Mob(Manager manager, String name, Tuple2i location, float scale)
    {
        super(manager, name, Object.ANIMATABLE, location, scale);
    }

    public Mob(Manager manager, String name, Tuple2i location)
    {
        super(manager, name, Object.ANIMATABLE, location);
    }

    public void move(int xMove, int yMove)
    {
        if (xMove != 0 && yMove != 0)
        {
            move(xMove, 0);
            move(0, yMove);
            return;
        }

        if (!isCollidingWithTile(xMove, yMove))
        {
            location.x += xMove;
            location.y += yMove;
        }
    }

    private boolean isCollidingWithTile(int xMove, int yMove)
    {
        if (level instanceof TiledLevel)
        {
            TiledLevel level = (TiledLevel) this.level;
            for (int i = 0; i < level.getData().length; i++)
            {
                int xt = (int) Math.floor(((location.x + xMove) - i % level.getWidth()) / level.getTileSize());
                int yt = (int) Math.floor(((location.y + yMove) - i / level.getHeight()) / level.getTileSize());

                if (level.getTile(xt, yt) == null) continue;
                if (level.getTile(xt, yt).isSolid()) return true;
            }
        }

        return false;
    }
}
