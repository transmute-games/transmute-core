package TransmuteCore.level;

import java.util.HashMap;
import java.util.Map;

import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.Manager;
import TransmuteCore.core.interfaces.Services.IRenderer;
import TransmuteCore.graphics.Bitmap;
import TransmuteCore.assets.Asset;
import TransmuteCore.assets.types.Image;

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
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (!Asset.cropFileExtension(filePath).equalsIgnoreCase(".png") ||
                !Asset.cropFileExtension(filePath).equalsIgnoreCase(".jpg") ||
                !Asset.cropFileExtension(filePath).equalsIgnoreCase(".jpeg"))
        {
            new Error("[TiledLevel]: The inputted file does not have the correct file-extension.\n"
                    + "Please make sure the file-extension is .png, .jpg or .jpeg.");
            return;
        }

        Bitmap bmp = Image.getAsBitmap(Image.load(TiledLevel.class, filePath));
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
    public void render(Manager manager, IRenderer renderer)
    {
        // Use renderer dimensions for viewport culling
        int viewportWidth = renderer.getWidth();
        int viewportHeight = renderer.getHeight();
        
        int xStart = Math.max(0, xOffset / tileSize);
        int xEnd = Math.min(width, xOffset + viewportWidth / tileSize + 2);
        int yStart = Math.max(0, yOffset / tileSize);
        int yEnd = Math.min(height, yOffset + viewportHeight / tileSize + 2);

        for (int x = xStart; x < xEnd; x++)
        {
            for (int y = yStart; y < yEnd; y++)
            {
                if (x < 0 || y < 0 || x >= width || y >= height) continue;

                Tile tile = getTile(x, y);
                if (tile == null) continue;
                tile.render(manager, renderer, x * tile.getWidth() - xOffset, y * tile.getHeight() - yOffset);
            }
        }

        super.render(manager, renderer);
    }

    public void addTile(int index, Tile tile)
    {
        if (tile == null) {
            throw new IllegalArgumentException("Tile cannot be null");
        }
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
        if (tileSize <= 0) {
            throw new IllegalArgumentException(
                String.format("Tile size must be positive. Got: %d", tileSize)
            );
        }
        this.tileSize = tileSize;
    }

    public int getTileSize()
    {
        return tileSize;
    }
}