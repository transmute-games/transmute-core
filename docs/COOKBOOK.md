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

## Playtest scripts

```
# playtests/jump.script
0 press SPACE
1 release SPACE
4 idle
```

```java
PlaytestScript.loadClasspath("playtests/jump.script")
    .play(harness, (SimulatedInput) game.getManager().getInputHandler());
```

## Tile world (top-down)

```java
World world = World.grid(20, 15, 16).clearColor(bg).solidColor(wall);
world.fillBorder(World.SOLID);
world.setTile(5, 7, World.SOLID);
World.Actor player = World.Actor.colored(32, 32, 16, 16, color);
world.add(player);
player.tryMove(2, 0); // rejects if solids block
```

Or from GameSpec (`world.cols`, `world.rows`, `world.border`, `world.solid=5,7;6,7`):

```java
World world = GameSpec.loadClasspath("gamespec.properties").createWorld();
```

See `examples/rpg`.

## Platformer body

```java
Body2D body = new Body2D(x, y, 16, 16);
body.setVelocityX(3);
body.jump();
body.step(platforms); // platforms implement Body2D.Solid
```

See `examples/platformer`.

## More

- Tutorials: [docs/tutorials/](tutorials/)
- Architecture: [WARP.md](../WARP.md)
- Domain vocab: [CONTEXT.md](../CONTEXT.md)
- Agent skill: `.cursor/skills/build-transmute-game/SKILL.md`
