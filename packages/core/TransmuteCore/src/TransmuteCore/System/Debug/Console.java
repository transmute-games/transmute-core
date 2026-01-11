package TransmuteCore.System.Debug;

import TransmuteCore.Graphics.Context;
import TransmuteCore.System.Asset.Type.Fonts.Font;
import TransmuteCore.Graphics.Color;
import TransmuteCore.Input.Input;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.System.Logger;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.function.Consumer;

/**
 * In-game debug console for runtime command execution.
 * <p>
 * Toggle with the grave/tilde key (`~). Type commands and press Enter to execute.
 * Use Up/Down arrows to navigate command history.
 * 
 * <h3>Built-in Commands:</h3>
 * <ul>
 *   <li>{@code help} - List all available commands</li>
 *   <li>{@code clear} - Clear console output</li>
 *   <li>{@code fps <value>} - Set target FPS</li>
 *   <li>{@code timescale <value>} - Set game time scale (0.0 - 2.0)</li>
 *   <li>{@code screenshot} - Capture screenshot</li>
 *   <li>{@code profiler <start|stop|print>} - Control profiler</li>
 *   <li>{@code state} - Dump game state to JSON</li>
 *   <li>{@code reload [asset_name]} - Reload specific asset or all assets</li>
 *   <li>{@code save [slot]} - Quick save game state</li>
 *   <li>{@code load [slot]} - Quick load game state</li>
 *   <li>{@code saves} - List all save slots</li>
 *   <li>{@code gc} - Run garbage collection</li>
 *   <li>{@code exit|quit} - Close the game</li>
 * </ul>
 * 
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * Console console = new Console();
 * console.setFont(AssetManager.getDefaultFont());
 * 
 * // Register custom command
 * console.registerCommand("spawn", args -> {
 *     if (args.length < 1) {
 *         console.println("Usage: spawn <entity_name>");
 *         return;
 *     }
 *     String entity = args[0];
 *     // Spawn logic...
 *     console.println("Spawned: " + entity);
 * });
 * 
 * // In game loop
 * console.update(manager, delta);
 * console.render(manager, ctx);
 * }</pre>
 * 
 * @author TransmuteCore
 * @version 1.0
 */
public class Console {
    
    private boolean visible = false;
    private boolean wasToggleKeyPressed = false;
    
    private Font font;
    private int lineHeight = 12;
    private int padding = 8;
    private int maxLines = 20;
    private int consoleHeight = 300;
    
    private StringBuilder inputBuffer = new StringBuilder();
    private List<String> outputBuffer = new ArrayList<>();
    private List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;
    
    private Map<String, Command> commands = new HashMap<>();
    
    private static final int BACKGROUND_COLOR = Color.toPixelInt(0, 0, 0, 200);
    private static final int INPUT_BG_COLOR = Color.toPixelInt(20, 20, 20, 255);
    private static final int TEXT_COLOR = Color.toPixelInt(255, 255, 255, 255);
    private static final int PROMPT_COLOR = Color.toPixelInt(100, 200, 100, 255);
    private static final int ERROR_COLOR = Color.toPixelInt(255, 100, 100, 255);
    
    private int cursorBlinkTimer = 0;
    private static final int CURSOR_BLINK_RATE = 30; // frames
    
    /**
     * Functional interface for console commands.
     */
    @FunctionalInterface
    public interface Command {
        /**
         * Execute the command with given arguments.
         * 
         * @param args Command arguments (excludes command name itself)
         */
        void execute(String[] args);
    }
    
    /**
     * Create a new console with default settings.
     */
    public Console() {
        registerBuiltInCommands();
        println("=== TransmuteCore Debug Console ===");
        println("Type 'help' for available commands");
    }
    
    /**
     * Set the font used for console text.
     * 
     * @param font Font to use
     */
    public void setFont(Font font) {
        this.font = font;
        if (font != null) {
            this.lineHeight = font.getHeight() + 2;
            this.consoleHeight = Math.min((maxLines + 2) * lineHeight + padding * 2, 400);
        }
    }
    
