# Agent guide — Transmute Core

How to build and verify a game from a prompt + assets without a human at the window.

## Golden path

1. Publish or depend on the engine (`games.transmute:transmute-core:1.0.0` via mavenLocal / JitPack).
2. Copy `examples/hello` or run `transmute new my-game -t basic -y`.
3. Put assets under `src/main/resources/` (sprites, audio, `fonts/font.png`).
4. Declare them in `src/main/resources/gamespec.properties`.
5. Implement `init` / `update` / `render` on a `TransmuteCore` subclass.
6. Verify headless: `GameHarness` + `FrameAssert`, or `./gradlew verifyHeadless`.

## Authoring seam (one model)

Use **Manager** as the service locator for game code:

```java
@Override
public void init() {
    Manager manager = getManager();
    manager.bootstrapDefaults(); // AssetManager, StateManager, ObjectManager
    AssetPack.create(manager.getAssetManager())
        .font(AssetPack.DEFAULT_FONT_RESOURCE)
        .ensureDefaultFont();
    // or: GameSpec.loadClasspath("gamespec.properties").loadAssets(manager.getAssetManager());
}
```

`GameContext` is an internal DI container. Prefer Manager in game code and tutorials.

## Verify loop

```java
GameConfig config = new GameConfig.Builder()
    .title("Test").version("0").size(320, 180).scale(1)
    .headless(true).showStartScreen(false).build();

try (GameHarness harness = GameHarness.of(() -> new MyGame(config))) {
    harness.step(1);
    FrameAssert.assertPixel(harness.renderer(), 0, 0, expectedArgb);

    SimulatedInput input = (SimulatedInput) harness.game().getManager().getInputHandler();
    input.pressKey(KeyEvent.VK_SPACE);
    harness.step(1);
}
```

Headless installs {@code SimulatedInput} automatically. Capture with `harness.capture(file)` or `Screenshot.captureAs`.

Prefer `manager.getInputHandler()` over `getInput()` so headless playthroughs work.

## GameSpec manifest

```
title=My Game
width=320
height=180
scale=3
font=fonts/font.png
image.player=sprites/player.png
audio.jump=sounds/jump.wav
clear.r=32
clear.g=32
clear.b=64
```

## Commands

```bash
# Engine
./gradlew :transmute-core:test
./gradlew :transmute-core:publishToMavenLocal

# Reference example
cd examples/hello && ./gradlew test verifyHeadless

# Scaffold
transmute new my-game -t basic -y
```

## Do / don't

- Do cast `IRenderer` to `Context` for pixel ops.
- Do call `StateManager.pop()` to leave a state (public API).
- Don't invent parallel Entity/TileMap systems when `Object`, `Mob`, and `TiledLevel` exist.
- Don't assume a display — always provide a headless verify path.
