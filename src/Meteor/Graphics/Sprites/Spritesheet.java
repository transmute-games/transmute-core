package Meteor.Graphics.Sprites;

import java.awt.image.BufferedImage;

import Meteor.Graphics.Bitmap;
import Meteor.System.Asset.Type.Images.ImageUtils;
import Meteor.Units.Tuple2i;
import Meteor.Units.Tuple4i;

public class Spritesheet extends Bitmap {
	/** Horizontal strip orientation */
	public static final int ORIENTATION_VERTICAL = 0x1;

	/** Vertical strip orientation */
	public static final int ORIENTATION_HORIZONTAL = 0x2;
	
	/** The main sprite-sheet image */
	public BufferedImage image;

	/** Size of each sprite cell (assuming they are constant) */
	private Tuple2i cellSize;

	/** Size of pixel gap between cells (0 if none) */
	private int verticalGapSize = 0, horizontalGapSize = 0;

	/** Initial offset to grab sprite from, this is the first coordinate for sprite position calculations */
	private Tuple2i startOffset = new Tuple2i(0, 0);

	/** Sorted cell sprite's are stored here */
	private Bitmap[][] cellSprites;

	/** Scaling **/
	private float scale = 1f;

	/**
	 * Instantiates a Spritesheet object from a given Bitmap object (the main image) with sprite-sheet
	 * parameters.
	 *
	 * @param bitmap The big image to be used as Spritesheet.
	 * @param cellSize Width and height of each sprite in a cell (Assuming cells are equal in size).
	 * @param startOffset Starting position to calculate sprite position (in pixels).
	 * @param vertGap Vertical pixel gap size (0 if none).
	 * @param horizGap Horizontal pixel gap size (0 if none).
	 */
	public Spritesheet(Bitmap bitmap, Tuple2i cellSize, Tuple2i startOffset, int vertGap, int horizGap) {
		super(bitmap);
		
		this.cellSize = cellSize;
		this.verticalGapSize = vertGap;
		this.horizontalGapSize = horizGap;
		this.startOffset = startOffset;

		int xOffset = startOffset.x;
		int yOffset = startOffset.y;
		int w = cellSize.x;
		int h = cellSize.y;
		int scaledWidth = bitmap.getWidth() / w;
		int scaledHeight = bitmap.getHeight() / h;
		cellSprites = new Bitmap[scaledWidth][scaledHeight];
		for(int x = 0; x < scaledWidth; x++) {
			for(int y = 0; y < scaledHeight; y++) {
				//Map cells into a grid for future reference
				int[] cellBmpPixels = bitmap.getData(xOffset + x * (w + vertGap), 
						yOffset + y * (h + horizGap), (x + 1) * w, (y + 1) * h);
				cellSprites[x][y] = new Bitmap(cellBmpPixels, w, h);
			}
		}
	}
	
	/**
	 * Supplies a sub-image within the Spritesheet.
	 * 
	 * @param x Number of tiles from left (starting with 0).
	 * @param y Number of tiles from top (starting with 0).
	 * @return A sub-image within the Spritesheet.
	 */
	public Bitmap crop(int x, int y) {
		return cellSprites[x][y];
	}
	
	/**
	 * Instantiates a Spritesheet object from a given BufferedImage object (the main image).
	 *
	 * @param image The big image to be used as Spritesheet.
	 * @param cellSize Width and height of each sprite in a cell (Assuming cells are equal in size).
	 */
	public Spritesheet(BufferedImage image, Tuple2i cellSize) {
		super(image);
		
		this.image = image;
		this.cellSize = cellSize;
	}
	
    /**
     * Supplies a sub-image within the Spritesheet.
     * 
     * @param x Number of tiles from left (starting with 0).
     * @param y Number of tiles from top (starting with 0).
     * @param cellWidth The width of the selection.
     * @param cellHeight The height of the selection.
     * @return The image cropped from the Spritesheet image.
     */
	public Bitmap crop(int x, int y, int cellWidth, int cellHeight) {
		if (image == null) new Error("[Spritesheet]: The [image] has not been instantiated.");
		
		Tuple4i properties = new Tuple4i(x, y, cellWidth, cellHeight);
        if (cellWidth == 0 || cellHeight == 0
        	|| cellWidth == getCellSize().x 
        	||cellHeight == getCellSize().y) {
            properties.setWidth(getCellSize().x);
            properties.setHeight(getCellSize().y);
        }
	        
        BufferedImage image = ImageUtils.crop(this.image, properties.getX(), properties.getY(), properties.getWidth(), properties.getHeight());
	    return ImageUtils.getAsBitmap(image);
	}

	/**
	 * Generates a scaled copy of the existing Spritesheet.
	 * 
	 * @param scale Scaling ratio (1f is 1:1 ratio).
	 * @return A scaled copy of the Spritesheet.
	 */
	@Override public Spritesheet getScaled(float scale) {
		int newWidth = (int) (getWidth() * scale);
		int newHeight = (int) (getHeight() * scale);
		Bitmap scaledBitmap = getScaled(newWidth, newHeight);
		
		int newCellWidth = (int) (cellSize.x * scale);
		int newCellHeight = (int) (cellSize.y * scale);
		Tuple2i newCellSize = new Tuple2i(newCellWidth, newCellHeight);
		
		Spritesheet s = new Spritesheet(scaledBitmap, newCellSize, startOffset, verticalGapSize, horizontalGapSize);
		s.scale = scale;
		return s;
	}

