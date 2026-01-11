package TransmuteCore.util.hotreload;

import TransmuteCore.assets.AssetManager;
import TransmuteCore.assets.Asset;
import TransmuteCore.assets.types.Image;
import TransmuteCore.assets.types.Audio;
import TransmuteCore.assets.types.Font;
import TransmuteCore.util.Logger;
import TransmuteCore.graphics.Bitmap;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles hot-reloading of game assets when files change.
 * <p>
 * Monitors asset directories and automatically reloads assets when their files are modified.
 * This significantly improves iteration speed during development.
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * AssetReloader reloader = new AssetReloader();
 * 
 * // Register asset with its file path
 * reloader.registerAsset("player", "res/images/player.png", AssetType.IMAGE);
 * reloader.registerAsset("jump", "res/audio/jump.wav", AssetType.AUDIO);
 * 
 * // Set up file watching
 * FileWatcher watcher = new FileWatcher();
 * watcher.watch("res/images", path -> reloader.onFileChanged(path));
 * watcher.watch("res/audio", path -> reloader.onFileChanged(path));
 * watcher.start();
 * 
 * // Assets will now automatically reload when files change!
 * }</pre>
 * 
 * @author TransmuteCore
 * @version 1.0
 */
public class AssetReloader {
    
    /**
     * Asset types that can be reloaded.
     */
    public enum AssetType {
        IMAGE,
        AUDIO,
        FONT
    }
    
    /**
     * Metadata about a registered asset.
     */
    public static class AssetInfo {
        public String name;
        public String filePath;
        public AssetType type;
        public long lastModified;
        
        public AssetInfo(String name, String filePath, AssetType type) {
            this.name = name;
            this.filePath = filePath;
            this.type = type;
            this.lastModified = 0;
        }
    }
    
    /**
     * Listener for asset reload events.
     */
    @FunctionalInterface
    public interface AssetReloadListener {
        /**
         * Called when an asset is reloaded.
         * 
         * @param assetName Name of the reloaded asset
         * @param assetType Type of the asset
         */
        void onAssetReloaded(String assetName, AssetType assetType);
    }
    
    private Map<String, AssetInfo> assetsByPath = new ConcurrentHashMap<>();
    private Map<String, AssetInfo> assetsByName = new ConcurrentHashMap<>();
    private List<AssetReloadListener> listeners = new ArrayList<>();
    private boolean enabled = true;
    
    // Debounce to handle multiple rapid file changes
    private Map<String, Long> debounceMap = new ConcurrentHashMap<>();
    private static final long DEBOUNCE_MS = 500;
    
    /**
     * Create a new AssetReloader.
     */
    public AssetReloader() {
    }
    
    /**
     * Register an asset for hot-reloading.
     * 
     * @param name Asset name (as used in AssetManager)
     * @param filePath Path to the asset file
     * @param type Type of asset
     */
    public void registerAsset(String name, String filePath, AssetType type) {
        AssetInfo info = new AssetInfo(name, filePath, type);
        
        // Normalize file path
        String normalizedPath = normalizePath(filePath);
        
        assetsByPath.put(normalizedPath, info);
        assetsByName.put(name.toLowerCase(), info);
        
        Logger.debug("Registered asset for hot reload: %s (%s)", name, filePath);
    }
    
    /**
     * Unregister an asset from hot-reloading.
     * 
     * @param name Asset name
     */
    public void unregisterAsset(String name) {
        AssetInfo info = assetsByName.remove(name.toLowerCase());
        if (info != null) {
            assetsByPath.remove(normalizePath(info.filePath));
            Logger.debug("Unregistered asset from hot reload: %s", name);
        }
    }
    
