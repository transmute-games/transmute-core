package TransmuteCore.System.Serialization;

import TransmuteCore.Input.Input;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.System.Logger;
import TransmuteCore.Serialization.TinyDatabase;

import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * High-level utility for quick save/load game state.
 * <p>
 * Provides keyboard shortcuts (F5/F9) and a simple API for saving and restoring game state.
 * Supports multiple save slots, auto-save, and state history.
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * StateSaver stateSaver = new StateSaver();
 * stateSaver.setSaveDirectory("saves");
 * 
 * // Enable quick save/load hotkeys (F5 = save, F9 = load)
 * stateSaver.enableHotkeys(true);
 * 
 * // Register custom save/load handlers
 * stateSaver.setOnSave(db -> {
 *     // Save player data
 *     TinyObject player = new TinyObject("player");
 *     player.addField(new TinyField("x", playerX));
 *     player.addField(new TinyField("y", playerY));
 *     player.addField(new TinyField("health", playerHealth));
 *     db.addObject(player);
 * });
 * 
 * stateSaver.setOnLoad(db -> {
 *     // Load player data
 *     TinyObject player = db.findObject("player");
 *     if (player != null) {
 *         playerX = player.findField("x").getIntValue();
 *         playerY = player.findField("y").getIntValue();
 *         playerHealth = player.findField("health").getIntValue();
 *     }
 * });
 * 
 * // In your game loop
 * stateSaver.update(manager, delta);
 * 
 * // Manual save/load
 * stateSaver.quickSave();
 * stateSaver.quickLoad();
 * }</pre>
 * 
 * @author TransmuteCore
 * @version 1.0
 */
public class StateSaver {
    
    private String saveDirectory = "saves";
    private String quickSaveSlot = "quicksave";
    private boolean hotkeysEnabled = true;
    
    private Consumer<TinyDatabase> onSave;
    private Consumer<TinyDatabase> onLoad;
    
    private List<SaveSlot> saveSlots = new ArrayList<>();
    private TinyDatabase currentState;
    
    // Hotkey state tracking
    private boolean wasF5Pressed = false;
    private boolean wasF9Pressed = false;
    
    /**
     * Metadata about a save slot.
     */
    public static class SaveSlot {
        public String name;
        public String filePath;
        public long timestamp;
        public String displayTime;
        
        public SaveSlot(String name, String filePath, long timestamp) {
            this.name = name;
            this.filePath = filePath;
            this.timestamp = timestamp;
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.displayTime = sdf.format(new Date(timestamp));
        }
    }
    
    /**
     * Create a new StateSaver with default settings.
     */
    public StateSaver() {
        ensureSaveDirectory();
    }
    
    /**
     * Set the directory where save files are stored.
     * 
     * @param directory Directory path
     */
    public void setSaveDirectory(String directory) {
        this.saveDirectory = directory;
        ensureSaveDirectory();
    }
    
    /**
     * Get the save directory.
     * 
     * @return Directory path
     */
    public String getSaveDirectory() {
        return saveDirectory;
    }
    
    /**
     * Set the callback for saving game state.
     * 
     * @param onSave Callback that populates the TinyDatabase
     */
    public void setOnSave(Consumer<TinyDatabase> onSave) {
        this.onSave = onSave;
    }
    
    /**
     * Set the callback for loading game state.
     * 
     * @param onLoad Callback that reads from the TinyDatabase
     */
    public void setOnLoad(Consumer<TinyDatabase> onLoad) {
        this.onLoad = onLoad;
    }
    
    /**
     * Enable or disable hotkey support (F5 = save, F9 = load).
     * 
     * @param enabled True to enable
     */
    public void enableHotkeys(boolean enabled) {
        this.hotkeysEnabled = enabled;
    }
    
    /**
     * Check if hotkeys are enabled.
     * 
     * @return True if enabled
     */
    public boolean areHotkeysEnabled() {
        return hotkeysEnabled;
    }
    
    /**
     * Update the StateSaver (call every frame).
     * Handles hotkey input if enabled.
     * 
     * @param manager Game manager
     * @param delta Delta time
     */
    public void update(Manager manager, double delta) {
        if (!hotkeysEnabled || manager.getInput() == null) {
            return;
        }
        
        Input input = manager.getInput();
        
        // F5 - Quick save
        boolean f5Pressed = input.isKeyPressed(KeyEvent.VK_F5);
        if (f5Pressed && !wasF5Pressed) {
            quickSave();
        }
        wasF5Pressed = f5Pressed;
        
        // F9 - Quick load
        boolean f9Pressed = input.isKeyPressed(KeyEvent.VK_F9);
        if (f9Pressed && !wasF9Pressed) {
            quickLoad();
        }
        wasF9Pressed = f9Pressed;
    }
    
    /**
     * Quick save to the default slot.
     * 
     * @return True if successful
     */
    public boolean quickSave() {
        return save(quickSaveSlot);
    }
    
