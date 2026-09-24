---
name: build-transmute-game
description: Build or extend a Transmute Core 2D pixel game from a prompt and assets. Use when the user wants an agent to scaffold, implement, and headless-verify a game with TransmuteCore.
---

# Build a Transmute Core game

## Goal

Turn a prompt + asset folder into a runnable game that passes headless verify without a human at the window.

## Golden path

1. Publish engine if needed: `./gradlew :transmute-core:publishToMavenLocal`
2. Prefer copying a verified example over inventing structure:
   - Java: `examples/java/hello|platformer|rpg`
   - Python / JS / TS / C: `examples/python/hello`, `examples/javascript/hello`, `examples/typescript/hello`, `packages/c` hello
   - Contracts: `contracts/gamespec.md`
3. Or scaffold (Java): `transmute new my-game -t basic|platformer|rpg -y`
4. Put assets under `src/main/resources/` and declare them in `gamespec.properties`
5. Implement `init` / `update` / `render` on a `TransmuteCore` subclass
6. Verify: `./gradlew test verifyHeadless`

## Authoring rules

- Call `getManager().bootstrapDefaults()` in `init`
- Load font via `AssetPack` or `GameSpec.loadAssets`
- Use `manager.getInputHandler()` (not only `getInput()`) so `SimulatedInput` works headless
- Cast `IRenderer` → `Context` for pixel draws
- Prefer `World` for tile maps; prefer `Body2D` + `Collision` for platformers
- Prefer GameSpec `spawn.*` / `trigger.*` / `state.initial` over hard-coded layout when possible
- Prefer `Camera` when the world is larger than the view
- Prefer `PlaytestScript` / `PlaytestRecorder` over hand-rolled key loops in tests
- Assert SFX with `AudioProbe.install()` + `AudioPlayer.setMuted(true)` in headless
- Do not invent parallel Entity/TileMap systems; do not use deprecated `ecs.Object` / `TiledLevel`
- See `docs/AUTHORING.md` for canonical vs legacy modules
- Agent eval contract: `eval/fixtures/*` + `./scripts/agent-eval.sh`

## Verify loop

```java
try (GameHarness harness = GameHarness.of(() -> new MyGame(headlessConfig))) {
    harness.step(1);
    FrameAssert.assertPixel(harness.renderer(), x, y, color);
    SimulatedInput input = (SimulatedInput) harness.game().getManager().getInputHandler();
    input.pressKey(KeyEvent.VK_SPACE);
    harness.step(1);
}
```

## Read first

- Repo root `AGENTS.md`
- `CONTEXT.md` vocabulary
- `docs/COOKBOOK.md`
- Matching `examples/*` playtest as the recipe to copy

## Done when

- `./gradlew test verifyHeadless` passes for the game project
- Game matches the prompt’s core verb (move / jump / collect) under SimulatedInput
