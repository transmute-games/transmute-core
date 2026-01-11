package TransmuteCore.System;

import java.io.PrintStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logging utility for TransmuteCore with configurable log levels.
 * This replaces the previous Util.log() approach with a more structured system.
 */
public class Logger {
    
    /**
     * Log levels in order of severity.
     */
    public enum Level {
        DEBUG(0, "DEBUG", System.out),
        INFO(1, "INFO", System.out),
        WARN(2, "WARN", System.err),
        ERROR(3, "ERROR", System.err);
        
        private final int priority;
        private final String label;
        private final PrintStream stream;
        
        Level(int priority, String label, PrintStream stream) {
            this.priority = priority;
            this.label = label;
            this.stream = stream;
        }
        
        public int getPriority() {
            return priority;
        }
        
        public String getLabel() {
            return label;
        }
        
        public PrintStream getStream() {
            return stream;
        }
    }
    
    private static Level currentLevel = Level.INFO;
    private static boolean showTimestamp = true;
    private static boolean showThreadName = false;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    
    // File logging
    private static PrintWriter fileWriter = null;
    private static String logFilePath = null;
    private static boolean fileLoggingEnabled = false;
    private static long maxFileSize = 10 * 1024 * 1024; // 10 MB default
    private static int maxBackupFiles = 5;
    
    /**
     * Sets the minimum log level. Messages below this level will be ignored.
     *
     * @param level The minimum log level to display.
     */
    public static void setLevel(Level level) {
        currentLevel = level;
    }
    
    /**
     * @return The current minimum log level.
     */
    public static Level getLevel() {
        return currentLevel;
    }
    
    /**
     * Sets whether to show timestamps in log messages.
     *
     * @param show True to show timestamps, false to hide them.
     */
    public static void setShowTimestamp(boolean show) {
        showTimestamp = show;
    }
    
    /**
     * Sets whether to show thread names in log messages.
     *
     * @param show True to show thread names, false to hide them.
     */
    public static void setShowThreadName(boolean show)
    {
        showThreadName = show;
    }
    
