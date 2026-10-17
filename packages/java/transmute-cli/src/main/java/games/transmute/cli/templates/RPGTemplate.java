package games.transmute.cli.templates;

import games.transmute.cli.ProjectConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static games.transmute.cli.templates.TemplateUtils.writeCommonFiles;
import static games.transmute.cli.templates.TemplateUtils.writeFile;

/**
 * RPG template aligned with {@code examples/java/rpg} (GameSpec World + Camera + triggers).
 */
public class RPGTemplate implements ProjectTemplate {
    
    @Override
    public void generate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException {
        String packagePath = config.getPackagePath();
        Path javaPath = projectPath.resolve("src/main/java").resolve(packagePath);
        
        writeCommonFiles(projectPath, vars);
        writeFile(projectPath.resolve("src/main/resources/gamespec.properties"), """
            title=%s
            version=%s
            width=320
            height=240
            scale=%s
            clear.r=20
            clear.g=20
            clear.b=30
            font=fonts/font.png
            world.cols=40
            world.rows=20
            world.tile=16
            world.border=true
            world.solid=5,7;6,7;10,3
            spawn.player=2,2
            spawn.player.color=100,150,255
            trigger.coin=6,2
            trigger.coin.audio=pickup
            state.initial=play
            """.formatted(vars.get("GAME_TITLE"), vars.get("GAME_VERSION"), vars.get("SCREEN_SCALE")));
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
            import TransmuteCore.core.GameSpec;
            import TransmuteCore.core.Manager;
            import TransmuteCore.core.TransmuteCore;
            import TransmuteCore.core.interfaces.services.IRenderer;
            import TransmuteCore.graphics.Camera;
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            import TransmuteCore.util.verify.FrameAssert;
            import TransmuteCore.util.verify.GameHarness;
            import TransmuteCore.world.World;
            
            public class Game extends TransmuteCore {
            
                public static final int TILE = 16;
                public static final int VIEW_W = 320;
                public static final int VIEW_H = 240;
                public static final int CLEAR = Color.toPixelInt(20, 20, 30, 255);
            
                private final GameSpec spec;
                private World world;
                private Player player;
                private Camera camera;
                private int collected;
            
                public Game(GameConfig config, GameSpec spec) {
                    super(config);
                    this.spec = spec;
                }
            
                @Override
                public void init() {
                    getManager().bootstrapDefaults();
                    AssetPack.create(getManager().getAssetManager())
                        .font(AssetPack.DEFAULT_FONT_RESOURCE)
                        .ensureDefaultFont();
            
                    world = spec.createWorld();
                    world.solidColor(Color.toPixelInt(60, 60, 80, 255));
            
                    world.removeActor("player");
                    player = new Player(TILE * 2, TILE * 2);
                    world.add(player);
            
                    var coin = world.findTrigger("coin");
                    if (coin != null) {
                        coin.setOnEnter(a -> collected++);
                    }
            
                    camera = new Camera(VIEW_W, VIEW_H);
                }
            
                @Override
                public void update(Manager manager, double delta) {
                    world.update(manager, delta);
                    camera.lookAt(player.getX() + player.getWidth() / 2f,
                        player.getY() + player.getHeight() / 2f);
                    camera.clampToWorld(world.pixelWidth(), world.pixelHeight());
                }
            
                @Override
                public void render(Manager manager, IRenderer renderer) {
                    world.render(manager, renderer, camera);
                    Context ctx = (Context) renderer;
                    ctx.renderText("WASD MOVE", 10, 10, Color.toPixelInt(255, 255, 255, 255));
                }
            
                public int getCollected() { return collected; }
            
                public static void main(String[] args) {
                    GameSpec spec = GameSpec.loadClasspath("gamespec.properties");
                    boolean headless = args.length > 0 && "--headless".equals(args[0]);
                    GameConfig config = new GameConfig.Builder()
                        .title(spec.getTitle())
                        .version("%s")
                        .size(VIEW_W, VIEW_H)
                        .scale(%s)
                        .headless(headless)
                        .showStartScreen(false)
                        .build();
                    if (headless) {
                        try (GameHarness harness = GameHarness.of(() -> new Game(config, spec))) {
                            harness.step(1);
                            FrameAssert.assertPixel(harness.renderer(), TILE + 1, TILE + 1, CLEAR);
                            System.out.println("headless ok");
                        }
                        return;
                    }
                    new Game(config, spec).start();
                }
            }
            """.formatted(
                vars.get("PACKAGE_NAME"),
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
                    named("player");
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
