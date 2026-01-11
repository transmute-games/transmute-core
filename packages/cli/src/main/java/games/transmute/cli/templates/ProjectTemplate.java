package games.transmute.cli.templates;

import games.transmute.cli.ProjectConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Interface for project templates.
 * Each template type implements this interface to generate its specific project structure.
 */
public interface ProjectTemplate {
    
    /**
     * Generate all files for this template.
     * 
     * @param projectPath The root path of the project
     * @param config The project configuration
     * @param vars Template variables (PROJECT_NAME, PACKAGE_NAME, etc.)
     * @throws IOException if file generation fails
     */
    void generate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException;
    
    /**
     * Get the name of this template.
     * 
     * @return The template name (e.g., "basic", "platformer", "rpg")
     */
    String getName();
}
