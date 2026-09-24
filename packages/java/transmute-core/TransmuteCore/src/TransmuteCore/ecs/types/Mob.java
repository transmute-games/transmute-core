package TransmuteCore.ecs.types;

import TransmuteCore.core.Manager;
import TransmuteCore.level.Tile;
import TransmuteCore.level.TiledLevel;
import TransmuteCore.ecs.Object;
import TransmuteCore.math.Tuple2i;

/**
 * Legacy mobile entity for {@link TransmuteCore.level.TiledLevel}.
 *
 * @deprecated Prefer {@link TransmuteCore.world.World.Actor}
 */
@Deprecated(since = "1.1")
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
        if (!(level instanceof TiledLevel tiled))
        {
            return false;
        }

        int tileSize = tiled.getTileSize();
        if (tileSize <= 0)
        {
            return false;
        }

        int newX = location.x + xMove;
        int newY = location.y + yMove;

        int footprintW = tileSize;
        int footprintH = tileSize;
        if (bounds != null && bounds.bounds != null)
        {
            footprintW = Math.max(1, (int) bounds.bounds.getWidth());
            footprintH = Math.max(1, (int) bounds.bounds.getHeight());
        }

        int minTx = Math.floorDiv(newX, tileSize);
        int maxTx = Math.floorDiv(newX + footprintW - 1, tileSize);
        int minTy = Math.floorDiv(newY, tileSize);
        int maxTy = Math.floorDiv(newY + footprintH - 1, tileSize);

        for (int tx = minTx; tx <= maxTx; tx++)
        {
            for (int ty = minTy; ty <= maxTy; ty++)
            {
                Tile tile = tiled.getTile(tx, ty);
                if (tile != null && tile.isSolid())
                {
                    return true;
                }
            }
        }

        return false;
    }
}
