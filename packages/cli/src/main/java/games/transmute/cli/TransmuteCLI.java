package games.transmute.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Main CLI entry point for the Transmute project generator.
 * 
 * Usage:
 *   transmute new <project-name> [options]
 *   transmute --help
 *   transmute --version
 */
public class TransmuteCLI {
    
    private static final String VERSION = "0.1.0-ALPHA";
    private static final List<String> AVAILABLE_TEMPLATES = Arrays.asList("basic", "platformer", "rpg");
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
        }
        
        String command = args[0];
        
        switch (command) {
            case "new":
                handleNewProject(args);
                break;
            case "--help":
            case "-h":
            case "help":
                printHelp();
                break;
            case "--version":
            case "-v":
            case "version":
                printVersion();
                break;
            case "list":
            case "templates":
                listTemplates();
                break;
            default:
                System.err.println("Unknown command: " + command);
                System.err.println("Run 'transmute --help' for usage information.");
                System.exit(1);
        }
    }
    
    private static void handleNewProject(String[] args) {
        if (args.length < 2) {
            System.err.println("Error: Project name required");
            System.err.println("Usage: transmute new <project-name> [options]");
            System.exit(1);
        }
        
        String projectName = args[1];
        String template = "basic"; // Default template
        boolean interactive = true;
        
        // Parse options
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--template") || arg.equals("-t")) {
                if (i + 1 < args.length) {
                    template = args[++i];
                } else {
                    System.err.println("Error: --template requires a value");
                    System.exit(1);
                }
            } else if (arg.equals("--no-interactive") || arg.equals("-y")) {
                interactive = false;
            }
        }
        
        // Validate template
        if (!AVAILABLE_TEMPLATES.contains(template)) {
            System.err.println("Error: Unknown template '" + template + "'");
            System.err.println("Available templates: " + String.join(", ", AVAILABLE_TEMPLATES));
            System.exit(1);
        }
        
        try {
            ProjectGenerator generator = new ProjectGenerator();
            
            if (interactive) {
                runInteractiveMode(generator, projectName, template);
            } else {
                generator.generateProject(projectName, template, new ProjectConfig());
            }
            
            printSuccessMessage(projectName);
        } catch (IOException e) {
            System.err.println("Error generating project: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void runInteractiveMode(ProjectGenerator generator, String projectName, String defaultTemplate) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ProjectConfig config = new ProjectConfig();
        
        System.out.println("\n=== Transmute Project Generator ===\n");
        System.out.println("Creating project: " + projectName);
        System.out.println();
        
        // Template selection
        System.out.println("Available templates:");
        for (int i = 0; i < AVAILABLE_TEMPLATES.size(); i++) {
            String tmpl = AVAILABLE_TEMPLATES.get(i);
            String marker = tmpl.equals(defaultTemplate) ? " (default)" : "";
            System.out.println("  " + (i + 1) + ". " + tmpl + marker);
        }
        System.out.print("\nSelect template [" + defaultTemplate + "]: ");
        String templateInput = reader.readLine().trim();
        String template = templateInput.isEmpty() ? defaultTemplate : templateInput;
        
        if (!AVAILABLE_TEMPLATES.contains(template)) {
            // Try parsing as number
            try {
                int index = Integer.parseInt(template) - 1;
                if (index >= 0 && index < AVAILABLE_TEMPLATES.size()) {
                    template = AVAILABLE_TEMPLATES.get(index);
                } else {
                    template = defaultTemplate;
                }
            } catch (NumberFormatException e) {
                template = defaultTemplate;
            }
        }
        
        // Game configuration
        System.out.print("\nGame Title [" + projectName + "]: ");
        String title = reader.readLine().trim();
        config.gameTitle = title.isEmpty() ? projectName : title;
        
        System.out.print("Game Version [1.0.0]: ");
        String version = reader.readLine().trim();
        config.gameVersion = version.isEmpty() ? "1.0.0" : version;
        
        System.out.print("Screen Width [640]: ");
        String widthStr = reader.readLine().trim();
        config.screenWidth = widthStr.isEmpty() ? 640 : Integer.parseInt(widthStr);
        
        System.out.print("Screen Scale [2]: ");
        String scaleStr = reader.readLine().trim();
        config.screenScale = scaleStr.isEmpty() ? 2 : Integer.parseInt(scaleStr);
        
        System.out.print("Package Name [com.example." + projectName.toLowerCase() + "]: ");
        String pkg = reader.readLine().trim();
        config.packageName = pkg.isEmpty() ? "com.example." + projectName.toLowerCase() : pkg;
        
        System.out.println("\n--- Configuration ---");
        System.out.println("Template: " + template);
        System.out.println("Title: " + config.gameTitle);
        System.out.println("Version: " + config.gameVersion);
        System.out.println("Screen: " + config.screenWidth + "x" + (config.screenWidth * 3 / 4) + " @ " + config.screenScale + "x");
        System.out.println("Package: " + config.packageName);
        System.out.println();
        
        generator.generateProject(projectName, template, config);
    }
    
    private static void printSuccessMessage(String projectName) {
        System.out.println("\n✓ Project '" + projectName + "' created successfully!");
        System.out.println("\nNext steps:");
        System.out.println("  cd " + projectName);
        System.out.println("  ./gradlew run");
        System.out.println("\nFor more information, see the README.md file in your project.");
    }
    
    private static void printUsage() {
        System.out.println("Transmute CLI - Project Generator for TransmuteCore Engine");
        System.out.println("\nUsage:");
        System.out.println("  transmute new <project-name> [options]");
        System.out.println("  transmute templates");
        System.out.println("  transmute --help");
        System.out.println("  transmute --version");
        System.out.println("\nRun 'transmute --help' for more information.");
    }
    
    private static void printHelp() {
        System.out.println("Transmute CLI v" + VERSION);
        System.out.println("Project Generator for TransmuteCore Engine");
        System.out.println("\nUSAGE:");
        System.out.println("  transmute <command> [options]");
        System.out.println("\nCOMMANDS:");
        System.out.println("  new <name>      Create a new project");
        System.out.println("  templates       List available templates");
        System.out.println("  help            Show this help message");
        System.out.println("  version         Show version information");
        System.out.println("\nOPTIONS:");
        System.out.println("  -t, --template <name>   Specify template (default: basic)");
        System.out.println("  -y, --no-interactive    Skip interactive prompts");
        System.out.println("\nEXAMPLES:");
        System.out.println("  # Create a basic project interactively");
        System.out.println("  transmute new my-game");
        System.out.println("\n  # Create a platformer project non-interactively");
        System.out.println("  transmute new my-platformer -t platformer -y");
        System.out.println("\n  # List available templates");
        System.out.println("  transmute templates");
        System.out.println("\nFor more information, visit:");
        System.out.println("  https://github.com/transmute-games/transmute-core");
    }
    
    private static void printVersion() {
        System.out.println("Transmute CLI v" + VERSION);
        System.out.println("TransmuteCore Engine v0.1.0-ALPHA");
    }
    
    private static void listTemplates() {
        System.out.println("Available templates:");
        System.out.println("\n  basic      - Minimal game with simple rendering");
        System.out.println("  platformer - 2D platformer with physics and collision");
        System.out.println("  rpg        - Top-down RPG with tile-based maps");
        System.out.println("\nUsage:");
        System.out.println("  transmute new <project-name> --template <template>");
    }
}
