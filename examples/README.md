# Examples

| Example | Purpose |
|---------|---------|
| [hello](hello/) | Golden-path game: GameSpec, AssetPack, Manager bootstrap, headless `FrameAssert` |
| [platformer](platformer/) | Action recipe: `Collision` + `SimulatedInput` jump playtest |

## hello

```bash
# from repo root
./gradlew :transmute-core:publishToMavenLocal
cd examples/hello
./gradlew test verifyHeadless
./gradlew run   # windowed
```

## platformer

```bash
./gradlew :transmute-core:publishToMavenLocal
cd examples/platformer
./gradlew test verifyHeadless   # includes SimulatedInput jump playtest
./gradlew run
```
