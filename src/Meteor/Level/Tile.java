package Meteor.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Meteor.GameEngine.Manager;
import Meteor.GameEngine.Interfaces.Updatable;
import Meteor.Graphics.Context;
import Meteor.Graphics.Sprites.Animation;
import Meteor.Graphics.Sprites.Sprite;

/** {@code Tile} is a generic tile class. 
* <br>
* This class is a generic representation of a tile.
*/
public class Tile implements Updatable {
	public static final int TILE_SIZE = 64; //The default size of a tile
	
	public static Manager manager; //The game manager object
	private Sprite sprite; //The sprite attached to the tile
	private List<Sprite> spriteList = new ArrayList<>(); //The list of sprite's attached to the object
	private Animation animation; //The animation attached to the tile
	private int width, height; //The width and height of the tile
	private int key; //The unique identifier of the tile used by the loader
	
    /**
     * The constructor used to define a tile.
     * 
     * @param sprite The sprite attached to the tile.
     * @param width The width of the tile.
     * @param height The height of the tile.
     * @param key The unique identifier of the tile.
     */
	public Tile(Sprite sprite, int width, int height, int key) {
		this.sprite = sprite;
		this.width = width;
		this.height = height;
		this.key = key;
	}
	
	/**
	 * The constructor used to define a tile.
	 * 
	 * @param width The width of the tile.
     * @param height The height of the tile.
     * @param key The unique identifier of the tile.
	 * @param spriteList The list of sprites.
	 */
	public Tile(int width, int height, int key, Sprite ... spriteList) {
		this.width = width;
		this.height = height;
		this.key = key;
		
		Collections.addAll(this.spriteList, spriteList);
	}
	
	/**
     * The constructor used to define a tile.
     * 
     * @param animation The animation attached to the tile.
     * @param width The width of the tile.
     * @param height The height of the tile.
     * @param key The unique identifier of the tile.
     */
	public Tile(Animation animation, int width, int height, int key) {
		this.animation = animation;
		this.width = width;
		this.height = height;
		this.key = key;
	}
	
	/**
	 * Method that MUST be called {@code super.update(manager, delta);} in order for the tile to be updated.
	 * 
	 * @param manager The engine manager object.
	 * @param delta Time elapsed between each frame.
     */
	@Override public void update(Manager manager, double delta) {
		//if (isAnimated()) animation.update(delta);
	}
	
	/**
	 * Renders the tile to the screen at a given x and y coordinate.
	 * Method that MUST be called in order for the tile to be rendered.
	 * 
	 * @param manager The game manager object.
	 * @param ctx The Game render 'canvas'.
	 * @param x The x-coordinate.
	 * @param y The y-coordinate.
	 */
	public void render(Manager manager, Context ctx, int x, int y) {
		//if (isAnimated()) ImageUtils.render(g, animation.getSprite(), x, y, width, height);
		//else ImageUtils.render(g, sprite.getImage(), x, y, width, height);
	}
    
    /**
     * Method used to grab a sprite object at a given index from the 'spriteList' object.
     * 
     * @param index The index attached to the sprite object.
     * @return The sprite object.
     */
    public Sprite getSprite(int index) {
        Sprite tempSprite = null;
        if (spriteList.get(index) != null)
            tempSprite = spriteList.get(index);
        return tempSprite;
    }
    
    /**
     * @return The sprite attached to the object.
     */
    public Sprite getSprite() {
        return sprite;
    }
    
    /**
     * @return The tile animation.
     */
    public Animation getAnimation() {
    	return animation;
    }
    
    /**
     * @return The width of the tile.
     */
    public int getWidth() {
    	return width;
    }
    
    /**
     * @return The height of the tile.
     */
    public int getHeight() {
    	return height;
    }
    
    /**
     * @return The unique identifier of the tile used by the loader.
     */
    public int getKey() {
    	return key;
    }
	
    /**
     * @return Weather or not the tile is solid.
     */
	public boolean isSolid() {
		return false;
	}
	
	/**
	 * @return Weather or not the tile is animated.
	 */
	public boolean isAnimated() {
		return false;
	}
	
	/**
	 * Sets the game manager object.
	 * 
	 * @param manager The game manager object.
	 */
	public void setManager(Manager manager) {
		Tile.manager = manager;
	}
	
	/**
	 * @return The game manager object.
	 */
	public Manager getManager() {
		return manager;
	}
}
