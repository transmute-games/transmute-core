# Cookbook

Short recipes for Transmute Core. For agent automation, start with [AGENTS.md](../AGENTS.md)
and [examples/hello](../examples/hello).

## Headless verify one frame

```java
GameConfig config = new GameConfig.Builder()
    .title("Smoke").version("0").size(160, 90).scale(1)
    .headless(true).showStartScreen(false).build();

try (GameHarness harness = GameHarness.of(() -> new MyGame(config))) {
    harness.step(1);
    FrameAssert.assertPixel(harness.renderer(), 0, 0, expectedColor);
}
```

## Load assets from a manifest

```properties
# src/main/resources/gamespec.properties
title=My Game
width=320
height=180
font=fonts/font.png
image.player=sprites/player.png
```

```java
Manager manager = getManager();
manager.bootstrapDefaults();
GameSpec.loadClasspath("gamespec.properties").loadAssets(manager.getAssetManager());
```

## Script input in headless mode

```java
SimulatedInput input = (SimulatedInput) harness.game().getManager().getInputHandler();
input.pressKey(KeyEvent.VK_SPACE);
harness.step(1);
```

## States

```java
StateManager states = manager.getStateManager();
states.push(new PlayState(...));
states.pop(); // leave current state
```

## AABB overlap

```java
boolean hit = MathUtils.rectanglesOverlap(x1, y1, w1, h1, x2, y2, w2, h2);
```

## More

- Tutorials: [docs/tutorials/](tutorials/)
- Architecture: [WARP.md](../WARP.md)
- Domain vocab: [CONTEXT.md](../CONTEXT.md)
