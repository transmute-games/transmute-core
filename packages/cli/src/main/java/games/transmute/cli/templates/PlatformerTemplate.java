package games.transmute.cli.templates;

import games.transmute.cli.ProjectConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static games.transmute.cli.templates.TemplateUtils.writeCommonFiles;
import static games.transmute.cli.templates.TemplateUtils.writeFile;

/**
 * Platformer template aligned with {@code examples/platformer} (Body2D + GameHarness).
 */
public class PlatformerTemplate implements ProjectTemplate {
    
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
            clear.r=40
            clear.g=60
            clear.b=80
            font=fonts/font.png
            state.initial=play
            """.formatted(vars.get("GAME_TITLE"), vars.get("GAME_VERSION"), vars.get("SCREEN_SCALE")));
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
            
            import TransmuteCore.assets.AssetPack;
            import TransmuteCore.core.GameConfig;
            import TransmuteCore.core.GameSpec;
            import TransmuteCore.core.Manager;
            import TransmuteCore.core.TransmuteCore;
            import TransmuteCore.core.interfaces.services.IRenderer;
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            import TransmuteCore.util.verify.FrameAssert;
            import TransmuteCore.util.verify.GameHarness;
            import java.util.ArrayList;
            import java.util.List;
            
            public class Game extends TransmuteCore {
            
                public static final int SCREEN_W = 320;
                public static final int SCREEN_H = 240;
                public static final int CLEAR = Color.toPixelInt(40, 60, 80, 255);
                public static final int GROUND_Y = 220;
            
                private Player player;
                private List<Platform> platforms;
            
                public Game(GameConfig config) {
                    super(config);
                }
            
                @Override
                public void init() {
                    getManager().bootstrapDefaults();
                    AssetPack.create(getManager().getAssetManager())
                        .font(AssetPack.DEFAULT_FONT_RESOURCE)
                        .ensureDefaultFont();
                    player = new Player(10, 50);
                    platforms = new ArrayList<>();
                    platforms.add(new Platform(0, GROUND_Y, SCREEN_W, 20));
                    platforms.add(new Platform(80, 180, 80, 20));
                    platforms.add(new Platform(200, 140, 80, 20));
                }
            
                @Override
                public void update(Manager manager, double delta) {
                    player.update(manager, platforms);
                }
            
                @Override
                public void render(Manager manager, IRenderer renderer) {
                    Context ctx = (Context) renderer;
                    ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), CLEAR);
                    for (Platform platform : platforms) {
                        platform.render(ctx);
                    }
                    player.render(ctx);
                    ctx.renderText("SPACE JUMP", 10, 10, Color.toPixelInt(255, 255, 255, 255));
                }
            
                public Player getPlayer() {
                    return player;
                }
            
                public static void main(String[] args) {
                    GameSpec spec = GameSpec.loadClasspath("gamespec.properties");
                    boolean headless = args.length > 0 && "--headless".equals(args[0]);
                    GameConfig config = new GameConfig.Builder()
                        .title(spec.getTitle())
                        .version("%s")
                        .size(SCREEN_W, SCREEN_H)
                        .scale(%s)
                        .headless(headless)
                        .showStartScreen(false)
                        .build();
                    if (headless) {
                        try (GameHarness harness = GameHarness.of(() -> new Game(config))) {
                            harness.step(90);
                            FrameAssert.assertPixel(harness.renderer(), 0, 0, CLEAR);
                            System.out.println("headless ok onGround="
                                + ((Game) harness.game()).getPlayer().isOnGround());
                        }
                        return;
                    }
                    new Game(config).start();
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
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            import TransmuteCore.physics.Body2D;
            import java.awt.event.KeyEvent;
            import java.util.ArrayList;
            import java.util.List;
            
            public class Player {
                public static final int WIDTH = 16;
                public static final int HEIGHT = 16;
                private final Body2D body;
                private final float moveSpeed = 3f;
            
                public Player(int x, int y) {
                    body = new Body2D(x, y, WIDTH, HEIGHT);
                }
            
                public void update(Manager manager, List<Platform> platforms) {
                    var input = manager.getInputHandler();
                    float vx = 0;
                    if (input != null) {
                        if (input.isKeyHeld(KeyEvent.VK_LEFT, KeyEvent.VK_A)) vx = -moveSpeed;
                        if (input.isKeyHeld(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) vx = moveSpeed;
                        if (input.isKeyPressed(KeyEvent.VK_SPACE)) body.jump();
                    }
                    body.setVelocityX(vx);
                    body.step(new ArrayList<>(platforms));
                }
            
                public void render(Context ctx) {
                    ctx.renderFilledRectangle((int) body.getX(), (int) body.getY(), WIDTH, HEIGHT,
                        Color.toPixelInt(100, 200, 255, 255));
                }
            
                public boolean isOnGround() { return body.isOnGround(); }
                public int getY() { return (int) body.getY(); }
            }
            """.formatted(vars.get("PACKAGE_NAME"));
    }
    
    private String generatePlatformClass(Map<String, String> vars) {
        return """
            package %s;
            
            import TransmuteCore.graphics.Color;
            import TransmuteCore.graphics.Context;
            import TransmuteCore.physics.Body2D;
            
            public class Platform implements Body2D.Solid {
                private final float x, y, width, height;
            
                public Platform(int x, int y, int width, int height) {
                    this.x = x; this.y = y; this.width = width; this.height = height;
                }
            
                public void render(Context ctx) {
                    ctx.renderFilledRectangle((int)x, (int)y, (int)width, (int)height,
                        Color.toPixelInt(100, 100, 100, 255));
                }
            
                public float getX() { return x; }
                public float getY() { return y; }
                public float getWidth() { return width; }
                public float getHeight() { return height; }
            }
            """.formatted(vars.get("PACKAGE_NAME"));
    }
}
