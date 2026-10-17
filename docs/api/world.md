# Worlds and physics

Use `World` for tile-based top-down games and `Body2D` with `Collision` for gravity and jumping. These modules are exercised by the checked-in RPG and platformer examples.

## Declare a world

```properties
world.cols=20
world.rows=15
world.tile=16
world.border=true
world.solid=5,7;6,7
spawn.player=2,2
spawn.player.color=100,150,255
trigger.coin=6,2
trigger.coin.audio=pickup
```

Coordinates in this manifest are tile coordinates. Declare `audio.pickup` if a trigger should play that sound.

```java
GameSpec spec = GameSpec.loadClasspath("gamespec.properties");
World world = spec.createWorld();
```

`World` lives in `TransmuteCore.world`. Keep map data in the manifest and gameplay behavior in the game subclass.

## Gravity and collisions

`Body2D` lives in `TransmuteCore.physics`. Advance it against the scene's solids with `step(solids)`. Follow the [platformer example](../../examples/java/platformer) for a complete jump, gravity, and collision setup; use the [collision tutorial](../tutorials/04-collision-detection.md) for the individual operations.

## Verify the result

Check movement and trigger behavior using simulated input. The [RPG example](../../examples/java/rpg) verifies collection, and the platformer verifies a jump. Both expose `verifyHeadless`.
