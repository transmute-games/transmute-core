package TransmuteCore.Objects;


import TransmuteCore.GameEngine.Interfaces.Renderable;
import TransmuteCore.GameEngine.Interfaces.Updatable;
import TransmuteCore.GameEngine.Interfaces.Services.IRenderer;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Sprites.Animation;
import TransmuteCore.Graphics.Sprites.Sprite;
import TransmuteCore.Level.Level;
import TransmuteCore.Units.Tuple2i;

/**
 * {@code Object} is a generic object class.
 * <br>
 * This class is a generic representation of a object.
 */
public abstract class Object implements Updatable, Renderable
{
    public static final int ANIMATABLE = 0x0; //A type of object that has an animation
    public static final int STATIC = 0x1; //A type of object that does not have an animation

    protected Manager manager;  //The engine manager object
    protected String name; //The name of the object
    private int type;
    protected Level level; //The level attached to the object
    protected Tuple2i location; //The objects location

    protected Animation currentAnimation; //The currently selected animation
    protected Sprite sprite; //The sprite of the object if no animation is specified
    protected float scale; //The scaling ratio
    protected Bounds bounds; //The object collision bounds

    private boolean isRemoved = false; //Weather or not the object was removed from a level

    public Object(Manager manager, String name, int type, Sprite sprite, Tuple2i location, float scale)
    {
        this.manager = manager;
        this.name = name.toLowerCase();
        this.type = type;
        this.sprite = sprite;
        this.location = location;
        this.scale = scale;
    }


    public Object(Manager manager, String name, int type, Tuple2i location, float scale)
    {
        this(manager, name, type, null, location, scale);
    }

    public Object(Manager manager, String name, int type, Tuple2i location)
    {
        this(manager, name, type, location, 1.0f);
    }

    /**
     * Method used to add this object to the level.
     *
     * @param level The level attached to the object.
     */
    public void init(Level level)
    {
        this.level = level;
    }

    @Override
    public void update(Manager manager, double delta)
    {
        if (type == Object.ANIMATABLE)
        {
            if (currentAnimation != null)
            {
                setSprite(currentAnimation);
                currentAnimation.update();
            }

            if (bounds != null)
            {
                setBounds(sprite, location);
                bounds.update(manager, delta);
            }
        } else if (type == Object.STATIC)
        {
            if (bounds != null)
            {
                setBounds(sprite, location);
                bounds.update(manager, delta);
            }
        }
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        if (type == Object.ANIMATABLE)
        {
            if (currentAnimation != null)
            {
                setSprite(currentAnimation);
                currentAnimation.render(renderer, location.x, location.y);
            }

            if (bounds != null)
            {
                setBounds(sprite, location);
                bounds.render(manager, renderer);
            }
        } else if (type == Object.STATIC)
        {
            if (bounds != null)
            {
                setBounds(sprite, location);
                bounds.render(manager, renderer);
            }

            renderer.renderBitmap(sprite, location.x, location.y);
        }
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

            if (object.bounds != null && this.bounds != null)
            {
                if (object.bounds.getBounds(0, 0).intersects(this.bounds.getBounds(xOffset, yOffset))) return true;
            }
        }

        return false;
    }

    /**
     * Sets the animation to be displayed on screen.
     *
     * @param currentAnimation The animation to be displayed on screen.
     */
    public void setCurrentAnimation(Animation currentAnimation)
    {
        this.currentAnimation = currentAnimation;
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
     * @return The width-location of the object.
     */
    public int getX()
    {
        return location.x;
    }

    /**
     * @return The height-location of the object.
     */
    public int getY()
    {
        return location.y;
    }

    /**
     * Sets the scale of the object
     *
     * @param scale Scaling ratio (1f is 1:1 ratio).
     */
    public void setScale(float scale)
    {
        this.scale = scale;
    }

    /**
     * @return The scaling ratio
     */
    public float getScale()
    {
        return scale;
    }

    public void setBounds(Sprite sprite, Tuple2i location)
    {
        bounds.bounds.setBounds(sprite, location);
    }

    public void setSprite(Animation animation)
    {
        this.sprite = animation.getSprite();
    }

    public Sprite getSprite()
    {
        return sprite;
    }
}