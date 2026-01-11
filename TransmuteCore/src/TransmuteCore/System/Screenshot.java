package TransmuteCore.System;

import TransmuteCore.Graphics.Context;
import TransmuteCore.Input.Input;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.KeyEvent;

/**
 * Utility for capturing screenshots of the game.
 * <p>
 * Press F12 to capture a screenshot. Screenshots are saved to the
 * "screenshots/" directory with timestamps.
 * <p>
 * Usage:
 * <pre>
 * {@code
 * // In update():
 * Screenshot.update();
 * 
 * // In render():
 * Screenshot.capture(ctx);
 * }
 * </pre>
 */
public class Screenshot
{
    private static String outputDirectory = "screenshots";
    private static boolean captureRequested = false;
    private static boolean lastF12State = false;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Updates screenshot capture logic. Call this in your update() method.
     */
    public static void update()
    {
        // Check for F12 key press
        boolean f12Pressed = Input.isKeyPressed(KeyEvent.VK_F12);
        if (f12Pressed && !lastF12State)
        {
            captureRequested = true;
            Logger.info("Screenshot capture requested");
        }
        lastF12State = f12Pressed;
    }
    
    /**
     * Captures a screenshot if requested. Call this in your render() method
     * AFTER all rendering is complete.
     *
     * @param ctx The rendering context to capture.
     */
    public static void capture(Context ctx)
    {
        if (!captureRequested) return;
        captureRequested = false;
        
        if (ctx == null)
        {
            Logger.error("Cannot capture screenshot: context is null");
            return;
        }
        
        captureNow(ctx);
    }
    
    /**
     * Immediately captures a screenshot without waiting for F12.
     *
     * @param ctx The rendering context to capture.
     * @return True if screenshot was saved successfully.
     */
    public static boolean captureNow(Context ctx)
    {
        if (ctx == null)
        {
            Logger.error("Cannot capture screenshot: context is null");
            return false;
        }
        
        try
        {
            // Ensure output directory exists
            File dir = new File(outputDirectory);
            if (!dir.exists())
            {
                if (!dir.mkdirs())
                {
                    Logger.error("Failed to create screenshot directory: %s", outputDirectory);
                    return false;
                }
            }
            
            // Generate filename with timestamp
            String timestamp = dateFormat.format(new Date());
            String filename = String.format("screenshot_%s.png", timestamp);
            File outputFile = new File(dir, filename);
            
            // Get the image from context
            BufferedImage image = ctx.getImage();
            
            // Save as PNG
            ImageIO.write(image, "PNG", outputFile);
            
            Logger.info("Screenshot saved: %s", outputFile.getAbsolutePath());
            return true;
        }
        catch (IOException e)
        {
            Logger.error("Failed to save screenshot", e);
            return false;
        }
    }
    
    /**
     * Captures a screenshot with a custom filename.
     *
     * @param ctx      The rendering context to capture.
     * @param filename Custom filename (without extension).
     * @return True if screenshot was saved successfully.
     */
    public static boolean captureAs(Context ctx, String filename)
    {
        if (ctx == null)
        {
            Logger.error("Cannot capture screenshot: context is null");
            return false;
        }
        
        if (filename == null || filename.trim().isEmpty())
        {
            Logger.error("Cannot capture screenshot: filename is null or empty");
            return false;
        }
        
        try
        {
            // Ensure output directory exists
            File dir = new File(outputDirectory);
            if (!dir.exists())
            {
                if (!dir.mkdirs())
                {
                    Logger.error("Failed to create screenshot directory: %s", outputDirectory);
                    return false;
                }
            }
            
            // Add .png extension if not present
            if (!filename.toLowerCase().endsWith(".png"))
            {
                filename += ".png";
            }
            
            File outputFile = new File(dir, filename);
            
            // Get the image from context
            BufferedImage image = ctx.getImage();
            
            // Save as PNG
            ImageIO.write(image, "PNG", outputFile);
            
            Logger.info("Screenshot saved: %s", outputFile.getAbsolutePath());
            return true;
        }
        catch (IOException e)
        {
            Logger.error("Failed to save screenshot", e);
            return false;
        }
    }
    
    /**
     * Sets the output directory for screenshots.
     *
     * @param directory Path to the output directory.
     */
    public static void setOutputDirectory(String directory)
    {
        if (directory == null || directory.trim().isEmpty())
        {
            Logger.warn("Screenshot output directory cannot be null or empty, using default");
            return;
        }
        outputDirectory = directory;
        Logger.info("Screenshot output directory set to: %s", directory);
    }
    
    /**
     * @return The current output directory for screenshots.
     */
    public static String getOutputDirectory()
    {
        return outputDirectory;
    }
    
    /**
     * Manually request a screenshot capture on the next render.
     */
    public static void requestCapture()
    {
        captureRequested = true;
    }
    
    private Screenshot()
    {
    }
}
