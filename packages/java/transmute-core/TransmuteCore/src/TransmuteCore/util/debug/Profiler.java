package TransmuteCore.util.Debug;

import java.util.*;

/**
 * Performance profiler for tracking timing of different game systems.
 * <p>
 * Usage:
 * <pre>
 * {@code
 * Profiler.begin("physics");
 * // ... physics code ...
 * Profiler.end("physics");
 * 
 * Profiler.begin("rendering");
 * // ... rendering code ...
 * Profiler.end("rendering");
 * 
 * // Get results
 * Map<String, ProfileSection> results = Profiler.getResults();
 * }
 * </pre>
 */
public class Profiler
{
    private static final int HISTORY_SIZE = 60;
    private static final Map<String, ProfileSection> sections = new LinkedHashMap<>();
    private static final Map<String, Long> activeTimers = new HashMap<>();
    private static boolean enabled = true;
    
    /**
     * Begins timing a profiler section.
     *
     * @param sectionName Name of the section to profile.
     */
    public static void begin(String sectionName)
    {
        if (!enabled) return;
        
        if (sectionName == null || sectionName.trim().isEmpty())
        {
            throw new IllegalArgumentException("Section name cannot be null or empty");
        }
        
        activeTimers.put(sectionName, System.nanoTime());
    }
    
    /**
     * Ends timing a profiler section and records the result.
     *
     * @param sectionName Name of the section to end.
     */
    public static void end(String sectionName)
    {
        if (!enabled) return;
        
        Long startTime = activeTimers.remove(sectionName);
        if (startTime == null)
        {
            return; // Section wasn't started, ignore
        }
        
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        
        // Get or create section
        ProfileSection section = sections.get(sectionName);
        if (section == null)
        {
            section = new ProfileSection(sectionName);
            sections.put(sectionName, section);
        }
        
        section.recordTime(elapsedMs);
    }
    
    /**
     * Resets all profiler data.
     */
    public static void reset()
    {
        sections.clear();
        activeTimers.clear();
    }
    
    /**
     * Resets a specific section.
     *
     * @param sectionName Name of the section to reset.
     */
    public static void resetSection(String sectionName)
    {
        ProfileSection section = sections.get(sectionName);
        if (section != null)
        {
            section.reset();
        }
    }
    
    /**
     * Enables or disables the profiler.
     *
     * @param enabled Whether to enable profiling.
     */
    public static void setEnabled(boolean enabled)
    {
        Profiler.enabled = enabled;
    }
    
    /**
     * @return Whether the profiler is enabled.
     */
    public static boolean isEnabled()
    {
        return enabled;
    }
    
    /**
     * Gets all profiler results.
     *
     * @return Map of section names to ProfileSection objects.
     */
    public static Map<String, ProfileSection> getResults()
    {
        return new LinkedHashMap<>(sections);
    }
    
    /**
     * Gets a specific section's results.
     *
     * @param sectionName Name of the section.
     * @return The ProfileSection, or null if not found.
     */
    public static ProfileSection getSection(String sectionName)
    {
        return sections.get(sectionName);
    }
    
    /**
     * Exports profiler data to CSV format.
     *
     * @return CSV string with profiler data.
     */
    public static String exportToCSV()
    {
        StringBuilder csv = new StringBuilder();
        csv.append("Section,Calls,Total (ms),Average (ms),Min (ms),Max (ms)\n");
        
        for (ProfileSection section : sections.values())
        {
            csv.append(String.format("%s,%d,%.3f,%.3f,%.3f,%.3f\n",
                section.getName(),
                section.getCallCount(),
                section.getTotalTime(),
                section.getAverageTime(),
                section.getMinTime(),
                section.getMaxTime()
            ));
        }
        
        return csv.toString();
    }
    
    /**
     * Prints profiler results to console.
     */
    public static void printResults()
    {
        System.out.println("\n========== PROFILER RESULTS ==========");
        System.out.printf("%-20s %8s %12s %12s %12s %12s\n", 
            "Section", "Calls", "Total (ms)", "Avg (ms)", "Min (ms)", "Max (ms)");
        System.out.println("─".repeat(88));
        
        for (ProfileSection section : sections.values())
        {
            System.out.printf("%-20s %8d %12.3f %12.3f %12.3f %12.3f\n",
                section.getName(),
                section.getCallCount(),
                section.getTotalTime(),
                section.getAverageTime(),
                section.getMinTime(),
                section.getMaxTime()
            );
        }
        
        System.out.println("======================================\n");
    }
    
    /**
     * Represents timing data for a profiled section.
     */
    public static class ProfileSection
    {
        private final String name;
        private final List<Double> history;
        private double totalTime;
        private double minTime;
        private double maxTime;
        private int callCount;
        
        public ProfileSection(String name)
        {
            this.name = name;
            this.history = new ArrayList<>(HISTORY_SIZE);
            this.minTime = Double.MAX_VALUE;
            this.maxTime = 0.0;
        }
        
        /**
         * Records a timing measurement.
         *
         * @param timeMs Time in milliseconds.
         */
        public void recordTime(double timeMs)
        {
            // Update statistics
            totalTime += timeMs;
            callCount++;
            
            if (timeMs < minTime) minTime = timeMs;
            if (timeMs > maxTime) maxTime = timeMs;
            
            // Add to history (circular buffer)
            if (history.size() >= HISTORY_SIZE)
            {
                history.remove(0);
            }
            history.add(timeMs);
        }
        
        /**
         * Resets this section's data.
         */
        public void reset()
        {
            history.clear();
            totalTime = 0.0;
            minTime = Double.MAX_VALUE;
            maxTime = 0.0;
            callCount = 0;
        }
        
        /**
         * @return Section name.
         */
        public String getName()
        {
            return name;
        }
        
        /**
         * @return Total accumulated time in milliseconds.
         */
        public double getTotalTime()
        {
            return totalTime;
        }
        
        /**
         * @return Average time per call in milliseconds.
         */
        public double getAverageTime()
        {
            return callCount > 0 ? totalTime / callCount : 0.0;
        }
        
        /**
         * @return Minimum recorded time in milliseconds.
         */
        public double getMinTime()
        {
            return minTime == Double.MAX_VALUE ? 0.0 : minTime;
        }
        
        /**
         * @return Maximum recorded time in milliseconds.
         */
        public double getMaxTime()
        {
            return maxTime;
        }
        
        /**
         * @return Number of times this section was called.
         */
        public int getCallCount()
        {
            return callCount;
        }
        
        /**
         * @return History of recent timing measurements.
         */
        public List<Double> getHistory()
        {
            return new ArrayList<>(history);
        }
        
        /**
         * @return Average of recent measurements in history.
         */
        public double getRecentAverage()
        {
            if (history.isEmpty()) return 0.0;
            
            double sum = 0.0;
            for (double time : history)
            {
                sum += time;
            }
            return sum / history.size();
        }
    }
    
    private Profiler()
    {
    }
}
