package TransmuteCore.System.HotReload;

import TransmuteCore.System.Logger;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Monitors file system for changes and notifies listeners.
 * <p>
 * Uses Java's WatchService API to efficiently monitor directories for file modifications.
 * Particularly useful for hot-reloading assets during development.
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * FileWatcher watcher = new FileWatcher();
 * 
 * // Watch a directory
 * watcher.watch("res/images", path -> {
 *     System.out.println("Image changed: " + path);
 *     // Reload the image...
 * });
 * 
 * // Start watching
 * watcher.start();
 * 
 * // Later, stop watching
 * watcher.stop();
 * }</pre>
 * 
 * @author TransmuteCore
 * @version 1.0
 */
public class FileWatcher {
    
    private WatchService watchService;
    private Map<WatchKey, Path> watchKeys = new ConcurrentHashMap<>();
    private Map<Path, List<FileChangeListener>> listeners = new ConcurrentHashMap<>();
    private ExecutorService executor;
    private volatile boolean running = false;
    
    /**
     * Functional interface for file change callbacks.
     */
    @FunctionalInterface
    public interface FileChangeListener {
        /**
         * Called when a watched file changes.
         * 
         * @param path Path to the changed file
         */
        void onFileChanged(Path path);
    }
    
    /**
     * Create a new FileWatcher.
     */
    public FileWatcher() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (Exception e) {
            Logger.error("Failed to create FileWatcher", e);
        }
    }
    
    /**
     * Watch a directory for file changes.
     * 
     * @param directory Directory path to watch
     * @param listener Callback when files change
     */
    public void watch(String directory, FileChangeListener listener) {
        watch(Paths.get(directory), listener);
    }
    
    /**
     * Watch a directory for file changes.
     * 
     * @param directory Directory path to watch
     * @param listener Callback when files change
     */
    public void watch(Path directory, FileChangeListener listener) {
        if (!Files.isDirectory(directory)) {
            Logger.warn("Not a directory: %s", directory);
            return;
        }
        
        try {
            // Register directory with watch service
            WatchKey key = directory.register(
                watchService,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE
            );
            
            watchKeys.put(key, directory);
            
            // Add listener
            listeners.computeIfAbsent(directory, k -> new CopyOnWriteArrayList<>()).add(listener);
            
            Logger.debug("Watching directory: %s", directory);
        } catch (Exception e) {
            Logger.error("Failed to watch directory: " + directory, e);
        }
    }
    
    /**
     * Stop watching a directory.
     * 
     * @param directory Directory to stop watching
     */
    public void unwatch(String directory) {
        unwatch(Paths.get(directory));
    }
    
    /**
     * Stop watching a directory.
     * 
     * @param directory Directory to stop watching
     */
    public void unwatch(Path directory) {
        listeners.remove(directory);
        
        // Find and cancel the watch key
        watchKeys.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(directory)) {
                entry.getKey().cancel();
                return true;
            }
            return false;
        });
        
        Logger.debug("Stopped watching directory: %s", directory);
    }
    
    /**
     * Start watching for file changes.
     * This runs in a background thread.
     */
    public void start() {
        if (running) {
            Logger.warn("FileWatcher is already running");
            return;
        }
        
        running = true;
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "FileWatcher");
            t.setDaemon(true);
            return t;
        });
        
        executor.submit(this::watchLoop);
        Logger.info("FileWatcher started");
    }
    
    /**
     * Stop watching for file changes.
     */
    public void stop() {
        if (!running) {
            return;
        }
        
        running = false;
        
        if (executor != null) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        Logger.info("FileWatcher stopped");
    }
    
    /**
     * Check if the watcher is currently running.
     * 
     * @return True if running
     */
    public boolean isRunning() {
        return running;
    }
    
    private void watchLoop() {
        while (running) {
            try {
                // Wait for events (timeout to check running flag)
                WatchKey key = watchService.poll(500, TimeUnit.MILLISECONDS);
                if (key == null) {
                    continue;
                }
                
                Path directory = watchKeys.get(key);
                if (directory == null) {
                    continue;
                }
                
                // Process events
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    Path fullPath = directory.resolve(filename);
                    
                    // Notify listeners
                    List<FileChangeListener> dirListeners = listeners.get(directory);
                    if (dirListeners != null) {
                        for (FileChangeListener listener : dirListeners) {
                            try {
                                listener.onFileChanged(fullPath);
                            } catch (Exception e) {
                                Logger.error("Error in FileChangeListener", e);
                            }
                        }
                    }
                }
                
                // Reset the key
                boolean valid = key.reset();
                if (!valid) {
                    watchKeys.remove(key);
                    Logger.warn("WatchKey no longer valid for: %s", directory);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                Logger.error("Error in watch loop", e);
            }
        }
    }
    
    /**
     * Clean up resources.
     */
    public void close() {
        stop();
        
        // Clear all watch keys
        for (WatchKey key : watchKeys.keySet()) {
            key.cancel();
        }
        watchKeys.clear();
        listeners.clear();
        
        if (watchService != null) {
            try {
                watchService.close();
            } catch (Exception e) {
                Logger.error("Error closing WatchService", e);
            }
        }
    }
}
