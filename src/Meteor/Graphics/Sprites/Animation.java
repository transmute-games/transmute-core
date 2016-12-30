package Meteor.Graphics.Sprites;

import java.util.LinkedList;

import Meteor.Graphics.Bitmap;
import Meteor.Graphics.Color;
import Meteor.Graphics.Context;

/**
 * An animation is a series of Bitmaps played in a timed sequence.
 */
public class Animation {
	/**
	 Implementation:
	 
	 //Get the Spritesheet object
	 Spritesheet SPRITE_SHEET = ResourceLoader.SPRITE_SHEET;
	 
	 //Bitmap for each animation
	 Bitmap[] idle = {
	 	SPRITE_SHEET.crop(0, 0), SPRITE_SHEET.crop(1,0)
	 };
	 
	 Bitmap[] walkLeft = {
	 	SPRITE_SHEET.crop(2, 0), SPRITE_SHEET.crop(3,0)
	 };
	 
	 //Animation states
	 Animation idle = new Animation(idle, 1);
	 Animation walkLeft = new Animation(walkLeft, 1);
	 
	 //Actual implementation
	 Animation playerAnimation = idle;
	 
	 //In the update or tick method you will have:
	 playerAnimation.update();
	 
	 //In the render or draw method you will have:
	 playerAnimation.render(ctx, x, y, alpha, tint);
	 
	 //Changing states
	 if (Input.isKeyPressed(KeyEvent.VK_A) {
	 	playerAnimation = walkLeft;
	 }
	 
	 if (Input.isKeyReleased(KeyEvent.VK_A) {
	 	playerAnimation = playerIdle;
	 }
	 */

	/** Number of frames in the action */
	public LinkedList<Frame> frames = new LinkedList<>();

	/** Current frame being drawn */
	private int frame = -1;

	/** Is the action playing? */
	private boolean isStarted = false;

	/** Relative speed to play the action in (original time * speed factor) */
	private float speed = 1f;

	/** Frame update timer */
	private long lastUpdate;

	/** Control for looping the action (i.e. start from beginning again when completed) */
	private boolean looping = false;

	/** Control for reversing the action (i.e. start from last frame of bitmap list) */
	private boolean isReverseMode = false;

	/** Control for ping-pong mode (i.e. start from beginning but play reverse when last frame is reached, loop) */
	private boolean isPingPongMode = false;

	/** Internal logic switch to initialize timers and variables on first update */
	private boolean firstUpdate = true;

	/**
	 * Creates an empty animation.
	 */
	public Animation() {}

	/**
	 * Creates an animation, assigning each frame with a delay time.
	 * The delay times must be the same size as the number of frames.
	 * 
	 * @param animation Series of frames to be played in order.
	 * @param duration Duration in seconds for each frame.
	 */
	public Animation(Bitmap[] animation, int[] duration) {
		if(animation.length != duration.length)
			throw new IllegalArgumentException("Animation frames and delay time length mismatch!");
		for(int i = 0; i < animation.length; i++) {
			frames.add(new Frame(animation[i], duration[i] * 1000));
		}
	}

	/**
	 * Creates an animation, assigning all frames with a single delay time.
	 * 
	 * @param animation Series of frames to be played in order.
	 * @param duration Duration in seconds for each frame.
	 */
	public Animation(Bitmap[] animation, int duration) {
		for(int i = 0; i < animation.length; i++) {
			frames.add(new Frame(animation[i], duration * 1000));
		}
	}

	/**
	 * Adds a new frame to the end of the action list
	 * 
	 * @param bitmap Bitmap to be drawn.
	 * @param duration Duration in seconds of the frame.
	 * @return A new frame to the end of the action list.
	 */
	public Animation addFrame(Bitmap bitmap, int duration) {
		frames.add(new Frame(bitmap, duration * 1000));
		return this;
	}

	/**
	 * Renders the action on a given context.
	 *
	 * @param ctx Render context to be drawn on.
	 * @param x x-coordinate on screen.
	 * @param y y-coordinate on screen.
	 */
	public void render(Context ctx, int x, int y) {
		render(ctx, x, y, 1.0f);
	}

	/**
	 * Renders the action on a given context with specified transparency.
	 *
	 * @param ctx Render context to be drawn on.
	 * @param x x-coordinate on screen.
	 * @param y y-coordinate on screen.
	 * @param alpha	Alpha transparency of the action.
	 */
	public void render(Context ctx, int x, int y, float alpha) {
		render(ctx, x, y, alpha, Color.toPixelInt(0, 0, 0, 0));
	}

	/**
	 * Renders the action on a given context with specified transparency and tint color.
	 *
	 * @param ctx Render context to be drawn on.
	 * @param x x-coordinate on screen.
	 * @param y y-coordinate on screen.
	 * @param alpha	Alpha transparency of the action.
	 * @param tint Tint color of the action.
	 */
	public void render(Context ctx, int x, int y, float alpha, int tint) {
		update();
		Frame f = frames.get(frame);
		ctx.renderBitmap(f.bmp, x, y, alpha, tint);
	}

