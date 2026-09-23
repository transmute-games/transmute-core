package games.transmute.cli.templates;

import games.transmute.cli.ProjectConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
        
        writeCommonFiles(projectPath, vars);
        writeDefaultAssets(projectPath, vars);
        
        writeFile(
            projectPath.resolve("src/main/java").resolve(packagePath).resolve("Game.java"),
            generateGameClass(vars)
        );
    }
    
    @Override
    public String getName() {
        return "basic";
    }

    private void writeDefaultAssets(Path projectPath, Map<String, String> vars) throws IOException {
        Path fontsDir = projectPath.resolve("src/main/resources/fonts");
        Files.createDirectories(fontsDir);

        Path bundled = findBundledFont();
        if (bundled != null && Files.exists(bundled)) {
            Files.copy(bundled, fontsDir.resolve("font.png"), StandardCopyOption.REPLACE_EXISTING);
        }

        writeFile(projectPath.resolve("src/main/resources/gamespec.properties"), """
            title=%s
            version=%s
            width=%s
            height=%s
            scale=%s
            clear.r=32
            clear.g=32
            clear.b=64
            font=fonts/font.png
            """.formatted(
                vars.get("GAME_TITLE"),
                vars.get("GAME_VERSION"),
                vars.get("SCREEN_WIDTH"),
                vars.get("SCREEN_HEIGHT"),
                vars.get("SCREEN_SCALE")
            ));
    }

    private Path findBundledFont() {
        // Prefer monorepo font when developing; fall back to cwd-relative paths
        Path[] candidates = {
            Path.of("packages/core/TransmuteCore/res/fonts/font.png"),
            Path.of("../core/TransmuteCore/res/fonts/font.png"),
            Path.of("fonts/font.png")
        };
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }
    
    private String generateGameClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.assets.AssetPack;
            import TransmuteCore.core.GameConfig;
            import TransmuteCore.core.GameSpec;
            import TransmuteCore.core.Manager;
            import TransmuteCore.core.TransmuteCore;
            import TransmuteCore.core.interfaces.services.IRenderer;
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            
            public class Game extends TransmuteCore {
            
                private int clearColor;
            
                public Game(GameConfig config, int clearColor) {
                    super(config);
                    this.clearColor = clearColor;
                }
            
                @Override
                public void init() {
                    Manager manager = getManager();
                    manager.bootstrapDefaults();
                    AssetPack.create(manager.getAssetManager())
                        .font(AssetPack.DEFAULT_FONT_RESOURCE)
                        .ensureDefaultFont();
                }
            
                @Override
                public void update(Manager manager, double delta) {
                    // Update game logic here
                }
            
                @Override
                public void render(Manager manager, IRenderer renderer) {
                    Context ctx = (Context) renderer;
                    
                    ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), clearColor);
                    ctx.renderFilledRectangle(20, 20, 40, 40,
                        Color.toPixelInt(220, 180, 60, 255));
                    ctx.renderText("%s", 20, 70,
                        Color.toPixelInt(255, 255, 255, 255));
                }
            
                public static void main(String[] args) {
                    GameSpec spec = GameSpec.loadClasspath("gamespec.properties");
                    boolean headless = args.length > 0 && "--headless".equals(args[0]);
            
                    GameConfig config = new GameConfig.Builder()
                        .title(spec.getTitle())
                        .version("%s")
                        .size(%s, %s)
                        .scale(%s)
                        .headless(headless)
                        .showStartScreen(false)
                        .build();
            
                    Game game = new Game(config, spec.getClearColor());
                    if (headless) {
                        game.initForHarness();
                        game.stepFrame(1.0);
                        System.out.println("headless ok");
                        return;
                    }
                    game.start();
                }
            }
            """.formatted(
                vars.get("PACKAGE_NAME"),
                vars.get("GAME_TITLE"),
                vars.get("GAME_VERSION"),
                vars.get("SCREEN_WIDTH"),
                vars.get("SCREEN_HEIGHT"),
                vars.get("SCREEN_SCALE")
            );
    }
}
