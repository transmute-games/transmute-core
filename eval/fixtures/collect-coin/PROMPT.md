# Agent eval fixture: collect a coin

Build a small top-down game that satisfies this prompt. CI scores a solution by
running the playtest in this fixture (see `src/test`).

## Prompt

Create a 320×240 top-down Transmute Core game:

1. Load config from `gamespec.properties` (`world.*` keys).
2. Call `Manager.bootstrapDefaults()` and load assets via `GameSpec.loadAssets`.
3. Spawn a player as a `World.Actor` at tile (2, 2).
4. Place a one-shot `Trigger` coin at tile (6, 2) that increments a collect counter.
5. On collect, call `AudioPlayer.play("pickup")` (asset key `audio.pickup` may be
   missing in headless — still call play so `AudioProbe` can assert).
6. Move with WASD / arrows via `manager.getInputHandler()`.
7. Provide `--headless` that steps one frame and prints `collect headless ok`.
8. Include a `PlaytestScript` that walks the player onto the coin.

## Pass criteria

- `./gradlew test verifyHeadless` exits 0
- Playtest asserts `collected == 1` and `AudioProbe.assertPlayed("pickup")`
- No custom Entity/TileMap systems — use `World` / `Trigger` / `GameSpec`

## Reference

The `src/` tree in this fixture is the golden solution. Agents should match its
behavior under the same playtest, not necessarily its exact source layout.
