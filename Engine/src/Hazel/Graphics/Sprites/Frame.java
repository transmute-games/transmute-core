package Hazel.Graphics.Sprites;

/**
 * A frame class that represents a Sprite with a given duration time (in seconds).
 */
public class Frame
{
    public Sprite sprite; //The sprite of this frame
    public int duration; //The duration of the frame

    /**
     * Creates a frame with a given sprite and duration.
     *
     * @param sprite   The sprite associated with this frame.
     * @param duration The duration of the frame
     */
    public Frame(Sprite sprite, int duration)
    {
        this.sprite = sprite;
        this.duration = duration;
    }
}
