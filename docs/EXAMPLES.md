# Verified examples

Each example has a Gradle wrapper, resources, tests, and a `verifyHeadless` task. Publish the core to Maven Local before running them.

| Example | Use it for | Verification |
| --- | --- | --- |
| [Hello](../examples/hello) | Minimal game lifecycle and text rendering | Frame assertions |
| [Platformer](../examples/platformer) | Gravity, jumping, and solids | Scripted jump |
| [RPG](../examples/rpg) | Tile worlds, movement, and triggers | Scripted collection |

## Run or copy

```bash
./gradlew :transmute-core:publishToMavenLocal
cd examples/rpg
./gradlew test verifyHeadless
./gradlew run
```

Copy the entire example directory when starting a project. Keep its verification task and update the playtest as you change behavior. `run` needs a display; tests and `verifyHeadless` do not.

## From prompt to game

The [authoring guide](AUTHORING.md) covers manifests and assets. The [evaluation fixtures](../eval/README.md) pair prompts with solutions and playtests so automated authors can verify their output.
