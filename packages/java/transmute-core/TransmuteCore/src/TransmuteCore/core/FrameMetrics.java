package TransmuteCore.core;

/**
 * Contains performance metrics for the game loop.
 * <p>
 * Tracks frame rate, update rate, and timing information to help
 * identify performance bottlenecks.
 * <p>
 * All timing values are in milliseconds unless otherwise noted.
 */
public class FrameMetrics
{
    private int currentFPS;
    private int currentUPS; // Updates per second
    private double averageUpdateTime;
    private double averageRenderTime;
    private double averageFrameTime;
    private long totalFrames;
    private long totalUpdates;
    
    // Running totals for averages
    private double updateTimeSum;
    private double renderTimeSum;
    private double frameTimeSum;
    private int sampleCount;
    
    /**
     * Creates a new FrameMetrics instance.
     */
    public FrameMetrics()
    {
        reset();
    }
    
    /**
     * Resets all metrics to zero.
     */
    public void reset()
    {
        this.currentFPS = 0;
        this.currentUPS = 0;
        this.averageUpdateTime = 0.0;
        this.averageRenderTime = 0.0;
        this.averageFrameTime = 0.0;
        this.totalFrames = 0;
        this.totalUpdates = 0;
        this.updateTimeSum = 0.0;
        this.renderTimeSum = 0.0;
        this.frameTimeSum = 0.0;
        this.sampleCount = 0;
    }
    
    /**
     * Updates FPS and UPS counters (called once per second).
     *
     * @param fps Current frames per second
     * @param ups Current updates per second
     */
    void updateRates(int fps, int ups)
    {
        this.currentFPS = fps;
        this.currentUPS = ups;
    }
    
    /**
     * Records an update timing sample.
     *
     * @param timeMs Time taken for update in milliseconds
     */
    void recordUpdateTime(double timeMs)
    {
        updateTimeSum += timeMs;
        sampleCount++;
        totalUpdates++;
        
        // Recalculate average every 60 samples to avoid overflow
        if (sampleCount >= 60)
        {
            averageUpdateTime = updateTimeSum / sampleCount;
            updateTimeSum = 0;
            sampleCount = 0;
        }
    }
    
    /**
     * Records a render timing sample.
     *
     * @param timeMs Time taken for render in milliseconds
     */
    void recordRenderTime(double timeMs)
    {
        renderTimeSum += timeMs;
        totalFrames++;
        
        // Update average render time
        if (totalFrames % 60 == 0)
        {
            averageRenderTime = renderTimeSum / 60;
            renderTimeSum = 0;
        }
    }
    
    /**
     * Records a complete frame timing sample.
     *
     * @param timeMs Time taken for entire frame in milliseconds
     */
    void recordFrameTime(double timeMs)
    {
        frameTimeSum += timeMs;
        
        // Update average frame time
        if (totalFrames % 60 == 0 && totalFrames > 0)
        {
            averageFrameTime = frameTimeSum / 60;
            frameTimeSum = 0;
        }
    }
    
    /**
     * @return Current frames per second.
     */
    public int getCurrentFPS()
    {
        return currentFPS;
    }
    
    /**
     * @return Current updates per second.
     */
    public int getCurrentUPS()
    {
        return currentUPS;
    }
    
    /**
     * @return Average time taken for update in milliseconds.
     */
    public double getAverageUpdateTime()
    {
        return averageUpdateTime;
    }
    
    /**
     * @return Average time taken for render in milliseconds.
     */
    public double getAverageRenderTime()
    {
        return averageRenderTime;
    }
    
    /**
     * @return Average time for complete frame in milliseconds.
     */
    public double getAverageFrameTime()
    {
        return averageFrameTime;
    }
    
    /**
     * @return Total number of frames rendered.
     */
    public long getTotalFrames()
    {
        return totalFrames;
    }
    
    /**
     * @return Total number of updates performed.
     */
    public long getTotalUpdates()
    {
        return totalUpdates;
    }
    
    @Override
    public String toString()
    {
        return String.format(
            "FrameMetrics{fps=%d, ups=%d, avgUpdate=%.2fms, avgRender=%.2fms, avgFrame=%.2fms, totalFrames=%d, totalUpdates=%d}",
            currentFPS, currentUPS, averageUpdateTime, averageRenderTime, averageFrameTime, totalFrames, totalUpdates
        );
    }
}
