# Agent guide — Transmute Core

How to build and verify a game from a prompt + assets without a human at the window.

## Golden path

1. Publish or depend on the engine (`games.transmute:transmute-core:1.0.0` via mavenLocal / JitPack).
2. Copy a verified example (`examples/hello`, `platformer`, `rpg`) or run `transmute new my-game -t basic|platformer|rpg -y`.
3. Put assets under `src/main/resources/` (sprites, audio, `fonts/font.png`).
4. Declare them in `src/main/resources/gamespec.properties` (optional `world.*`, `spritesheet.*` keys).
5. Implement `init` / `update` / `render` on a `TransmuteCore` subclass.
6. Verify headless: `GameHarness` + `FrameAssert`, or data-driven `PlaytestScript`, or `./gradlew verifyHeadless`.

Use the repo skill `.cursor/skills/build-transmute-game/SKILL.md` when scaffolding from a prompt + assets.
Authoring model: [docs/AUTHORING.md](docs/AUTHORING.md).

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

## Modules to prefer

| Need | Module |
|------|--------|
| Tile top-down | `World` / `GameSpec.createWorld()` |
| Gravity / jump | `Body2D` + `Collision` |
| Headless play | `GameHarness`, `FrameAssert`, `AudioProbe`, `SimulatedInput` |
| Assets | `AssetPack` / `GameSpec` |

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

Headless installs `SimulatedInput` automatically. Prefer `manager.getInputHandler()` over `getInput()`.

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
world.cols=20
world.rows=15
world.tile=16
world.border=true
world.solid=5,7;6,7
spawn.player=2,2
spawn.player.color=100,150,255
trigger.coin=6,2
trigger.coin.audio=pickup
state.initial=play
```

## Commands

```bash
./gradlew :transmute-core:test
./gradlew :transmute-core:publishToMavenLocal
cd examples/hello && ./gradlew test verifyHeadless
cd examples/platformer && ./gradlew test verifyHeadless
cd examples/rpg && ./gradlew test verifyHeadless
./scripts/agent-eval.sh
transmute new my-game -t basic -y
```

Agent eval fixtures (prompt + golden solution + playtest): [eval/README.md](eval/README.md).

## Do / don't

- Do cast `IRenderer` to `Context` for pixel ops.
- Do call `StateManager.pop()` to leave a state (public API).
- Do copy verified examples before inventing structure.
- Do use `World` / `Body2D` instead of DIY physics/maps.
- Don't invent parallel Entity/TileMap systems.
- Don't assume a display — always provide a headless verify path.
