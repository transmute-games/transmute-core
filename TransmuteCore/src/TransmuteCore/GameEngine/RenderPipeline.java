package TransmuteCore.GameEngine;

import TransmuteCore.GameEngine.Interfaces.Cortex;
import TransmuteCore.GameEngine.Interfaces.Services.IGameWindow;
import TransmuteCore.GameEngine.Interfaces.Services.IRenderer;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

/**
 * Handles the rendering pipeline with hardware acceleration.
 * Uses VolatileImage for hardware-accelerated rendering and manages buffer strategies.
 */
public class RenderPipeline
{
    private final Cortex cortex;
    private final IRenderer renderer;
    private final IGameWindow gameWindow;
    private final ManagerProvider managerProvider;
    private final int numBuffers;
    private final int scaledWidth;
    private final int scaledHeight;
    
    /**
     * Functional interface for lazy Manager access.
     */
    @FunctionalInterface
    public interface ManagerProvider {
        Manager getManager();
    }
    
    private VolatileImage nativeImage;
    
    /**
     * Creates a new render pipeline.
     *
     * @param cortex            The game's cortex implementation
     * @param renderer          The renderer (Context)
     * @param gameWindow        The game window
     * @param managerProvider   Provider for lazy Manager access (for backward compatibility)
     * @param numBuffers        Number of buffers for buffer strategy
     * @param width             Unscaled width
     * @param height            Unscaled height
     * @param scale             Scale factor
     */
    public RenderPipeline(Cortex cortex, IRenderer renderer, IGameWindow gameWindow, 
                         ManagerProvider managerProvider, int numBuffers, int width, int height, int scale)
    {
        this.cortex = cortex;
        this.renderer = renderer;
        this.gameWindow = gameWindow;
        this.managerProvider = managerProvider;
        this.numBuffers = numBuffers;
        this.scaledWidth = width * scale;
        this.scaledHeight = height * scale;
    }
    
    /**
     * Executes the render pipeline: clears context, renders game, composites to window.
     */
    public void render()
    {
        int ctxWidth = renderer.getWidth();
        int ctxHeight = renderer.getHeight();
        
        // Create or recreate volatile image if needed
        if (nativeImage == null || nativeImage.contentsLost())
        {
            nativeImage = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration()
                    .createCompatibleVolatileImage(ctxWidth, ctxHeight, VolatileImage.TRANSLUCENT);
        }
        
        BufferStrategy bs = gameWindow.getBufferStrategy();
        if (bs == null)
        {
            gameWindow.getCanvas().createBufferStrategy(numBuffers);
            gameWindow.getCanvas().requestFocus();
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        
        // Clear and render to context
        renderer.clear();
        cortex.render(managerProvider.getManager(), renderer);
        
        // Composite context to hardware-accelerated volatile image
        Graphics2D volatileGraphics = nativeImage.createGraphics();
        volatileGraphics.drawImage(renderer.getImage(), 0, 0, null);
        volatileGraphics.dispose();
        
        // Scale and draw to window
        g.drawImage(nativeImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        bs.show();
    }
    
    /**
     * Cleans up resources used by the render pipeline.
     */
    public void cleanUp()
    {
        if (nativeImage != null)
        {
            nativeImage.flush();
            nativeImage = null;
        }
    }
}
