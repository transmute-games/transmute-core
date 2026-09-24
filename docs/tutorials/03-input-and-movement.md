# Tutorial 3: Input and Movement

Drive a character with `IInputHandler` so the same code works under `SimulatedInput` headless.

## What you'll learn

- `isKeyPressed` / `isKeyHeld` / `isKeyReleased`
- `manager.getInputHandler()` (required for headless)
- Top-down move with `World.Actor`, or free movement
- `PlaytestScript` for data-driven input

## Input states

| Method | When true |
|--------|-----------|
| `isKeyPressed` | First frame of the press |
| `isKeyHeld` | Every frame while down |
| `isKeyReleased` | First frame after release |

Always use `manager.getInputHandler()`, not only `getInput()`, so `GameHarness` can inject `SimulatedInput`.

## Top-down (preferred)

Copy [`examples/rpg`](../../examples/rpg) `Player`:

```java
var input = manager.getInputHandler();
int dx = 0, dy = 0;
if (input.isKeyHeld(KeyEvent.VK_A, KeyEvent.VK_LEFT)) dx -= speed;
if (input.isKeyHeld(KeyEvent.VK_D, KeyEvent.VK_RIGHT)) dx += speed;
if (input.isKeyHeld(KeyEvent.VK_W, KeyEvent.VK_UP)) dy -= speed;
if (input.isKeyHeld(KeyEvent.VK_S, KeyEvent.VK_DOWN)) dy += speed;
if (dx != 0) tryMove(dx, 0);
if (dy != 0) tryMove(0, dy);
```

`World.Actor.tryMove` rejects moves blocked by solid tiles.

## Headless script

`src/main/resources/playtests/walk.script`:

```
0 hold D
20 release D
22 idle
```

```java
try (GameHarness harness = GameHarness.of(() -> new Game(headlessConfig))) {
    SimulatedInput input = (SimulatedInput) harness.game().getManager().getInputHandler();
    PlaytestScript.loadClasspath("playtests/walk.script").play(harness, input);
}
```

Or press once in code:

```java
input.pressKey(KeyEvent.VK_SPACE);
harness.step(1);
```

## Platformer movement

Use `Body2D` (next tutorial / [`examples/platformer`](../../examples/platformer)) instead of hand-rolled gravity.

## Next

[Tutorial 4: Collision Detection](04-collision-detection.md)
