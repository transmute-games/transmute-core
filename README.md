<p align="center"><img src="assets/logo.svg" alt="Transmute Core logo" width="64"></p>

# Transmute Core

![Small engine. Whole worlds.](assets/og-image.png)

[![CI](https://github.com/transmute-games/transmute-core/actions/workflows/ci.yml/badge.svg)](https://github.com/transmute-games/transmute-core/actions/workflows/ci.yml)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-526447?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Gradle](https://img.shields.io/badge/Gradle-wrapper-02303A?logo=gradle)](build.gradle)
[![Bun](https://img.shields.io/badge/Bun-tooling-14151A?logo=bun)](package.json)
[![Flox](https://img.shields.io/badge/Flox-reproducible-526447)](.flox/env/manifest.toml)
[![Release](https://img.shields.io/github/v/release/transmute-games/transmute-core?filter=cli-*&label=CLI)](https://github.com/transmute-games/transmute-core/releases)
[![License](https://img.shields.io/badge/License-PolyForm_Shield_1.0.0-blue)](LICENSE)

A focused Java engine for **2D pixel games**, with a fixed timestep loop,
pixel rendering, sprites, audio, tile worlds, and physics. The core has no
external runtime dependencies. The CLI scaffolds basic, platformer, and RPG games.

Build through `Manager`, declare resources with `GameSpec`, and verify behavior
with `GameHarness`, simulated input, pixel assertions, and audio probes—without a display.

## Start building

With [Flox](https://flox.dev/docs/install-flox/) installed:

```bash
flox activate
./gradlew :transmute-core:publishToMavenLocal
cd examples/hello
./gradlew test verifyHeadless
./gradlew run
```

Or install Java 17 and run the same Gradle commands without Flox.
The windowed `run` task requires a display; verification does not.

## Find your next step

- [Documentation](docs/README.md): setup, tutorials, and recipes.
- [API reference](docs/api/README.md): lifecycle, assets, worlds, and testing.
- [Examples](docs/EXAMPLES.md): verified starting points for your game.
- [Architecture](docs/ARCHITECTURE.md): how the engine fits together.
- [Contributing](docs/CONTRIBUTING.md): Flox, hooks, CI, releases, and LOC budgets.
- [Website development](docs/WEBSITE.md): Bun commands and content editing.

## License

[PolyForm Shield 1.0.0](LICENSE) · Copyright © 2026 transmute-games.
