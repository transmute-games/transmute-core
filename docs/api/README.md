# Transmute Core API

The Java API gives your game a fixed timestep loop, pixel rendering, and a small set of services. A game is a `TransmuteCore` subclass: initialize resources, update logic, and render through `Manager`.

No server, API key, or network connection is needed at runtime. The reference below describes the public authoring surface; generate complete Javadocs with `./gradlew :transmute-core:javadoc`.

Version 1.0.0 · Java 17+ · `games.transmute:transmute-core`

## Engine

Configure the game and its services. Start here when writing your first subclass.

- [Game lifecycle](game.md) — `TransmuteCore`, `GameConfig`, `Manager`
- [Assets and manifests](assets.md) — `AssetPack`, `GameSpec`

## Gameplay

Build your world and verify what happens inside it.

- [Worlds and physics](world.md) — `World`, `Body2D`, `Collision`
- [Headless harness](testing.md) — `GameHarness`, `SimulatedInput`, `FrameAssert`

## Source and compatibility

The source is the authority for signatures. Browse [core source](../../packages/java/transmute-core/TransmuteCore/src/TransmuteCore) or generate Javadocs. Published engine coordinates and CLI release tags have separate version histories; use the version your game was tested against.