	/**
	 * Generates a series of timed action.
	 *
	 * @param duration Time in seconds to be applied to all generated frames.
	 * @param spriteLoc Location of the sprite's (in terms of cells).
	 * @return Generated action with specified cells and provided time for all frames.
	 */
	public Animation generateAnimation(int duration, Tuple2i ... spriteLoc) {
		Bitmap[] sprites = new Bitmap[spriteLoc.length];
		int i = 0;
		for(Tuple2i p : spriteLoc) {
			Bitmap sprite = crop(p.x, p.y);
			sprites[i] = sprite;
			i++;
		}
		return new Animation(sprites, duration);
	}

	/**
	 * Generates a series of timed action.
	 *
	 * Note that the length of the {@code duration[]} must be the same as {@code spriteLoc[]}.
	 *
	 * @param duration Time in seconds to be applied for each frame. Size should be equal to <strong>spriteLoc</strong>.
	 * @param spriteLoc Location of the sprite's (in terms of cells).
	 * @return Generated action with specified cells and provided time for each frame.
	 */
	public Animation generateAnimation(int[] duration, Tuple2i[] spriteLoc) {
		if(duration.length != spriteLoc.length)
			throw new IllegalArgumentException("Frame time array size should have the same length as spriteLoc array size!");
		Bitmap[] sprites = new Bitmap[spriteLoc.length];
		int i = 0;
		for(Tuple2i p : spriteLoc) {
			Bitmap sprite = crop(p.x, p.y);
			sprites[i] = sprite;
			i++;
		}
		return new Animation(sprites, duration);
	}

	/**
	 * Generates a series of timed action, assuming the Spritesheet is either a vertical
	 * or horizontal strip (i.e. 1 in width or height) and stores frames for an action.
	 *
	 * @param sequenceOrientation Orientation of the Spritesheet.
	 * @param duration Time in seconds to be applied to every frame.
	 * @return Generated action with specified cells and provided time for every frame.
	 */
	public Animation generateAnimation(int sequenceOrientation, int duration) {
		Bitmap[] sprites;
		Animation animation = null;
		switch(sequenceOrientation) {
			case ORIENTATION_HORIZONTAL:
				sprites = new Bitmap[cellSprites.length];
				for(int i = 0; i < cellSprites.length; i++)
					sprites[i] = cellSprites[i][0];
				animation = new Animation(sprites, duration);
				return animation;
			case ORIENTATION_VERTICAL:
				sprites = new Bitmap[cellSprites[0].length];
				for(int i = 0; i < sprites.length; i++)
					sprites[i] = cellSprites[0][i];
				animation = new Animation(sprites, duration);
				return animation;
			default:
				return null;
		}
	}

	/**
	 * Generates a series of timed action, assuming the Spritesheet is either a vertical
	 * or horizontal strip (i.e. 1 in width or height) and stores frames for an action.
	 *
	 * @param sequenceOrientation Orientation of the Spritesheet.
	 * @param duration Time in seconds to be applied to each frame.
	 * @return Generated action with specified cells and provided time for each frame.
	 */
	public Animation generateAnimation(int sequenceOrientation, int[] duration) {
		Bitmap[] sprites;
		Animation animation = null;
		switch(sequenceOrientation) {
			case ORIENTATION_HORIZONTAL:
				sprites = new Bitmap[cellSprites.length];
				for(int i = 0; i < cellSprites.length; i++)
					sprites[i] = cellSprites[i][0];
				animation = new Animation(sprites, duration);
				return animation;
			case ORIENTATION_VERTICAL:
				sprites = new Bitmap[cellSprites[0].length];
				for(int i = 0; i < sprites.length; i++)
					sprites[i] = cellSprites[0][i];
				animation = new Animation(sprites, duration);
				return animation;
			default:
				return null;
		}
	}

	/**
	 * Supplies the current scale of the Spritesheet image.
	 * 
	 * @return Current scale of Spritesheet image.
	 */
	public float getScale() {
		return scale;
	}
	
	/**
	 * @return The Spritesheet image.
	 */
	@Override public BufferedImage getImage() {
		return image;
	}

	/**
	 * Supplies all the pre-divided sprite's in this sprite-sheet.
	 * 
	 * @return All sprite's within this Spritesheet.
	 */
	public Bitmap[][] getSprites() {
		return cellSprites;
	}

	/**
	 * @return Vertical gap between each sprite.
	 */
	public int getVerticalGapSize() {
		return verticalGapSize;
	}

	/**
	 * 
	 * @return Horizontal gap between each sprite.
	 */
	public int getHorizontalGapSize() {
		return horizontalGapSize;
	}

	/**
	 * @return Initial starting offset for sprite calculation.
	 */
	public Tuple2i getStartOffset() {
		return startOffset;
	}

	/**
	 * 
	 * @return Dimension for each sprite in this sprite-sheet.
	 */
	public Tuple2i getCellSize() {
		return cellSize;
	}
}