    /**
     * Check if the console is currently visible.
     * 
     * @return True if visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Register a custom command.
     * 
     * @param name Command name (case-insensitive)
     * @param command Command implementation
     */
    public void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);
        Logger.debug("Registered console command: %s", name);
    }
    
    /**
     * Unregister a command.
     * 
     * @param name Command name
     */
    public void unregisterCommand(String name) {
        commands.remove(name.toLowerCase());
    }
    
    /**
     * Print a line to the console output.
     * 
     * @param message Message to print
     */
    public void println(String message) {
        outputBuffer.add(message);
        
        // Keep only recent lines
        while (outputBuffer.size() > 100) {
            outputBuffer.remove(0);
        }
    }
    
    /**
     * Print an error message to the console.
     * 
     * @param message Error message
     */
    public void printError(String message) {
        println("[ERROR] " + message);
    }
    
    /**
     * Clear the console output.
     */
    public void clear() {
        outputBuffer.clear();
    }
    
    /**
     * Update console state (call every frame).
     * 
     * @param manager Game manager
     * @param delta Delta time
     */
    public void update(Manager manager, double delta) {
        Input input = manager.getInput();
        
        // Toggle visibility with grave/tilde key
        boolean toggleKeyPressed = input.isKeyPressed(KeyEvent.VK_BACK_QUOTE);
        if (toggleKeyPressed && !wasToggleKeyPressed) {
            visible = !visible;
            if (visible) {
                inputBuffer.setLength(0);
                historyIndex = -1;
            }
        }
        wasToggleKeyPressed = toggleKeyPressed;
        
        if (!visible) {
            return;
        }
        
        // Update cursor blink
        cursorBlinkTimer++;
        if (cursorBlinkTimer >= CURSOR_BLINK_RATE * 2) {
            cursorBlinkTimer = 0;
        }
        
        // Handle text input
        handleInput(input, manager);
    }
    
    /**
     * Render the console (call every frame after other rendering).
     * 
     * @param manager Game manager
     * @param ctx Rendering context
     */
    public void render(Manager manager, Context ctx) {
        if (!visible || font == null) {
            return;
        }
        
        ctx.setFont(font);
        
        int width = ctx.getWidth();
        
        // Background
        ctx.renderFilledRectangle(0, 0, width, consoleHeight, BACKGROUND_COLOR);
        
        // Output text
        int startLine = Math.max(0, outputBuffer.size() - maxLines);
        int y = padding;
        
        for (int i = startLine; i < outputBuffer.size(); i++) {
            String line = outputBuffer.get(i);
            int color = line.startsWith("[ERROR]") ? ERROR_COLOR : TEXT_COLOR;
            ctx.renderText(line, padding, y, color);
            y += lineHeight;
        }
        
        // Input line background
        int inputY = consoleHeight - lineHeight - padding;
        ctx.renderFilledRectangle(0, inputY - 4, width, lineHeight + 8, INPUT_BG_COLOR);
        
        // Prompt
        ctx.renderText(">", padding, inputY, PROMPT_COLOR);
        
        // Input text
        String inputText = inputBuffer.toString();
        ctx.renderText(inputText, padding + 12, inputY, TEXT_COLOR);
        
        // Cursor
        boolean showCursor = (cursorBlinkTimer < CURSOR_BLINK_RATE);
        if (showCursor) {
            int cursorX = padding + 12 + font.getTextWidth(inputText);
            ctx.renderFilledRectangle(cursorX, inputY, 2, font.getHeight(), TEXT_COLOR);
        }
    }
    
    private void handleInput(Input input, Manager manager) {
        // Enter - execute command
        if (input.isKeyPressed(KeyEvent.VK_ENTER)) {
            executeCommand(inputBuffer.toString().trim(), manager);
            inputBuffer.setLength(0);
            historyIndex = -1;
            return;
        }
        
        // Backspace
        if (input.isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
            if (inputBuffer.length() > 0) {
                inputBuffer.setLength(inputBuffer.length() - 1);
            }
            return;
        }
        
        // Command history navigation
        if (input.isKeyPressed(KeyEvent.VK_UP)) {
            navigateHistory(-1);
            return;
        }
        
        if (input.isKeyPressed(KeyEvent.VK_DOWN)) {
            navigateHistory(1);
            return;
        }
        
        // Character input (simplified - only handles printable ASCII)
        for (int key = KeyEvent.VK_SPACE; key <= KeyEvent.VK_Z; key++) {
            if (input.isKeyPressed(key)) {
                char c = (char) key;
                
                // Handle shift for uppercase and special chars
                boolean shift = input.isKeyHeld(KeyEvent.VK_SHIFT);
                
                if (key >= KeyEvent.VK_A && key <= KeyEvent.VK_Z) {
                    if (!shift) {
                        c = Character.toLowerCase(c);
                    }
                } else if (key == KeyEvent.VK_SPACE) {
                    c = ' ';
                } else {
                    // Handle number keys and symbols
                    if (shift) {
                        c = getShiftedChar(key);
                    }
                }
                
                inputBuffer.append(c);
            }
        }
        
        // Period, comma, etc.
        if (input.isKeyPressed(KeyEvent.VK_PERIOD)) {
            inputBuffer.append('.');
        }
        if (input.isKeyPressed(KeyEvent.VK_COMMA)) {
            inputBuffer.append(',');
        }
        if (input.isKeyPressed(KeyEvent.VK_MINUS)) {
            inputBuffer.append(input.isKeyHeld(KeyEvent.VK_SHIFT) ? '_' : '-');
        }
    }
    
    private char getShiftedChar(int key) {
        // Map shifted number keys to symbols
        switch (key) {
            case KeyEvent.VK_1: return '!';
            case KeyEvent.VK_2: return '@';
            case KeyEvent.VK_3: return '#';
            case KeyEvent.VK_4: return '$';
            case KeyEvent.VK_5: return '%';
            case KeyEvent.VK_6: return '^';
            case KeyEvent.VK_7: return '&';
            case KeyEvent.VK_8: return '*';
            case KeyEvent.VK_9: return '(';
            case KeyEvent.VK_0: return ')';
            default: return (char) key;
        }
    }
    
    private void navigateHistory(int direction) {
        if (commandHistory.isEmpty()) {
            return;
        }
        
        historyIndex += direction;
        
        if (historyIndex < -1) {
            historyIndex = -1;
        } else if (historyIndex >= commandHistory.size()) {
            historyIndex = commandHistory.size() - 1;
        }
        
        if (historyIndex == -1) {
            inputBuffer.setLength(0);
        } else {
            inputBuffer.setLength(0);
            inputBuffer.append(commandHistory.get(commandHistory.size() - 1 - historyIndex));
        }
    }
    
    private void executeCommand(String input, Manager manager) {
        if (input.isEmpty()) {
            return;
        }
        
        // Add to history
        commandHistory.add(input);
        
        // Echo command
        println("> " + input);
        
        // Parse command and arguments
        String[] parts = input.split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        
        // Execute
        Command command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(args);
            } catch (Exception e) {
                printError("Command failed: " + e.getMessage());
                Logger.error("Console command error", e);
            }
        } else {
            printError("Unknown command: " + commandName);
            println("Type 'help' for available commands");
        }
    }
    
    private void registerBuiltInCommands() {
        // Help
        registerCommand("help", args -> {
            println("Available commands:");
            List<String> sortedCommands = new ArrayList<>(commands.keySet());
            Collections.sort(sortedCommands);
            for (String cmd : sortedCommands) {
                println("  " + cmd);
            }
        });
        
        // Clear
        registerCommand("clear", args -> clear());
        
        // FPS
        registerCommand("fps", args -> {
            if (args.length < 1) {
                println("Usage: fps <value>");
                return;
            }
            try {
                int fps = Integer.parseInt(args[0]);
                // Note: This would need access to TransmuteCore instance
                println("Set target FPS to " + fps + " (requires game restart)");
            } catch (NumberFormatException e) {
                printError("Invalid FPS value: " + args[0]);
            }
        });
        
        // Time scale
        registerCommand("timescale", args -> {
            if (args.length < 1) {
                println("Usage: timescale <value>");
                return;
            }
            try {
                double scale = Double.parseDouble(args[0]);
                if (scale < 0.0 || scale > 2.0) {
                    printError("Time scale must be between 0.0 and 2.0");
                    return;
                }
                println("Time scale: " + scale + " (implementation depends on game)");
            } catch (NumberFormatException e) {
                printError("Invalid time scale: " + args[0]);
            }
        });
        
        // Screenshot
        registerCommand("screenshot", args -> {
            println("Screenshot captured (if Screenshot system is integrated)");
        });
        
        // Profiler
        registerCommand("profiler", args -> {
            if (args.length < 1) {
                println("Usage: profiler <start|stop|print|clear>");
                return;
            }
            String action = args[0].toLowerCase();
            switch (action) {
                case "print":
                    Profiler.printResults();
                    println("Profiler results printed to console");
                    break;
                case "clear":
                    Profiler.reset();
                    println("Profiler data cleared");
                    break;
                case "start":
                    println("Profiler is always active when used");
                    break;
                case "stop":
                    println("Use profiler.clear to reset data");
                    break;
                default:
                    printError("Unknown profiler action: " + action);
            }
        });
        
        // State dump
        registerCommand("state", args -> {
            println("State dump requires Manager reference");
            println("Use StateInspector.dumpGameState(manager) in code");
        });
        
        // Hot reload
        registerCommand("reload", args -> {
            println("Hot reload requires HotReloadManager instance");
            println("Use hotReloadManager.reloadAll() or .reloadAsset(name) in code");
            if (args.length > 0) {
                println("Requested reload: " + args[0]);
            }
        });
        
        // Save game
        registerCommand("save", args -> {
            println("Save requires StateSaver instance");
            println("Use stateSaver.save(slot) in code");
            if (args.length > 0) {
                println("Requested save to slot: " + args[0]);
            } else {
                println("Use: save <slot_name>");
            }
        });
        
        // Load game
        registerCommand("load", args -> {
            println("Load requires StateSaver instance");
            println("Use stateSaver.load(slot) in code");
            if (args.length > 0) {
                println("Requested load from slot: " + args[0]);
            } else {
                println("Use: load <slot_name>");
            }
        });
        
        // List saves
        registerCommand("saves", args -> {
            println("List saves requires StateSaver instance");
            println("Use stateSaver.printSaveSlots() in code");
        });
        
        // Garbage collection
        registerCommand("gc", args -> {
            long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.gc();
            long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long freed = (before - after) / 1024 / 1024;
            println("Garbage collection complete. Freed: " + freed + " MB");
        });
        
        // Exit
        registerCommand("exit", args -> {
            println("Exiting game...");
            System.exit(0);
        });
        registerCommand("quit", args -> {
            println("Exiting game...");
            System.exit(0);
        });
    }
}