	/**
	 * Updates action timers and frame information.
	 */
	private void update() {
		if(firstUpdate && !isStarted) {
			start();
			firstUpdate = false;
		}

		if(isStarted) {
			Frame f = frames.get(frame);
			int delay = (int) ((float) f.duration * speed);
			if(System.currentTimeMillis() - lastUpdate > delay) {
				if(isReverseMode) {
					frame--;
					if(frame < 0) {
						if(isPingPongMode) {
							setReverseMode(false);
							frame++;
						} else if(looping) {
							frame = frames.size() - 1;
						} else {
							frame = 0;
							isStarted = false;
						}
					}
				} else {
					frame++;
					if(frame > frames.size() - 1) {
						if(isPingPongMode) {
							setReverseMode(true);
							frame--;
						} else if(looping) {
							frame = 0;
						} else {
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
	public Bitmap getBitmap() {
		return frames.get(frame).bmp;
	}

	/**
	 * Flips all frames within the animation on one or more axis.
	 * 
	 * @param horizontal Flip all frames according to a horizontal axis.
	 * @param vertical Flip all frames according to a vertical axis.
	 */
	public void flipFrames(boolean horizontal, boolean vertical) {
		for(int i = 0; i < frames.size(); i++) {
			frames.get(i).bmp = frames.get(i).bmp.getFlipped(horizontal, vertical);
		}
	}
	
	/**
	 * Grabs the mirrored/flipped version of the current frame on one or more axis.
	 * 
	 * @param horizontal Flip all frames according to a horizontal axis.
	 * @param vertical Flip all frames according to a vertical axis.
	 * @return The mirrored version of the current frame.
	 */
	public Bitmap getMirror(boolean horizontal, boolean vertical) {
		return getBitmap().getFlipped(horizontal, vertical);
	}
	
	/**
	 * Grabs the mirrored/flipped version of the current frame on the vertical axis.
	 * 
	 * @return The mirrored version of the current frame.
	 */
	public Bitmap getMirror() {
		return getBitmap().getFlipped(false, true);
	}

	/**
	 * Supplies the play speed of the animation. 
	 * 1f means normal speed, while 0.5f denotes half speed and 2.0f denotes 2x speed.
	 * 
	 * @return Play speed of the animation.
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Changes the animation play speed. 1.0f is 1x speed.
	 * 
	 * @param speed Play speed of the animation.
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Supplies the flag that denotes if the animation loops after finished playing.
	 * 
	 * @return Whether if animation loops after stopped playing.
	 */
	public boolean isLooping() {
		return looping;
	}

	/**
	 * Sets whether the animation will loop after finished playing once. 
	 * 
	 * @param looping Whether animation loops after stopped playing once.
	 */
	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	/**
	 * Supplies whether the animation plays backwards.
	 * 
	 * @return Whether if animation plays backwards.
	 */
	public boolean isReverseMode() {
		return isReverseMode;
	}

	/**
	 * Sets whether animation will play backwards. 
	 * 
	 * @param reverseMode Flag that denotes backwards animation.
	 */
	public void setReverseMode(boolean reverseMode) {
		this.isReverseMode = reverseMode;
	}

	/**
	 * Determines whether the animation is in ping-pong mode.
	 * Ping-pong mode animations will alter between reverse animation
	 * and normal animation each time after completing the animation sequence. 
	 * 
	 * @return Whether animation is in ping pong mode.
	 */
	public boolean isPingpongMode() {
		return isPingPongMode;
	}

	/**
	 * Sets whether animation will play in ping-pong mode.
	 * 
	 * @param pingpongMode Flag that denotes ping-pong mode.
	 */
	public void setPingpongMode(boolean pingpongMode) {
		this.isPingPongMode = pingpongMode;
	}

	/**
	 * Begin playing action.
	 */
	public void start() {
		if(!isStarted) {
			restart();
		}
	}

	/**
	 * Resets action timer to current time and frame to 0.
	 * Plays the action again if stopped.
	 */
	public void restart() {
		frame = 0;
		lastUpdate = System.currentTimeMillis();
		isStarted = true;
	}

	/**
	 * Stops playing action.
	 */
	public void stop() {
		if(isStarted) {
			lastUpdate = System.currentTimeMillis();
		}
	}

	/**
	 * Supplies the play state of the animation.
	 * 
	 * @return Play state of the animation. 
	 */
	public boolean hasStopped() {
		return !isStarted;
	}
	
	/**
	 * Method used to scale each frame of the animation.
	 * 
	 * @param scale Scaling ratio. 
	 */
	public void setScale(float scale) {
		for (int i = 0; i < frames.size(); i++) frames.get(i).bmp = frames.get(i).bmp.getScaled(scale);
	}

	/**
	 * A frame struct that represents a Bitmap with a given duration time (in seconds).
	 */
	class Frame {

		private Bitmap bmp;
		private int duration;

		private Frame(Bitmap bmp, int duration) {
			this.bmp = bmp;
			this.duration = duration;
		}

	}

}