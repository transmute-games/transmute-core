package games.transmute.cli.util;

import games.transmute.cli.ProjectConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static games.transmute.cli.CliConstants.*;

/**
 * Utility for interactive user prompts.
 */
public class InteractivePrompt {
    
    private final BufferedReader reader;
    
    public InteractivePrompt() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }
    
    /**
     * Prompt for a template selection.
     */
    public String promptTemplate(String defaultTemplate) throws IOException {
        System.out.println("Available templates:");
        for (int i = 0; i < AVAILABLE_TEMPLATES.size(); i++) {
            String tmpl = AVAILABLE_TEMPLATES.get(i);
            String marker = tmpl.equals(defaultTemplate) ? " (default)" : "";
            System.out.println("  " + (i + 1) + ". " + tmpl + marker);
        }
        System.out.print("\nSelect template [" + defaultTemplate + "]: ");
        String input = reader.readLine().trim();
        
        if (input.isEmpty()) {
            return defaultTemplate;
        }
        
        // Check if it's a valid template name
        if (AVAILABLE_TEMPLATES.contains(input)) {
            return input;
        }
        
        // Try parsing as number
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < AVAILABLE_TEMPLATES.size()) {
                return AVAILABLE_TEMPLATES.get(index);
            }
        } catch (NumberFormatException e) {
            // Fall through to default
        }
        
        return defaultTemplate;
    }
    
    /**
     * Prompt for a string value with a default.
     */
    public String promptString(String prompt, String defaultValue) throws IOException {
        System.out.print(prompt + " [" + defaultValue + "]: ");
        String input = reader.readLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
    
    /**
     * Prompt for an integer value with a default.
     */
    public int promptInt(String prompt, int defaultValue) throws IOException {
        System.out.print(prompt + " [" + defaultValue + "]: ");
        String input = reader.readLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Run interactive configuration for a project.
     */
    public ProjectConfig.Builder configure(String projectName, String defaultTemplate) throws IOException {
        System.out.println("\n=== Transmute Project Generator ===\n");
        System.out.println("Creating project: " + projectName);
        System.out.println();
        
        // Template selection
        String template = promptTemplate(defaultTemplate);
        
        // Game configuration
        String title = promptString("\nGame Title", projectName);
        String version = promptString("Game Version", DEFAULT_GAME_VERSION);
        int width = promptInt("Screen Width", DEFAULT_SCREEN_WIDTH);
        int scale = promptInt("Screen Scale", DEFAULT_SCREEN_SCALE);
        
        // Package name
        String sanitizedProjectName = projectName.toLowerCase().replaceAll("[^a-z0-9]", "");
        String defaultPackage = "com.example." + sanitizedProjectName;
        String packageName = promptString("Package Name", defaultPackage);
        
        return ProjectConfig.builder()
            .gameTitle(title)
            .gameVersion(version)
            .screenWidth(width)
            .screenScale(scale)
            .packageName(packageName);
    }
    
    /**
     * Display configuration summary.
     */
    public void displayConfigSummary(String template, ProjectConfig config) {
        System.out.println("\n--- Configuration ---");
        System.out.println("Template: " + template);
        System.out.println("Title: " + config.getGameTitle());
        System.out.println("Version: " + config.getGameVersion());
        System.out.println("Screen: " + config.getScreenWidth() + "x" + config.getScreenHeight() 
                         + " @ " + config.getScreenScale() + "x");
        System.out.println("Package: " + config.getPackageName());
        System.out.println();
    }
}
