package Meteor.Objects;

import Meteor.GameEngine.Interfaces.Cortex;
import Meteor.GameEngine.Manager;
import Meteor.Graphics.Context;
import Meteor.Graphics.Rectangle;
import Meteor.Units.Tuple2i;
import Meteor.Units.Tuple4i;

public class Bounds implements Cortex
{
    public static boolean SHOW = true; //A value for the {@code shouldDisplay}
    public static boolean HIDE = false; //A value for the {@code shouldDisplay}

    private Object object; //the parent object
    private Rectangle bounds; //The rectangular collision bounds
    private Tuple4i properties; //The properties to be attached to the collision bounds
    private Tuple2i offsets; //The (x, y) offsets to be applied to the collision bounds
    private float scale; //Scaling ratio
    private int color; //The color of the collision bounds
    private boolean shouldDisplay; //Weather or not the bounds should be seen

    /**
     * The constructor used to create the collision component.
     *
     * @param object        The object attached to this component.
     * @param bounds        The rectangular collision bounds.
     * @param properties    The properties to be attached to the collision bounds.
     * @param color         The color of the collision bounds.
     * @param shouldDisplay Weather or not the bounds should be seen.
     */
    public Bounds(Object object, Rectangle bounds, Tuple4i properties, int color, boolean shouldDisplay)
    {
        this.object = object;
        this.bounds = bounds;
        this.properties = properties;
        this.offsets = new Tuple2i();
        this.color = color;
        this.shouldDisplay = shouldDisplay;

        scale = 1.0f;
    }

    /** 
     * The constructor used to create the collision component.
     *
     * @param object        The object attached to this component.
     * @param bounds        The rectangular collision bounds.
     * @param properties    The properties to be attached to the collision bounds.
     * @param scale         Scaling ratio (1f is 1:1 ratio).
     * @param color         The color of the collision bounds.
     * @param shouldDisplay Weather or not the bounds should be seen.
     */
    public Bounds(Object object, Rectangle bounds, Tuple4i properties, float scale, int color, boolean shouldDisplay)
    {
        this.object = object;
        this.bounds = bounds;
        this.properties = properties;
        this.scale = scale;
        this.offsets = new Tuple2i();
        this.color = color;
        this.shouldDisplay = shouldDisplay;
    }

    /**
     * The constructor used to create the collision component.
     *
     * @param object        The object attached to this component.
     * @param properties    The properties to be attached to the collision bounds.
     * @param scale         Scaling ratio (1f is 1:1 ratio).
     * @param color         The color of the collision bounds.
     * @param shouldDisplay Weather or not the bounds should be seen.
     */
    public Bounds(Object object, Tuple4i properties, float scale, int color, boolean shouldDisplay)
    {
        this.object = object;
        this.properties = properties;
        this.scale = scale;
        this.offsets = new Tuple2i();
        this.color = color;
        this.shouldDisplay = shouldDisplay;

        init();
    }

    @Override
    public void init()
    {
        setBounds(properties);
    }

    /**
     * Method used to configure the rectangular collision bounds.
     *
     * @param properties The properties to be attached to the collision bounds.
     */
    public void setBounds(Tuple4i properties)
    {
        if (this.bounds == null) this.bounds = new Rectangle();
        this.bounds.x = properties.getX();
        this.bounds.y = properties.getY();
        this.bounds.width = properties.getWidth();
        this.bounds.height = properties.getHeight();
    }

    /**
     * Method used to grab the rectangular collision bounds based on a (x, y) offset.
     *
     * @param xOffset The x-offset of the object.
     * @param yOffset The y-offset of the object.
     * @return The rectangular collision bounds.
     */
    public Rectangle getBounds(int xOffset, int yOffset)
    {
        return new Rectangle(object.getX() + bounds.x + xOffset, object.getY() + bounds.y + yOffset, bounds.width * scale, bounds.height * scale);
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
        bounds.setBounds(properties.location.x, properties.location.y, properties.getWidth() * scale, properties.getHeight() * scale);
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
            ctx.renderRectangle(object.getX() + bounds.x - offsets.x, object.getY() + bounds.y - offsets.y, bounds.width, bounds.height, color);
        }
    }

    /**
     * @return The properties to be attached to the collision bounds.
     */
    public Tuple4i getProperties()
    {
        return properties;
    }

    /**
     * Method used to configure the (x, y) offsets.
     *
     * @param xOffset The x-offset of the object.
     * @param yOffset The y-offset of the object.
     */
    public void setOffsets(int xOffset, int yOffset)
    {
        offsets.x = xOffset;
        offsets.y = yOffset;
    }

    /**
     * @return The (x, y) offsets to be applied to the collision bounds.
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

    @Override
    public String toString()
    {
        return String.format("(%d - %d, %d - %d), %.2f, %.2f %.2f", bounds.x, offsets.x, bounds.y, offsets.y, bounds.width, bounds.height, scale);
    }
}
