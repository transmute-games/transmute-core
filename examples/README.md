# Examples

| Example | Purpose |
|---------|---------|
| [hello](hello/) | Golden-path game: GameSpec, AssetPack, Manager bootstrap, headless `FrameAssert` |

## hello

```bash
# from repo root
./gradlew :transmute-core:publishToMavenLocal
cd examples/hello
./gradlew test verifyHeadless
./gradlew run   # windowed
```
