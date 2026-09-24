# Agent guide — Transmute Core

How to build and verify a game from a prompt + assets without a human at the window.

## Pick a language

| Language | Package | Hello verify |
|----------|---------|--------------|
| **Java** (reference) | `packages/java/transmute-core` | `cd examples/java/hello && ./gradlew verifyHeadless` |
| **Python** | `packages/python` | `pip install -e packages/python && cd examples/python/hello && python -m hello --headless` |
| **JavaScript** | `packages/javascript` | `cd examples/javascript/hello && npm install && npm run verify` |
| **C** | `packages/c` | build with CMake, run `hello_headless` |

Shared contracts: [contracts/gamespec.md](contracts/gamespec.md), [contracts/playtest.md](contracts/playtest.md).
Authoring model: [docs/AUTHORING.md](docs/AUTHORING.md).

## Golden path (any language)

1. Depend on the matching package (Maven / pip / npm / CMake).
2. Copy the language’s `examples/*/hello` (Java also has platformer + rpg).
3. Declare layout in `gamespec.properties` (`world.*`, `spawn.*`, `trigger.*`).
4. Implement init / update / render.
5. Verify headless (pixel assert + optional PlaytestScript).

Java CLI: `transmute new my-game -t basic|platformer|rpg -y`.

## Java authoring seam

Use **Manager** as the service locator:

```java
@Override
public void init() {
    Manager manager = getManager();
    manager.bootstrapDefaults();
    AssetPack.create(manager.getAssetManager())
        .font(AssetPack.DEFAULT_FONT_RESOURCE)
        .ensureDefaultFont();
}
```

## Modules to prefer

| Need | Module |
|------|--------|
| Tile top-down | `World` / `GameSpec.createWorld()` |
| Gravity / jump | `Body2D` |
| Headless play | Harness + FrameAssert + SimulatedInput + PlaytestScript |
| Assets | GameSpec / AssetPack (Java) |

## GameSpec manifest

Portable across languages — see [contracts/gamespec.md](contracts/gamespec.md).

```
title=My Game
width=320
height=180
world.cols=20
world.rows=15
world.tile=16
world.border=true
spawn.player=2,2
trigger.coin=6,2
trigger.coin.audio=pickup
state.initial=play
```

## Commands

```bash
./gradlew :transmute-core:test
./gradlew :transmute-core:publishToMavenLocal
cd examples/java/hello && ./gradlew test verifyHeadless
./scripts/agent-eval.sh   # Java eval fixtures + Python/JS/C hello
```

## Do / don't

- Do use the same GameSpec / playtest files across languages when possible.
- Do prefer `World` / `Body2D` over DIY physics/maps.
- Don't invent parallel Entity/TileMap systems.
- Don't use deprecated Java `ecs.Object` / `TiledLevel` in new work.
- Don't assume pixel hashes match across languages (assert colors/logic).
