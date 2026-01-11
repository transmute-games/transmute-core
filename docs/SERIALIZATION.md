# TransmuteCore Serialization Guide

Complete guide to TransmuteCore's TinyDatabase serialization system for save games and data persistence.

## Table of Contents

- [Overview](#overview)
- [TinyDatabase Basics](#tinydatabase-basics)
- [Saving Game Data](#saving-game-data)
- [Loading Game Data](#loading-game-data)
- [Data Types](#data-types)
- [Best Practices](#best-practices)
- [Advanced Usage](#advanced-usage)

---

## Overview

TransmuteCore uses **TinyDatabase**, a custom binary serialization format for saving and loading game data. It's designed to be simple, fast, and game-friendly.

### Features

- **Binary format** - Compact file sizes
- **Type-safe** - Strongly typed fields
- **Hierarchical** - Nested objects supported
- **Fast** - Direct binary read/write
- **Simple API** - Easy to use

### Use Cases

- Save game states
- Player profiles
- Game settings
- Level data
- High scores
- Achievements

---

## TinyDatabase Basics

### Structure

```
TinyDatabase
└── TinyObject (e.g., "player")
    ├── TinyField (e.g., "x", "y")
    ├── TinyString (e.g., "name")
    └── TinyArray (e.g., "inventory")
```

### Creating a Database

```java
import TransmuteCore.Serialization.*;

// Create database
TinyDatabase db = new TinyDatabase();

// Create object
TinyObject player = new TinyObject("player");

// Add fields
player.addField("x", new TinyField("x", 100));
player.addField("y", new TinyField("y", 200));
player.addField("health", new TinyField("health", 80));

// Add to database
db.addObject(player);

// Save to file
db.serializeToFile("saves/player.save");
```

### Loading a Database

```java
// Load from file
TinyDatabase db = TinyDatabase.DeserializeFromFile("saves/player.save");

// Get object
TinyObject player = db.getObject("player");

// Read fields
int x = player.getField("x").getIntValue();
int y = player.getField("y").getIntValue();
int health = player.getField("health").getIntValue();
```

---

## Saving Game Data

### Complete Save Example

```java
public class GameSaveManager {
    
    public static void saveGame(String filename, GameState gameState) {
        TinyDatabase db = new TinyDatabase();
        
        // Save player data
        TinyObject playerObj = new TinyObject("player");
        playerObj.addField("x", new TinyField("x", gameState.player.getX()));
        playerObj.addField("y", new TinyField("y", gameState.player.getY()));
        playerObj.addField("health", new TinyField("health", gameState.player.getHealth()));
        playerObj.addField("score", new TinyField("score", gameState.player.getScore()));
        db.addObject(playerObj);
        
        // Save game progress
        TinyObject progressObj = new TinyObject("progress");
        progressObj.addField("currentLevel", new TinyField("currentLevel", gameState.currentLevel));
        progressObj.addField("totalTime", new TinyField("totalTime", gameState.totalTime));
        progressObj.addField("timestamp", new TinyField("timestamp", System.currentTimeMillis()));
        db.addObject(progressObj);
        
        // Save inventory
        TinyObject inventoryObj = new TinyObject("inventory");
        TinyArray items = new TinyArray("items");
        for (String item : gameState.player.getInventory()) {
            items.add(new TinyString(item));
        }
        inventoryObj.addArray(items);
        db.addObject(inventoryObj);
        
        // Write to file
        db.serializeToFile(filename);
        Logger.info("Game saved to: %s", filename);
    }
}
```

### Save Multiple Slots

```java
public class SaveSlotManager {
    private static final String SAVE_DIR = "saves/";
    private static final int MAX_SLOTS = 3;
    
    public static void saveToSlot(int slot, GameState gameState) {
        if (slot < 0 || slot >= MAX_SLOTS) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }
        
        String filename = SAVE_DIR + "slot" + slot + ".save";
        GameSaveManager.saveGame(filename, gameState);
    }
    
    public static boolean slotExists(int slot) {
        String filename = SAVE_DIR + "slot" + slot + ".save";
        return new File(filename).exists();
    }
    
    public static SaveInfo getSlotInfo(int slot) {
        if (!slotExists(slot)) return null;
        
        String filename = SAVE_DIR + "slot" + slot + ".save";
        TinyDatabase db = TinyDatabase.DeserializeFromFile(filename);
        
        TinyObject progress = db.getObject("progress");
        if (progress == null) return null;
        
        SaveInfo info = new SaveInfo();
        info.slot = slot;
        info.level = progress.getField("currentLevel").getIntValue();
        info.timestamp = progress.getField("timestamp").getLongValue();
        
        return info;
    }
}

class SaveInfo {
    int slot;
    int level;
    long timestamp;
    
    public String getFormattedDate() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date(timestamp));
    }
}
```

---

## Loading Game Data

### Complete Load Example

```java
public class GameLoadManager {
    
    public static GameState loadGame(String filename) {
        try {
            TinyDatabase db = TinyDatabase.DeserializeFromFile(filename);
            GameState gameState = new GameState();
            
            // Load player data
            TinyObject playerObj = db.getObject("player");
            if (playerObj != null) {
                gameState.player.setX(playerObj.getField("x").getIntValue());
                gameState.player.setY(playerObj.getField("y").getIntValue());
                gameState.player.setHealth(playerObj.getField("health").getIntValue());
                gameState.player.setScore(playerObj.getField("score").getIntValue());
            }
            
            // Load progress
            TinyObject progressObj = db.getObject("progress");
            if (progressObj != null) {
                gameState.currentLevel = progressObj.getField("currentLevel").getIntValue();
                gameState.totalTime = progressObj.getField("totalTime").getFloatValue();
            }
            
            // Load inventory
            TinyObject inventoryObj = db.getObject("inventory");
            if (inventoryObj != null) {
                TinyArray items = inventoryObj.getArray("items");
                for (int i = 0; i < items.size(); i++) {
                    TinyString item = items.getString(i);
                    gameState.player.addItem(item.getValue());
                }
            }
            
            Logger.info("Game loaded from: %s", filename);
            return gameState;
            
        } catch (Exception e) {
            Logger.error("Failed to load game", e);
            return null;
        }
    }
}
```

### Safe Loading with Validation

```java
public static GameState loadGameSafe(String filename) {
    TinyDatabase db = TinyDatabase.DeserializeFromFile(filename);
    
    // Validate database
    if (!validateSaveFile(db)) {
        Logger.error("Save file corrupted: %s", filename);
        return null;
    }
    
    return loadGame(filename);
}

private static boolean validateSaveFile(TinyDatabase db) {
    // Check required objects exist
    if (db.getObject("player") == null) return false;
    if (db.getObject("progress") == null) return false;
    
    // Check required fields
    TinyObject player = db.getObject("player");
    if (player.getField("x") == null) return false;
    if (player.getField("y") == null) return false;
    if (player.getField("health") == null) return false;
    
    // Validate values
    int health = player.getField("health").getIntValue();
    if (health < 0 || health > 100) return false;
    
    return true;
}
```

---

## Data Types

### TinyField (Numbers)

```java
// Integer
TinyField intField = new TinyField("age", 25);
int value = field.getIntValue();

// Float
TinyField floatField = new TinyField("speed", 5.5f);
float value = field.getFloatValue();

// Long
TinyField longField = new TinyField("timestamp", System.currentTimeMillis());
long value = field.getLongValue();

// Boolean (stored as int)
TinyField boolField = new TinyField("unlocked", 1);  // 1 = true, 0 = false
boolean value = field.getIntValue() == 1;
```

### TinyString

```java
// Create string
TinyString nameField = new TinyString("name", "Player1");
String value = nameField.getValue();

// Add to object
object.addString("playerName", new TinyString("playerName", "Hero"));

// Retrieve
TinyString str = object.getString("playerName");
String name = str.getValue();
```

### TinyArray

```java
// Create array
TinyArray items = new TinyArray("items");

// Add strings
items.add(new TinyString("sword"));
items.add(new TinyString("shield"));
items.add(new TinyString("potion"));

// Add to object
object.addArray(items);

// Retrieve and iterate
TinyArray loadedItems = object.getArray("items");
for (int i = 0; i < loadedItems.size(); i++) {
    TinyString item = loadedItems.getString(i);
    System.out.println(item.getValue());
}

// Array of numbers
TinyArray scores = new TinyArray("highScores");
scores.add(new TinyField("score1", 1000));
scores.add(new TinyField("score2", 850));
scores.add(new TinyField("score3", 700));
```

### Nested Objects

```java
// Create nested structure
TinyObject weapon = new TinyObject("weapon");
weapon.addString("type", new TinyString("type", "sword"));
weapon.addField("damage", new TinyField("damage", 25));
weapon.addField("durability", new TinyField("durability", 100));

TinyObject player = new TinyObject("player");
player.addString("name", new TinyString("name", "Hero"));
// Note: TinyDatabase doesn't directly support nested objects
// Workaround: Use prefixed names or separate objects

// Alternative approach:
TinyObject playerWeapon = new TinyObject("player_weapon");
playerWeapon.addString("type", new TinyString("type", "sword"));
playerWeapon.addField("damage", new TinyField("damage", 25));
db.addObject(playerWeapon);
```

---

## Best Practices

### Save File Organization

```java
public class GameData {
    // Use consistent naming
    public static final String SAVE_DIR = "saves/";
    public static final String SETTINGS_FILE = "settings.dat";
    public static final String PROGRESS_FILE = "progress.dat";
    
    // Version your save format
    private static final int SAVE_VERSION = 1;
    
    public static void save(GameState state) {
        TinyDatabase db = new TinyDatabase();
        
        // Always include version
        TinyObject meta = new TinyObject("meta");
        meta.addField("version", new TinyField("version", SAVE_VERSION));
        db.addObject(meta);
        
        // ... save other data
        
        db.serializeToFile(SAVE_DIR + "save.dat");
    }
    
    public static GameState load() {
        TinyDatabase db = TinyDatabase.DeserializeFromFile(SAVE_DIR + "save.dat");
        
        // Check version
        TinyObject meta = db.getObject("meta");
        int version = meta.getField("version").getIntValue();
        
        if (version != SAVE_VERSION) {
            // Handle migration or show error
            return migrateFromVersion(db, version);
        }
        
        // ... load data
    }
}
```

### Auto-Save

```java
public class AutoSaveManager {
    private long lastAutoSave = 0;
    private final long AUTO_SAVE_INTERVAL = 60000;  // 1 minute
    
    public void update(GameState gameState) {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastAutoSave > AUTO_SAVE_INTERVAL) {
            autoSave(gameState);
            lastAutoSave = currentTime;
        }
    }
    
    private void autoSave(GameState gameState) {
        try {
            GameSaveManager.saveGame("saves/autosave.dat", gameState);
            Logger.info("Auto-save complete");
        } catch (Exception e) {
            Logger.error("Auto-save failed", e);
        }
    }
}
```

### Backup Strategy

```java
public static void saveWithBackup(String filename, GameState gameState) {
    String backupFile = filename + ".backup";
    
    // If save exists, back it up first
    File saveFile = new File(filename);
    if (saveFile.exists()) {
        File backup = new File(backupFile);
        saveFile.renameTo(backup);
    }
    
    // Save new data
    try {
        GameSaveManager.saveGame(filename, gameState);
        
        // Delete backup if save succeeded
        new File(backupFile).delete();
    } catch (Exception e) {
        // Restore backup if save failed
        File backup = new File(backupFile);
        if (backup.exists()) {
            backup.renameTo(new File(filename));
        }
        throw e;
    }
}
```

---

## Advanced Usage

### Compression

```java
import java.util.zip.*;

public static void saveCompressed(String filename, TinyDatabase db) throws IOException {
    // Serialize to bytes
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    // Note: TinyDatabase needs to implement Serializable for this
    
    // Compress
    FileOutputStream fos = new FileOutputStream(filename);
    GZIPOutputStream gzos = new GZIPOutputStream(fos);
    gzos.write(baos.toByteArray());
    gzos.close();
}
```

### Encryption (Simple XOR)

```java
public static void saveEncrypted(String filename, TinyDatabase db, byte key) {
    // Save to temp file
    String tempFile = filename + ".tmp";
    db.serializeToFile(tempFile);
    
    // Read and encrypt
    try {
        byte[] data = Files.readAllBytes(Paths.get(tempFile));
        
        // XOR encryption (simple, not secure for real use)
        for (int i = 0; i < data.length; i++) {
            data[i] ^= key;
        }
        
        // Write encrypted data
        Files.write(Paths.get(filename), data);
        
        // Delete temp
        new File(tempFile).delete();
    } catch (IOException e) {
        Logger.error("Encryption failed", e);
    }
}
```

### Migration Between Versions

```java
public static GameState migrateFromVersion(TinyDatabase db, int fromVersion) {
    switch (fromVersion) {
        case 1:
            // Migrate from v1 to v2
            TinyObject player = db.getObject("player");
            // Add new field that didn't exist in v1
            if (player.getField("stamina") == null) {
                player.addField("stamina", new TinyField("stamina", 100));
            }
            // Fall through to next version
        case 2:
            // Migrate from v2 to v3
            // ... more migrations
            break;
    }
    
    return loadFromMigratedDatabase(db);
}
```

### Save File Inspector

```java
public class SaveFileInspector {
    public static void inspect(String filename) {
        TinyDatabase db = TinyDatabase.DeserializeFromFile(filename);
        
        System.out.println("=== Save File: " + filename + " ===");
        
        // List all objects
        for (TinyObject obj : db.getAllObjects()) {
            System.out.println("\nObject: " + obj.getName());
            
            // List fields
            for (TinyField field : obj.getAllFields()) {
                System.out.println("  " + field.getName() + " = " + field.getValue());
            }
            
            // List strings
            for (TinyString str : obj.getAllStrings()) {
                System.out.println("  " + str.getName() + " = " + str.getValue());
            }
            
            // List arrays
            for (TinyArray arr : obj.getAllArrays()) {
                System.out.println("  " + arr.getName() + " [" + arr.size() + " items]");
            }
        }
    }
}
```

---

## Common Patterns

### Settings Persistence

```java
public class Settings {
    private float masterVolume = 1.0f;
    private float musicVolume = 0.8f;
    private float sfxVolume = 1.0f;
    private boolean fullscreen = false;
    
    public void save() {
        TinyDatabase db = new TinyDatabase();
        TinyObject settings = new TinyObject("settings");
        
        settings.addField("masterVolume", new TinyField("masterVolume", masterVolume));
        settings.addField("musicVolume", new TinyField("musicVolume", musicVolume));
        settings.addField("sfxVolume", new TinyField("sfxVolume", sfxVolume));
        settings.addField("fullscreen", new TinyField("fullscreen", fullscreen ? 1 : 0));
        
        db.addObject(settings);
        db.serializeToFile("settings.dat");
    }
    
    public void load() {
        TinyDatabase db = TinyDatabase.DeserializeFromFile("settings.dat");
        TinyObject settings = db.getObject("settings");
        
        if (settings != null) {
            masterVolume = settings.getField("masterVolume").getFloatValue();
            musicVolume = settings.getField("musicVolume").getFloatValue();
            sfxVolume = settings.getField("sfxVolume").getFloatValue();
            fullscreen = settings.getField("fullscreen").getIntValue() == 1;
        }
    }
}
```

---

## See Also

- [Tutorial 5: State Management](tutorials/05-state-management.md) - State data persistence
- [DX_FEATURES.md](DX_FEATURES.md) - StateSaver and StateDiff tools
- [COOKBOOK.md](COOKBOOK.md) - Save/load patterns
