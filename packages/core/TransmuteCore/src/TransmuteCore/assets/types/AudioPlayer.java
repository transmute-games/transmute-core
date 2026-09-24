package TransmuteCore.assets.types;

import javax.sound.sampled.Clip;

import TransmuteCore.util.Logger;
import TransmuteCore.util.verify.AudioProbe;
import TransmuteCore.assets.AssetManager;

/**
 * {@code AudioPlayer} is a audio manager class.
 * <br>
 * This class can be used to play audio files.
 * Play/loop/stop/resume are recorded by an installed {@link AudioProbe} even when muted,
 * so headless verify can assert cues without a sound device.
 */
public class AudioPlayer
{
    private static final String CLASS_NAME = "AudioPlayer"; //The name of the class for use in error reporting
    private static boolean muteAudio = false; //The main boolean variable to turn on or off the audio

    /**
     * When true, clips are not started (probe recording still happens).
     */
    public static void setMuted(boolean muted)
    {
        muteAudio = muted;
    }

    public static boolean isMuted()
    {
        return muteAudio;
    }

    /**
     * Method used to play a given audio file based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     */
    public static void play(String name)
    {
        AudioProbe.record(AudioProbe.Kind.PLAY, name);
        if (muteAudio) return;
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) return;
        Clip clip = assetManager.getAudio(name);
        if (clip == null) return;
        int gap = 0;
        Logger.debug("[%s]: Playing [%s]", CLASS_NAME, name);
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
     * Stops a named clip if it is running.
     *
     * @param name The name attached to the audio file.
     */
    public static void stop(String name)
    {
        AudioProbe.record(AudioProbe.Kind.STOP, name);
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) return;
        Clip clip = assetManager.getAudio(name);
        if (clip == null) return;
        Logger.debug("[%s]: Stopping [%s]", CLASS_NAME, name);
        if (clip.isRunning()) clip.stop();
    }

    /**
     * Method used to resume playing a given audio file based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     */
    public static void resume(String name)
    {
        AudioProbe.record(AudioProbe.Kind.RESUME, name);
        if (muteAudio) return;
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) return;
        Clip clip = assetManager.getAudio(name);
        if (clip == null) return;
        if (clip.isRunning()) return;
        Logger.debug("[%s]: Starting [%s]", CLASS_NAME, name);
        clip.start();
    }

    /**
     * Method used to loop a given audio file based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     */
    public static void loop(String name)
    {
        AudioProbe.record(AudioProbe.Kind.LOOP, name);
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) return;
        Clip clip = assetManager.getAudio(name);
        stopInternal(name, clip);
        if (muteAudio) return;
        if (clip == null) return;
        Logger.debug("[%s]: Looping [%s]", CLASS_NAME, name);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private static void stopInternal(String name, Clip clip)
    {
        if (clip == null) return;
        Logger.debug("[%s]: Stopping [%s]", CLASS_NAME, name);
        if (clip.isRunning()) clip.stop();
    }

    /**
     * Method used to return the given audio file's length based on an inputted string value.
     *
     * @param name The name attached to the audio file.
     * @return The length of the audio file.
     */
    public static int getAudioFrames(String name)
    {
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) return 0;
        return assetManager.getAudio(name).getFrameLength();
    }
}