package games.transmute.cli.commands;

import static games.transmute.cli.CliConstants.*;

/**
 * Command to display help information.
 */
public class HelpCommand implements Command {
    
    @Override
    public void execute(String[] args) {
        System.out.println("Transmute CLI v" + CLI_VERSION);
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
        System.out.println("  " + REPOSITORY_URL);
    }
    
    @Override
    public String getName() {
        return "help";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"--help", "-h"};
    }
    
    @Override
    public String getDescription() {
        return "Show help information";
    }
}
