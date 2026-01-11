package TransmuteCore.System.Debug;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Level.Level;
import TransmuteCore.Objects.Object;
import TransmuteCore.Objects.ObjectManager;
import TransmuteCore.States.State;
import TransmuteCore.System.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for inspecting and dumping game state for debugging.
 * Exports game state, level data, and object lists to JSON format.
 * <p>
 * Usage:
 * <pre>
 * {@code
 * // Dump current game state
 * StateInspector.dumpGameState(manager, "debug/state.json");
 * 
 * // Dump level data
 * StateInspector.dumpLevel(level, "debug/level.json");
 * 
 * // Dump all objects
 * StateInspector.dumpObjectList(objectManager, "debug/objects.json");
 * }
 * </pre>
 */
public class StateInspector
{
    private static final String DEFAULT_OUTPUT_DIR = "debug";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Dumps the current game state to a JSON file.
     *
     * @param manager  The game manager.
     * @param filename Output filename.
     * @return True if successful.
     */
    public static boolean dumpGameState(Manager manager, String filename)
    {
        if (manager == null)
        {
            Logger.error("Cannot dump game state: manager is null");
            return false;
        }
        
        try
        {
            ensureOutputDirectory();
            
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": \"").append(new Date().toString()).append("\",\n");
            json.append("  \"engine\": {\n");
            json.append("    \"name\": \"TransmuteCore\",\n");
            json.append("    \"targetFPS\": ").append(manager.getGameWindow() != null ? 60 : 0).append("\n");
            json.append("  },\n");
            
            // State manager info
            if (manager.getStateManager() != null)
            {
                json.append("  \"stateManager\": {\n");
                json.append("    \"stackSize\": ").append(manager.getStateManager().getStackSize()).append(",\n");
                
                State currentState = manager.getStateManager().peek();
                if (currentState != null)
                {
                    json.append("    \"currentState\": {\n");
                    json.append("      \"name\": \"").append(currentState.getName()).append("\",\n");
                    json.append("      \"type\": \"").append(currentState.getClass().getSimpleName()).append("\"\n");
                    json.append("    }\n");
                }
                else
                {
                    json.append("    \"currentState\": null\n");
                }
                
                json.append("  },\n");
            }
            
            // Input info
            if (manager.getInput() != null)
            {
                json.append("  \"input\": {\n");
                json.append("    \"mouseX\": ").append(manager.getInput().getMouseX()).append(",\n");
                json.append("    \"mouseY\": ").append(manager.getInput().getMouseY()).append("\n");
                json.append("  },\n");
            }
            
            // Memory info
            Runtime runtime = Runtime.getRuntime();
            json.append("  \"memory\": {\n");
            json.append("    \"used\": ").append(runtime.totalMemory() - runtime.freeMemory()).append(",\n");
            json.append("    \"total\": ").append(runtime.totalMemory()).append(",\n");
            json.append("    \"max\": ").append(runtime.maxMemory()).append("\n");
            json.append("  }\n");
            
            json.append("}\n");
            
            return writeToFile(filename, json.toString());
        }
        catch (Exception e)
        {
            Logger.error("Failed to dump game state", e);
            return false;
        }
    }
    
    /**
     * Dumps level data to a JSON file.
     *
     * @param level    The level to dump.
     * @param filename Output filename.
     * @return True if successful.
     */
    public static boolean dumpLevel(Level level, String filename)
    {
        if (level == null)
        {
            Logger.error("Cannot dump level: level is null");
            return false;
        }
        
        try
        {
            ensureOutputDirectory();
            
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": \"").append(new Date().toString()).append("\",\n");
            json.append("  \"level\": {\n");
            json.append("    \"type\": \"").append(level.getClass().getSimpleName()).append("\",\n");
            json.append("    \"width\": ").append(level.getWidth()).append(",\n");
            json.append("    \"height\": ").append(level.getHeight()).append("\n");
            json.append("  }\n");
            json.append("}\n");
            
            return writeToFile(filename, json.toString());
        }
        catch (Exception e)
        {
            Logger.error("Failed to dump level", e);
            return false;
        }
    }
    
    /**
     * Dumps all objects from an ObjectManager to a JSON file.
     *
     * @param objectManager The object manager.
     * @param filename      Output filename.
     * @return True if successful.
     */
    public static boolean dumpObjectList(ObjectManager objectManager, String filename)
    {
        if (objectManager == null)
        {
            Logger.error("Cannot dump objects: objectManager is null");
            return false;
        }
        
        try
        {
            ensureOutputDirectory();
            
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": \"").append(new Date().toString()).append("\",\n");
            json.append("  \"objectCount\": ").append(objectManager.objectList.size()).append(",\n");
            json.append("  \"objects\": [\n");
            
            for (int i = 0; i < objectManager.objectList.size(); i++)
            {
                Object obj = objectManager.objectList.get(i);
                json.append("    {\n");
                json.append("      \"index\": ").append(i).append(",\n");
                json.append("      \"type\": \"").append(obj.getClass().getSimpleName()).append("\",\n");
                json.append("      \"x\": ").append(obj.getX()).append(",\n");
                json.append("      \"y\": ").append(obj.getY()).append("\n");
                json.append("    }");
                
                if (i < objectManager.objectList.size() - 1)
                {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}\n");
            
            return writeToFile(filename, json.toString());
        }
        catch (Exception e)
        {
            Logger.error("Failed to dump object list", e);
            return false;
        }
    }
    
    /**
     * Dumps detailed information about a specific object.
     *
     * @param obj      The object to inspect.
     * @param filename Output filename.
     * @return True if successful.
     */
    public static boolean inspectObject(Object obj, String filename)
    {
        if (obj == null)
        {
            Logger.error("Cannot inspect object: object is null");
            return false;
        }
        
        try
        {
            ensureOutputDirectory();
            
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": \"").append(new Date().toString()).append("\",\n");
            json.append("  \"object\": {\n");
            json.append("    \"type\": \"").append(obj.getClass().getSimpleName()).append("\",\n");
            json.append("    \"className\": \"").append(obj.getClass().getName()).append("\",\n");
            json.append("    \"position\": {\n");
            json.append("      \"x\": ").append(obj.getX()).append(",\n");
            json.append("      \"y\": ").append(obj.getY()).append("\n");
            json.append("    }\n");
            json.append("  }\n");
            json.append("}\n");
            
            return writeToFile(filename, json.toString());
        }
        catch (Exception e)
        {
            Logger.error("Failed to inspect object", e);
            return false;
        }
    }
    
    /**
     * Dumps game state with a timestamped filename.
     *
     * @param manager The game manager.
     * @return True if successful.
     */
    public static boolean dumpGameState(Manager manager)
    {
        String timestamp = dateFormat.format(new Date());
        String filename = DEFAULT_OUTPUT_DIR + "/state_" + timestamp + ".json";
        return dumpGameState(manager, filename);
    }
    
    /**
     * Dumps level data with a timestamped filename.
     *
     * @param level The level to dump.
     * @return True if successful.
     */
    public static boolean dumpLevel(Level level)
    {
        String timestamp = dateFormat.format(new Date());
        String filename = DEFAULT_OUTPUT_DIR + "/level_" + timestamp + ".json";
        return dumpLevel(level, filename);
    }
    
    /**
     * Dumps object list with a timestamped filename.
     *
     * @param objectManager The object manager.
     * @return True if successful.
     */
    public static boolean dumpObjectList(ObjectManager objectManager)
    {
        String timestamp = dateFormat.format(new Date());
        String filename = DEFAULT_OUTPUT_DIR + "/objects_" + timestamp + ".json";
        return dumpObjectList(objectManager, filename);
    }
    
    /**
     * Ensures the output directory exists.
     */
    private static void ensureOutputDirectory()
    {
        File dir = new File(DEFAULT_OUTPUT_DIR);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
    }
    
    /**
     * Writes content to a file.
     */
    private static boolean writeToFile(String filename, String content)
    {
        try (FileWriter writer = new FileWriter(filename))
        {
            writer.write(content);
            Logger.info("State dump saved: %s", filename);
            return true;
        }
        catch (IOException e)
        {
            Logger.error("Failed to write file: %s", filename);
            return false;
        }
    }
    
    private StateInspector()
    {
    }
}
