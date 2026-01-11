package games.transmute.cli.templates;

import games.transmute.cli.ProjectConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static games.transmute.cli.templates.TemplateUtils.writeCommonFiles;
import static games.transmute.cli.templates.TemplateUtils.writeFile;

/**
 * Platformer project template with physics, jumping, and collision detection.
 */
public class PlatformerTemplate implements ProjectTemplate {
    
    @Override
    public void generate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException {
        String packagePath = config.getPackagePath();
        Path javaPath = projectPath.resolve("src/main/java").resolve(packagePath);
        
        // Write common files
        writeCommonFiles(projectPath, vars);
        
        // Write platformer-specific classes
        writeFile(javaPath.resolve("Game.java"), generateGameClass(vars));
        writeFile(javaPath.resolve("Player.java"), generatePlayerClass(vars));
        writeFile(javaPath.resolve("Platform.java"), generatePlatformClass(vars));
    }
    
    @Override
    public String getName() {
        return "platformer";
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
            import java.awt.event.KeyEvent;
            import java.util.ArrayList;
            import java.util.List;
            
            public class Game extends TransmuteCore {
            
                private Player player;
                private List<Platform> platforms;
            
                public Game(GameConfig config) {
                    super(config);
                }
            
                @Override
                public void init() {
                    // Create player
                    player = new Player(50, 50);
            
                    // Create platforms
                    platforms = new ArrayList<>();
                    platforms.add(new Platform(0, 220, 320, 20));      // Ground
                    platforms.add(new Platform(80, 180, 80, 20));      // Platform 1
                    platforms.add(new Platform(200, 140, 80, 20));     // Platform 2
                    platforms.add(new Platform(50, 100, 60, 20));      // Platform 3
                }
            
                @Override
                public void update(Manager manager, double delta) {
                    // Exit on ESC
                    if (manager.getInput().isKeyPressed(KeyEvent.VK_ESCAPE)) {
                        System.exit(0);
                    }
            
                    // Update player
                    player.update(manager, delta);
                    player.checkCollision(platforms);
                }
            
                @Override
                public void render(Manager manager, IRenderer renderer) {
                    Context ctx = (Context) renderer;
                    
                    // Clear screen
                    ctx.setClearColor(Color.toPixelInt(40, 60, 80, 255));
                    
                    // Render platforms
                    for (Platform platform : platforms) {
                        platform.render(ctx);
                    }
                    
                    // Render player
                    player.render(ctx);
                    
                    // Instructions
                    ctx.renderText("Arrow Keys / WASD: Move", 10, 10, 
                        Color.toPixelInt(255, 255, 255, 255));
                    ctx.renderText("SPACE: Jump", 10, 20, 
                        Color.toPixelInt(255, 255, 255, 255));
                    ctx.renderText("ESC: Exit", 10, 30, 
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
                vars.get("GAME_TITLE"),
                vars.get("GAME_VERSION"),
                vars.get("SCREEN_WIDTH"),
                vars.get("SCREEN_SCALE")
            );
    }
    
    private String generatePlayerClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.core.Manager;
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            import java.awt.event.KeyEvent;
            import java.util.List;
            
            public class Player {
                private float x, y;
                private float velocityX = 0;
                private float velocityY = 0;
                private boolean onGround = false;
                private final float gravity = 0.5f;
                private final float jumpStrength = -10f;
                private final float moveSpeed = 3f;
                private final int width = 16;
                private final int height = 16;
            
                public Player(int x, int y) {
                    this.x = x;
                    this.y = y;
                }
            
                public void update(Manager manager, double delta) {
                    // Horizontal movement
                    velocityX = 0;
                    if (manager.getInput().isKeyHeld(KeyEvent.VK_LEFT, KeyEvent.VK_A)) {
                        velocityX = -moveSpeed;
                    }
                    if (manager.getInput().isKeyHeld(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) {
                        velocityX = moveSpeed;
                    }
            
                    // Jumping
                    if (manager.getInput().isKeyPressed(KeyEvent.VK_SPACE) && onGround) {
                        velocityY = jumpStrength;
                        onGround = false;
                    }
            
                    // Apply gravity
                    if (!onGround) {
                        velocityY += gravity;
                    }
            
                    // Update position
                    x += velocityX;
                    y += velocityY;
                }
            
                public void checkCollision(List<Platform> platforms) {
                    onGround = false;
                    for (Platform platform : platforms) {
                        if (intersects(platform)) {
                            // Bottom collision (landing on platform)
                            if (velocityY > 0 && y < platform.getY()) {
                                y = platform.getY() - height;
                                velocityY = 0;
                                onGround = true;
                            }
                        }
                    }
                }
            
                private boolean intersects(Platform platform) {
                    return x < platform.getX() + platform.getWidth() &&
                           x + width > platform.getX() &&
                           y < platform.getY() + platform.getHeight() &&
                           y + height > platform.getY();
                }
            
                public void render(Context ctx) {
                    ctx.renderFilledRectangle((int)x, (int)y, width, height, 
                        Color.toPixelInt(100, 200, 255, 255));
                }
            
                public int getX() { return (int)x; }
                public int getY() { return (int)y; }
            }
            """.formatted(vars.get("PACKAGE_NAME"));
    }
    
    private String generatePlatformClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            
            public class Platform {
                private int x, y, width, height;
            
                public Platform(int x, int y, int width, int height) {
                    this.x = x;
                    this.y = y;
                    this.width = width;
                    this.height = height;
                }
            
                public void render(Context ctx) {
                    ctx.renderFilledRectangle(x, y, width, height,
                        Color.toPixelInt(100, 100, 100, 255));
                }
            
                public int getX() { return x; }
                public int getY() { return y; }
                public int getWidth() { return width; }
                public int getHeight() { return height; }
            }
            """.formatted(vars.get("PACKAGE_NAME"));
    }
}
