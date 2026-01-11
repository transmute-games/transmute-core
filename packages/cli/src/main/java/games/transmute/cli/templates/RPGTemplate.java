package games.transmute.cli.templates;

import games.transmute.cli.ProjectConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static games.transmute.cli.templates.TemplateUtils.writeCommonFiles;
import static games.transmute.cli.templates.TemplateUtils.writeFile;

/**
 * RPG project template with tile-based maps, entities, and grid movement.
 */
public class RPGTemplate implements ProjectTemplate {
    
    @Override
    public void generate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException {
        String packagePath = config.getPackagePath();
        Path javaPath = projectPath.resolve("src/main/java").resolve(packagePath);
        
        // Write common files
        writeCommonFiles(projectPath, vars);
        
        // Write RPG-specific classes
        writeFile(javaPath.resolve("Game.java"), generateGameClass(vars));
        writeFile(javaPath.resolve("Entity.java"), generateEntityClass(vars));
        writeFile(javaPath.resolve("TileMap.java"), generateTileMapClass(vars));
    }
    
    @Override
    public String getName() {
        return "rpg";
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
            
            public class Game extends TransmuteCore {
            
                private TileMap tileMap;
                private int playerX = 50;
                private int playerY = 50;
                private final int playerSize = 16;
                private final int moveSpeed = 2;
            
                public Game(GameConfig config) {
                    super(config);
                }
            
                @Override
                public void init() {
                    // Create a simple tile map (20x15 tiles)
                    tileMap = new TileMap(20, 15, 16);
                    
                    // Create some simple walls
                    for (int x = 0; x < 20; x++) {
                        tileMap.setTile(x, 0, 1);  // Top wall
                        tileMap.setTile(x, 14, 1); // Bottom wall
                    }
                    for (int y = 0; y < 15; y++) {
                        tileMap.setTile(0, y, 1);  // Left wall
                        tileMap.setTile(19, y, 1); // Right wall
                    }
                    
                    // Add some interior obstacles
                    for (int x = 5; x < 10; x++) {
                        tileMap.setTile(x, 7, 1);
                    }
                    for (int y = 3; y < 8; y++) {
                        tileMap.setTile(15, y, 1);
                    }
                }
            
                @Override
                public void update(Manager manager, double delta) {
                    // Exit on ESC
                    if (manager.getInput().isKeyPressed(KeyEvent.VK_ESCAPE)) {
                        System.exit(0);
                    }
            
                    // Grid-based movement
                    int newX = playerX;
                    int newY = playerY;
                    
                    if (manager.getInput().isKeyPressed(KeyEvent.VK_W, KeyEvent.VK_UP)) {
                        newY -= moveSpeed;
                    }
                    if (manager.getInput().isKeyPressed(KeyEvent.VK_S, KeyEvent.VK_DOWN)) {
                        newY += moveSpeed;
                    }
                    if (manager.getInput().isKeyPressed(KeyEvent.VK_A, KeyEvent.VK_LEFT)) {
                        newX -= moveSpeed;
                    }
                    if (manager.getInput().isKeyPressed(KeyEvent.VK_D, KeyEvent.VK_RIGHT)) {
                        newX += moveSpeed;
                    }
                    
                    // Simple collision detection
                    if (!checkCollision(newX, newY)) {
                        playerX = newX;
                        playerY = newY;
                    }
                }
                
                private boolean checkCollision(int x, int y) {
                    // Check if player would collide with a tile
                    int tileX = x / 16;
                    int tileY = y / 16;
                    
                    // Check all corners of player
                    return checkTile(tileX, tileY) ||
                           checkTile((x + playerSize - 1) / 16, tileY) ||
                           checkTile(tileX, (y + playerSize - 1) / 16) ||
                           checkTile((x + playerSize - 1) / 16, (y + playerSize - 1) / 16);
                }
                
                private boolean checkTile(int tileX, int tileY) {
                    // Simple tile collision (tile type 1 is solid)
                    int tileType = tileMap.getTile(tileX, tileY);
                    return tileType == 1; // Type 1 tiles are walls/solid
                }
            
                @Override
                public void render(Manager manager, IRenderer renderer) {
                    Context ctx = (Context) renderer;
                    
                    // Clear screen
                    ctx.setClearColor(Color.toPixelInt(20, 20, 30, 255));
                    
                    // Render tile map
                    tileMap.render(ctx);
                    
                    // Render player
                    ctx.renderFilledRectangle(playerX, playerY, playerSize, playerSize,
                        Color.toPixelInt(100, 150, 255, 255));
                    
                    // Instructions
                    int white = Color.toPixelInt(255, 255, 255, 255);
                    ctx.renderText("WASD / Arrow Keys: Move", 10, 10, white);
                    ctx.renderText("ESC: Exit", 10, 20, white);
                    ctx.renderText("Position: " + playerX + ", " + playerY, 10, 30, white);
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
    
    private String generateEntityClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.graphics.Context;
            
            public abstract class Entity {
                protected int x, y;
                protected int health;
            
                public Entity(int x, int y) {
                    this.x = x;
                    this.y = y;
                    this.health = 100;
                }
            
                public abstract void update(double delta);
                public abstract void render(Context ctx);
            
                public int getX() { return x; }
                public int getY() { return y; }
            }
            """.formatted(vars.get("PACKAGE_NAME"));
    }
    
    private String generateTileMapClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            
            public class TileMap {
                private int[][] tiles;
                private int tileSize;
                private int width;
                private int height;
            
                public TileMap(int width, int height, int tileSize) {
                    this.width = width;
                    this.height = height;
                    this.tiles = new int[height][width];
                    this.tileSize = tileSize;
                }
            
                public void setTile(int x, int y, int type) {
                    if (x >= 0 && x < tiles[0].length && y >= 0 && y < tiles.length) {
                        tiles[y][x] = type;
                    }
                }
                
                public int getTile(int x, int y) {
                    if (x >= 0 && x < tiles[0].length && y >= 0 && y < tiles.length) {
                        return tiles[y][x];
                    }
                    return 0;
                }
            
                public void render(Context ctx) {
                    // Render tiles with simple colors
                    for (int y = 0; y < tiles.length; y++) {
                        for (int x = 0; x < tiles[y].length; x++) {
                            int tileType = tiles[y][x];
                            int color;
                            
                            // Simple color mapping
                            switch (tileType) {
                                case 0: // Empty/floor
                                    color = Color.toPixelInt(40, 40, 50, 255);
                                    break;
                                case 1: // Wall
                                    color = Color.toPixelInt(100, 100, 100, 255);
                                    break;
                                default:
                                    color = Color.toPixelInt(60, 60, 70, 255);
                                    break;
                            }
                            
                            ctx.renderFilledRectangle(
                                x * tileSize, 
                                y * tileSize, 
                                tileSize, 
                                tileSize, 
                                color
                            );
                        }
                    }
                }
                
                public int getTileSize() { return tileSize; }
                public int getWidth() { return width; }
                public int getHeight() { return height; }
            }
            """.formatted(vars.get("PACKAGE_NAME"));
    }
}
