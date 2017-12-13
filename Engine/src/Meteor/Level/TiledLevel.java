package Meteor.Level;

import java.util.HashMap;
import java.util.Map;

import Meteor.GameEngine.Manager;
import Meteor.GameEngine.Meteor;
import Meteor.Graphics.Bitmap;
import Meteor.Graphics.Context;
import Meteor.System.Asset.Asset;
import Meteor.System.Asset.Type.Images.Image;
import Meteor.System.Asset.Type.Images.ImageUtils;

public class TiledLevel extends Level
{
    private int tileSize;
    private int[] tileArray;
    private Map<Integer, Tile> tileMap = new HashMap<>();

    public TiledLevel(int width, int height)
    {
        super(width, height);
    }

    public TiledLevel(String filePath)
    {
        super(filePath);
    }

    /**
     * Loads in a level when given a file-path.
     * NOTE: The file-extension (e.g. .png) of the file must be one of the following: .png, .jpg or .jpeg.
     *
     * @param filePath The path to the level file.
     */
    @Override
    public void load(String filePath)
    {
        if (!Asset.cropFileExtension(filePath).equalsIgnoreCase(".png") ||
                !Asset.cropFileExtension(filePath).equalsIgnoreCase(".jpg") ||
                !Asset.cropFileExtension(filePath).equalsIgnoreCase(".jpeg"))
        {
            new Error("[TiledLevel]: The inputted file does not have the correct file-extension.\n"
                    + "Please make sure the file-extension is .png, .jpg or .jpeg.");
            return;
        }

        Bitmap bmp = ImageUtils.getAsBitmap(Image.load(TiledLevel.class, filePath));
        tileArray = bmp.getData();
        width = bmp.getWidth();
        height = bmp.getHeight();
    }

    @Override
    public void update(Manager manager, double delta)
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                getTile(x, y).update(manager, delta);
            }
        }

        super.update(manager, delta);
    }

    @Override
    public void render(Manager manager, Context ctx)
    {
        int xStart = Math.max(0, xOffset / tileSize);
        int xEnd = Math.min(width, xOffset + Meteor.getWidth() / Meteor.getScale() / tileSize + 2);
        int yStart = Math.max(0, yOffset / tileSize);
        int yEnd = Math.min(height, yOffset + Meteor.getHeight() / Meteor.getScale() / tileSize + 2);

        for (int x = xStart; x < xEnd; x++)
        {
            for (int y = yStart; y < yEnd; y++)
            {
                if (x < 0 || y < 0 || x >= width || y >= height) continue;

                Tile tile = getTile(x, y);
                if (tile == null) continue;
                tile.render(manager, ctx, x * tile.getWidth() - xOffset, y * tile.getHeight() - yOffset);
            }
        }

        super.render(manager, ctx);
    }

    public void addTile(int index, Tile tile)
    {
        tileMap.put(index, tile);
    }

    public Tile getTile(int x, int y)
    {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;

        Tile tile = null;

        int key = tileArray[x + y * width];
        if (tileMap.containsKey(key))
        {
            tile = tileMap.get(key);
        }

        return tile;
    }

    public int[] getData()
    {
        return tileArray;
    }

    public void setTileSize(int tileSize)
    {
        this.tileSize = tileSize;
    }

    public int getTileSize()
    {
        return tileSize;
    }
}