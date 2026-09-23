package TransmuteCore.world;

import TransmuteCore.core.Manager;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.math.Collision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Deep tile-world module: grid solids + actors behind one interface.
 * Prefer this over inventing per-game TileMap/Entity classes.
 */
public final class World
{
    public static final int EMPTY = 0;
    public static final int SOLID = 1;

    private final int cols;
    private final int rows;
    private final int tileSize;
    private final int[] tiles;
    private final List<Actor> actors = new ArrayList<>();
    private int clearColor;
    private int solidColor;

    private World(int cols, int rows, int tileSize)
    {
        if (cols <= 0 || rows <= 0 || tileSize <= 0)
        {
            throw new IllegalArgumentException(
                String.format("cols, rows, tileSize must be positive. Got %dx%d tile=%d", cols, rows, tileSize));
        }
        this.cols = cols;
        this.rows = rows;
        this.tileSize = tileSize;
        this.tiles = new int[cols * rows];
        this.clearColor = 0xFF14141E;
        this.solidColor = 0xFF3C3C50;
    }

    public static World grid(int cols, int rows, int tileSize)
    {
        return new World(cols, rows, tileSize);
    }

    public World clearColor(int argb)
    {
        this.clearColor = argb;
        return this;
    }

    public World solidColor(int argb)
    {
        this.solidColor = argb;
        return this;
    }

    public void fillBorder(int tile)
    {
        for (int x = 0; x < cols; x++)
        {
            setTile(x, 0, tile);
            setTile(x, rows - 1, tile);
        }
        for (int y = 0; y < rows; y++)
        {
            setTile(0, y, tile);
            setTile(cols - 1, y, tile);
        }
    }

    public void setTile(int tx, int ty, int tile)
    {
        if (!inBounds(tx, ty))
        {
            throw new IllegalArgumentException(String.format("Tile out of bounds: (%d,%d)", tx, ty));
        }
        tiles[tx + ty * cols] = tile;
    }

    public int getTile(int tx, int ty)
    {
        if (!inBounds(tx, ty))
        {
            return SOLID;
        }
        return tiles[tx + ty * cols];
    }

    public boolean isSolid(int tx, int ty)
    {
        return getTile(tx, ty) == SOLID;
    }

    /**
     * @return true if an axis-aligned box at pixel coords would overlap any solid tile.
     */
    public boolean blocks(int x, int y, int w, int h)
    {
        int minTx = Math.floorDiv(x, tileSize);
        int maxTx = Math.floorDiv(x + w - 1, tileSize);
        int minTy = Math.floorDiv(y, tileSize);
        int maxTy = Math.floorDiv(y + h - 1, tileSize);
        for (int tx = minTx; tx <= maxTx; tx++)
        {
            for (int ty = minTy; ty <= maxTy; ty++)
            {
                if (isSolid(tx, ty))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void add(Actor actor)
    {
        actors.add(Objects.requireNonNull(actor, "actor"));
        actor.attach(this);
    }

    public List<Actor> getActors()
    {
        return Collections.unmodifiableList(actors);
    }

    public void update(Manager manager, double delta)
    {
        for (Actor actor : actors)
        {
            actor.update(manager, delta);
        }
    }

    public void render(Manager manager, IRenderer renderer)
    {
        Context ctx = (Context) renderer;
        ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), clearColor);
        for (int ty = 0; ty < rows; ty++)
        {
            for (int tx = 0; tx < cols; tx++)
            {
                if (isSolid(tx, ty))
                {
                    ctx.renderFilledRectangle(
                        tx * tileSize, ty * tileSize, tileSize, tileSize, solidColor);
                }
            }
        }
        for (Actor actor : actors)
        {
            actor.render(manager, renderer);
        }
    }

    public int getCols()
    {
        return cols;
    }

    public int getRows()
    {
        return rows;
    }

    public int getTileSize()
    {
        return tileSize;
    }

    public int pixelWidth()
    {
        return cols * tileSize;
    }

    public int pixelHeight()
    {
        return rows * tileSize;
    }

    private boolean inBounds(int tx, int ty)
    {
        return tx >= 0 && ty >= 0 && tx < cols && ty < rows;
    }

    /**
     * Movable thing in a {@link World}. Subclass or use {@link #colored(int, int, int, int, int)}.
     */
    public static class Actor
    {
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        protected int color;
        protected World world;

        public Actor(int x, int y, int width, int height, int color)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        public static Actor colored(int x, int y, int width, int height, int color)
        {
            return new Actor(x, y, width, height, color);
        }

        void attach(World world)
        {
            this.world = world;
        }

        public void update(Manager manager, double delta)
        {
        }

        public void render(Manager manager, IRenderer renderer)
        {
            Context ctx = (Context) renderer;
            ctx.renderFilledRectangle(x, y, width, height, color);
        }

        /**
         * Tries to move by {@code dx},{@code dy}; rejects the move if any solid blocks the new box.
         *
         * @return true if the move applied
         */
        public boolean tryMove(int dx, int dy)
        {
            if (world == null)
            {
                x += dx;
                y += dy;
                return true;
            }
            int nx = x + dx;
            int ny = y + dy;
            if (world.blocks(nx, ny, width, height))
            {
                return false;
            }
            x = nx;
            y = ny;
            return true;
        }

        public boolean overlaps(Actor other)
        {
            return Collision.aabb(x, y, width, height, other.x, other.y, other.width, other.height);
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

        public int getColor()
        {
            return color;
        }

        public World getWorld()
        {
            return world;
        }
    }
}
