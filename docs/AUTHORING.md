# Authoring model

Canonical modules for new games (humans and agents):

| Need | Use |
|------|-----|
| Game loop | `TransmuteCore` + `Manager.bootstrapDefaults()` |
| Top-down tiles | `World` + `World.Actor` + `Trigger` |
| Map as data | `GameSpec` `world.*` / `spawn.*` / `trigger.*` / `state.initial` → `createWorld()` |
| Platformer physics | `Body2D` + `Collision` |
| Scrolling | `Camera` + `World.render(..., camera)` |
| Assets | `AssetPack` / `GameSpec` (`image.*`, `audio.*`, `spritesheet.*`, `font`) + `SpriteAtlas` |
| Verify | `GameHarness`, `FrameAssert`, `AudioProbe`, `SimulatedInput`, `PlaytestScript` / `PlaytestRecorder` |

## Legacy (deprecated — do not use in new work)

- `TransmuteCore.ecs.Object` / `Mob` — `@Deprecated`; prefer `World.Actor` / `Body2D`
- `TransmuteCore.level.TiledLevel` / `Level` — `@Deprecated`; prefer `World`
- `ObjectManager` remains (bootstrap); package name is not renamed for binary compat

Do not invent a third TileMap/Entity system in game code.

See [AGENTS.md](../AGENTS.md), [COOKBOOK.md](COOKBOOK.md), [examples/](../examples/).