    /**
     * Quick load from the default slot.
     * 
     * @return True if successful
     */
    public boolean quickLoad() {
        return load(quickSaveSlot);
    }
    
    /**
     * Save game state to a named slot.
     * 
     * @param slotName Name of the save slot
     * @return True if successful
     */
    public boolean save(String slotName) {
        if (onSave == null) {
            Logger.warn("StateSaver: No save handler registered");
            return false;
        }
        
        try {
            // Create database
            TinyDatabase db = new TinyDatabase(slotName);
            
            // Let game populate the database
            onSave.accept(db);
            
            // Save to file
            String filePath = saveDirectory + "/" + slotName + ".sav";
            db.serializeToFile(filePath);
            
            // Update current state
            currentState = db;
            
            // Update slot list
            updateSaveSlots();
            
            Logger.info("Game saved: %s", slotName);
            return true;
            
        } catch (Exception e) {
            Logger.error("Failed to save game: " + slotName, e);
            return false;
        }
    }
    
    /**
     * Load game state from a named slot.
     * 
     * @param slotName Name of the save slot
     * @return True if successful
     */
    public boolean load(String slotName) {
        if (onLoad == null) {
            Logger.warn("StateSaver: No load handler registered");
            return false;
        }
        
        try {
            String filePath = saveDirectory + "/" + slotName + ".sav";
            
            // Check if file exists
            File file = new File(filePath);
            if (!file.exists()) {
                Logger.warn("Save file not found: %s", slotName);
                return false;
            }
            
            // Load from file
            TinyDatabase db = TinyDatabase.DeserializeFromFile(filePath);
            if (db == null) {
                Logger.error("Failed to deserialize save file: %s", slotName);
                return false;
            }
            
            // Let game read the database
            onLoad.accept(db);
            
            // Update current state
            currentState = db;
            
            Logger.info("Game loaded: %s", slotName);
            return true;
            
        } catch (Exception e) {
            Logger.error("Failed to load game: " + slotName, e);
            return false;
        }
    }
    
    /**
     * Delete a save slot.
     * 
     * @param slotName Name of the save slot
     * @return True if successful
     */
    public boolean deleteSave(String slotName) {
        try {
            String filePath = saveDirectory + "/" + slotName + ".sav";
            File file = new File(filePath);
            
            if (file.exists() && file.delete()) {
                updateSaveSlots();
                Logger.info("Save deleted: %s", slotName);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            Logger.error("Failed to delete save: " + slotName, e);
            return false;
        }
    }
    
    /**
     * Get all available save slots.
     * 
     * @return List of save slots
     */
    public List<SaveSlot> getSaveSlots() {
        updateSaveSlots();
        return new ArrayList<>(saveSlots);
    }
    
    /**
     * Check if a save slot exists.
     * 
     * @param slotName Name of the save slot
     * @return True if exists
     */
    public boolean hasSave(String slotName) {
        String filePath = saveDirectory + "/" + slotName + ".sav";
        return new File(filePath).exists();
    }
    
    /**
     * Get the current loaded state database.
     * Useful for inspecting save data.
     * 
     * @return Current TinyDatabase or null
     */
    public TinyDatabase getCurrentState() {
        return currentState;
    }
    
    /**
     * Create a timestamped auto-save.
     * 
     * @return True if successful
     */
    public boolean autoSave() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String slotName = "autosave_" + timestamp;
        
        if (save(slotName)) {
            Logger.info("Auto-save created: %s", slotName);
            return true;
        }
        
        return false;
    }
    
    /**
     * Get the most recent save slot.
     * 
     * @return Most recent SaveSlot or null
     */
    public SaveSlot getMostRecentSave() {
        updateSaveSlots();
        
        if (saveSlots.isEmpty()) {
            return null;
        }
        
        SaveSlot mostRecent = saveSlots.get(0);
        for (SaveSlot slot : saveSlots) {
            if (slot.timestamp > mostRecent.timestamp) {
                mostRecent = slot;
            }
        }
        
        return mostRecent;
    }
    
    /**
     * Print all save slots to console.
     */
    public void printSaveSlots() {
        updateSaveSlots();
        
        System.out.println("========== SAVE SLOTS ==========");
        if (saveSlots.isEmpty()) {
            System.out.println("No saves found");
        } else {
            for (SaveSlot slot : saveSlots) {
                System.out.println(slot.name + " - " + slot.displayTime);
            }
        }
        System.out.println("================================");
    }
    
    private void ensureSaveDirectory() {
        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
            Logger.debug("Created save directory: %s", saveDirectory);
        }
    }
    
    private void updateSaveSlots() {
        saveSlots.clear();
        
        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            return;
        }
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sav"));
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            String name = file.getName().replace(".sav", "");
            SaveSlot slot = new SaveSlot(name, file.getAbsolutePath(), file.lastModified());
            saveSlots.add(slot);
        }
        
        // Sort by timestamp (most recent first)
        saveSlots.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
    }
}
