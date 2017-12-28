package Meteor.Graphics.Sprites;

import java.util.*;

import Meteor.Graphics.Bitmap;
import Meteor.Graphics.Color;
import Meteor.Graphics.Context;
import Meteor.System.Asset.Type.Images.Image;

/**
 * An animation is a series of Bitmaps played in a timed sequence.
 */
public class Animation
{
    /**
     * Number of frames in the action
     */
    public LinkedList<Frame> frames = new LinkedList<>();

    /**
     * Current frame being drawn
     */
    private int frame = -1;

    /**
     * Is the action playing?
     */
    private boolean isStarted = false;

    /**
     * Relative speed to play the action in (original time * speed factor)
     */
    private float speed = 1f;

    /**
     * Frame update timer
     */
    private long lastUpdate;

    /**
     * Control for looping the action (i.e. tile from beginning again when completed)
     */
    private boolean looping = false;

    /**
     * Control for reversing the action (i.e. tile from last frame of bitmap list)
     */
    private boolean isReverseMode = false;

    /**
     * Control for ping-pong mode (i.e. tile from beginning but play reverse when last frame is reached, loop)
     */
    private boolean isPingPongMode = false;

    /**
     * Internal logic switch to initialize timers and variables on first update
     */
    private boolean firstUpdate = true;

    /**
     * The name of the animation
     */
    private String name;

    /**
     * Creates an empty animation.
     */
    public Animation()
    {
    }

    /**
     * Creates an animation, assigning each frame with a delay time.
     * The delay times must be the same size as the number of frames.
     *
     * @param animation Series of frames to be played in order.
     * @param duration  Duration in seconds for each frame.
     */
    public Animation(String name, Sprite[] animation, int[] duration)
    {
        this.name = name;

        if (animation.length != duration.length)
            throw new IllegalArgumentException("Animation frames and delay time length mismatch!");
        for (int i = 0; i < animation.length; i++)
        {
            frames.add(new Frame(animation[i], duration[i] * 1000));
        }
    }

    /**
     * Creates an animation, assigning all frames with a single delay time.
     *
     * @param animation Series of frames to be played in order.
     * @param duration  Duration in seconds for each frame.
     */
    public Animation(String name, Sprite[] animation, int duration)
    {
        this.name = name;

        for (int i = 0; i < animation.length; i++)
        {
            frames.add(new Frame(animation[i], duration * 1000));
        }
    }

    /**
     * Adds a new frame to the end of the action list
     *
     * @param sprite   Sprite to be drawn.
     * @param duration Duration in seconds of the frame.
     * @return A new frame to the end of the action list.
     */
    public Animation addFrame(Sprite sprite, int duration)
    {
        frames.add(new Frame(sprite, duration * 1000));
        return this;
    }

    /**
     * Renders the action on a given context.
     *
     * @param ctx Render context to be drawn on.
     * @param x   x-coordinate on screen.
     * @param y   y-coordinate on screen.
     */
    public void render(Context ctx, int x, int y)
    {
        render(ctx, x, y, 1.0f);
    }

    /**
     * Renders the action on a given context with specified transparency.
     *
     * @param ctx   Render context to be drawn on.
     * @param x     x-coordinate on screen.
     * @param y     y-coordinate on screen.
     * @param alpha Alpha transparency of the action.
     */
    public void render(Context ctx, int x, int y, float alpha)
    {
        render(ctx, x, y, alpha, Color.toPixelInt(0, 0, 0, 0));
    }

    /**
     * Renders the action on a given context with specified transparency and tint color.
     *
     * @param ctx   Render context to be drawn on.
     * @param x     x-coordinate on screen.
     * @param y     y-coordinate on screen.
     * @param alpha Alpha transparency of the action.
     * @param tint  Tint color of the action.
     */
    public void render(Context ctx, int x, int y, float alpha, int tint)
    {
        Frame f = frames.get(frame);
        ctx.renderBitmap(f.sprite.bitmap, x, y, alpha, tint);
    }

    /**
     * Updates action timers and frame information.
     */
    public void update()
    {
        if (firstUpdate && !isStarted)
        {
            start();
            firstUpdate = false;
        }

        if (isStarted)
        {
            Frame f = frames.get(frame);
            int delay = (int) ((float) f.duration * speed);
            if (System.currentTimeMillis() - lastUpdate > delay)
            {
                if (isReverseMode)
                {
                    frame--;
                    if (frame < 0)
                    {
                        if (isPingPongMode)
                        {
                            setReverseMode(false);
                            frame++;
                        } else if (looping)
                        {
                            frame = frames.size() - 1;
                        } else
                        {
                            frame = 0;
                            isStarted = false;
                        }
                    }
                } else
                {
                    frame++;
                    if (frame > frames.size() - 1)
                    {
                        if (isPingPongMode)
                        {
                            setReverseMode(true);
                            frame--;
                        } else if (looping)
                        {
                            frame = 0;
                        } else
                        {
                            frame = frames.size() - 1;
                            isStarted = false;
                        }
                    }
                }
                lastUpdate = System.currentTimeMillis();
            }
        }
    }

    /**
     * @return The bitmap of the current frame.
     */
    public Bitmap getBitmap()
    {
        return frames.get(frame).sprite.bitmap;
    }

    /**
     * @return The sprite of the current frame.
     */
    public Sprite getSprite()
    {
        return frames.get(frame).sprite;
    }

    /**
     * Flips all frames within the animation on one or more axis.
     *
     * @param horizontal Flip all frames according to a horizontal axis.
     * @param vertical   Flip all frames according to a vertical axis.
     */
    public void flipFrames(boolean horizontal, boolean vertical)
    {
        for (int i = 0; i < frames.size(); i++)
        {
            frames.get(i).sprite.bitmap = frames.get(i).sprite.bitmap.getFlipped(horizontal, vertical);
        }
    }

