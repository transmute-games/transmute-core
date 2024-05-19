package TransmuteCore.Objects;

import TransmuteCore.GameEngine.Interfaces.Cortex;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Rectangle;
import TransmuteCore.Units.Dimension2f;
import TransmuteCore.Units.Tuple2i;

/**
 * {@code Bounds} is an classes used to represent 2D-object-based collisions.
 * <br>
 * This class is a a object that contains and manipulates the bounding box of an object.
 */
public class Bounds implements Cortex
{
    public static boolean SHOW = true; //A value for the {@code shouldDisplay}
    public static boolean HIDE = false; //A value for the {@code shouldDisplay}

    public Rectangle bounds; //The rectangular collision bounds
    private Tuple2i location; //The x and y coordinates of the collision bounds
    private Dimension2f dimensions; //The width and height of the collision bounds
    private Tuple2i offsets; //The (width, height) offsets to be applied to the collision bounds
    private float scale; //Scaling ratio
    private int color; //The color of the collision bounds
    private boolean shouldDisplay; //Weather or not the bounds should be seen

    /**
     * The constructor used to create the collision component.
     *
     * @param location      The x and y coordinates of the collision bounds
     * @param dimensions    The width and height of the collision bounds
     * @param scale         The scaling ratio
     * @param color         The color of the collision bounds.
     * @param shouldDisplay Weather or not the bounds should be seen.
     */
    public Bounds(Tuple2i location, Dimension2f dimensions, float scale, int color, boolean shouldDisplay)
    {
        this.location = location;
        this.dimensions = dimensions;
        this.offsets = new Tuple2i();
        this.color = color;
        this.shouldDisplay = shouldDisplay;
        this.scale = scale;

        init();
    }

    /**
     * The constructor used to create the collision component.
     *
     * @param location      The x and y coordinates of the collision bounds
     * @param dimensions    The width and height of the collision bounds
     * @param color         The color of the collision bounds.
     * @param shouldDisplay Weather or not the bounds should be seen.
     */
    public Bounds(Tuple2i location, Dimension2f dimensions, int color, boolean shouldDisplay)
    {
        this(location, dimensions, 1.0f, color, shouldDisplay);
    }

    /**
     * The constructor used to create the collision component.
     *
     * @param location      The x and y coordinates of the collision bounds
     * @param rectangle     The calculated collision bounds from Sprite.class
     * @param color         The color of the collision bounds.
     * @param shouldDisplay Weather or not the bounds should be seen.
     */
    public Bounds(Tuple2i location, Rectangle rectangle, int color, boolean shouldDisplay)
    {
        if (rectangle == null) rectangle = new Rectangle();

        this.location = new Tuple2i(location.x + rectangle.x, location.y + rectangle.y);
        this.dimensions = new Dimension2f(rectangle.width, rectangle.height);
        this.offsets = new Tuple2i();
        this.color = color;
        this.shouldDisplay = shouldDisplay;

        scale = 1.0f;

        init();
    }


    @Override
    public void init()
    {

        setBounds(location.x, location.y, dimensions.width * scale, dimensions.height * scale);
    }

    /**
     * Method used to grab the rectangular collision bounds based on a (width, height) offset.
     *
     * @param xOffset The width-offset of the object.
     * @param yOffset The height-offset of the object.
     * @return The rectangular collision bounds.
     */
    public Rectangle getBounds(int xOffset, int yOffset)
    {
        return new Rectangle(bounds.x + xOffset, bounds.y + yOffset, bounds.width * scale, bounds.height * scale);
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
        bounds.setBounds(location.x, location.y, dimensions.width, dimensions.height, scale);
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
        if (shouldDisplay)
        {
            ctx.renderRectangle(bounds.x - offsets.x, bounds.y - offsets.y, bounds.width, bounds.height, color);
        }
    }

    public Tuple2i getLocation()
    {
        return location;
    }

    public float getWidth()
    {
        return dimensions.width;
    }

    public float getHeight()
    {
        return dimensions.height;
    }

    public float getScale()
    {
        return scale;
    }

    /**
     * Method used to configure the (width, height) offsets.
     *
     * @param xOffset The width-offset of the object.
     * @param yOffset The height-offset of the object.
     */
    public void setOffsets(int xOffset, int yOffset)
    {
        offsets.x = xOffset;
        offsets.y = yOffset;
    }

    /**
     * @return The (width, height) offsets to be applied to the collision bounds.
     */
    public Tuple2i getOffsets()
    {
        return offsets;
    }

    /**
     * @return The color of the collision bounds.
     */
    public int getColor()
    {
        return color;
    }

    /**
     * Initializes the bounding rectangle.
     *
     * @param x The x-location of the rectangle.
     * @param y The y-location of the rectangle.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public void setBounds(int x, int y, float width, float height)
    {
        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public String toString()
    {
        return "Bounds{" +
                "bounds=" + bounds +
                ", location=" + location +
                ", dimensions=" + dimensions +
                ", offsets=" + offsets +
                ", scale=" + scale +
                ", color=" + color +
                ", shouldDisplay=" + shouldDisplay +
                '}';
    }

}
