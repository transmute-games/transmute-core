package games.transmute.cli.templates;

import games.transmute.cli.ProjectConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static games.transmute.cli.templates.TemplateUtils.writeCommonFiles;
import static games.transmute.cli.templates.TemplateUtils.writeFile;

/**
 * RPG project template built on {@code TransmuteCore.world.World}.
 */
public class RPGTemplate implements ProjectTemplate {
    
    @Override
    public void generate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException {
        String packagePath = config.getPackagePath();
        Path javaPath = projectPath.resolve("src/main/java").resolve(packagePath);
        
        writeCommonFiles(projectPath, vars);
        writeFile(javaPath.resolve("Game.java"), generateGameClass(vars));
        writeFile(javaPath.resolve("Player.java"), generatePlayerClass(vars));
    }
    
    @Override
    public String getName() {
        return "rpg";
    }
    
    private String generateGameClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.assets.AssetPack;
            import TransmuteCore.core.GameConfig;
            import TransmuteCore.core.Manager;
            import TransmuteCore.core.TransmuteCore;
            import TransmuteCore.core.interfaces.services.IRenderer;
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            import TransmuteCore.world.World;
            
            public class Game extends TransmuteCore {
            
                private static final int TILE = 16;
                private World world;
                private Player player;
            
                public Game(GameConfig config) {
                    super(config);
                }
            
                @Override
                public void init() {
                    getManager().bootstrapDefaults();
                    AssetPack.create(getManager().getAssetManager())
                        .font(AssetPack.DEFAULT_FONT_RESOURCE)
                        .ensureDefaultFont();
            
                    world = World.grid(20, 15, TILE)
                        .clearColor(Color.toPixelInt(20, 20, 30, 255))
                        .solidColor(Color.toPixelInt(60, 60, 80, 255));
                    world.fillBorder(World.SOLID);
                    for (int x = 5; x < 10; x++) {
                        world.setTile(x, 7, World.SOLID);
                    }
                    for (int y = 3; y < 8; y++) {
                        world.setTile(15, y, World.SOLID);
                    }
            
                    player = new Player(TILE * 2, TILE * 2);
                    world.add(player);
                }
            
                @Override
                public void update(Manager manager, double delta) {
                    world.update(manager, delta);
                }
            
                @Override
                public void render(Manager manager, IRenderer renderer) {
                    world.render(manager, renderer);
                    Context ctx = (Context) renderer;
                    ctx.renderText("WASD MOVE", 10, 10, Color.toPixelInt(255, 255, 255, 255));
                }
            
                public static void main(String[] args) {
                    boolean headless = args.length > 0 && "--headless".equals(args[0]);
                    GameConfig config = new GameConfig.Builder()
                        .title("%s")
                        .version("%s")
                        .size(20 * TILE, 15 * TILE)
                        .scale(%s)
                        .headless(headless)
                        .showStartScreen(false)
                        .build();
            
                    Game game = new Game(config);
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
                vars.get("SCREEN_SCALE")
            );
    }
    
    private String generatePlayerClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.core.Manager;
            import TransmuteCore.world.World;
            import java.awt.event.KeyEvent;
            
            public class Player extends World.Actor {
                public static final int SIZE = 16;
                private final int moveSpeed = 2;
            
                public Player(int x, int y) {
                    super(x, y, SIZE, SIZE, 0xFF6496FF);
                }
            
                @Override
                public void update(Manager manager, double delta) {
                    var input = manager.getInputHandler();
                    if (input == null) {
                        return;
                    }
                    int dx = 0, dy = 0;
                    if (input.isKeyHeld(KeyEvent.VK_A, KeyEvent.VK_LEFT)) dx -= moveSpeed;
                    if (input.isKeyHeld(KeyEvent.VK_D, KeyEvent.VK_RIGHT)) dx += moveSpeed;
                    if (input.isKeyHeld(KeyEvent.VK_W, KeyEvent.VK_UP)) dy -= moveSpeed;
                    if (input.isKeyHeld(KeyEvent.VK_S, KeyEvent.VK_DOWN)) dy += moveSpeed;
                    if (dx != 0) tryMove(dx, 0);
                    if (dy != 0) tryMove(0, dy);
                }
            }
            """.formatted(vars.get("PACKAGE_NAME"));
    }
}
