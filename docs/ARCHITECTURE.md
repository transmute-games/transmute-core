# Architecture

Transmute Core owns the loop and rendering surface. Your game supplies three callbacks and obtains services through `Manager`. The core has no external runtime dependencies.

## One authoring model

```mermaid
flowchart TD
    Game[Your TransmuteCore subclass] --> Manager[Manager]
    Manager --> Assets[AssetManager]
    Manager --> States[StateManager]
    Manager --> Objects[ObjectManager]
    Manager --> Input[Input handler]
    Game --> Context[Context pixel renderer]
    Game --> World[World and Body2D]
```

Call `manager.bootstrapDefaults()` in `init()` to install the standard managers. `GameContext` is the internal dependency container; game code should use `Manager`.

## Lifecycle

`init()` runs once. `update(Manager, double)` advances game logic using the engine's fixed timestep. `render(Manager, IRenderer)` draws the frame. Cast `IRenderer` to `Context` when you need pixel operations.

```mermaid
sequenceDiagram
    participant Engine as TransmuteCore
    participant Game as Your game
    participant Renderer as Context
    Engine->>Game: init()
    loop Each frame
        Engine->>Game: update(manager, delta)
        Engine->>Game: render(manager, renderer)
        Game->>Renderer: Draw sprites and pixels
    end
```

## Pick the built-in module

| Need | Use |
| --- | --- |
| Images, fonts, sound | `AssetPack` and `GameSpec` |
| Menus and gameplay screens | `StateManager`; leave a state with `pop()` |
| Tile worlds and triggers | `World` and `GameSpec.createWorld()` |
| Gravity and collision | `Body2D` and `Collision` |
| Repeatable tests | `GameHarness`, `SimulatedInput`, `FrameAssert`, `AudioProbe` |

Avoid building a parallel entity or map system. See the [API reference](api/README.md) and [authoring guide](AUTHORING.md) for concrete usage.
