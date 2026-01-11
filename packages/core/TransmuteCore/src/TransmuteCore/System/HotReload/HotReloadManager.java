package TransmuteCore.System.HotReload;

import TransmuteCore.System.Logger;
import TransmuteCore.GameEngine.Manager;

import java.nio.file.Paths;
import java.util.*;

/**
 * Centralized manager for all hot-reload functionality.
 * <p>
 * Coordinates FileWatcher and AssetReloader to provide seamless hot-reloading
 * during development. Automatically watches configured directories and reloads
 * assets when files change.
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * // In your game's init() method
 * HotReloadManager hotReload = new HotReloadManager();
 * 
 * // Configure watched directories
 * hotReload.watchDirectory("res/images");
 * hotReload.watchDirectory("res/audio");
 * 
 * // Register assets for hot reload
 * hotReload.registerAsset("player", "res/images/player.png", AssetReloader.AssetType.IMAGE);
 * hotReload.registerAsset("music", "res/audio/music.wav", AssetReloader.AssetType.AUDIO);
 * 
 * // Optional: Add listener for reload events
 * hotReload.addReloadListener((name, type) -> {
 *     System.out.println("Reloaded: " + name);
 * });
 * 
 * // Start hot reloading
 * hotReload.start();
 * 
 * // Use Ctrl+R in console to manually reload all assets
 * // Or call hotReload.reloadAll() programmatically
 * }</pre>
 * 
 * @author TransmuteCore
 * @version 1.0
 */
public class HotReloadManager {
    
    private FileWatcher fileWatcher;
    private AssetReloader assetReloader;
    private boolean enabled = false;
    private Set<String> watchedDirectories = new HashSet<>();
    
    /**
     * Create a new HotReloadManager.
     */
    public HotReloadManager() {
        this.fileWatcher = new FileWatcher();
        this.assetReloader = new AssetReloader();
        
        // Wire up file watcher to asset reloader
        // (No need to manually connect - done in watchDirectory)
    }
    
    /**
     * Watch a directory for file changes.
     * Any assets in this directory will be automatically reloaded when modified.
     * 
     * @param directory Directory path to watch
     */
    public void watchDirectory(String directory) {
        if (watchedDirectories.contains(directory)) {
            Logger.warn("Directory already being watched: %s", directory);
            return;
        }
        
        fileWatcher.watch(directory, path -> {
            assetReloader.onFileChanged(path);
        });
        
        watchedDirectories.add(directory);
        Logger.info("Hot reload watching: %s", directory);
    }
    
    /**
     * Stop watching a directory.
     * 
     * @param directory Directory to stop watching
     */
    public void unwatchDirectory(String directory) {
        fileWatcher.unwatch(directory);
        watchedDirectories.remove(directory);
        Logger.info("Stopped watching: %s", directory);
    }
    
    /**
     * Register an asset for hot-reloading.
     * 
     * @param name Asset name (as used in AssetManager)
     * @param filePath Path to the asset file
     * @param type Type of asset
     */
    public void registerAsset(String name, String filePath, AssetReloader.AssetType type) {
        assetReloader.registerAsset(name, filePath, type);
    }
    
    /**
     * Unregister an asset from hot-reloading.
     * 
     * @param name Asset name
     */
    public void unregisterAsset(String name) {
        assetReloader.unregisterAsset(name);
    }
    
    /**
     * Add a listener for asset reload events.
     * 
     * @param listener Listener to add
     */
    public void addReloadListener(AssetReloader.AssetReloadListener listener) {
        assetReloader.addListener(listener);
    }
    
    /**
     * Remove a reload listener.
     * 
     * @param listener Listener to remove
     */
    public void removeReloadListener(AssetReloader.AssetReloadListener listener) {
        assetReloader.removeListener(listener);
    }
    
    /**
     * Start hot reloading.
     * Begins watching all registered directories for file changes.
     */
    public void start() {
        if (enabled) {
            Logger.warn("HotReloadManager is already running");
            return;
        }
        
        fileWatcher.start();
        assetReloader.setEnabled(true);
        enabled = true;
        
        Logger.info("=== Hot Reload Started ===");
        Logger.info("Watching %d directories", watchedDirectories.size());
        Logger.info("Tracking %d assets", assetReloader.getRegisteredAssets().size());
    }
    
    /**
     * Stop hot reloading.
     * Stops watching directories but keeps registration intact.
     */
    public void stop() {
        if (!enabled) {
            return;
        }
        
        fileWatcher.stop();
        assetReloader.setEnabled(false);
        enabled = false;
        
        Logger.info("Hot reload stopped");
    }
    
    /**
     * Check if hot reloading is currently active.
     * 
     * @return True if running
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Manually reload a specific asset by name.
     * 
     * @param name Asset name
     * @return True if asset was found and reloaded
     */
    public boolean reloadAsset(String name) {
        return assetReloader.reloadAsset(name);
    }
    
    /**
     * Manually reload all registered assets.
     * Useful for force-refresh or testing.
     * 
     * @return Number of assets reloaded
     */
    public int reloadAll() {
        Logger.info("Reloading all assets...");
        int count = assetReloader.reloadAll();
        Logger.info("Reloaded %d assets", count);
        return count;
    }
    
    /**
     * Get the asset reloader instance.
     * 
     * @return AssetReloader
     */
    public AssetReloader getAssetReloader() {
        return assetReloader;
    }
    
    /**
     * Get the file watcher instance.
     * 
     * @return FileWatcher
     */
    public FileWatcher getFileWatcher() {
        return fileWatcher;
    }
    
    /**
     * Get all watched directories.
     * 
     * @return Set of directory paths
     */
    public Set<String> getWatchedDirectories() {
        return new HashSet<>(watchedDirectories);
    }
    
    /**
     * Get all registered assets.
     * 
     * @return Collection of asset info
     */
    public Collection<AssetReloader.AssetInfo> getRegisteredAssets() {
        return assetReloader.getRegisteredAssets();
    }
    
    /**
     * Print hot reload status to console.
     */
    public void printStatus() {
        System.out.println("========== HOT RELOAD STATUS ==========");
        System.out.println("Enabled: " + (enabled ? "YES" : "NO"));
        System.out.println("Watched Directories: " + watchedDirectories.size());
        for (String dir : watchedDirectories) {
            System.out.println("  - " + dir);
        }
        System.out.println("Registered Assets: " + assetReloader.getRegisteredAssets().size());
        for (AssetReloader.AssetInfo info : assetReloader.getRegisteredAssets()) {
            System.out.println("  - " + info.name + " (" + info.type + "): " + info.filePath);
        }
        System.out.println("======================================");
    }
    
    /**
     * Clean up resources and stop watching.
     */
    public void shutdown() {
        stop();
        fileWatcher.close();
        assetReloader.clear();
        watchedDirectories.clear();
        Logger.info("HotReloadManager shut down");
    }
    
    /**
     * Quick setup for standard TransmuteCore project structure.
     * Watches res/images, res/audio, and res/fonts directories.
     * 
     * @param resDirectory Root resource directory (typically "res")
     */
    public void setupStandardWatching(String resDirectory) {
        watchDirectory(resDirectory + "/images");
        watchDirectory(resDirectory + "/audio");
        watchDirectory(resDirectory + "/fonts");
        
        Logger.info("Standard hot reload watching configured");
    }
}
