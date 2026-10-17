package TransmuteCore.util.Debug;

import TransmuteCore.core.Manager;
import TransmuteCore.graphics.Context;
import TransmuteCore.input.Input;
import TransmuteCore.assets.AssetManager;
import TransmuteCore.assets.types.Font;

import java.awt.event.KeyEvent;

/**
 * Debug overlay system for displaying performance metrics and game state.
 * <p>
 * Toggle with F3 key. Displays FPS, memory usage, entity count, and more.
 * <p>
 * Usage:
 * <pre>
 * {@code
 * // In init():
 * debugOverlay = new DebugOverlay();
 * debugOverlay.setFont(AssetManager.getDefaultFont());
 * 
 * // In update():
 * debugOverlay.update(manager, delta);
 * 
 * // In render() - AFTER game rendering:
 * debugOverlay.render(manager, ctx);
 * }
 * </pre>
 */
public class DebugOverlay
{
    private DebugStats stats;
    private DebugRenderer renderer;
    private boolean enabled = false;
    private boolean lastF3State = false;
    
    private static final int OVERLAY_X = 4;
    private static final int OVERLAY_Y = 4;
    
    /**
     * Creates a new DebugOverlay instance.
     */
    public DebugOverlay()
    {
        this.stats = new DebugStats();
    }
    
    /**
     * Updates the debug overlay statistics.
     *
     * @param manager The game manager.
     * @param delta   Time elapsed since last frame.
     */
    public void update(Manager manager, double delta)
    {
        Input input = manager != null ? manager.getInput() : null;
        if (input == null) return;
        
        // Toggle with F3
        boolean f3Pressed = input.isKeyPressed(KeyEvent.VK_F3);
        if (f3Pressed && !lastF3State)
        {
            enabled = !enabled;
        }
        lastF3State = f3Pressed;
        
        if (!enabled) return;
        
        // Update statistics
        stats.updateFPS();
        stats.updateFrameTime();
        stats.updateMemory();
        
        // Count entities if ObjectManager is available
        if (manager.getStateManager() != null && 
            manager.getStateManager().peek() != null)
        {
            // Entity count can be set manually by the game
            // stats.setEntityCount(count);
        }
    }
    
    /**
     * Renders the debug overlay.
     *
     * @param manager The game manager.
     * @param ctx     The rendering context.
     */
    public void render(Manager manager, Context ctx)
    {
        if (!enabled || renderer == null) return;
        
        renderer.beginPanel(ctx, OVERLAY_X, OVERLAY_Y);
        
        // Performance Section
        renderer.renderSection(ctx, OVERLAY_X, "PERFORMANCE");
        
        // FPS with color coding
        int currentFPS = stats.getCurrentFPS();
        int targetFPS = stats.getTargetFPS();
        boolean fpsGood = currentFPS >= targetFPS * 0.9; // Within 90% of target
        renderer.renderValue(ctx, OVERLAY_X, "FPS", String.valueOf(currentFPS), fpsGood);
        renderer.renderValue(ctx, OVERLAY_X, "Target", String.valueOf(targetFPS));
        renderer.renderValue(ctx, OVERLAY_X, "Avg", String.valueOf(stats.getAverageFPS()));
        renderer.renderValue(ctx, OVERLAY_X, "Min/Max", 
            stats.getMinFPS() + "/" + stats.getMaxFPS());
        
        // Frame time
        renderer.renderThresholdValue(ctx, OVERLAY_X, "Frame Time (ms)", 
            stats.getCurrentFrameTime(), 16.0, 20.0, false);
        
        renderer.addSpacing(2);
        
        // Memory Section
        renderer.renderSection(ctx, OVERLAY_X, "MEMORY");
        renderer.renderValue(ctx, OVERLAY_X, "Used", 
            String.format("%.1f MB", stats.getUsedMemoryMB()));
        renderer.renderValue(ctx, OVERLAY_X, "Total", 
            String.format("%.1f MB", stats.getTotalMemoryMB()));
        renderer.renderValue(ctx, OVERLAY_X, "Max", 
            String.format("%.1f MB", stats.getMaxMemoryMB()));
        
        int memPercent = stats.getMemoryPercentage();
        boolean memGood = memPercent < 80;
        renderer.renderValue(ctx, OVERLAY_X, "Usage", memPercent + "%", memGood);
        
        renderer.addSpacing(2);
        
        // Game State Section
        renderer.renderSection(ctx, OVERLAY_X, "GAME");
        renderer.renderValue(ctx, OVERLAY_X, "Entities", 
            String.valueOf(stats.getEntityCount()));
        
        if (manager.getStateManager() != null && 
            manager.getStateManager().peek() != null)
        {
            String stateName = manager.getStateManager().peek().getClass().getSimpleName();
            renderer.renderValue(ctx, OVERLAY_X, "State", stateName);
        }
        
        renderer.addSpacing(2);
        renderer.renderLine(ctx, OVERLAY_X, "Press F3 to toggle");
    }
    
    /**
     * Sets the font to use for rendering the overlay.
     *
     * @param font The font to use.
     */
    public void setFont(Font font)
    {
        if (font != null)
        {
            this.renderer = new DebugRenderer(font);
        }
    }
    
    /**
     * Sets the target FPS for comparison.
     *
     * @param fps Target FPS.
     */
    public void setTargetFPS(int fps)
    {
        stats.setTargetFPS(fps);
    }
    
    /**
     * Sets the current entity count.
     *
     * @param count Number of active entities.
     */
    public void setEntityCount(int count)
    {
        stats.setEntityCount(count);
    }
    
    /**
     * Enables or disables the debug overlay.
     *
     * @param enabled Whether to show the overlay.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    /**
     * @return Whether the overlay is currently enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }
    
    /**
     * @return The debug statistics object.
     */
    public DebugStats getStats()
    {
        return stats;
    }
}
