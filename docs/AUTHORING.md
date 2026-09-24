# Authoring model

Canonical modules for new games (humans and agents):

| Need | Use |
|------|-----|
| Game loop | `TransmuteCore` + `Manager.bootstrapDefaults()` |
| Top-down tiles | `World` + `World.Actor` + `Trigger` |
| Map as data | `GameSpec` `world.*` → `createWorld()` |
| Platformer physics | `Body2D` + `Collision` |
| Scrolling | `Camera` + `World.render(..., camera)` |
| Assets | `AssetPack` / `GameSpec` (`image.*`, `audio.*`, `spritesheet.*`, `font`) |
| Verify | `GameHarness`, `FrameAssert`, `AudioProbe`, `SimulatedInput`, `PlaytestScript` |

## Legacy (supported, not preferred for new work)

- `TransmuteCore.ecs.*` — inheritance entities (`Object`, `Mob`), not a real ECS
- `TransmuteCore.level.TiledLevel` — PNG color-indexed tile maps

Do not invent a third TileMap/Entity system in game code.

See [AGENTS.md](../AGENTS.md), [COOKBOOK.md](COOKBOOK.md), [examples/](../examples/).
