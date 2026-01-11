package TransmuteCore.data;

import TransmuteCore.data.TinyDatabase;
import TransmuteCore.util.Logger;

import java.util.*;

/**
 * Utility for comparing game states and identifying differences.
 * <p>
 * Helps debug save/load issues by showing what changed between two states.
 * Useful for validating serialization logic and tracking state mutations.
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * // Save initial state
 * StateSaver saver = new StateSaver();
 * saver.quickSave();
 * TinyDatabase before = saver.getCurrentState();
 * 
 * // ... game plays, state changes ...
 * 
 * // Save new state
 * saver.save("after");
 * TinyDatabase after = saver.getCurrentState();
 * 
 * // Compare states
 * StateDiff diff = StateDiff.compare(before, after);
 * diff.print();
 * 
 * // Or just print changes
 * StateDiff.printChanges(before, after);
 * }</pre>
 * 
 * @author TransmuteCore
 * @version 1.0
 */
public class StateDiff {
    
    /**
     * Type of change detected.
     */
    public enum ChangeType {
        ADDED,
        REMOVED,
        MODIFIED,
        UNCHANGED
    }
    
    /**
     * A detected change in state.
     */
    public static class Change {
        public ChangeType type;
        public String path;
        public String oldValue;
        public String newValue;
        
        public Change(ChangeType type, String path, String oldValue, String newValue) {
            this.type = type;
            this.path = path;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
        
        @Override
        public String toString() {
            switch (type) {
                case ADDED:
                    return "[+] " + path + " = " + newValue;
                case REMOVED:
                    return "[-] " + path + " = " + oldValue;
                case MODIFIED:
                    return "[~] " + path + ": " + oldValue + " → " + newValue;
                case UNCHANGED:
                    return "[ ] " + path + " = " + oldValue;
                default:
                    return "";
            }
        }
    }
    
    private List<Change> changes = new ArrayList<>();
    private int addedCount = 0;
    private int removedCount = 0;
    private int modifiedCount = 0;
    private int unchangedCount = 0;
    
    /**
     * Get all detected changes.
     * 
     * @return List of changes
     */
    public List<Change> getChanges() {
        return new ArrayList<>(changes);
    }
    
    /**
     * Get only changes of a specific type.
     * 
     * @param type Change type to filter
     * @return List of changes
     */
    public List<Change> getChanges(ChangeType type) {
        List<Change> filtered = new ArrayList<>();
        for (Change change : changes) {
            if (change.type == type) {
                filtered.add(change);
            }
        }
        return filtered;
    }
    
    /**
     * Get count of added fields.
     * 
     * @return Count
     */
    public int getAddedCount() {
        return addedCount;
    }
    
    /**
     * Get count of removed fields.
     * 
     * @return Count
     */
    public int getRemovedCount() {
        return removedCount;
    }
    
    /**
     * Get count of modified fields.
     * 
     * @return Count
     */
    public int getModifiedCount() {
        return modifiedCount;
    }
    
    /**
     * Get count of unchanged fields.
     * 
     * @return Count
     */
    public int getUnchangedCount() {
        return unchangedCount;
    }
    
    /**
     * Check if any changes were detected.
     * 
     * @return True if there are changes
     */
    public boolean hasChanges() {
        return addedCount > 0 || removedCount > 0 || modifiedCount > 0;
    }
    
    /**
     * Print all changes to console.
     */
    public void print() {
        print(false);
    }
    
    /**
     * Print changes to console.
     * 
     * @param includeUnchanged Include unchanged fields
     */
    public void print(boolean includeUnchanged) {
        System.out.println("========== STATE DIFF ==========");
        System.out.println("Added:     " + addedCount);
        System.out.println("Removed:   " + removedCount);
        System.out.println("Modified:  " + modifiedCount);
        System.out.println("Unchanged: " + unchangedCount);
        System.out.println("================================");
        
        for (Change change : changes) {
            if (!includeUnchanged && change.type == ChangeType.UNCHANGED) {
                continue;
            }
            System.out.println(change);
        }
        
        System.out.println("================================");
    }
    
    /**
     * Export diff as formatted text.
     * 
     * @param includeUnchanged Include unchanged fields
     * @return Formatted diff text
     */
    public String export(boolean includeUnchanged) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== STATE DIFF ==========\n");
        sb.append("Added:     ").append(addedCount).append("\n");
        sb.append("Removed:   ").append(removedCount).append("\n");
        sb.append("Modified:  ").append(modifiedCount).append("\n");
        sb.append("Unchanged: ").append(unchangedCount).append("\n");
        sb.append("================================\n");
        
        for (Change change : changes) {
            if (!includeUnchanged && change.type == ChangeType.UNCHANGED) {
                continue;
            }
            sb.append(change).append("\n");
        }
        
        sb.append("================================");
        return sb.toString();
    }
    
    private void addChange(Change change) {
        changes.add(change);
        
        switch (change.type) {
            case ADDED:
                addedCount++;
                break;
            case REMOVED:
                removedCount++;
                break;
            case MODIFIED:
                modifiedCount++;
                break;
            case UNCHANGED:
                unchangedCount++;
                break;
        }
    }
    
    /**
     * Compare two TinyDatabase instances and return differences.
     * Note: This is a simplified comparison that works at the object/field level.
     * For full comparison, actual TinyObject/TinyField inspection would be needed.
     * 
     * @param before State before
     * @param after State after
     * @return StateDiff containing all changes
     */
    public static StateDiff compare(TinyDatabase before, TinyDatabase after) {
        StateDiff diff = new StateDiff();
        
        if (before == null && after == null) {
            return diff;
        }
        
        if (before == null) {
            diff.addChange(new Change(ChangeType.ADDED, "database", null, after.getName()));
            return diff;
        }
        
        if (after == null) {
            diff.addChange(new Change(ChangeType.REMOVED, "database", before.getName(), null));
            return diff;
        }
        
        // Compare database names
        if (!before.getName().equals(after.getName())) {
            diff.addChange(new Change(ChangeType.MODIFIED, "database.name", 
                before.getName(), after.getName()));
        } else {
            diff.addChange(new Change(ChangeType.UNCHANGED, "database.name", 
                before.getName(), before.getName()));
        }
        
        // Note: Full comparison would require accessing TinyDatabase internals
        // For now, we provide a basic framework that can be extended
        
        Logger.info("State diff: %d added, %d removed, %d modified", 
            diff.addedCount, diff.removedCount, diff.modifiedCount);
        
        return diff;
    }
    
    /**
     * Quick print comparison of two states.
     * 
     * @param before State before
     * @param after State after
     */
    public static void printChanges(TinyDatabase before, TinyDatabase after) {
        StateDiff diff = compare(before, after);
        diff.print();
    }
    
    /**
     * Quick print comparison including unchanged fields.
     * 
     * @param before State before
     * @param after State after
     */
    public static void printAll(TinyDatabase before, TinyDatabase after) {
        StateDiff diff = compare(before, after);
        diff.print(true);
    }
    
    /**
     * Create a snapshot summary of a database for debugging.
     * 
     * @param db Database to summarize
     * @return Summary string
     */
    public static String snapshot(TinyDatabase db) {
        if (db == null) {
            return "Database: <null>";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("========== STATE SNAPSHOT ==========\n");
        sb.append("Name: ").append(db.getName()).append("\n");
        sb.append("Size: ").append(db.getSize()).append(" bytes\n");
        
        // Note: Would need to iterate through objects/fields for full snapshot
        // This provides a basic framework
        
        sb.append("====================================");
        return sb.toString();
    }
}
