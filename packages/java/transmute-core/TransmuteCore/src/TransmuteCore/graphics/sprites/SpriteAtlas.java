package TransmuteCore.graphics.sprites;

import TransmuteCore.assets.AssetManager;
import TransmuteCore.graphics.Bitmap;
import TransmuteCore.math.Tuple2i;

import java.util.Objects;

/**
 * Thin atlas helper: crop frames and build {@link Animation}s from a grid sheet.
 * Prefer {@code spritesheet.*} GameSpec keys to register the underlying image.
 */
public final class SpriteAtlas
{
    private final Spritesheet sheet;
    private final int tile;

    private SpriteAtlas(Spritesheet sheet, int tile)
    {
        this.sheet = sheet;
        this.tile = tile;
    }

    public static SpriteAtlas from(Bitmap bitmap, int tileSize)
    {
        Objects.requireNonNull(bitmap, "bitmap");
        if (tileSize <= 0)
        {
            throw new IllegalArgumentException("tileSize must be positive");
        }
        Spritesheet sheet = new Spritesheet(
            bitmap.getImage(), new Tuple2i(tileSize, tileSize), new Tuple2i(0, 0), 0, 0);
        return new SpriteAtlas(sheet, tileSize);
    }

    /**
     * Loads from an already-registered image asset (e.g. {@code sheet-player} from GameSpec).
     */
    public static SpriteAtlas fromAsset(AssetManager assets, String imageName, int tileSize)
    {
        Objects.requireNonNull(assets, "assets");
        Bitmap bmp = assets.getImage(Objects.requireNonNull(imageName, "imageName"));
        if (bmp == null)
        {
            throw new IllegalArgumentException("No image asset named: " + imageName);
        }
        return from(bmp, tileSize);
    }

    public int getTileSize()
    {
        return tile;
    }

    public Sprite crop(int col, int row)
    {
        return sheet.crop(col, row);
    }

    /**
     * Builds an animation from column/row pairs: {@code animation("walk", 150, 0,0, 1,0, 2,0)}.
     */
    public Animation animation(String name, int frameMs, int... colRowPairs)
    {
        if (colRowPairs == null || colRowPairs.length == 0 || colRowPairs.length % 2 != 0)
        {
            throw new IllegalArgumentException("colRowPairs must be non-empty even-length col,row,…");
        }
        Sprite[] frames = new Sprite[colRowPairs.length / 2];
        for (int i = 0; i < frames.length; i++)
        {
            frames[i] = crop(colRowPairs[i * 2], colRowPairs[i * 2 + 1]);
        }
        return new Animation(name, frames, frameMs);
    }

    public Spritesheet getSheet()
    {
        return sheet;
    }
}
