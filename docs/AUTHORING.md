# Authoring model

Canonical modules for new games (humans and agents), available in **Java, Python,
JavaScript, and C** under the same contract ([contracts/](../contracts/)):

| Need | Use |
|------|-----|
| Game loop | Language game base + bootstrap |
| Top-down tiles | `World` + `Actor` + `Trigger` |
| Map as data | `GameSpec` `world.*` / `spawn.*` / `trigger.*` / `state.initial` |
| Platformer physics | `Body2D` + AABB solids |
| Assets | GameSpec `image.*` / `audio.*` / `spritesheet.*` / `font` (Java fullest) |
| Verify | Headless harness, pixel assert, playtest script, audio probe |

Java remains the **reference implementation** for edge cases. Other languages ship
the agent subset first (see package READMEs under `packages/`).

## Packages

| Language | Path |
|----------|------|
| Java | [`packages/java/transmute-core`](../packages/java/transmute-core) |
| Python | [`packages/python`](../packages/python) |
| JavaScript | [`packages/javascript`](../packages/javascript) |
| C | [`packages/c`](../packages/c) |

## Legacy Java-only (deprecated)

- `TransmuteCore.ecs.Object` / `Mob`
- `TransmuteCore.level.TiledLevel` / `Level`

Do not invent a third TileMap/Entity system in game code.

See [AGENTS.md](../AGENTS.md), [COOKBOOK.md](COOKBOOK.md), [examples/](../examples/).
