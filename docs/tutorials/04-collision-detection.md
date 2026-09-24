# Tutorial 4: Collision Detection

Use engine collision helpers: `Body2D` + `Collision` for platformers, `World` solids / `Trigger` for top-down.

## What you'll learn

- AABB via `Collision` / `Body2D.step`
- Platforms as `Body2D.Solid`
- Top-down blocking tiles and trigger volumes

**Do not** invent a second physics/entity system. Prefer [`examples/java/platformer`](../../examples/java/platformer) and [`examples/java/rpg`](../../examples/java/rpg).

## Platformer body

```java
Body2D body = new Body2D(x, y, 16, 16);
body.setVelocityX(3f);
if (input.isKeyPressed(KeyEvent.VK_SPACE)) {
    body.jump();
}
body.step(platforms); // List<? extends Body2D.Solid>
```

Platforms implement `Body2D.Solid` (`getX/Y/Width/Height`). See `examples/java/platformer` `Platform` + `Player`.

## One-shot playtest

```
# playtests/jump.script
0 press SPACE
1 release SPACE
4 idle
```

```java
PlaytestScript.loadClasspath("playtests/jump.script")
    .play(harness, (SimulatedInput) game.getManager().getInputHandler());
assertTrue(game.getPlayer().getY() < groundedY);
```

## Top-down

- **Solids** — `world.setTile(tx, ty, World.SOLID)` or GameSpec `world.solid=5,7;6,7`
- **Pickups / doors** — `world.addTrigger(new Trigger(x, y, w, h, actor -> { ... }))`

```java
World world = GameSpec.loadClasspath("gamespec.properties").createWorld();
world.fillBorder(World.SOLID);
world.addTrigger(new Trigger(coinX, coinY, 16, 16, a -> score++));
```

## Low-level AABB

```java
boolean hit = Collision.aabb(ax, ay, aw, ah, bx, by, bw, bh);
Collision.resolveAabb(...); // see COOKBOOK / Body2D internals
```

## Legacy

`TransmuteCore.ecs.Object` / `TiledLevel` still exist; prefer `World` / `Body2D` for new work ([AUTHORING.md](../AUTHORING.md)).

## Next

[Tutorial 5: State Management](05-state-management.md)
