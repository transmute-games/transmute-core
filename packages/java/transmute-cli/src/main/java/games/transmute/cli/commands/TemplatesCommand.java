package games.transmute.cli.commands;

import static games.transmute.cli.CliConstants.*;

/**
 * Command to list available templates.
 */
public class TemplatesCommand implements Command {
    
    @Override
    public void execute(String[] args) {
        System.out.println("Available templates:");
        System.out.println("\n  " + TEMPLATE_BASIC + " - " + DESC_BASIC);
        System.out.println("  " + TEMPLATE_PLATFORMER + " - " + DESC_PLATFORMER);
        System.out.println("  " + TEMPLATE_RPG + " - " + DESC_RPG);
        System.out.println("\nUsage:");
        System.out.println("  transmute new <project-name> --template <template>");
    }
    
    @Override
    public String getName() {
        return "templates";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"list"};
    }
    
    @Override
    public String getDescription() {
        return "List available templates";
    }
}
