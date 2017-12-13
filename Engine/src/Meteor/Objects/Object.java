package Meteor.Objects;

import Meteor.GameEngine.Interfaces.Renderable;
import Meteor.GameEngine.Interfaces.Updatable;
import Meteor.GameEngine.Manager;
import Meteor.Graphics.Color;
import Meteor.Graphics.Context;
import Meteor.Graphics.Sprites.Animation;
import Meteor.Graphics.Sprites.Frame;
import Meteor.Graphics.Sprites.Sprite;
import Meteor.Level.Level;
import Meteor.Units.Tuple4i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * {@code Object} is a generic object class.
 * <br>
 * This class is a generic representation of a object.
 */
public abstract class Object implements Updatable, Renderable
{
    public static final boolean SHOULD_DISPLAY = true; //Weather or not to display the collision bounds

    protected Manager manager;  //The engine manager object
    protected Level level; //The level object
    protected String name; //The name of the object
    protected Tuple4i properties; //The x, y, width and height properties
    private float scale; //Scaling ratio
    protected Sprite sprite; //The image attached to the object
    protected Animation defaultAnimation; //The default animation associated with this object
    protected List<Animation> animationList = new ArrayList<>(); //The animation associated with this object

    private boolean isRemoved = false; //Weather or not the object was removed from a level
    private Bounds bounds; //The object collision bounds

    /**
     * The constructor used to define a object.
     *
     * @param manager    The engine manager object.
     * @param name       The name of the object.
     * @param sprite     The sprite attached to the object.
     * @param properties The x, y, width, and height properties.
     */
    protected Object(Manager manager, String name, Sprite sprite, Tuple4i properties)
    {
        this.manager = manager;
        this.name = name.toLowerCase();
        this.sprite = sprite;
        this.properties = properties;

        scale = 1.0f;
    }

    /**
     * The constructor used to define a object.
     *
     * @param manager    The engine manager object.
     * @param name       The name of the object.
     * @param sprite     The sprite attached to the object.
     * @param properties The x, y, width, and height properties.
     * @param scale      Scaling ratio (1f is 1:1 ratio).
     */
    protected Object(Manager manager, String name, Sprite sprite, Tuple4i properties, float scale)
    {
        this.manager = manager;
        this.name = name.toLowerCase();
        this.sprite = sprite;
        this.properties = properties;
        this.scale = scale;
    }

    /**
     * The constructor used to define a object.
     * <p>
     * NOTE: You must specify the list of animations in the sub-class.
     * To do this, add to the {@code animationList} by using the
     * {@code addAnimations()} method in the constructor of the sub-class.
     *
     * @param manager    The engine manager object.
     * @param name       The name of the object.
     * @param properties The x, y, width, and height properties.
     * @param scale      Scaling ratio (1f is 1:1 ratio).
     */
    protected Object(Manager manager, String name, Tuple4i properties, float scale)
    {
        this.manager = manager;
        this.name = name.toLowerCase();
        this.properties = properties;
        this.scale = scale;
    }

    /**
     * The constructor used to define a object.
     * <p>
     * NOTE: You must specify the list of animations in the sub-class.
     * To do this, add to the {@code animationList} by using the
     * {@code addAnimations()} method in the constructor of the sub-class.
     *
     * @param manager    The engine manager object.
     * @param name       The name of the object.
     * @param properties The x, y, width, and height properties.
     */
    protected Object(Manager manager, String name, Tuple4i properties)
    {
        this.manager = manager;
        this.name = name.toLowerCase();
        this.properties = properties;

        scale = 1.0f;
    }

    /**
     * Method used to add this object to the level.
     *
     * @param level The level attached to the object.
     */
    public void init(Level level)
    {
        this.level = level;

        properties = new Tuple4i(properties.location.x * level.getWidth(), properties.location.y * level.getHeight(), properties.getWidth(), properties.getHeight());
        bounds = new Bounds(this, properties, scale, Color.RED, SHOULD_DISPLAY);
    }

    /**
     * Adds a list of animations to the {@code animationList}.
     * <p>
     * NOTE: this method MUST be called within the sub-class's
     * constructor or else {@code animationList} will be NULL.
     *
     * @param animations The list of animations.
     */
    public void addAnimations(Animation... animations)
    {
        Collections.addAll(animationList, animations);
    }

    /**
     * Sets the default animation if, for example, the player is idle.
     *
     * @param animation The default animation.
     */
    public void setDefaultAnimation(Animation animation)
    {
        this.defaultAnimation = animation;
    }

    /**
     * Method used to grab a sprite object attached
     * to an index in the specified animation.
     *
     * @param name  The name of the animation holding the sprite
     * @param index The index attached to the sprite object.
     * @return The sprite object.
     */
    public Sprite getImage(String name, int index)
    {
        Sprite tempSprite = null;

        for (Animation a : animationList)
        {
            if (a.getName().equalsIgnoreCase(name))
            {
                LinkedList<Frame> frames = a.frames;

                Frame frame = frames.get(index);

                if (frame != null)
                {
                    tempSprite = frame.sprite;
                }
            }
        }

        return tempSprite;
    }

    /**
     * @return The sprite attached to the object.
     */
    public Sprite getSprite()
    {
        return sprite;
    }

    /**
     * Method used to check weather or not an object is colliding with another object.
     *
     * @param xOffset The x-offset of the object.
     * @param yOffset The y-offset of the object.
     * @return Weather or not an object is colliding with another object.
     */
    protected boolean isCollidingWithObject(int xOffset, int yOffset)
    {
        for (Object object : manager.getObjectManager().objectList)
        {
            if (object.name.equalsIgnoreCase(this.name)) continue;

            if (object.bounds.getBounds(0, 0).intersects(this.bounds.getBounds(xOffset, yOffset))) return true;
        }

        return false;
    }

    /**
     * Method that MUST be called {@code super.update(manager, delta);} in order for the collision bounds to be updated.
     *
     * @param manager The engine manager object.
     * @param delta   Time elapsed between each frame.
     */
    @Override
    public void update(Manager manager, double delta)
    {
        if (bounds != null) bounds.update(manager, delta);
    }

    /**
     * Method that MUST be called {@code super.render(manager, ctx);} in order for the collision bounds to be rendered.
     *
     * @param manager The engine manager object.
     * @param ctx     The Game render 'canvas'.
     */
    @Override
    public void render(Manager manager, Context ctx)
    {
        if (bounds != null) bounds.render(manager, ctx);
    }

    /**
     * @return Weather or not an object has been removed from the level.
     */
    public boolean isRemoved()
    {
        return isRemoved;
    }

    /**
     * Switches the 'isRemoved' variable to true to indicate that the object has been removed from the level.
     */
    public void remove()
    {
        isRemoved = true;
    }

    /**
     * @return The x-location of the object.
     */
    public int getX()
    {
        return properties.location.x;
    }

    /**
     * @return The y-location of the object.
     */
    public int getY()
    {
        return properties.location.y;
    }

    /**
     * @return The width of the object.
     */
    public int getWidth()
    {
        return properties.width;
    }

    /**
     * @return The height of the object.
     */
    public int getHeight()
    {
        return properties.height;
    }

    /**
     * @return Scaling ratio (1f is 1:1 ratio).
     */
    public float getScale()
    {
        return scale;
    }
}