    /**
     * Grabs the mirrored/flipped version of the current frame on one or more axis.
     *
     * @param horizontal Flip all frames according to a horizontal axis.
     * @param vertical   Flip all frames according to a vertical axis.
     * @return The mirrored version of the current frame.
     */
    public Bitmap getMirror(boolean horizontal, boolean vertical)
    {
        return getBitmap().getFlipped(horizontal, vertical);
    }

    /**
     * Flips each frame of a given animation, as well as reverses each frames' order
     * in the animation sequence (e.g. frames[0] -(becomes)-> frames[frames.length - 1])
     * if specified.
     *
     * @param horizontal Flip all frames according to a horizontal axis.
     * @param vertical   Flip all frames according to a vertical axis.
     * @param isReversed Weather or not the animation sequence should be reversed.
     * @return The animation with each frame of a given animation mirrored,
     * as well as reverses each frames' order in the animation sequence.
     */
    public Animation getMirror(String name, boolean horizontal, boolean vertical, boolean isReversed)
    {
        if (!horizontal && !vertical && !isReversed) return this;

        Sprite[] animation = new Sprite[frames.size()];
        int duration = frames.getFirst().duration;

        for (int i = 0; i < frames.size(); i++)
        {
            Bitmap original = frames.get(i).sprite.bitmap;

            if (original == null)
            {
                original = Image.getAsBitmap(frames.get(i).sprite.getImage());
            }

            original = Image.getAsBitmap(Image.getFlipped(original.getImage(), horizontal, vertical));
            Sprite mirror = new Sprite(original);
            animation[i] = mirror;
        }

        if (isReversed)
        {
            animation = getReverseOrderOf(animation);
        }

        return new Animation(name, animation, duration);
    }

    /**
     * Reverses the order of the indices in the array of Bitmap frames.
     * e.g. frames[0] -(becomes)-> frames[frames.length - 1]
     *
     * @param animation The array of bitmap animation frames.
     * @return Reversed order of animation frames.
     */
    private Sprite[] getReverseOrderOf(Sprite[] animation)
    {
        Sprite[] result = new Sprite[animation.length];
        List<Sprite> list = Arrays.asList(animation);
        Collections.reverse(list);

        for (int i = 0; i < list.size(); i++)
        {
            result[i] = list.get(i);
        }

        return result;
    }

    /**
     * Grabs the mirrored/flipped version of the current frame on the vertical axis.
     *
     * @return The mirrored version of the current frame.
     */
    public Sprite getMirror()
    {
        return new Sprite(getBitmap().getFlipped(false, true));
    }

    /**
     * Supplies the play speed of the animation.
     * 1f means normal speed, while 0.5f denotes half speed and 2.0f denotes 2x speed.
     *
     * @return Play speed of the animation.
     */
    public float getSpeed()
    {
        return speed;
    }

    /**
     * Changes the animation play speed. 1.0f is 1x speed.
     *
     * @param speed Play speed of the animation.
     */
    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    /**
     * Supplies the flag that denotes if the animation loops after finished playing.
     *
     * @return Whether if animation loops after stopped playing.
     */
    public boolean isLooping()
    {
        return looping;
    }

    /**
     * Sets whether the animation will loop after finished playing once.
     *
     * @param looping Whether animation loops after stopped playing once.
     */
    public void setLooping(boolean looping)
    {
        this.looping = looping;
    }

    /**
     * Supplies whether the animation plays backwards.
     *
     * @return Whether if animation plays backwards.
     */
    public boolean isReverseMode()
    {
        return isReverseMode;
    }

    /**
     * Sets whether animation will play backwards.
     *
     * @param reverseMode Flag that denotes backwards animation.
     */
    public void setReverseMode(boolean reverseMode)
    {
        this.isReverseMode = reverseMode;
    }

    /**
     * Determines whether the animation is in ping-pong mode.
     * Ping-pong mode animations will alter between reverse animation
     * and normal animation each time after completing the animation sequence.
     *
     * @return Whether animation is in ping pong mode.
     */
    public boolean isPingpongMode()
    {
        return isPingPongMode;
    }

    /**
     * Sets whether animation will play in ping-pong mode.
     *
     * @param pingpongMode Flag that denotes ping-pong mode.
     */
    public void setPingpongMode(boolean pingpongMode)
    {
        this.isPingPongMode = pingpongMode;
    }

    /**
     * Begin playing action.
     */
    public void start()
    {
        if (!isStarted)
        {
            restart();
        }
    }

    /**
     * Resets action timer to current time and frame to 0.
     * Plays the action again if stopped.
     */
    public void restart()
    {
        frame = 0;
        lastUpdate = System.currentTimeMillis();
        isStarted = true;
    }

    /**
     * Stops playing action.
     */
    public void stop()
    {
        if (isStarted)
        {
            lastUpdate = System.currentTimeMillis();
        }
    }

    /**
     * Supplies the play state of the animation.
     *
     * @return Play state of the animation.
     */
    public boolean hasStopped()
    {
        return !isStarted;
    }

    /**
     * Method used to scale each frame of the animation.
     *
     * @param scale Scaling ratio.
     */
    public void setScale(float scale)
    {
        for (int i = 0; i < frames.size(); i++) frames.get(i).sprite.bitmap = frames.get(i).sprite.bitmap.getScaled(scale);
    }

    /**
     * @return The name of the animation.
     */
    public String getName()
    {
        return name;
    }

}