    /**
     * Enables file logging to the specified file.
     *
     * @param filePath Path to the log file.
     * @return True if file logging was successfully enabled.
     */
    public static boolean enableFileLogging(String filePath)
    {
        try
        {
            logFilePath = filePath;
            File logFile = new File(filePath);
            File parentDir = logFile.getParentFile();
            
            if (parentDir != null && !parentDir.exists())
            {
                parentDir.mkdirs();
            }
            
            fileWriter = new PrintWriter(new FileWriter(logFile, true), true);
            fileLoggingEnabled = true;
            info("File logging enabled: %s", filePath);
            return true;
        }
        catch (IOException e)
        {
            System.err.println("Failed to enable file logging: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Disables file logging and closes the log file.
     */
    public static void disableFileLogging()
    {
        if (fileWriter != null)
        {
            fileWriter.close();
            fileWriter = null;
        }
        fileLoggingEnabled = false;
        logFilePath = null;
    }
    
    /**
     * Sets the maximum log file size before rotation.
     *
     * @param sizeInBytes Maximum file size in bytes.
     */
    public static void setMaxFileSize(long sizeInBytes)
    {
        maxFileSize = sizeInBytes;
    }
    
    /**
     * Sets the maximum number of backup log files to keep.
     *
     * @param count Number of backup files.
     */
    public static void setMaxBackupFiles(int count)
    {
        maxBackupFiles = count;
    }
    /**
     * Logs a debug message. Debug messages are for detailed diagnostic information.
     *
     * @param message The message to log.
     */
    public static void debug(String message) {
        log(Level.DEBUG, message);
    }
    
    /**
     * Logs a debug message with formatting.
     *
     * @param format The format string.
     * @param args   Arguments referenced by the format specifiers.
     */
    public static void debug(String format, Object... args) {
        log(Level.DEBUG, String.format(format, args));
    }
    
    /**
     * Logs an info message. Info messages are for general informational messages.
     *
     * @param message The message to log.
     */
    public static void info(String message) {
        log(Level.INFO, message);
    }
    
    /**
     * Logs an info message with formatting.
     *
     * @param format The format string.
     * @param args   Arguments referenced by the format specifiers.
     */
    public static void info(String format, Object... args) {
        log(Level.INFO, String.format(format, args));
    }
    
    /**
     * Logs a warning message. Warnings indicate potentially harmful situations.
     *
     * @param message The message to log.
     */
    public static void warn(String message) {
        log(Level.WARN, message);
    }
    
    /**
     * Logs a warning message with formatting.
     *
     * @param format The format string.
     * @param args   Arguments referenced by the format specifiers.
     */
    public static void warn(String format, Object... args) {
        log(Level.WARN, String.format(format, args));
    }
    
    /**
     * Logs an error message. Errors indicate serious problems.
     *
     * @param message The message to log.
     */
    public static void error(String message) {
        log(Level.ERROR, message);
    }
    
    /**
     * Logs an error message with formatting.
     *
     * @param format The format string.
     * @param args   Arguments referenced by the format specifiers.
     */
    public static void error(String format, Object... args) {
        log(Level.ERROR, String.format(format, args));
    }
    
    /**
     * Logs an error message with exception details.
     *
     * @param message   The message to log.
     * @param throwable The exception that occurred.
     */
    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message);
        throwable.printStackTrace(Level.ERROR.getStream());
    }
    
    /**
     * Internal method that handles the actual logging.
     *
     * @param level   The log level.
     * @param message The message to log.
     */
    private static void log(Level level, String message) {
        if (level.getPriority() < currentLevel.getPriority()) {
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Add timestamp
        if (showTimestamp) {
            sb.append("[").append(dateFormat.format(new Date())).append("] ");
        }
        
        // Add log level
        sb.append("[").append(level.getLabel()).append("] ");
        
        // Add thread name
        if (showThreadName) {
            sb.append("[").append(Thread.currentThread().getName()).append("] ");
        }
        
        // Add message
        sb.append(message);
        
        // Output to console stream
        level.getStream().println(sb.toString());
        
        // Output to file if enabled
        if (fileLoggingEnabled && fileWriter != null)
        {
            fileWriter.println(sb.toString());
            checkLogRotation();
        }
    }
    
    /**
     * Checks if log rotation is needed and performs it if necessary.
     */
    private static void checkLogRotation()
    {
        if (logFilePath == null) return;
        
        File logFile = new File(logFilePath);
        if (!logFile.exists()) return;
        
        if (logFile.length() > maxFileSize)
        {
            rotateLogFile();
        }
    }
    
    /**
     * Rotates the log file by renaming old logs and creating a new one.
     */
    private static void rotateLogFile()
    {
        try
        {
            // Close current file writer
            if (fileWriter != null)
            {
                fileWriter.close();
            }
            
            // Rotate backup files
            for (int i = maxBackupFiles - 1; i > 0; i--)
            {
                File oldFile = new File(logFilePath + "." + i);
                File newFile = new File(logFilePath + "." + (i + 1));
                if (oldFile.exists())
                {
                    if (newFile.exists())
                    {
                        newFile.delete();
                    }
                    oldFile.renameTo(newFile);
                }
            }
            
            // Rename current log to .1
            File currentLog = new File(logFilePath);
            File backup = new File(logFilePath + ".1");
            if (backup.exists())
            {
                backup.delete();
            }
            currentLog.renameTo(backup);
            
            // Create new log file
            fileWriter = new PrintWriter(new FileWriter(logFilePath, true), true);
            info("Log file rotated");
        }
        catch (IOException e)
        {
            System.err.println("Failed to rotate log file: " + e.getMessage());
        }
    }
    
    /**
     * Convenience method for logging asset operations.
     *
     * @param className The name of the class performing the operation.
     * @param key       The key of the asset.
     * @param mapName   The name of the map/registry.
     */
    public static void logAssetAdd(String className, String key, String mapName) {
        debug("[%s]: Added [%s] to [%s]", className, key, mapName);
    }
    
    /**
     * Convenience method for logging asset caching.
     *
     * @param className The name of the class performing the operation.
     * @param fileName  The name of the file being cached.
     */
    public static void logAssetCached(String className, String fileName) {
        debug("[%s]: Cached [%s]", className, fileName);
    }
    
    /**
     * Convenience method for logging asset removal.
     *
     * @param className The name of the class performing the operation.
     * @param key       The key of the asset.
     * @param mapName   The name of the map/registry.
     */
    public static void logAssetRemove(String className, String key, String mapName) {
        debug("[%s]: Removed [%s] from [%s]", className, key, mapName);
    }
}
