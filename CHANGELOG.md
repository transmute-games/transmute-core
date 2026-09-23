# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Headless verify loop: `GameHarness`, `FrameAssert`, `stepFrame` / `initForHarness`, headless still renders into `Context`
- `SimulatedInput` auto-installed in headless mode; `Manager.getInputHandler()`
- `AssetPack` manifest loader + bundled default `fonts/font.png`
- `GameSpec` properties manifest → `GameConfig` + assets
- `Manager.bootstrapDefaults()` authoring seam
- `World` tile-grid + Actor module for top-down games
- `examples/hello`, `examples/platformer`, `examples/rpg` with headless playtests
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
