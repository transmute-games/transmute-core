package TransmuteCore.System.Debug;

/**
 * Tracks and calculates performance statistics for the debug overlay.
 * Monitors FPS, memory usage, frame times, and other metrics.
 */
public class DebugStats
{
    private static final int HISTORY_SIZE = 60;
    
    // FPS tracking
    private int currentFPS = 0;
    private int targetFPS = 60;
    private long lastFPSUpdate = System.currentTimeMillis();
    private int frameCount = 0;
    private int[] fpsHistory = new int[HISTORY_SIZE];
    private int historyIndex = 0;
    
    // Frame time tracking
    private long lastFrameTime = System.nanoTime();
    private double currentFrameTime = 0.0;
    private double avgFrameTime = 0.0;
    private double minFrameTime = Double.MAX_VALUE;
    private double maxFrameTime = 0.0;
    
    // Memory tracking
    private long usedMemory = 0;
    private long totalMemory = 0;
    private long maxMemory = 0;
    
    // Entity/Object tracking
    private int entityCount = 0;
    
    /**
     * Updates FPS calculation. Should be called once per frame.
     */
    public void updateFPS()
    {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastFPSUpdate >= 1000)
        {
            currentFPS = frameCount;
            fpsHistory[historyIndex] = currentFPS;
            historyIndex = (historyIndex + 1) % HISTORY_SIZE;
            
            frameCount = 0;
            lastFPSUpdate = currentTime;
        }
    }
    
    /**
     * Updates frame time statistics. Should be called once per frame.
     */
    public void updateFrameTime()
    {
        long currentTime = System.nanoTime();
        currentFrameTime = (currentTime - lastFrameTime) / 1_000_000.0; // Convert to milliseconds
        lastFrameTime = currentTime;
        
        // Update min/max
        if (currentFrameTime < minFrameTime) minFrameTime = currentFrameTime;
        if (currentFrameTime > maxFrameTime) maxFrameTime = currentFrameTime;
        
        // Calculate moving average
        avgFrameTime = avgFrameTime * 0.95 + currentFrameTime * 0.05;
    }
    
    /**
     * Updates memory usage statistics.
     */
    public void updateMemory()
    {
        Runtime runtime = Runtime.getRuntime();
        totalMemory = runtime.totalMemory();
        usedMemory = totalMemory - runtime.freeMemory();
        maxMemory = runtime.maxMemory();
    }
    
    /**
     * Sets the target FPS for comparison.
     *
     * @param fps Target FPS value.
     */
    public void setTargetFPS(int fps)
    {
        this.targetFPS = fps;
    }
    
    /**
     * Sets the current entity count.
     *
     * @param count Number of active entities.
     */
    public void setEntityCount(int count)
    {
        this.entityCount = count;
    }
    
    /**
     * Resets min/max frame time statistics.
     */
    public void resetFrameTimeStats()
    {
        minFrameTime = Double.MAX_VALUE;
        maxFrameTime = 0.0;
    }
    
    /**
     * @return Current FPS.
     */
    public int getCurrentFPS()
    {
        return currentFPS;
    }
    
    /**
     * @return Target FPS.
     */
    public int getTargetFPS()
    {
        return targetFPS;
    }
    
    /**
     * @return Average FPS over the last second.
     */
    public int getAverageFPS()
    {
        int sum = 0;
        int count = 0;
        for (int fps : fpsHistory)
        {
            if (fps > 0)
            {
                sum += fps;
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }
    
    /**
     * @return Minimum FPS in history.
     */
    public int getMinFPS()
    {
        int min = Integer.MAX_VALUE;
        for (int fps : fpsHistory)
        {
            if (fps > 0 && fps < min) min = fps;
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }
    
    /**
     * @return Maximum FPS in history.
     */
    public int getMaxFPS()
    {
        int max = 0;
        for (int fps : fpsHistory)
        {
            if (fps > max) max = fps;
        }
        return max;
    }
    
    /**
     * @return Current frame time in milliseconds.
     */
    public double getCurrentFrameTime()
    {
        return currentFrameTime;
    }
    
    /**
     * @return Average frame time in milliseconds.
     */
    public double getAverageFrameTime()
    {
        return avgFrameTime;
    }
    
    /**
     * @return Minimum frame time in milliseconds.
     */
    public double getMinFrameTime()
    {
        return minFrameTime == Double.MAX_VALUE ? 0.0 : minFrameTime;
    }
    
    /**
     * @return Maximum frame time in milliseconds.
     */
    public double getMaxFrameTime()
    {
        return maxFrameTime;
    }
    
    /**
     * @return Used memory in bytes.
     */
    public long getUsedMemory()
    {
        return usedMemory;
    }
    
    /**
     * @return Total allocated memory in bytes.
     */
    public long getTotalMemory()
    {
        return totalMemory;
    }
    
    /**
     * @return Maximum available memory in bytes.
     */
    public long getMaxMemory()
    {
        return maxMemory;
    }
    
    /**
     * @return Memory usage as percentage (0-100).
     */
    public int getMemoryPercentage()
    {
        if (maxMemory == 0) return 0;
        return (int) ((usedMemory * 100.0) / maxMemory);
    }
    
    /**
     * @return Used memory in megabytes.
     */
    public double getUsedMemoryMB()
    {
        return usedMemory / (1024.0 * 1024.0);
    }
    
    /**
     * @return Total memory in megabytes.
     */
    public double getTotalMemoryMB()
    {
        return totalMemory / (1024.0 * 1024.0);
    }
    
    /**
     * @return Max memory in megabytes.
     */
    public double getMaxMemoryMB()
    {
        return maxMemory / (1024.0 * 1024.0);
    }
    
    /**
     * @return Current entity count.
     */
    public int getEntityCount()
    {
        return entityCount;
    }
    
    /**
     * @return FPS history array.
     */
    public int[] getFPSHistory()
    {
        return fpsHistory;
    }
}
