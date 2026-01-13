# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
