# Tutorial 2: Sprites and Animation

Load images via `AssetPack` / `GameSpec`, crop a `Spritesheet`, drive an `Animation`.

## What you'll learn

- Manifest keys: `image.*`, `spritesheet.*`
- `Spritesheet` + `Animation`
- Drawing with `Context`

## Declare assets

```properties
# gamespec.properties
image.player=images/player.png
spritesheet.player=images/player.png
spritesheet.player.tile=16
```

```java
@Override
public void init() {
    getManager().bootstrapDefaults();
    GameSpec spec = GameSpec.loadClasspath("gamespec.properties");
    spec.loadAssets(getManager().getAssetManager());

    Bitmap sheetBmp = getManager().getAssetManager().getImage("sheet-player");
    // or getImage("player") if you only used image.player
    Spritesheet sheet = new Spritesheet(
        sheetBmp, new Tuple2i(16, 16), new Tuple2i(0, 0), 0, 0);

    Sprite[] walk = {
        sheet.crop(0, 0), sheet.crop(1, 0), sheet.crop(2, 0), sheet.crop(3, 0)
    };
    walkAnimation = new Animation("walk", walk, 200);
}
```

`spritesheet.<name>` registers an image asset named `sheet-<name>`.

## Update / render

```java
@Override
public void update(Manager manager, double delta) {
    walkAnimation.update();
}

@Override
public void render(Manager manager, IRenderer renderer) {
    Context ctx = (Context) renderer;
    ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), bg);
    walkAnimation.render(ctx, x, y);
}
```

## Verify

```java
try (GameHarness harness = GameHarness.of(() -> new Game(headlessConfig))) {
    harness.step(5);
    FrameAssert.assertPixel(harness.renderer(), 0, 0, bg);
}
```

## Tips

- Keep frame size uniform; match `Tuple2i` to the sheet grid.
- Prefer GameSpec keys over ad-hoc `new Image(...)` so agents can list assets.
- Free sheets: OpenGameArt, itch.io.

## Next

[Tutorial 3: Input and Movement](03-input-and-movement.md)