    /**
     * Add a listener for asset reload events.
     * 
     * @param listener Listener to add
     */
    public void addListener(AssetReloadListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener.
     * 
     * @param listener Listener to remove
     */
    public void removeListener(AssetReloadListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Enable or disable hot reloading.
     * 
     * @param enabled True to enable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        Logger.info("Asset hot reload: %s", enabled ? "ENABLED" : "DISABLED");
    }
    
    /**
     * Check if hot reloading is enabled.
     * 
     * @return True if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Handle a file change event.
     * Called by FileWatcher when a file changes.
     * 
     * @param changedPath Path to the changed file
     */
    public void onFileChanged(Path changedPath) {
        if (!enabled) {
            return;
        }
        
        String pathStr = normalizePath(changedPath.toString());
        
        // Debounce - ignore if we recently processed this file
        Long lastChange = debounceMap.get(pathStr);
        long now = System.currentTimeMillis();
        if (lastChange != null && (now - lastChange) < DEBOUNCE_MS) {
            return;
        }
        debounceMap.put(pathStr, now);
        
        // Find matching asset
        AssetInfo info = assetsByPath.get(pathStr);
        if (info == null) {
            // Try matching by filename only
            String filename = changedPath.getFileName().toString();
            for (AssetInfo asset : assetsByPath.values()) {
                if (asset.filePath.endsWith(filename)) {
                    info = asset;
                    break;
                }
            }
        }
        
        if (info != null) {
            reloadAsset(info);
        }
    }
    
    /**
     * Manually reload an asset by name.
     * 
     * @param name Asset name
     * @return True if asset was reloaded
     */
    public boolean reloadAsset(String name) {
        AssetInfo info = assetsByName.get(name.toLowerCase());
        if (info != null) {
            reloadAsset(info);
            return true;
        }
        return false;
    }
    
    /**
     * Reload all registered assets.
     * 
     * @return Number of assets reloaded
     */
    public int reloadAll() {
        int count = 0;
        for (AssetInfo info : assetsByName.values()) {
            reloadAsset(info);
            count++;
        }
        return count;
    }
    
    /**
     * Get all registered assets.
     * 
     * @return Collection of asset info
     */
    public Collection<AssetInfo> getRegisteredAssets() {
        return new ArrayList<>(assetsByName.values());
    }
    
    private void reloadAsset(AssetInfo info) {
        try {
            Logger.info("Hot reloading asset: %s (%s)", info.name, info.type);
            
            switch (info.type) {
                case IMAGE:
                    reloadImage(info);
                    break;
                case AUDIO:
                    reloadAudio(info);
                    break;
                case FONT:
                    reloadFont(info);
                    break;
            }
            
            // Notify listeners
            for (AssetReloadListener listener : listeners) {
                try {
                    listener.onAssetReloaded(info.name, info.type);
                } catch (Exception e) {
                    Logger.error("Error in AssetReloadListener", e);
                }
            }
            
        } catch (Exception e) {
            Logger.error("Failed to reload asset: " + info.name, e);
        }
    }
    
    private void reloadImage(AssetInfo info) {
        Logger.info("Image reload: %s from %s", info.name, info.filePath);
        
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) {
            Logger.warn("Cannot reload image: AssetManager global instance is null");
            return;
        }
        
        try {
            // Create new Image asset and load it
            Image newImage = new Image(info.name, info.filePath);
            newImage.load();
            
            // The asset will automatically re-register with AssetManager,
            // replacing the old one due to the same key
            Logger.debug("Successfully reloaded image: %s", info.name);
        } catch (Exception e) {
            Logger.error("Failed to reload image %s: %s", info.name, e.getMessage());
            throw e;
        }
    }
    
    private void reloadAudio(AssetInfo info) {
        Logger.info("Audio reload: %s from %s", info.name, info.filePath);
        
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) {
            Logger.warn("Cannot reload audio: AssetManager global instance is null");
            return;
        }
        
        try {
            // Get existing clip and stop it if playing
            javax.sound.sampled.Clip oldClip = assetManager.getAudio(info.name);
            if (oldClip != null && oldClip.isRunning()) {
                oldClip.stop();
            }
            
            // Create new Audio asset and load it
            Audio newAudio = new Audio(info.name, info.filePath);
            newAudio.load();
            
            Logger.debug("Successfully reloaded audio: %s", info.name);
        } catch (Exception e) {
            Logger.error("Failed to reload audio %s: %s", info.name, e.getMessage());
            throw e;
        }
    }
    
    private void reloadFont(AssetInfo info) {
        Logger.info("Font reload: %s from %s", info.name, info.filePath);
        
        AssetManager assetManager = AssetManager.getGlobalInstance();
        if (assetManager == null) {
            Logger.warn("Cannot reload font: AssetManager global instance is null");
            return;
        }
        
        Logger.warn("Font hot-reloading is not yet fully supported - fonts require additional metadata (glyphMap, rows, columns, etc.)");
        Logger.warn("To reload a font, you must manually recreate it with the same parameters and re-register it");
        
        // Note: Font reloading is complex because Font requires additional parameters
        // (glyphMap, rows, columns, defaultGlyphSize) that aren't stored in AssetInfo.
        // Users would need to extend AssetReloader to support custom Font reloading.
    }
    
    private String normalizePath(String path) {
        // Normalize path separators and remove relative components
        return path.replace('\\', '/').replaceAll("/+", "/");
    }
    
    /**
     * Clear all registered assets and listeners.
     */
    public void clear() {
        assetsByPath.clear();
        assetsByName.clear();
        listeners.clear();
        debounceMap.clear();
    }
}
