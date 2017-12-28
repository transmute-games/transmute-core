package Meteor.System.Asset.Type.Audio;

import javax.sound.sampled.Clip;

import Meteor.System.Util;
import Meteor.System.Asset.AssetManager;

/**
 * {@code AudioPlayer} is a audio manager class.
 * <br>
 * This class can be used to play audio files.
 */
public class AudioPlayer
{
    private static final String CLASS_NAME = "AudioPlayer"; //The name of the class for use in error reporting
    private static boolean muteAudio = false; //The main boolean variable to turn on or off the audio

    /**
     * Method used to play a given audio file based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     */
    public static void play(String name)
    {
        if (muteAudio) return;
        Clip clip = AssetManager.getAudio(name);
        if (clip == null) return;
        int gap = 0;
        Util.log("[" + CLASS_NAME + "]: Playing [" + name + "].");
        play(clip, gap);
    }

    /**
     * Method used to play a clip file based on an inputted "gap" value.
     *
     * @param clip The audio file.
     * @param gap  The "gap" value.
     */
    private static void play(Clip clip, int gap)
    {
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(gap);
        while (!clip.isRunning())
            clip.start();
    }

    /**
     * Method used to stop playing a given audio file based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     */
    private static void stop(String name)
    {
        Clip clip = AssetManager.getAudio(name);
        if (clip == null) return;
        Util.log("[" + CLASS_NAME + "]: Stopping [" + name + "].");
        if (clip.isRunning()) clip.stop();
    }

    /**
     * Method used to resume playing a given audio file based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     */
    public static void resume(String name)
    {
        if (muteAudio) return;
        Clip clip = AssetManager.getAudio(name);
        if (clip == null) return;
        if (clip.isRunning()) return;
        Util.log("[" + CLASS_NAME + "]: Starting [" + name + "].");
        clip.start();
    }

    /**
     * Method used to loop a given audio file based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     */
    public static void loop(String name)
    {
        Clip clip = AssetManager.getAudio(name);
        stop(name);
        if (muteAudio) return;
        if (clip == null) return;
        Util.log("[" + CLASS_NAME + "]: Looping [" + name + "].");
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Method used to return the given audio file's length based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     * @return The length of the audio file.
     */
    public static int getAudioFrames(String name)
    {
        return AssetManager.getAudio(name).getFrameLength();
    }
}