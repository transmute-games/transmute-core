# Tutorial 7: Level Design

Author tile worlds with `World` + GameSpec `world.*` keys. Prefer this over legacy `TiledLevel`.

## What you'll learn

- `World.grid` / solids / actors
- Declarative maps in `gamespec.properties`
- `Camera` for maps larger than the view
- `Trigger` for pickups and doors

## GameSpec map

```properties
world.cols=40
world.rows=20
world.tile=16
world.border=true
world.solid=5,7;6,7;10,3
clear.r=20
clear.g=20
clear.b=30
```

```java
World world = spec.createWorld();
world.solidColor(wallColor);
world.add(player); // World.Actor
world.addTrigger(new Trigger(x, y, tile, tile, actor -> collected++));
```

## Camera

When the world is larger than the framebuffer (see [`examples/rpg`](../../examples/rpg)):

```java
Camera camera = new Camera(viewW, viewH);
// update:
camera.lookAt(player.getX() + player.getWidth() / 2f,
              player.getY() + player.getHeight() / 2f);
camera.clampToWorld(world.pixelWidth(), world.pixelHeight());
// render:
world.render(manager, renderer, camera);
```

## Code-built map

```java
World world = World.grid(20, 15, 16).clearColor(bg).solidColor(wall);
world.fillBorder(World.SOLID);
world.setTile(8, 4, World.SOLID);
World.Actor npc = World.Actor.colored(64, 64, 16, 16, color);
world.add(npc);
```

## Verify

```java
try (GameHarness harness = GameHarness.of(() -> new RpgGame(config, spec))) {
    harness.step(1);
    FrameAssert.assertPixel(harness.renderer(), px, py, Player.COLOR);
}
```

Walk scripts: [Tutorial 3](03-input-and-movement.md). Collect + audio: [`eval/fixtures/collect-coin`](../../eval/fixtures/collect-coin).

## Legacy

`TransmuteCore.level.TiledLevel` (PNG color-indexed maps) remains supported but is not the preferred path for new games. See [AUTHORING.md](../AUTHORING.md).

## What's next

- [AGENTS.md](../../AGENTS.md) — prompt → verify loop
- [COOKBOOK.md](../COOKBOOK.md) — short recipes
- [examples/](../../examples/) — hello / platformer / rpg
