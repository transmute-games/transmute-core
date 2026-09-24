# Agent eval fixture: collect a coin

Build a small top-down game that satisfies this prompt. CI scores a solution by
running the playtest in this fixture (see `src/test`).

## Prompt

Create a 320×240 top-down Transmute Core game:

1. Load config from `gamespec.properties` with `world.*`, `spawn.*`, `trigger.*`, `state.initial=play`.
2. Call `Manager.bootstrapDefaults()` and load assets via `GameSpec.loadAssets`.
3. `createWorld()` should place trigger `coin` (and optional spawn markers).
4. Replace/add a controllable `World.Actor` player; move with WASD via `getInputHandler()`.
5. `findTrigger("coin").setOnEnter(...)` increments collect (GameSpec may already play `audio` cue).
6. Provide `--headless` and a `PlaytestScript` that walks onto the coin.
7. Do not use deprecated `ecs.Object` / `TiledLevel`.

## Pass criteria

- `./gradlew test verifyHeadless` exits 0
- Playtest asserts `collected == 1` and `AudioProbe.assertPlayed("pickup")`
