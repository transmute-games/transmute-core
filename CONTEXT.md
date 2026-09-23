# Transmute Core — domain context

Vocabulary for humans and agents working in this repository.

## Core concepts

- **Cortex** — game author contract: `init`, `update(Manager, delta)`, `render(Manager, IRenderer)`.
- **TransmuteCore** — abstract engine host implementing Cortex; owns the loop, window (optional), and pixel `Context`.
- **Manager** — authoring seam / service locator for AssetManager, StateManager, ObjectManager, Input. Call `bootstrapDefaults()` in `init`.
- **GameContext** — immutable DI snapshot used internally; not the primary game-authoring surface.
- **Context** — pixel buffer (`DataBufferInt`); cast from `IRenderer` to draw.
- **GameHarness / FrameAssert** — headless verify loop: step frames, assert pixels/hashes without a window.
- **SimulatedInput** — scriptable `IInputHandler` installed automatically in headless mode; drive keys between harness steps.
- **AssetPack** — loads assets from a properties manifest; owns default-font initialization.
- **GameSpec** — declarative title/size/clear/asset keys → `GameConfig` + AssetPack.
- **State / StateManager** — stack of screens; `push` / `peek` / `pop`.
- **Object / ObjectManager / Mob** — game entities (inheritance model under the `ecs` package name, not a true ECS).
- **TiledLevel** — PNG-indexed tile map with viewport culling.
- **World** — deep tile-grid + Actor module for top-down games (`blocks`, `tryMove`, `fillBorder`). Prefer over inventing TileMap.
- **Body2D** — gravity/jump/AABB platformer body resolved against `Body2D.Solid` list.
- **Camera** — view offset helper (`lookAt`, `clampToWorld`, world↔screen).
- **GameConfig** — immutable runtime config (`headless`, size, scale, FPS).

## Invariants

1. Headless mode must still render into `Context` so agents can assert frames.
2. Manager is the one authoring seam for game code.
3. Asset paths keep filesystem case; keys remain case-insensitive.
4. Templates and `examples/hello` must demonstrate Manager + AssetPack + verify, not reinvented systems.
