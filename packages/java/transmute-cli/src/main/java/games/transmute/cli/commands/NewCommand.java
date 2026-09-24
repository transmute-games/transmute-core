package games.transmute.cli.commands;

import games.transmute.cli.ProjectConfig;
import games.transmute.cli.ProjectGenerator;
import games.transmute.cli.util.InteractivePrompt;

import java.io.IOException;

import static games.transmute.cli.CliConstants.*;

/**
 * Command to create a new project.
 */
public class NewCommand implements Command {
    
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Project name required");
            System.err.println("Usage: transmute new <project-name> [options]");
            System.exit(1);
        }
        
        String projectName = args[0];
        String template = DEFAULT_TEMPLATE;
        boolean interactive = true;
        
        // Parse options
        for (int i = 1; i < args.length; i++) {
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
            ProjectConfig config;
            
            if (interactive) {
                InteractivePrompt prompt = new InteractivePrompt();
                ProjectConfig.Builder builder = prompt.configure(projectName, template);
                config = builder.build();
                prompt.displayConfigSummary(template, config);
            } else {
                config = ProjectConfig.builder().build();
            }
            
            generator.generateProject(projectName, template, config);
            printSuccessMessage(projectName);
            
        } catch (IOException e) {
            System.err.println("Error generating project: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public String getName() {
        return "new";
    }
    
    @Override
    public String getDescription() {
        return "Create a new project";
    }
    
    private void printSuccessMessage(String projectName) {
        System.out.println("\n✓ Project '" + projectName + "' created successfully!");
        System.out.println("\nNext steps:");
        System.out.println("  cd " + projectName);
        System.out.println("  ./gradlew run");
        System.out.println("\nFor more information, see the README.md file in your project.");
    }
}
