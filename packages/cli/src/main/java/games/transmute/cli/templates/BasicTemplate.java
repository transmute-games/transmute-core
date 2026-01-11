package games.transmute.cli.templates;

import games.transmute.cli.ProjectConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static games.transmute.cli.templates.TemplateUtils.writeCommonFiles;
import static games.transmute.cli.templates.TemplateUtils.writeFile;

/**
 * Basic project template with minimal game structure.
 */
public class BasicTemplate implements ProjectTemplate {
    
    @Override
    public void generate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException {
        String packagePath = config.getPackagePath();
        
        // Write common files
        writeCommonFiles(projectPath, vars);
        
        // Write Game class
        writeFile(
            projectPath.resolve("src/main/java").resolve(packagePath).resolve("Game.java"),
            generateGameClass(vars)
        );
    }
    
    @Override
    public String getName() {
        return "basic";
    }
    
    private String generateGameClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.core.GameConfig;
            import TransmuteCore.core.Manager;
            import TransmuteCore.core.TransmuteCore;
            import TransmuteCore.core.interfaces.services.IRenderer;
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            
            public class Game extends TransmuteCore {
            
                public Game(GameConfig config) {
                    super(config);
                }
            
                @Override
                public void init() {
                    // Initialize your game here
                }
            
                @Override
                public void update(Manager manager, double delta) {
                    // Update game logic here
                }
            
                @Override
                public void render(Manager manager, IRenderer renderer) {
                    Context ctx = (Context) renderer;
                    
                    // Clear screen
                    ctx.renderFilledRectangle(0, 0, %s, %s, 
                        Color.toPixelInt(32, 32, 64, 255));
                    
                    // Render game here
                    ctx.renderText("%s", 10, 10, 
                        Color.toPixelInt(255, 255, 255, 255));
                }
            
                public static void main(String[] args) {
                    GameConfig config = new GameConfig.Builder()
                        .title("%s")
                        .version("%s")
                        .dimensions(%s, GameConfig.ASPECT_RATIO_SQUARE)
                        .scale(%s)
                        .build();
            
                    Game game = new Game(config);
                    game.start();
                }
            }
            """.formatted(
                vars.get("PACKAGE_NAME"),
                vars.get("SCREEN_WIDTH"),
                vars.get("SCREEN_HEIGHT"),
                vars.get("GAME_TITLE"),
                vars.get("GAME_TITLE"),
                vars.get("GAME_VERSION"),
                vars.get("SCREEN_WIDTH"),
                vars.get("SCREEN_SCALE")
            );
    }
}
