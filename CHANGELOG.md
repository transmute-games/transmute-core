# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Multi-language monorepo: Java under `packages/java`, plus Python / JavaScript / TypeScript / C agent-subset ports
- Shared `contracts/` for GameSpec + PlaytestScript
- Examples under `examples/java|python|javascript|typescript`
- GameSpec `spawn.*` / `trigger.*` / `state.initial`; World find/remove by name
- `SpriteAtlas` grid helper; `PlaytestRecorder` for script capture
- Agent eval fixtures: collect-coin, platformer-jump, menu-state
- CLI templates aligned with examples (GameHarness, GameSpec, Camera)
- `@Deprecated` on `ecs.Object` / `Mob` and `level.Level` / `TiledLevel`
- Tutorials 01–07 rewritten for Manager / World / Body2D / GameSpec / PlaytestScript
- `AudioProbe` + `AudioPlayer.setMuted` for headless audio cue asserts
- Agent eval fixtures under `eval/` with CI via `scripts/agent-eval.sh`
- Headless verify loop: `GameHarness`, `FrameAssert`, `stepFrame` / `initForHarness`, headless still renders into `Context`
- `SimulatedInput` auto-installed in headless mode; `Manager.getInputHandler()`
- `AssetPack` manifest loader + bundled default `fonts/font.png`
- `GameSpec` properties manifest → `GameConfig` + assets
- `Manager.bootstrapDefaults()` authoring seam
- `World` tile-grid + Actor module for top-down games
- `Body2D` platformer physics body
- GameSpec `world.*` keys → `createWorld()`
- GameSpec `spritesheet.*` → AssetPack image registration
- `PlaytestScript` data-driven headless playthroughs
- `.cursor/skills/build-transmute-game` agent skill
- `examples/java/hello`, `examples/java/platformer`, `examples/java/rpg` with headless playtests
- `docs/AUTHORING.md` canonical vs legacy module map
- `AGENTS.md` and `CONTEXT.md` for agent-autonomous authoring
- CLI templates emit `gamespec.properties`, `--headless`, real Gradle wrapper copy, `verifyHeadless` task
- `Collision.resolveAabb` and CI smoke workflow

### Fixed
- `StateManager.pop()` is public (tutorials were calling a private method)
- `TiledLevel` extension check used `||` and always rejected valid files
- `Mob` tile collision sampled tiles incorrectly
- Asset paths no longer force-lowercased (case-sensitive filesystems)
- `ObjectManager` encapsulates its list; implements `IObjectManager`

### Changed
- Manager is the documented authoring seam; GameContext is internal DI
- CLI default dependency coord: `games.transmute:transmute-core` (mavenLocal-friendly)

## [1.0.1] - 2026-01-13

### Added
- IRenderer interface implementation for Context class
- Integer overload for `renderRectangle` method in Context
- `getClearColor` getter method in Context for accessing the clear color

### Changed
- Updated documentation and branding references to "Transmute Core"

## [1.0.0] - 2025-01-13

### Added
- Initial release of Transmute Core engine
- Standalone CLI distribution with JitPack integration
- Custom pixel buffer rendering system with 32-bit ARGB format
- Fixed timestep game loop (60 FPS default)
- Manager system for centralized subsystem coordination
- State management system for game states (menus, gameplay, pause)
- Asset management with deferred loading
- Input handling for keyboard and mouse
- Sprite sheet and animation system
- Level system with tile-based level support
- Custom binary serialization system (TinyDatabase)
- A* pathfinding for grid-based navigation
- Comprehensive documentation and tutorials

[Unreleased]: https://github.com/transmute-games/transmute-core/compare/v1.0.1...HEAD
[1.0.1]: https://github.com/transmute-games/transmute-core/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/transmute-games/transmute-core/releases/tag/v1.0.0
