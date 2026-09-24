# Tutorial 1: Hello World

Build a minimal verified game with `Manager`, `AssetPack` / `GameSpec`, and headless `GameHarness`.

## What you'll learn

- `TransmuteCore` loop: `init` / `update` / `render`
- `Manager.bootstrapDefaults()` authoring seam
- Headless verify with `GameHarness` + `FrameAssert`

## Prefer examples

Copy [`examples/java/hello`](../../examples/java/hello) instead of inventing structure. Canonical modules: [AUTHORING.md](../AUTHORING.md).

## Scaffold

```bash
transmute new hello-world-game -t basic -y
cd hello-world-game
```

Or: `./gradlew :transmute-core:publishToMavenLocal` then copy `examples/java/hello`.

## Game class

```java
public class Game extends TransmuteCore {
    private final int clearColor;

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
    public void update(Manager manager, double delta) { }

    @Override
    public void render(Manager manager, IRenderer renderer) {
        Context ctx = (Context) renderer;
        ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), clearColor);
        ctx.renderText("HELLO", 20, 70, Color.toPixelInt(255, 255, 255, 255));
    }

    public static void main(String[] args) {
        GameSpec spec = GameSpec.loadClasspath("gamespec.properties");
        boolean headless = args.length > 0 && "--headless".equals(args[0]);
        GameConfig config = new GameConfig.Builder()
            .title(spec.getTitle()).version("1.0")
            .size(320, 180).scale(2)
            .headless(headless).showStartScreen(false).build();

        if (headless) {
            try (GameHarness harness = GameHarness.of(() -> new Game(config, spec.getClearColor()))) {
                harness.step(1);
                FrameAssert.assertPixel(harness.renderer(), 0, 0, spec.getClearColor());
            }
            return;
        }
        new Game(config, spec.getClearColor()).start();
    }
}
```

## GameSpec

`src/main/resources/gamespec.properties`:

```properties
title=Hello World
width=320
height=180
clear.r=32
clear.g=32
clear.b=64
font=fonts/font.png
```

## Run

```bash
./gradlew run
./gradlew verifyHeadless   # or: ./gradlew run --args='--headless'
```

## Notes

- Prefer `Manager` in game code; `GameContext` is internal DI.
- Cast `IRenderer` → `Context` for pixel draws.
- Always ship a headless path — agents and CI have no window.

## Next

[Tutorial 2: Sprites and Animation](02-sprites-and-animation.md)
